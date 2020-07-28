package converter.ecomXslToJson.entities.ecom;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Класс содержащий в себе тарифную единицу весовое ограничение, цена.
 */
public class Tariff {
    private final int ww;
    private final int v;
    private boolean ws;

    /**
     * Конструктор тарифной единицы.
     *
     * @param ww весовое ограничение.
     * @param v цена.
     */
    public Tariff(int ww, int v) {
        this.ww = ww;
        this.v = v;
    }

    /**
     * Преобразование тарифной единицы в строку JSON.
     *
     * @return JSON строку.
     */
    public String toJsonString() {
        return ww * JSON_WW_MULTIPLIER + "|{" +
                "v:" + (v * JSON_V_MULTIPLIER) +
                ",ws:" + (ws ? 1 : 0) +
                "}|";
    }
}
