package converter.ecomXslToJson.entities.common;

/**
 * Класс содержащий все используемые константы.
 */
public class HCN {
    public static final int MAX_THREADS_COUNT = 4;
    public static final String CITY_TO_CITY_TARIFFS = "Город-Город тарифы";
    public static final String CITY_TO_REGIONS_TARIFFS = "Город-Регион тарифы";
    public static final String CITY_TO_CITY = "Город-Город";
    public static final String CITY_TO_REGION = "Город-Регион";
    public static final String RECEPTIONS = "Места приема";

    public static final String CITY_FROM = "Город откуда";
    public static final String REGION_FROM = "Регион откуда";
    public static final String CITY_TO = "Город куда";
    public static final String REGION_TO = "Регион куда";
    public static final String WAY = "Направление";
    public static final String TARIFF_CODE = "Код тарифа";

    public static final String INDEX = "Индекс";
    public static final String FROM = "Откуда";
    public static final String TYPE = "Тип";
    public static final String ATTACHMENT = "Принадлежность";
    public static final String INCREASE = "Надбавка";
    public static final String YES = "да";


    public static final String NOT_FOUND = "Not found";

    public static final int TO_CITY_TARIFF = 1;
    public static final int TO_REGION_TARIFF = 2;
    public static final int SIGN_PLACE_OF_RECEPTION = 1;


    public static final String START_COMMAND = "PUT|/tariff/trv/";
    public static final String JSON_DIVIDER = "-";
    public static final String JSON_COMMAND_DIVIDER = "|";
    public static final String NEW_LINE = System.lineSeparator();
    public static final String START_END_SYMBOLS = "######";
    public static final int JSON_WW_MULTIPLIER = 1000;
    public static final int JSON_V_MULTIPLIER = 100;

    public static final String ECOM_CONTRACTS = "ЕКОМ договора";
    public static final String CODE_REGION = "Код региона";
    public static final String INN = "ИНН";
    public static final String CONTRACT_NUMBER = "№ договора";
    public static final String DATE_START = "Дата начала действия признака";
    public static final String DATE_END = "Дата окончания действия признака";
    public static final String SIGN = "Признак";
    public static final int MAX_CODE_REGION = 99;
    public static final long MAX_INN = 999999999999L;
    public static final int LAST_DATE_BORDER = 20291231;
}
