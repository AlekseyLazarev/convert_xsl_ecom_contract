package converter.ecomXslToJson.entities.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс описывающий статус задачи, содержит состояние задачи, лист сообщений задачи, полосу выполнения задачи,
 * лист файлов задачи, карту значений задачи, статус необходимости завершения.
 */
@Getter
public class TaskStatus {
    private TaskState state = TaskState.NOSTATE;
    private final List<TaskMessage> msg = new ArrayList<>();
    private final ProgressBar bar = new ProgressBar();
    private final List<String> files = new ArrayList<>();
    private final Map<String, Object> values = new HashMap<>();
    private boolean abort;

    /**
     * Метод проверяет необходимость завершить задачу.
     *
     * @return true если необходимо завершить задачу, иначе false.
     */
    public boolean isAbort() {
        return abort;
    }

    /**
     * Установить флаг необходимости завершения задачи.
     */
    public void abort() {
        this.abort = true;
    }

    /**
     * Изменить текущее состояние задачи на указанное в параметрах.
     *
     * @param state состояние на которое изменяется.
     */
    public void changeState(TaskState state) {
        this.state = state;
    }

    /**
     * Метод добавления сообщения.
     *
     * @param type тип сообщения задачи.
     * @param message текст сообщения задачи.
     */
    public void addMessage(TaskMessageType type, String message) {
        this.msg.add(new TaskMessage(type, message));
    }

    /**
     * Увеличить полосу выполнения.
     */
    public void changeProgress() {
        this.bar.incValue();
    }

}
