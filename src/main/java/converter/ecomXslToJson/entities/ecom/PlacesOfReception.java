package converter.ecomXslToJson.entities.ecom;

import caits.utils.CalculateException;
import caits.utils.Post;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Класс содержащий информацию о местах приёма.
 */
@Getter
public class PlacesOfReception {
    private final int index;
    @Setter
    private String indexGuid;
    private final String from;
    @Setter
    private String fromGuid;
    private final String type;
    private final String attachment;
    private final boolean increase;

    /**
     * Конструктор мест приёма.
     *
     * @param index индекс.
     * @param from откуда.
     * @param type тип.
     * @param attachment принадлежность.
     * @param increase надбавка.
     */
    public PlacesOfReception(int index, String from, String type, String attachment, boolean increase) {
        this.index = index;
        this.from = from;
        this.type = type;
        this.attachment = attachment;
        this.increase = increase;
    }

    //TODO ????
    /**
     * Получение индекса правильного для поиска формата.
     *
     * @return форматированный индекс.
     */
    public String getFormattedIndex() {
        return index + ", " + getFormattedFrom() + ", " + type.replaceAll("[^а-яА-Яa-zA-Z0-9]", "");
    }

    /**
     * Получение города откуда в правильном для поиска формате.
     *
     * @return форматированное представление города.
     */
    public String getFormattedFrom() {
        return "г " + from;
    }

    /**
     * Получение форматированного идентификатора "откуда".
     *
     * @return форматированный идентификатор "откуда".
     */
    public String getFormattedFromGuid() {
        return fromGuid.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /**
     * Получение форматированного идентификатора "индекс".
     *
     * @return форматированный идентификатор "индекс".
     */
    public String getFormattedIndexGuid() {
        return indexGuid.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /**
     * Получение JSON строки данного места приёма.
     *
     * @param tariffCode код тарифа.
     *
     * @return сформированную JSON строку.
     * @throws CalculateException исключение выбрасываемое методом encodeStringRus, класса Post.
     */
    public String getJsonString(String tariffCode) throws CalculateException {
        return START_COMMAND + tariffCode +
                JSON_DIVIDER + Post.encodeStringRUS(getFormattedIndexGuid()) +
                JSON_DIVIDER + SIGN_PLACE_OF_RECEPTION +
                "|" + new JSONObject().
                put("pf", Post.encodeStringRUS(getFormattedFromGuid())).
                put("nf", isIncrease() ? 1 : 0) +
                "|" + NEW_LINE;
    }
}
