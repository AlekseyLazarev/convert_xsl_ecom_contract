package converter.ecomXslToJson.entities.ecom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Класс содержит карту наборов тарифных единиц.
 */
public class TariffSheet {
    private final String ecomTariffCode;
    private final int sign;
    private final String name;
    private final List<Integer> columns = new ArrayList<>();
    private final Map<Integer, Tariffs> tariffs = new LinkedHashMap<>();
    private final JSONObject calc = new JSONObject();

    /**
     * Конструктор.
     *
     * @param ecomTariffCode общий тарифный код.
     * @param sign принадлежность тарифной сетки.
     * @param name имя тарифной сетки.
     */
    public TariffSheet(String ecomTariffCode, int sign, String name) {
        this.ecomTariffCode = ecomTariffCode;
        this.sign = sign;
        this.name = name;
    }

    /**
     * Метод добавляет значения столбцов ограничений.
     *
     * @param columns лист значений столбцов ограничений.
     */
    public void addColumns(List<Integer> columns) {
        this.columns.addAll(columns);
        fillCalc();
    }

    /**
     * Формирует начальный JSON объект с параметрами взаимодействия.
     */
    public void fillCalc() {
        JSONArray a1 = new JSONArray();
        a1.put("w");
        for (Integer column : this.columns) {
            a1.put(column * JSON_WW_MULTIPLIER);
        }
        JSONObject json1 = new JSONObject();
        json1.put("f", "interval").put("a", a1).put("res", "ww");
        JSONObject json2 = new JSONObject();
        JSONArray a2 = new JSONArray();
        a2.put(this.ecomTariffCode).put("m").put("z").put("ww");
        json2.put("f", "var").put("a", a2);
        this.calc.put("calc", new JSONArray().put(json1).put(json2));
    }

    /**
     * Метод добавления тарифной единицы.
     *
     * @param codeTariff код тарифа.
     * @param weight весовое ограничение.
     * @param cost цена.
     */
    public void addTariff(int codeTariff, int weight, int cost) {
        if (this.tariffs.containsKey(codeTariff)) {
            this.tariffs.get(codeTariff).addTariff(weight, cost);
        } else {
            this.tariffs.put(codeTariff, new Tariffs(codeTariff, weight, cost));
        }
    }
    /**
     * Преобразование данного класса в строку JSON необходимого формата.
     *
     * @return JSON строку.
     */
    public String toJsonString() {
        StringBuilder sb = new StringBuilder();
        sb.append(START_END_SYMBOLS).append(this.name).append(START_END_SYMBOLS).append(NEW_LINE).
                append(START_COMMAND).append(this.ecomTariffCode).append(JSON_DIVIDER).
                append(this.sign).append(calc.toString()).append(NEW_LINE);
        for (Tariffs t : this.tariffs.values()) {
            sb.append(t.toJsonString(this.sign, this.ecomTariffCode));
        }
        sb.append(NEW_LINE);
        return sb.toString();
    }
}
