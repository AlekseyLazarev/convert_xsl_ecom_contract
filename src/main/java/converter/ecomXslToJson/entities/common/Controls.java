package converter.ecomXslToJson.entities.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс отвечающий за элементы управления.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Getter
@Setter
public class Controls {
    @Setter(AccessLevel.NONE)
    private ControlsType t;
    @Setter(AccessLevel.NONE)
    private String id;
    private String n;
    private boolean disable;
    private boolean readonly;
    private int min;
    private int max;
    private int precision;
    private int wmin;
    private int wmax;
    private int hmin;
    private int hmax;
    private boolean breakNewLine;
    private Object param;
    private String error_msg;
    private Object list;
    private Controls controls;
    private List<String> events;
    private boolean required;

    /**
     * Конструктор элемента управления.
     *
     * @param t  тип элемента управления.
     * @param id идентификатор элемента управления.
     */
    public Controls(ControlsType t, String id) {
        this.t = t;
        this.id = id;
    }
}
