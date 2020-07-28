package converter.ecomXslToJson.entities.common;

import lombok.Getter;

import java.util.Date;

/**
 * Класс описывает сообщения задачи.
 */
@Getter
public class TaskMessage {
    private TaskMessageType type;
    private String msg;
    private long time;

    /**
     * Конструктор сообщения задачи, время проставляется автоматически.
     *
     * @param type тип сообщения.
     * @param msg текст сообщения.
     */
    public TaskMessage(TaskMessageType type, String msg) {
        this.type = type;
        this.msg = msg;
        this.time = new Date().getTime();
    }
}
