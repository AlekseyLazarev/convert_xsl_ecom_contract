package converter.ecomXslToJson.logic;

import converter.ecomXslToJson.entities.common.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static converter.ecomXslToJson.entities.common.HCN.MAX_THREADS_COUNT;
public interface IXslToJson {
    Map<String, TaskStatus> statusMap = new ConcurrentHashMap<>();
    Map<String, Map> taskMap = new ConcurrentHashMap<>();
    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS_COUNT);

    /**
     * Метод устанавливает статус задачи ОК, если ошибок не встретилось.
     *
     * @param sid уникальный идентификатор.
     */
    default void setStateOkIfAllRight(String sid) throws AbortException {
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
    default void recInfo(String sid, String msg) throws AbortException {
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
    default void recError(String sid, String msg) {
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
    default TaskControls getControls() {
        TaskControls tk = new TaskControls();
        List<Controls> controlsList = new ArrayList<>();
        Controls firstLine = new Controls(ControlsType.FILE, "xsl");
        Controls secondLine = new Controls(ControlsType.STR, "json");
        Controls thirdLine = new Controls(ControlsType.STR, "tariffcode");
        firstLine.setN("Excel file path");
        secondLine.setN("JSON file name");
        thirdLine.setN("Tariff code");
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
    default TaskStatus stop(String sid) {
        TaskStatus status = statusMap.get(sid);
        if (status == null) {
            status = new TaskStatus();
            status.changeState(TaskState.ERROR);
            status.addMessage(TaskMessageType.ERROR, "Не найдена задача с идентификатором " + sid);
        } else {
            status.changeState(TaskState.ABORT);
            status.abort();
        }
        this.statusMap.remove(sid);
        return status;
    }

    /**
     * Вовзвращает статус задачи по её уникальному идентификатору.
     *
     * @param sid уникальный идентификатор.
     * @return статус задачи.
     */
    default TaskStatus status(String sid) {
        return this.statusMap.getOrDefault(sid, null);
    }

    /**
     * Запуск выполнения задачи
     *
     * @param sid  уникальный идентификатор.
     * @param data необходимые данные.
     * @return статус задачи.
     */
    default TaskStatus run(String sid, Map<String, Object> data) {
        TaskStatus currentTaskStatus = new TaskStatus();
        String tmpDir = (String) data.get("tmp_dir");
        Map<String, Object> values = (Map<String, Object>) data.get("values");
        currentTaskStatus.changeState(TaskState.WORK);
        this.statusMap.put(sid, currentTaskStatus);
        this.taskMap.put(sid, data);
        String xslPath = (String) values.get("xsl");
        String jsonPath = tmpDir + "/" + values.get("json");
        String tariffCode = String.valueOf(values.get("tariffcode"));
        if (new File(xslPath).exists()) {
            this.executorService.execute(() -> this.execute(sid, xslPath, jsonPath, tariffCode));
        } else {
            recError(sid, "Excel file not exists");
        }
        return currentTaskStatus;
    }

    /**
     * Метод получения наименований столбцов из итератора по строкам.
     *
     * @param rows итератор строк.
     * @return карту имя столбца - его номер.
     */
    default Map<String, Integer> getColumnNameMap(Iterator<Row> rows) {
        Map<String, Integer> columnNamesMap = new LinkedHashMap<>();
        if (rows.hasNext()) {
            Row row = rows.next();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cur = row.getCell(i);
                columnNamesMap.put(cur.getStringCellValue(), cur.getColumnIndex());
            }
        }
        return columnNamesMap;
    }

    /**
     * Исполняющий метод парсинга и преобразования в JSON.
     *
     * @param sid          уникальный идентификатор задания.
     * @param xslFilePath  путь к файлу экселя.
     * @param jsonFilePath путь к файлу JSON.
     * @param tariffCode   общий тарифный код.
     */
    void execute(String sid, String xslFilePath, String jsonFilePath, String tariffCode);
}
