package converter.ecomXslToJson.entities.ecom;

import caits.utils.CalculateException;
import caits.utils.Post;
import lombok.Getter;
import org.json.JSONObject;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Табличная сущность включающая в себя место откуда, место куда, направление, код тарифа, признак принадлежности таблицы.
 */
@Getter
public class SheetEntity {
    private final int sign;
    private final Place from;
    private final Place to;
    private String way;
    private final int idTariffGrid;

    /**
     * Конструктор без направления.
     *
     * @param from место откуда.
     * @param to место куда.
     * @param idTariffGrid код тарифа.
     * @param sign признак таблицы.
     */
    public SheetEntity(Place from, Place to, int idTariffGrid, int sign) {
        this.from = from;
        this.to = to;
        this.idTariffGrid = idTariffGrid;
        this.sign = sign;
    }

    /**
     * Конструктор включающий направление.
     *
     * @param from место откуда.
     * @param to место куда.
     * @param way направление.
     * @param idTariffGrid код тарифа.
     * @param sign признак таблицы.
     */
    public SheetEntity(Place from, Place to, String way, int idTariffGrid, int sign) {
        this.from = from;
        this.to = to;
        this.way = way;
        this.idTariffGrid = idTariffGrid;
        this.sign = sign;
    }

    /**
     * Получение JSON строки данной сущности.
     *
     * @param tariffCode код тарифа.
     *
     * @return сформированную JSON строку.
     * @throws CalculateException исключение выбрасываемое методом encodeStringRus, класса Post.
     */
    public String getJsonString(String tariffCode) throws CalculateException {
        return START_COMMAND + tariffCode +
                JSON_DIVIDER + Post.encodeStringRUS(getFrom().getGuid()) +
                JSON_DIVIDER + Post.encodeStringRUS(getTo().getGuid()) +
                "|" + new JSONObject().put("m", this.sign).put("z", idTariffGrid).toString() +
                "|" + NEW_LINE;
    }
}