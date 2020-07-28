package caits.utils;

import java.util.ArrayList;
import java.util.Map;

public class CountryAttribute {
	public static final int COUNTRY_INNER = 643;	 
	public static final String COUNTRY_INNER_NAME = "РФ";
	
	public int id;
	public String name;
	public String place;
	public boolean isOC;
	public boolean isNP;
	public ArrayList<Integer> serviceOff;
	
	public CountryAttribute() {
		super();
		clear();
	}
	
	public CountryAttribute(int countryID, String countryPlace, PostDictionary.PostDictionaryGet dict) throws TariffException {
		this();
		id = countryID;
		place = countryPlace;
		prepare(dict);
	}		
	
	public void clear() {
		id = 0;
		name = null;
		place = null;
		isOC = false;
		isNP = false;
		serviceOff = null;
	}
	
	@Override
	public String toString() {
		if (id>0)
			return String.valueOf(id);
		else
			return new String();
	}
	
	public boolean isInner() {
		return id==COUNTRY_INNER;
	}	

	public void setInner() {
		id = COUNTRY_INNER;
		name = COUNTRY_INNER_NAME;
	}	
	
	public String toString(String mask) {
		if (id>0 && name!=null && !name.isEmpty()) 
			return String.format(mask, id, name);
		if (isInner())
			return String.format(mask, id, COUNTRY_INNER_NAME);		
		return new String();
	}	
	
	public void prepare(PostDictionary.PostDictionaryGet dict) throws TariffException {
		name = null;
		serviceOff = null;
		if (id<=0) {
			if (place!=null) //Страна должна быть обязательно указана, т.к. есть ограничения стран
				throw new TariffException("Не указана страна назначения для международной доставки.", 1534);
			isOC = false;
			isNP = false;
			return;
		}
		if (dict==null)
			throw new TariffException("Не указаны справочники.", 1599);
		PostDictionary d = dict.getPostDictionary("country");
		if (d==null)
			throw new TariffException("Не указан справочник стран (country).", 1599);
		Map<String, Object> pl = d.get(id);
		if (pl==null) 
			throw new TariffException("Непподерживаемый код страны " + id + ".", 1531);
		name = Post.getStrEmpty(pl.get("name"));
		if (place!=null) {
    		Object obj = pl.get(place);
	    	if (obj!=null && obj instanceof Map) {
	    		pl = Post.getMapStr(obj);
	    		if (pl==null) 
	    			throw new TariffException("Внутренняя ошибка. Непподерживаемый код группы свойств \"" + place + "\" страны " + id + ".", 1587);
	    		isOC = Post.getBool(pl.get("sumoc"), true);
	    		isNP = Post.getBool(pl.get("sumnp"), true);
	    		serviceOff = Post.getArrayIntegerInstance(pl.get("serviceoff"));
	    	}	
		} else {
			isOC = true;
			isNP = true;
		}	
	}	
	
}
