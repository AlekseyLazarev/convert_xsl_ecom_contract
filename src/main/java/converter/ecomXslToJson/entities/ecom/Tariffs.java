package converter.ecomXslToJson.entities.ecom;

import java.util.ArrayList;
import java.util.List;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Класс содержит набор тарифных единиц принадлежащих одному коду тарифа.
 */
public class Tariffs {
    private final int codeTariff;
    private final List<Tariff> tariffsList = new ArrayList<>();

    /**
     * Конструктор набора тарифов, автоматически добавляет первую тарифную единицу.
     *
     * @param codeTariff код тарифа.
     * @param weight первое весовое ограничение.
     * @param cost первая цена.
     */
    public Tariffs(int codeTariff, int weight, int cost) {
        this.codeTariff = codeTariff;
        addTariff(weight, cost);
    }

    /**
     * Метод добавляет тарифную единицу.
     *
     * @param weight весовое ограничение.
     * @param cost цена.
     */
    public void addTariff(int weight, int cost){
        tariffsList.add(new Tariff(weight,cost));
    }

    /**
     * Преобразование набора тарифов в строку JSON формата.
     *
     * @param sign принадлежность тарифной сетки.
     * @param ecomTariffCode общий код тарифа.
     *
     * @return строку JSON.
     */
    public String toJsonString(int sign, String ecomTariffCode) {
        StringBuilder sb = new StringBuilder();
        for (Tariff tariff: tariffsList) {
            sb.append(START_COMMAND).append(ecomTariffCode).append(JSON_DIVIDER)
                    .append(sign).append(JSON_DIVIDER)
                    .append(codeTariff).append(JSON_DIVIDER)
                    .append(tariff.toJsonString()).append(NEW_LINE);
        }
        return sb.toString();
    }
}
