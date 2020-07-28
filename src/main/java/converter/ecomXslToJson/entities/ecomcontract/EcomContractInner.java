package converter.ecomXslToJson.entities.ecomcontract;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Класс еком контракт.
 */
public class EcomContractInner {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd");
    private LocalDateTime startDate = LocalDateTime.of(2019, 1, 1, 0, 0);
    private LocalDateTime endDate = LocalDateTime.of(2029, 12, 31, 23, 59);
    private boolean trueIfWeight;

    /**
     * Конструктор контракта.
     *
     * @param startDate дата начала контракта.
     * @param endDate   дата конца контракта.
     * @param sign      признак вес или размер.
     */
    public EcomContractInner(LocalDateTime startDate, LocalDateTime endDate, String sign) {
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
        if ("размер".equals(sign.toLowerCase())) this.trueIfWeight = true;
    }

    /**
     * Получение форматированной даты начала контракта.
     *
     * @return форматированная строка даты начала контракта.
     */
    public String getFormattedStartDate() {
        return this.startDate.format(DTF);
    }

    /**
     * Получение форматированной даты конца контракта.
     *
     * @return форматированная строка конца начала контракта.
     */
    public String getFormattedEndDate() {
        return this.endDate.format(DTF);
    }

    /**
     * Получение форматированного признака вес или размер.
     *
     * @return "1" если это вес "0" если это размер.
     */
    public String getFormattedSign() {
        return this.trueIfWeight ? "1" : "0";
    }

    /**
     * Получение представления контракта в строке JSON формата.
     *
     * @return строка JSON формата.
     */
    public String toJsonString() {
        return getFormattedStartDate() + JSON_COMMAND_DIVIDER +
                new JSONObject().put("m", getFormattedSign()).toString() +
                JSON_COMMAND_DIVIDER;
    }
}
