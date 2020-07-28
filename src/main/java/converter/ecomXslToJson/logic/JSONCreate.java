package converter.ecomXslToJson.logic;

import caits.utils.CalculateException;
import converter.ecomXslToJson.entities.ecom.PlacesOfReception;
import converter.ecomXslToJson.entities.ecom.SheetEntity;

import java.util.List;

import static converter.ecomXslToJson.entities.common.HCN.NEW_LINE;
import static converter.ecomXslToJson.entities.common.HCN.START_END_SYMBOLS;

/**
 * Класс формирования JSON
 */
public class JSONCreate {
    private final String tariffCode;

    /**
     * Конструктор принимающий общий код тарифа.
     *
     * @param tariffCode
     */
    public JSONCreate(String tariffCode) {
        this.tariffCode = tariffCode;
    }

    /**
     * Метод создания JSON строки из листа табличных сущностей.
     *
     * @param list лист табличных сущностей.
     * @param name имя таблицы.
     * @return строку JSON.
     * @throws CalculateException исключение выбрасываемое методом encodeStringRus, класса Post.
     */
    public String createEcomJson(List<SheetEntity> list, String name) throws CalculateException {
        StringBuilder sb = new StringBuilder();
        sb.append(START_END_SYMBOLS).append(name).append(START_END_SYMBOLS).append(NEW_LINE);
        for (SheetEntity cur : list) {
            sb.append(cur.getJsonString(tariffCode));
        }
        sb.append(NEW_LINE);
        return sb.toString();
    }

    /**
     * Метод создания JSON строки из мест приёма.
     *
     * @param placesOfReceptions лист мест приёма.
     * @param name               имя таблицы.
     * @return строку JSON.
     * @throws CalculateException исключение выбрасываемое методом encodeStringRus, класса Post.
     */
    public String createEcomReceptionsJson(List<PlacesOfReception> placesOfReceptions, String name) throws CalculateException {
        StringBuilder sb = new StringBuilder();
        sb.append(START_END_SYMBOLS).append(name).append(START_END_SYMBOLS).append(NEW_LINE);
        for (PlacesOfReception place : placesOfReceptions) {
            sb.append(place.getJsonString(tariffCode));
        }
        sb.append(NEW_LINE);
        return sb.toString();
    }
}
