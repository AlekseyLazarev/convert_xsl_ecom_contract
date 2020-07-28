package caits.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CommonUtils {
	/**
	 * Массив чисел в строку через разделитель
	 * @param inputArray массив
	 * @param glueString разделитель
	 * @return
	 */
	public static String implodeArray(List<?> inputArray, String glueString) {
		return implodeArrayOffset(inputArray, glueString, 1);
	}
	
	public static String implodeArrayOffset(List<?> inputArray, String glueString, int offset) {
		if (!inputArray.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(inputArray.get(0));
			for (int i=offset; i<inputArray.size(); i++) {
				sb.append(glueString);
				sb.append(inputArray.get(i));
			}
			return sb.toString();
		}
		return "";
	}

	// обьект = null или пустая строка
	public static Boolean isEmptyObject(Object O) {
		return (O == null) || ((O != null) && (O.toString().trim().isEmpty()));
	}

	// надо ли удалять значение атрибута
	public static Boolean isDeleteVal(Object valOld, Object valNew) {
		if (valOld == null) return false;
		return CommonUtils.isEmptyObject(valNew);
	}

	
	// надо ли обновлять значение атрибута
	public static Boolean isUpdateVal(Object valOld, Object valNew) {
		if ((valNew == null) && (valOld == null)) return false;
		if ((valNew == null) && (valOld != null)) return true;
		if (valNew != null && (valOld == null)) {
			if (!valNew.toString().trim().isEmpty())
				return true;
			else 
				return false;
		}
		return (!valOld.toString().equals(valNew.toString()));
	}

	// надо ли добавлять значение атрибута
	public static Boolean isAddVal(Object valOld, Object valNew) {
		if (valOld != null) return false; // если старое значение есть - его надо обновлять, а не добавлять
		if (isEmptyObject(valNew)) return false; // нечего добавлять
		return true;
	}

	// целое значение из Map
	public static int intFromObject(Object O) {
		if (O == null) return 0;
		int val;
		try {
			val = Integer.parseInt(O.toString());
		} catch (Exception E) {
			val = 0;
		}
		return val;
	}
	
	public static int intFromObject(Object O, int def) {
		if (O == null) return def;
		int val;
		try {
			val = Integer.parseInt(O.toString());
		} catch (Exception E) {
			val = def;
		}
		return val;
	}	
	
	// целое значение из Map
	public static int intFromObjectFloat(Object O) {
		if (O == null) return 0;
		int val = 0;
		try {
			val = Integer.parseInt(O.toString());
		} catch (Exception E) {
			try {
				float f = Float.parseFloat(O.toString());
				val = (int)f;
			} catch (Exception E1) {
				val = 0;
			}
		}
		return val;
	}
	
	public static long longFromObject(Object O){
		if (O == null) return 0;
		long val = 0;
		try {
			val = Long.parseLong(O.toString());
		} catch (Exception E) {
			val = 0;
		}
		return val;
	}

	public static boolean boolFromObject(Object O){
		boolean val=false;
		if(O!=null){
			String str=O.toString().trim().toLowerCase();
			val=(!str.isEmpty()) && ((str.compareTo("true")==0)||(str.charAt(0)=='1')
					||(str.compareTo("есть")==0)||(str.compareTo("да")==0)
					||(str.compareTo("yes")==0)||(str.compareTo("+")==0));
		}
		return val;
	}
	
	// целое значение из Map
	public static Integer integerFromObjectSql(Object O, boolean isEmptyNull) {
		if (O == null) {
			return null;
		}
		int val = intFromObject(O);
		if ((isEmptyNull) && (val == 0)) {
			return null;
		} else {
			return val;
		}
	}


	public static Integer integerFromObjectSqlNull(Object O) {
		return integerFromObjectSql(O, true);
	}


	public static Long longFromObjectSql(Object O, boolean isEmptyNull){
		if (O == null) {
			return null;
		}
		long val = longFromObject(O);
		if ((isEmptyNull) && (val == 0)) {
			return null;
		} else {
			return val;
		}
	}
	
	public static Long longFromObjectSqlNull(Object O) {
		return longFromObjectSql(O, true);
	}

	/**
	 * stringFromObject для null и пустых строк возвращает null
	 * @param O
	 * @return
	 */
	public static String stringFromObject(Object O) {
		if (O == null) return null;
		if (O.toString().isEmpty()) return null;
		return O.toString();
	}
	
	/**
	 * strFromObject возвращает пустую строку для null- объектов
	 * @param O- 
	 * @return
	 */
	public static String strFromObject(Object O){
		if (O == null){
			return "";
		}else{
			return O.toString();
		}
	}
	
	/**
	 * strFromObjectNull возвращает null для null объектов или для пустых строк
	 * @param O- 
	 * @return
	 */
	public static String strFromObjectNull(Object O){
		if (O == null){
			return null;
		}else{
			String res = O.toString();
			if (res==null || res.isEmpty())
				return null;
			return res;
		}
	}
		

	/**
	 * 
	 * @return текущая дата в формате XML
	 */
	public static String getNowXml() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
		return dateFormat.format( new Date() );
	}

	public static int dateToInt(Date d) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return Integer.parseInt(dateFormat.format(d));
	}
	
	public static String getNowDateSql() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format( new Date() );
	}

	public static String getFormatedDate(String d) throws ParseException {
		SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat dateFormatOut = new SimpleDateFormat("dd.MM.yyyy");
		return dateFormatOut.format(dateFormatIn.parse(d));
	}
	
	public static Date getDateFromXml(String d) {
		if (d.isEmpty()) return null;
		SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			return dateFormatIn.parse(d);
		} catch (ParseException e) {
			dateFormatIn.applyPattern("yyyy-MM-dd");
			try {
				return dateFormatIn.parse(d);
			} catch (ParseException e2) {
				return null;
			}
		}
	}

	public static Date getDateFromString(String d) {
		if (d.isEmpty()) return null;
		SimpleDateFormat dateFormatIn = new SimpleDateFormat("MM.dd.yyyy");
		try {
			return dateFormatIn.parse(d);
		} catch (ParseException e) {
			dateFormatIn.applyPattern("MM-dd-yyyy");
			try {
				return dateFormatIn.parse(d);
			} catch (ParseException e2) {
				return null;
			}
		}
	}
	

	/**
	 * Дата из строки. Если разделитель "." - ищем как "dd.MM.yyyy", если "-" - "yyyy-MM-dd"
	 * @param d дата в строке
	 * @return дата
	 */
	public static Date getDateFromString2(String d) {
		if (d.isEmpty()) return null;
		SimpleDateFormat dateFormatIn;
		if (d.indexOf(".") != -1) {
			dateFormatIn = new SimpleDateFormat("dd.MM.yyyy");
			try {
				return dateFormatIn.parse(d);
			} catch (ParseException e) {
				return null;
			}
		} else if (d.indexOf("-") != -1) {
			dateFormatIn = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return dateFormatIn.parse(d);
			} catch (ParseException e2) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * попытка чтения даты из строки разных форматов
	 * yyyy-MM-dd'T'HH:mm:ss
	 * yyyy-MM-dd
	 * yyyy.MM.dd
	 * EEE MMM dd HH:mm:ss zzz yyyy (из дефолтного преобразования Date.toString())
	 * @param d
	 * @return
	 */
	public static Date getDateFromString3(String d) {
		SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.US);
		try {
			return dateFormatIn.parse(d);
		} catch (ParseException e) {
			dateFormatIn.applyPattern("yyyy-MM-dd");
			try {
				return dateFormatIn.parse(d);
			} catch (ParseException e2) {
				try{
					dateFormatIn.applyPattern("yyyy.MM.dd");
					return dateFormatIn.parse(d);					
				}catch (ParseException e3){
					try{
						// дефолтный формат, используемый в Date.toString()
						dateFormatIn.applyPattern("EEE MMM dd HH:mm:ss zzz yyyy");
						return dateFormatIn.parse(d);	
					}catch (ParseException e4){
						return null;											
					}
				}
			}
		}
	}
	
	/**
	 * Дата из строки. Если разделитель "." - ищем как "dd.MM.yyyy", если "-" - "dd-MM-yyyy"
	 * @param d дата в строке
	 * @return дата
	 */	
	public static Date getDateFromString4(String d) {
		if (d.isEmpty()) return null;
		SimpleDateFormat dateFormatIn = new SimpleDateFormat("dd.MM.yyyy");
		try {
			return dateFormatIn.parse(d);
		} catch (ParseException e) {
			dateFormatIn.applyPattern("dd-MM-yyyy");
			try {
				return dateFormatIn.parse(d);
			} catch (ParseException e2) {
				return null;
			}
		}
	}	
	
	/**
	 * преобразование даты в строку формата ISO
	 * может принимать LocalDate- дефолтное преобразование toString()
	 * @param o
	 * @return
	 */
	public static String getStringFromDate(Object o){
		if (o==null){
			return "";
		}else if (o instanceof Date){
			SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return dateFormatIn.format((Date)o);
//		}else if(o instanceof LocalDate){
//			return o.toString();
		}else
			return o.toString();
	}
	
	
	public static Date getTimeFromString(String d, String format) {
		if (d.isEmpty()) return null;
		SimpleDateFormat dateFormatIn = new SimpleDateFormat(format);
		try {
			return dateFormatIn.parse(d);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date getDateFromMMDD(Object O) {
		String d = stringFromObject(O);
		d += ".2000"; 
		SimpleDateFormat dateFormatIn = new SimpleDateFormat("dd.MM.yyyy");
		try {
			return dateFormatIn.parse(d);
		} catch (ParseException e) {
			dateFormatIn.applyPattern("dd-MM.yyyy");
			try {
				return dateFormatIn.parse(d);
			} catch (ParseException e2) {
				return null;
			}
		}
	}
	
	/**
	 * Сравнение 2 дат без учета вермени
	 * @param d1 дата
	 * @param d2 дата
	 * @return Boolean d1 < d2
	 */
	public static Boolean compareOnlyDate(Date d1, Date d2) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(d1);
		calendar2.setTime(d2);
		return  calendar1.get(Calendar.YEAR) < calendar2.get(Calendar.YEAR) ? true : 
			calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.DAY_OF_YEAR) < calendar2.get(Calendar.DAY_OF_YEAR);
	}


	public static Map<String, Object> findMapById(List<Map<String, Object>> List, int id, String fieldId) {
		for(Map<String, Object> item: List) {
			if ((int)item.get(fieldId) == id) {
				return item;
			}
		}
		return new HashMap<String, Object>();
	}

	public static Map<String, Object> findMapByString(List<Map<String, Object>> List, String val, String fieldId) {
		for(Map<String, Object> item: List) {
			if (item.get(fieldId) != null && val.equalsIgnoreCase(item.get(fieldId).toString())) {
				return item;
			}
		}
		return new HashMap<String, Object>();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> objectToMap(Object obj) {
		if (obj == null) return null;
		if (obj instanceof Map) {
			return (Map<String, Object>) obj;
		} else {
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public static List<Object> objectToListObject(Object obj) {
		if (obj == null) return null;
		if (obj instanceof List) {
			return (List<Object>) obj;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Integer> objectToListInteger(Object obj) {
		if (obj == null) return null;
		if (obj instanceof List) {
			return (List<Integer>) obj;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Long> objectToListLong(Object obj) {
		if (obj == null) return null;
		if (obj instanceof List) {
			return (List<Long>) obj;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> objectToListMap(Object obj) {
		if (obj == null) return null;
		if (obj instanceof List) {
			return (List<Map<String, Object>>) obj;
		} else {
			return null;
		}
	}
	
	public static double roundUp(double value, int places) {
		return new BigDecimal(value).setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
}
