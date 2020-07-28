package converter.ecomXslToJson.entities.ecom;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс содержащий информацию о городе, регионе и идентификаторе данного места.
 */
@Getter
@EqualsAndHashCode(exclude = "guid")
public class Place {
    private String city = null;
    private String region;
    @Setter
    private String guid;

    /**
     * Конструктор только регион.
     *
     * @param region регион.
     */
    public Place(String region) {
        this.region = region;
    }

    /**
     * Конструктор город и регион.
     *
     * @param city город.
     * @param region регион.
     */
    public Place(String city, String region) {
        this.city = city;
        this.region = region;
    }

    /**
     * Возвращает идентификатор содержащий только буквы и цифры.
     *
     * @return форматированный идентификатор.
     */
    public String getGuid() {
        return guid.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /**
     * В случае если город не установлен возвращает регион,
     * если регион и город имеют одинаковые значение или регион состоит из 1 слова возвращает регион с подстановкой "г",
     * иначе возвращает сочетание региона и города
     *
     * @return строку для поиска в ГИС ПА.
     */
    public String getForGuidString() {
        if (this.city == null || "".equals(this.city) || this.region.equals(this.city)) {
            if (!this.region.contains(" ")) {
                return "г " + this.region;
            }
            return region;
        } else {
            return region + ", " + city;
        }
    }
}