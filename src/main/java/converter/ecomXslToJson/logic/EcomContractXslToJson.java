package converter.ecomXslToJson.logic;

import caits.utils.CalculateException;
import converter.ecomXslToJson.entities.common.TaskMessageType;
import converter.ecomXslToJson.entities.common.TaskState;
import converter.ecomXslToJson.entities.common.TaskStatus;
import converter.ecomXslToJson.entities.ecomcontract.EcomContract;
import converter.ecomXslToJson.entities.ecomcontract.EcomContractInner;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Класс отвечающий за парсинг эксель фалов ЕКОМ контракт.
 */
@Service
public class EcomContractXslToJson implements IXslToJson {
    /**
     * Метод получает ЕКОМ контракты с листа и записывает их в файл JSON формата.
     * @param sid          уникальный идентификатор задания.
     * @param xslFilePath  путь к файлу экселя.
     * @param jsonFilePath путь к файлу JSON.
     * @param tariffCode   общий тарифный код.
     */
    @Override
    public void execute(String sid, String xslFilePath, String jsonFilePath, String tariffCode) {
        Map<String, EcomContract> ecomContractMap = new HashMap<>();
        File xslFile = new File(xslFilePath);
        try (Workbook workbook = WorkbookFactory.create(xslFile)) {
            recInfo(sid, "Начало парсинга файла " + xslFilePath);
            Sheet sheet = workbook.getSheet(ECOM_CONTRACTS);
            int rowCount = sheet.getLastRowNum();
            this.statusMap.get(sid).getBar().setMax(rowCount);
            Iterator<Row> rows = sheet.rowIterator();
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
                this.statusMap.get(sid).changeProgress();
            }
            recInfo(sid, "Получение контрактов завершено");
            if (this.statusMap.get(sid).getState() == TaskState.WORK) {
                recInfo(sid, "Начало записи контрактов в файл JSON " + jsonFilePath);
                try (FileWriter fileWriter = new FileWriter(new File(jsonFilePath))) {
                    for (EcomContract ecomContract : ecomContractMap.values()) {
                        fileWriter.write(ecomContract.getJsonString(tariffCode));
                    }
                }
                recInfo(sid, "Запись завершена.");
            } else {
                recInfo(sid, "При получении контрактов возникли ошибки, файо JSON не был создан. Задача прервана");
            }
            setStateOkIfAllRight(sid);
        } catch (IOException | CalculateException e) {
            recError(sid,e.getMessage());
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
        return CellType.NUMERIC.equals(cell.getCellType()) ? cell.getLocalDateTimeCellValue() : null;
    }
}