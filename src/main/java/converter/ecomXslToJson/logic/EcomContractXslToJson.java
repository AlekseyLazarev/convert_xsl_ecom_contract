package converter.ecomXslToJson.logic;

import caits.utils.CalculateException;
import converter.ecomXslToJson.entities.common.*;
import converter.ecomXslToJson.entities.ecomcontract.EcomContract;
import converter.ecomXslToJson.entities.ecomcontract.EcomContractInner;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Класс отвечающий за парсинг эксель фалов ЕКОМ контракт.
 */
@Service
public class EcomContractXslToJson {
    private final Map<String, TaskStatus> statusMap = new ConcurrentHashMap<>();
    private final Map<String, Map> taskMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS_COUNT);

    /**
     * Метод устанавливает статус задачи ОК, если ошибок не встретилось.
     *
     * @param sid уникальный идентификатор.
     */
    private void setStateOkIfAllRight(String sid) throws AbortException {
        TaskStatus current = this.statusMap.get(sid);
        if (current.getState() == TaskState.WORK) {
            current.changeState(TaskState.OK);
            recInfo(sid, "Выполнение задачи " + sid + " завершено успешно.");
        }
    }

    /**
     * Метод записи информации в текущий статус заданий.
     *
     * @param sid текущий идентификатор задания.
     * @param msg сообщение.
     */
    private void recInfo(String sid, String msg) throws AbortException {
        TaskStatus currentTaskStatus = this.statusMap.get(sid);
        if (currentTaskStatus.isAbort()) {
            throw new AbortException("Задача остановлена");
        } else {
            currentTaskStatus.addMessage(TaskMessageType.INFO, msg);
        }
    }

    /**
     * Метод записи ошибки в текущий статус заданий.
     *
     * @param sid текущий идентификатор задания.
     * @param msg сообщение.
     */
    private void recError(String sid, String msg) {
        TaskStatus currentTaskStatus = this.statusMap.get(sid);
        if (!currentTaskStatus.isAbort()) {
            currentTaskStatus.changeState(TaskState.ERROR);
        }
        currentTaskStatus.addMessage(TaskMessageType.ERROR, msg);
    }

    /**
     * Метод создаёт и возвращает объект управления задачей.
     *
     * @return объект управления задачей.
     */
    public TaskControls getControls() {
        TaskControls tk = new TaskControls();
        List<Controls> controlsList = new ArrayList<>();
        Controls firstLine = new Controls(ControlsType.FILE, "xsl");
        Controls secondLine = new Controls(ControlsType.STR, "json");
        Controls thirdLine = new Controls(ControlsType.STR, "tariffcode");
        firstLine.setN("Путь к excel файлу ЕКОМ");
        secondLine.setN("Имя JSON файла");
        thirdLine.setN("Код тарифа");
        controlsList.add(firstLine);
        controlsList.add(secondLine);
        controlsList.add(thirdLine);
        tk.setControls(controlsList);
        return tk;
    }

    /**
     * Метод остановки задачи по её уникальному идентификатору.
     *
     * @param sid уникальный идентификатор.
     * @return статус задачи.
     */
    public TaskStatus stop(String sid) {
        TaskStatus status = statusMap.get(sid);
        if (status == null) {
            status = new TaskStatus();
            status.changeState(TaskState.ERROR);
            status.addMessage(TaskMessageType.ERROR, "Не найдена задача с идентификатором " + sid);
        } else {
            status.changeState(TaskState.ABORT);
            status.abort();
        }
        return status;
    }

    /**
     * Вовзвращает статус задачи по её уникальному идентификатору.
     *
     * @param sid уникальный идентификатор.
     * @return статус задачи.
     */
    public TaskStatus status(String sid) {
        return this.statusMap.getOrDefault(sid, null);
    }

    /**
     * Запуск выполнения задачи
     *
     * @param sid  уникальный идентификатор.
     * @param data необходимые данные.
     * @return статус задачи.
     */
    public TaskStatus run(String sid, Map<String, Object> data) {
        TaskStatus currentTaskStatus = new TaskStatus();
        String tmpDir = (String) data.get("tmp_dir");
        Map<String, Object> values = (Map<String, Object>) data.get("values");
        currentTaskStatus.changeState(TaskState.WORK);
        this.statusMap.put(sid, currentTaskStatus);
        this.taskMap.put(sid, data);
        String xslPath = (String) values.get("xsl");
        if (values.get("json").toString().length() == 0) {
            recError(sid, "Не указан файл json");
            return currentTaskStatus;
        }
        String jsonFile = tmpDir + "/" + values.get("json");
        if (values.get("tariffcode").toString().length() == 0) {
            recError(sid, "Не указан код тарифа");
            return currentTaskStatus;
        }
        String tariffCode = String.valueOf(values.get("tariffcode"));
        if (new File(xslPath).exists()) {
            this.executorService.execute(() -> this.execute(sid, xslPath, jsonFile, tariffCode));
        } else {
            recError(sid, "Excel файл не найден");
        }
        return currentTaskStatus;
    }

    /**
     * Метод получения наименований столбцов из итератора по строкам.
     *
     * @param rows итератор строк.
     * @return карту имя столбца - его номер.
     */
    private Map<String, Integer> getColumnNameMap(Iterator<Row> rows) {
        Map<String, Integer> columnNamesMap = new LinkedHashMap<>();
        if (rows.hasNext()) {
            Row row = rows.next();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cur = row.getCell(i);
                columnNamesMap.put(cur.getStringCellValue().replaceAll(" ", ""), cur.getColumnIndex());
            }
        }
        return columnNamesMap;
    }

    /**
     * Метод получает ЕКОМ контракты с листа и записывает их в файл JSON формата.
     *
     * @param sid          уникальный идентификатор задания.
     * @param xslFilePath  путь к файлу экселя.
     * @param jsonFilePath путь к файлу JSON.
     * @param tariffCode   общий тарифный код.
     */
    private void execute(String sid, String xslFilePath, String jsonFilePath, String tariffCode) {
        Map<String, EcomContract> ecomContractMap = new HashMap<>();
        File xslFile = new File(xslFilePath);
        TaskStatus currentStatus = this.statusMap.get(sid);
        try (Workbook workbook = WorkbookFactory.create(xslFile)) {
            recInfo(sid, "Начало парсинга файла " + xslFilePath);
            Sheet sheet = workbook.getSheet(ECOM_CONTRACTS);
            if (sheet == null) {
                throw new AbortException("Лист ЕКОМ договоров отсутствует");
            }
            int rowCount = sheet.getLastRowNum();
            Iterator<Row> rows = sheet.rowIterator();
            currentStatus.getBar().setMax(rowCount);
            Map<String, Integer> columnNames = getColumnNameMap(rows);
            recInfo(sid, "Начало получения контрактов . . .");
            for (int i = 2; rows.hasNext(); i++) {
                Row row = rows.next();
                LocalDateTime startDate = getDateValueFromCell(row.getCell(columnNames.get(DATE_START)));
                LocalDateTime endDate = getDateValueFromCell(row.getCell(columnNames.get(DATE_END)));
                String sign = row.getCell(columnNames.get(SIGN)).getStringCellValue();
                EcomContractInner currentContractEntity = new EcomContractInner(startDate, endDate, sign);
                short codeRegion = getShortValueFromCell(row.getCell(columnNames.get(CODE_REGION)));
                long inn = getLongValueFromCell(row.getCell(columnNames.get(INN)));
                String contractNumber = getStringValue(row.getCell(columnNames.get(CONTRACT_NUMBER)));
                EcomContract currentContract = new EcomContract(codeRegion, inn, contractNumber, currentContractEntity);
                if (codeRegion > MAX_CODE_REGION) {
                    recError(sid, "Строка №" + i + " содержит недопустимые значения в поле " + CODE_REGION);
                }
                if (inn > MAX_INN) {
                    recError(sid, "Строка №" + i + " содержит недопустимые значения в поле " + INN);
                }
                String identityString = currentContract.identityString();
                if (!ecomContractMap.containsKey(identityString)) {
                    ecomContractMap.put(identityString, currentContract);
                } else {
                    ecomContractMap.get(identityString).addEcomContractEntity(currentContractEntity);
                }
                currentStatus.changeProgress();
            }
            recInfo(sid, "Получение контрактов завершено");
            if (currentStatus.getState() == TaskState.WORK) {
                recInfo(sid, "Начало записи контрактов в файл JSON " + jsonFilePath);
                try (FileWriter fileWriter = new FileWriter(new File(jsonFilePath))) {
                    for (EcomContract ecomContract : ecomContractMap.values()) {
                        fileWriter.write(ecomContract.getJsonString(tariffCode));
                    }
                    currentStatus.addFilePath(jsonFilePath);
                }
                recInfo(sid, "Запись завершена.");
            } else {
                recInfo(sid, "При получении контрактов возникли ошибки, файо JSON не был создан. Задача прервана");
            }
            setStateOkIfAllRight(sid);
        } catch (IOException | CalculateException e) {
            recError(sid, e.getMessage());
        } catch (AbortException ae) {
            TaskStatus ts = this.statusMap.get(sid);
            ts.addMessage(TaskMessageType.WARNING, ae.getMessage());
        }
    }

    /**
     * Метод получения string значения из ячейки.
     *
     * @param cell ячейка из которой нужно получить значение.
     * @return string из ячейки.
     */
    private String getStringValue(Cell cell) {
        return CellType.STRING.equals(cell.getCellType()) ?
                cell.getStringCellValue() :
                String.valueOf(cell.getNumericCellValue());
    }

    /**
     * Метод получения short значения из ячейки.
     *
     * @param cell ячейка из которой нужно получить значение.
     * @return short из ячейки.
     */
    private short getShortValueFromCell(Cell cell) {
        return CellType.NUMERIC.equals(cell.getCellType()) ?
                (short) cell.getNumericCellValue() :
                Short.parseShort(cell.getStringCellValue().replace(" ", ""));
    }

    /**
     * Метод получения long значения из ячейки.
     *
     * @param cell ячейка из которой нужно получить значение.
     * @return long из ячейки.
     */
    private long getLongValueFromCell(Cell cell) {
        return CellType.NUMERIC.equals(cell.getCellType()) ?
                (long) cell.getNumericCellValue() :
                Long.parseLong(cell.getStringCellValue().replace(" ", ""));
    }

    /**
     * Метод получения LocalDateTime значения из ячейки.
     *
     * @param cell ячейка из которой нужно получить значение.
     * @return LocalDateTime из ячейки.
     */
    private LocalDateTime getDateValueFromCell(Cell cell) {
        return cell == null || cell.getCellType() != CellType.NUMERIC ? null : cell.getLocalDateTimeCellValue();
    }
}