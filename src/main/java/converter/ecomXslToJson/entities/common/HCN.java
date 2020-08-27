package converter.ecomXslToJson.entities.common;

/**
 * Класс содержащий все используемые константы.
 */
public class HCN {
    public static final int MAX_THREADS_COUNT = 4;
    public static final String START_COMMAND = "PUT|/tariff/trv/";
    public static final String JSON_DIVIDER = "-";
    public static final String JSON_COMMAND_DIVIDER = "|";
    public static final String NEW_LINE = System.lineSeparator();
    public static final String ECOM_CONTRACTS = "ЕКОМ договора";
    public static final String CODE_REGION = "Кодрегиона";
    public static final String INN = "ИНН";
    public static final String CONTRACT_NUMBER = "№договора";
    public static final String DATE_START = "Датаначаладействияпризнака";
    public static final String DATE_END = "Датаокончаниядействияпризнака";
    public static final String SIGN = "Признак";
    public static final int MAX_CODE_REGION = 99;
    public static final long MAX_INN = 999999999999L;
    public static final int LAST_DATE_BORDER = 20291231;
}
