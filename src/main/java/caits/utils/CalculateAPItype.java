package caits.utils;

public enum CalculateAPItype {
	API_TARIFF_V1("/tariff/v1"),
	API_DELIVERY_V1("/delivery/v1"),
	API_DELIVERY_V0("/delivery/v1");

	public final String name;

	CalculateAPItype(String name) {
		this.name = name;
	}

	public static CalculateAPItype getFromURI(String val, CalculateAPItype def) {
		if (val==null || val.isEmpty())
			return def;
		val = val.toLowerCase();
		for (CalculateAPItype t : CalculateAPItype.values())
			if (val.indexOf(t.name)==0)
				return t;
		return def;
	}
	
	public static CalculateAPItype getFromURI(String val) {
		return getFromURI(val, null);
	}
	
}
