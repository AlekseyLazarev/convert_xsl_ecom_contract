package converter.ecomXslToJson.logic;

/**
 * Исключение выбрасываемые в случае необходимости завершения задачи.
 */
public class AbortException extends Exception {
    /**
     * Конструктор исключения с сообщением.
     *
     * @param message сообщение.
     */
    public AbortException(String message) {
        super(message);
    }
}
