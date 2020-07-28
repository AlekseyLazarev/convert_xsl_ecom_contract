package converter.ecomXslToJson.entities.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс описывающий полосу выполнения задачи.
 */
@Getter
@Setter
public class ProgressBar {
    private int min = 0;
    private int max = 100;
    @Setter(AccessLevel.NONE)
    private int value = 0;

    /**
     * Метод увеличивает текущее значение на 1 единицу.
     */
    public void incValue() {
        if (this.value < this.max) {
            this.value++;
        }
    }
}
