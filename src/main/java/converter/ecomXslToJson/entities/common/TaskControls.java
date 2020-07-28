package converter.ecomXslToJson.entities.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс содержащий несколько элементов управления, а так же карту введённых значений.
 */
@Getter
@Setter
public class TaskControls {
    private List<Controls> controls = new ArrayList<>();
    private Map<String, Object> values = new LinkedHashMap<>();

    /**
     * Метод записывает лист элементов управления, создаёт для них поля в карте значений.
     *
     * @param controls лист элементов управления.
     */
    public void setControls(List<Controls> controls) {
        this.controls = controls;
        for (Controls current: controls) {
            this.values.put(current.getN(), null);
        }
    }
}
