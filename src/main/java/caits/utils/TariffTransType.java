package caits.utils;


// Типы ограничений для таблицы ограничений
public enum TariffTransType {
	NONE(-1, "", 0), //0 
	CLOSED(0, "запрещено", 1), 
	GROUND(1, "наземно", 2), 
	AVIA(2, "авиа", 3), 
	COMBO(3, "комбинированно", 4), 
	QUICK(4, "ускоренно", 5), 
	ELECTRO(5, "электронно", 6),
	STANDART(6, "стандарт", 7);

	public final short id;
	public final String desc;
	public final short code;

	TariffTransType(int id, String desc, int code) {
		this.id = (short)id;
		this.desc = desc;
		this.code = (short)code;
	}
	
	public String toString(String mask) {
		return String.format(mask, code, desc, id);
	}
	
	public static TariffTransType get(int val) {
		for (TariffTransType t : TariffTransType.values())
			if (t.id == val)
				return t;
		return NONE;
	}
	
	public static TariffTransType get(String val) {
		if (val==null || val.isEmpty()) return NONE;
		else try {
			return get(Integer.parseUnsignedInt(val));
		} catch (NumberFormatException е) {
			return NONE;
		}
	}
	
	public static TariffTransType getByCode(int val) {
		for (TariffTransType t : TariffTransType.values())
			if (t.code == val)
				return t;
		return null;
	}	

}
