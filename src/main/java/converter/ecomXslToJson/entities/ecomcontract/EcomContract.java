package converter.ecomXslToJson.entities.ecomcontract;

import caits.utils.CalculateException;
import caits.utils.Post;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Класс описывающий множество ЕКОМ контрактов.
 */
@Getter
public class EcomContract {
    private final List<EcomContractInner> ecomContractInnerList = new ArrayList<>();
    private final Short codeRegion;
    private final Long inn;
    private final String contractNumber;
    private final JSONObject calc = new JSONObject();

    /**
     * Конструктор еком контракт сущности.
     *
     * @param codeRegion код региона.
     * @param inn ИНН.
     * @param contractNumber номер контракта.
     * @param ecomContractInner первый контракт в списке.
     */
    public EcomContract(short codeRegion, long inn, String contractNumber, EcomContractInner ecomContractInner) {
        this.codeRegion = codeRegion;
        this.inn = inn;
        this.contractNumber = contractNumber.toLowerCase();
        this.ecomContractInnerList.add(ecomContractInner);
    }

    /**
     * Метод формирования шапки.
     */
    public void fillCalc() {
        List<Object> dates = new ArrayList<>();
        dates.add("date");
        for (EcomContractInner inner : getEcomContractInnerList()) {
            dates.add(inner.getFormattedStartDate());
            dates.add(inner.getFormattedEndDate());
        }
        dates.add(LAST_DATE_BORDER);
        calc.put("calc", new JSONArray().put(new JSONObject().
                put("f", "interval-down").
                put("a", dates).
                put("res", "d").
                put("error", "Договор не действует на дату 'date'")));
    }

    /**
     * Метод добавляет контракт в список
     *
     * @param ecomContractInner контракт.
     */
    public void addEcomContractEntity(EcomContractInner ecomContractInner) {
        this.ecomContractInnerList.add(ecomContractInner);
    }

    /**
     * Метод получения форматированного кода региона.
     *
     * @return форматированный код региона, две цифры с подстановкой нулей перед ними.
     */
    public String getFormattedCodeRegion() {
        return String.format("%02d", this.codeRegion);
    }

    /**
     * Метод получения форматированного ИНН, 12 или 10 цифр с подстановкой нулей перед ними.
     * @return форматированный ИНН, 10 или 12 цифр с подстановкой нулей перед ними.
     */
    public String getFormattedInn() {
        return this.inn > 9999999999L ? String.format("%012d", this.inn) : String.format("%010d", this.inn);
    }

    /**
     * Метод полученя форматированного номера контракта.
     *
     * @return номер контракта перекодированный методом encodeStringRus, класса Post.
     * @throws CalculateException исключение выбрасываемое методом encodeStringRus, класса Post.
     */
    public String getFormattedContractNumber() throws CalculateException {
        return Post.encodeStringRUS(this.contractNumber);
    }

    /**
     * Получение строки уникального идентификатора записи.
     *
     * @return сочетание номера контракта, кода региона и ИНН.
     */
    public String identityString() {
        return this.contractNumber + this.codeRegion + this.inn;
    }

    /**
     * Получение строки JSON формата.
     *
     * @param tariffCode общий код тарифа.
     *
     * @return строку JSON формата готовую к записи.
     * @throws CalculateException исключение выбрасываемое методом encodeStringRus, класса Post.
     */
    public String getJsonString(String tariffCode) throws CalculateException {
        fillCalc();
        StringBuilder result = new StringBuilder();
        StringBuilder startString = new StringBuilder();
        startString.append(START_COMMAND).append(tariffCode).
                append(JSON_DIVIDER).append(getFormattedCodeRegion()).
                append(JSON_DIVIDER).append(getFormattedInn()).
                append(JSON_DIVIDER).append(getFormattedContractNumber());
        result.append(startString).append("|").
                append(calc.toString()).append(NEW_LINE);
        for (EcomContractInner inner : ecomContractInnerList) {
            result.append(startString).append(JSON_DIVIDER).
                    append(inner.toJsonString()).append(NEW_LINE);
        }
        return result.toString();
    }
}
