package caits.utils;

import java.nio.charset.Charset;
import java.time.DateTimeException;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import static java.nio.file.FileVisitResult.CONTINUE;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;

@SuppressWarnings("restriction")
public class Post {

	public enum BoolExt {

		NONE(-1), FALSE(0), TRUE(1);

		public final short index;

		BoolExt(int idx) {
			index = (short)idx;
		}

		public short ord() {
			return index;
		}

		public static BoolExt get(int val) {
			for (BoolExt t : BoolExt.values())
				if (t.index == val)
					return t;
			return NONE;
		}

		public static BoolExt get(Boolean val) {
			if (val==null) return NONE;
			else if (val.booleanValue()) return TRUE;
			else return FALSE;
		}
		
		public static BoolExt get(String val) {
			if (val==null) return NONE;
			else if (val.isEmpty() || val.equalsIgnoreCase("false")) return FALSE;
			else try {
				return get(Long.parseUnsignedLong(val)!=0);
			} catch (NumberFormatException е) {
				return TRUE;
			}
		}
		
		public static BoolExt get(Object val) {
			if (val==null) return NONE;
			return get(val.toString());
		}
		
		public static String format(BoolExt val, String msgTrue, String msgFalse, String msgNone) {
			switch (val) {
			case NONE: return msgNone;
			case FALSE: return msgFalse;
			case TRUE: return msgTrue;
			default: return null;
			}
		}
		

	}	
	
	public enum DataType {

		NONE(0), 
		INT(1), 
		STR(2), 
		NUM(3), 
		DATE(4), 
		BOOL(5),
		TIME(7), 
		SUM(13), 
		DAY(16), 
		MONTH(17), 
		WEIGHT(29), 
		SIZE(30), 
		POST(33), 
		LIST(41), 
		SET(52), 
		BIT(55),
		GUID(63),
		ARRAY(100);

		public final short index;

		DataType(int idx) {
			index = (short)idx;
		}

		public short ord() {
			return index;
		}

		public static DataType get(int val) {
			for (DataType t : DataType.values())
				if (t.ord() == val)
					return t;
			return NONE;
		}

		public static DataType get(Object val) {
			if (val==null) return NONE;
			return get(Post.getInt(val));
		}

	}	
	
	/**
	 * Преобразование объекта в строку, возвращает строку "как есть"
	 * @param obj объект для преобразования
	 * @return строка "как есть"
	 */
	public static String getStr(Object obj) {
		if (obj == null) 
			return null;
		return obj.toString();
	}	

	/**
	 * Преобразование объекта в строку, если объект null или возвращает пустую строку, то возвращается пустая строка
	 * @param obj объект для преобразования
	 * @return строка
	 */	
	public static String getStrEmpty(Object obj) {
		if (obj != null)
			return obj.toString();
		return new String();
	}
	
	/**
	 * Преобразование объекта в строку, если объект =null или возвращает пустую строку, то возвращается null
	 * @param obj объект для преобразования
	 * @return строка
	 */		
	public static String getStrNull(Object obj) {
		if (obj == null) 
			return null;
		String res = obj.toString();
		if (res == null || res.isEmpty()) 
			return null;
		return res;
	}	
	
	/**
	 * Преобразование объекта в строку, если объект null или возвращает пустую строку, то возвращается def
	 * @param obj объект для преобразования
	 * @param def строка по умолчанию
	 * @return строка
	 */	
	public static String getStr(Object obj, String def) {
		if (obj != null)
			return obj.toString();
		return def;
	}	
	
	@Deprecated
	public static String ObjectToString(Object obj) {
		return getStrEmpty(obj);
	}

	@Deprecated
	public static String ObjectToStringNull(Object obj) {
		return getStr(obj);
	}
	
	@Deprecated
	public static String ObjectToStringNullNotEmpty(Object obj) {
		return getStrNull(obj);
	}	

	public static boolean getBool(String val, boolean defval) {
		if (val == null || val.isEmpty() || val.equalsIgnoreCase("null")) return defval;
		return !val.equalsIgnoreCase("false") && getInt(val.toString(), -1)!=0;
	}	
	
	public static boolean getBool(Object val, boolean defval) {
		if (val == null) return defval;
		else if (val instanceof Boolean) return (Boolean)val;
		else if (val instanceof Number) return ((Number)val).intValue()!=0;
		else if (val instanceof String) return getBool((String)val, defval);
		return getBool(val.toString(), defval);
	}	
	
	/**
	 * Преобразование строки в целое число
	 * @param val объект для преобразования
	 * @return целое число
	 */			
	public static Integer getIntNull(String val) {
		if ((val == null) || (val.isEmpty())) 
			return null;
		else try {
			return Integer.parseInt(val.trim());
		} catch (NumberFormatException е) {
			return null;
		}
	}	
	
	/**
	 * Преобразование объекта в целое число
	 * @param val объект для преобразования
	 * @return целое число
	 */		
	public static Integer getIntNull(Object val) {
		if (val == null) return null;
		else if (val instanceof Double) return new BigDecimal(((Double)val).doubleValue()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		else if (val instanceof Number) return ((Number)val).intValue();
		else if (val instanceof String) return getIntNull((String)val);
        else if (val instanceof Boolean) return ((Boolean) val ? 1 : 0);
		return getIntNull(val.toString());
	}
	
	/**
	 * Преобразование объекта/строки в целое число
	 * @param val объект для преобразования
	 * @param defval возвращаемое значение, если объект/строка равна null, пустая или содержит не целое число 
	 * @return целое число
	 */		
	public static int getInt(String val, int defval) {
		Integer v = getIntNull(val);
		if (v!=null) return v;
		return defval;
	}
	
	public static int getInt(String val) {
		return getInt(val, 0);
	}

	public static int getInt(Object val, int defval) {
		Integer v = getIntNull(val);
		if (v!=null) return v;
		return defval;
	}
	
	public static int getInt(Object obj) {
		return getInt(obj, 0);
	}	

	public static int getInt(Object val, int min, int max) {
		int res = getInt(val, min);
		if (res<min)
			res = min;
		else if (max>min && res>max)
			res = max;
		return res;
	}	
	
	@Deprecated
	public static int StrToInt(String val, int defval) {
		return getInt(val, defval);
	}
	
	@Deprecated
	public static int StrToInt(String val) {
		return getInt(val, 0);
	}

	@Deprecated
	public static int StrToInt(Object obj, int defval) {
		return getInt(obj, defval);
	}

	@Deprecated
	public static int StrToInt(Object obj) {
		return getInt(obj, 0);
	}
	
	/**
	 * Преобразование строки в длинное целое число
	 * @param val объект для преобразования
	 * @return длинное целое число
	 */			
	public static Long getLongNull(String val) {
		if ((val == null) || (val.isEmpty())) 
			return null;
		else try {
			return Long.parseLong(val.trim());
		} catch (NumberFormatException е) {
			return null;
		}
	}	
	
	/**
	 * Преобразование объекта в длинное целое число
	 * @param val объект для преобразования
	 * @return длинное целое число
	 */			
	public static Long getLongNull(Object val) {
		if (val == null) return null;
		else if (val instanceof Double) return new BigDecimal(((Double)val).doubleValue()).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
		else if (val instanceof Number) return ((Number)val).longValue();
		else if (val instanceof String) return getLongNull((String)val);
        else if (val instanceof Boolean) return new Long((Boolean)val ? 1 : 0);        
		return getLongNull(val.toString());
	}		

	/**
	 * Преобразование объекта/строки в длинное целое число
	 * @param val объект для преобразования
	 * @param defval возвращаемое значение, если объект/строка равна null, пустая или содержит не целое число 
	 * @return длинное целое число
	 */		
	public static long getLong(String val, long defval) {
		Long v = getLongNull(val);
		if (v!=null) return v;
		return defval;
	}
	
	public static long getLong(String val) {
		return getLong(val, 0);
	}

	
	public static long getLong(Object val, long defval) {
		Long v = getLongNull(val);
		if (v!=null) return v;
		return defval;
	}
	
	public static long getLong(Object obj) {
		return getLong(obj, 0);
	}
		
	@Deprecated
	public static long StrToLong(String val, long defval) {
		return getLong(val, defval);
	}

	@Deprecated
	public static long StrToLong(String val) {
		return getLong(val, 0);
	}

	@Deprecated
	public static long StrToLong(Object obj, long defval) {
		return getLong(obj, defval);
	}

	@Deprecated
	public static long StrToLong(Object obj) {
		return getLong(obj, 0);
	}
	
	/**
	 * Преобразование объекта/строки в дробное число
	 * @param val объект для преобразования
	 * @param defval возвращаемое значение, если объект/строка равна null, пустая или содержит не дробное число 
	 * @return дробное число
	 */	
	public static double getNum(String val, double defval) {
		if ((val == null) || (val.isEmpty())) return defval;
		else try {
			return Double.parseDouble(val);
		} catch (NumberFormatException е) {
			return defval;
		}
	}
	
	public static double getNum(Object val, double defval) {
		if (val == null) return defval;
		else if (val instanceof Number) return ((Number)val).doubleValue();
		else if (val instanceof String) return getNum((String)val, defval);
        else if (val instanceof Boolean) return ((Boolean) val ? 1 : 0);
		else if (val.toString().trim().isEmpty()) return defval;
		return getNum(val.toString(), defval);
	}

	public static Double getNumNull(String val) {
		if ((val == null) || (val.isEmpty())) return null;
		else try {
			return Double.parseDouble(val);
		} catch (NumberFormatException е) {
			return null;
		}
	}	
	
	public static Double getNumNull(Object val) {
		if (val == null) return null;
		else if (val instanceof Number) return ((Number)val).doubleValue();
		else if (val instanceof String) return getNumNull((String)val);
        else if (val instanceof Boolean) return new Double((Boolean) val ? 1 : 0);
		else if (val.toString().trim().isEmpty()) return null;
		return getNumNull(val.toString());
	}	
	
	public static double getNum(Object val) {
		return getNum(val, 0);
	}	
	
	@Deprecated
	public static double StrToNum(String val, double defval) {
		return getNum(val, defval);
	}

	@Deprecated
	public static double StrToNum(String val) {
		return getNum(val, 0);
	}

	@Deprecated
	public static double StrToNum(Object obj, double defval) {
		return getNum(obj, defval);
	}

	@Deprecated
	public static double StrToNum(Object obj) {
		return getNum(obj, 0);
	}
	

	public static String WeightToStr(long val, boolean isUnit) {
		long gr = val % 1000;
		String res = new String();
		if (gr != 0) {
			res = res + String.format("%d", gr);
			if (isUnit)
				res = res + " г";
		}
		long kg = val / 1000;
		if (kg == 0)
			return res;
		long tn = kg / 1000;
		kg = kg % 1000;
		if (kg != 0) {
			if (!res.isEmpty())
				res = " " + res;
			if (isUnit)
				res = " кг" + res;
			res = String.format("%d", kg) + res;
		}
		if (tn == 0)
			return res;
		if (tn != 0) {
			if (!res.isEmpty())
				res = " " + res;
			if (isUnit)
				res = " " + formatSuffixByNumeric(tn, "тонна", "тонны", "тонн") + res;
			res = String.format("%d", tn) + res;
		}
		return res;
	}
	
	public static String WeightToStr(int val, boolean isUnit) {
		return WeightToStr((long)val, isUnit);
	}

	public static String doubleToStr(Double val) {
		if (val==null)
			return new String("");
		else if ((val-val.intValue())==0) 
			return String.valueOf(val.intValue());
		else
			return val.toString();
	}
	
	public static Object doubleToIntegerNoDecimal(Object val) {
		if (!(val instanceof Double))
			return val;
		Double v = (Double)val;
		if ((v-v.intValue())==0) 
			return Integer.valueOf(v.intValue());
		else
			return val; 
	}
	
	
	public static String getCurrecyLong(int val) {
		return String.format("%,d-%02d", val / 100, val % 100);
	}
	
	@Deprecated
	public static String CurrecyToStr(int val) {
		return getCurrecyLong(val);
	}

	public static String getCurrecyLong(long val) {
		return String.format("%,d-%02d", val / 100, val % 100);
	}
	
	@Deprecated
	public static String CurrecyToStr(long val) {
		return getCurrecyLong(val);
	}
	
	public static String getCurrecyShort(int val) {
		return String.format("%d-%02d", val / 100, val % 100);
	}
	
	@Deprecated
	public static String CurrecyToStrShort(int val) {
		return getCurrecyShort(val);
	}

	public static String getCurrecyShort(long val) {
		return String.format("%d-%02d", val / 100, val % 100);
	}
	
	public static String getCurrecyShort(long val, String empty) {
		if (val!=0)
			return getCurrecyShort(val);
		else
			return empty;
	}		
	
	@Deprecated
	public static String CurrecyToStrShort(long val) {
		return getCurrecyShort(val);
	}
	
	public static String getCurrecyJSON(long val) {
		return String.format("%d.%d", val / 100, val % 100);
	}		
	
	public static String arrayToString(List<Object> list, String selector) {
		String res = new String();
		for (Object item : list) if (item!=null) {
			if (!res.isEmpty()) res = res + selector;
			res = res + item.toString();
		}
		return res;
	}
	
	public static String arrayToString(List<Object> list) {
		return arrayToString(list, ",");
	}	

	public static String arrayToString(Object list, String selector) {
		ArrayList<Object> arr = getArray(list);
		if (arr!=null) return arrayToString(arr, selector);
		else return new String();
	}
	
	public static String arrayToString(Object list) {
		return arrayToString(list, ",");
	}

	public static void stringToArrayIntegerPositive(List<Integer> arr, String val, String selector) {
		if (val == null || val.isEmpty() || arr == null) return;
		arr.clear();
		val = val.trim();
		if (val.isEmpty()) return;
		if (val.charAt(0) == '[')
			val = val.substring(1);
		if (val.charAt(val.length() - 1) == ']')
			val = val.substring(0, val.length() - 1);
		val = val.trim();
		if (val.isEmpty()) return;
		String[] items = val.split(selector);
		int v;
		for (int i = 0; i < items.length; i++) {
			v = Post.getInt(items[i].trim(), 0);
			if (v > 0 && arr.indexOf(v) == -1)
				arr.add(v);
		}
	}
	
	public static ArrayList<Integer> stringToArrayIntegerPositive(String val, String selector) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		stringToArrayIntegerPositive(arr, val, selector);
		return arr;
	}
	
	public static int calculateDateDay(int val, int delta) throws DateTimeException {
		if (delta == 0)
			return val;
		Calendar cl = decodeDate(val);
		cl.add(Calendar.DAY_OF_MONTH, delta);
		return encodeDate(cl);
/*		
 		//Ошибка исправлена
 		 
		// Ошибка в реализации GregorianCalendar.add(Calendar.DAY_OF_MONTH, -1)
		// для даты 01.01.2016
		
		int valin = val;
		int d = val % 100;
		val = val / 100;
		int m = val % 100;
		val = val / 100;
		int y = val % 10000;
		Calendar cl = Calendar.getInstance();
		if (y<1000 || y>9999)
			throw new DateTimeException("Неверное значение года " + y + " в дате " + valin); 
		if (m<1 || m>12)
			throw new DateTimeException("Неверное значение месяца " + m + " в дате " + valin);
		cl.set(Calendar.YEAR, y);
		cl.set(Calendar.MONTH, m);
		int mm = cl.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (d<1 || d>mm)
			throw new DateTimeException("Неверное значение дня " + d + " в дате " + valin);			
		d = d + delta;
		if (delta > 0) {
			while (d > mm) {
				d = d - mm;
				m = m + 1;
				if (m > 12) {
					m = 1;
					y = y + 1;
				}
				cl.set(Calendar.YEAR, y);
				cl.set(Calendar.MONTH, m);				
				mm = cl.getActualMaximum(Calendar.DAY_OF_MONTH);
			}
		} else {
			while (d < 1) {
				m = m - 1;
				if (m < 1) {
					m = 12;
					y = y - 1;
				}
				cl.set(Calendar.YEAR, y);
				cl.set(Calendar.MONTH, m);						
				mm = cl.getActualMaximum(Calendar.DAY_OF_MONTH);
				d = d + mm;
			}
		}
		val = y * 10000 + m * 100 + d;
		// System.out.println(valin +" > "+ val);
		return val;
*/		
	}
	
	public static String killDublSpace(String val) {
		if (val == null)
			return null;
		// return val.replace("\u0020{2,}", "\u0020");
		return val.replaceAll("[ ]+", " ");
	}

	public static Calendar decodeDate(int date) throws DateTimeException {
		if (date==0)
			return null;
		int v = date / 100;
		int d = date - v * 100;
		int m = v % 100 - 1;
		int y = (v / 100) % 10000;
		if (y<1000 || y>9999)
			throw new DateTimeException("Неверное значение года " + y + " в дате " + date); 
		Calendar res = Calendar.getInstance();
		res.set(Calendar.YEAR, y);
		if (m<res.getActualMinimum(Calendar.MONTH) || m>res.getActualMaximum(Calendar.MONTH))
			throw new DateTimeException("Неверное значение месяца " + m + " в дате " + date);
		res.set(Calendar.MONTH, m);
		if (d<res.getActualMinimum(Calendar.DAY_OF_MONTH) || d>res.getActualMaximum(Calendar.DAY_OF_MONTH))
			throw new DateTimeException("Неверное значение дня " + d + " в дате " + date);
		res.set(Calendar.DAY_OF_MONTH, d);
		return res;
	}
	
	public static Calendar decodeDate(String date) throws DateTimeException {
		if (date==null || date.isEmpty())
			return null;
		return decodeDate(encodeDate(date));
	}	
	
	public static int encodeDate(int year, int month, int day) {
		return (year % 10000) * 10000 + (month % 100) * 100 + (day % 100);
	}

	public static int encodeDate(Calendar date) {
		return Post.encodeDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
	}

	public static int encodeDate() {
		return Post.encodeDate(Calendar.getInstance());
	}
	
	public static int encodeDate(String date) {
		if (date==null)
			return 0;
		int sz = date.length();
		if (sz>10) {
			date = date.substring(0, 10);
			sz = 10;
		}
		switch (sz) {
		case 6:
		case 7:
			return Post.encodeDate(2000+Integer.parseUnsignedInt(date.substring(4,6)), Integer.parseUnsignedInt(date.substring(2,4)), Integer.parseUnsignedInt(date.substring(0,2)));
		case 8:
		case 9:
			int m = Integer.parseUnsignedInt(date.substring(4,6));
			if (m>12) //ДДММГГГГ
				return Post.encodeDate(Integer.parseUnsignedInt(date.substring(4,8)), Integer.parseUnsignedInt(date.substring(2,4)), Integer.parseUnsignedInt(date.substring(0,2)));
			else //ГГГГММДД
				return Post.encodeDate(Integer.parseUnsignedInt(date.substring(0,4)), m, Integer.parseUnsignedInt(date.substring(6,8)));
		case 10:
			return Post.encodeDate(Integer.parseUnsignedInt(date.substring(6,10)), Integer.parseUnsignedInt(date.substring(3,5)), Integer.parseUnsignedInt(date.substring(0,2)));
		default: return 0; 
		}
	}
	
	public static int checkDate(int val) throws NumberFormatException {
		if (val <= 0)
			return 0;
		int y = val / 10000;
		int m = (val / 100) % 100;
		if ((y < 2000) || (y > 2999))
			throw new NumberFormatException("неверный год в \"" + val + "\"");
		y = y % 4;
		if ((m < 1) || (m > 12))
			throw new NumberFormatException("неверный месяц в \"" + val + "\"");
		int d = val % 100;
		int dm = 31;
		if ((m == 4) || (m == 6) || (m == 9) || (m == 11))
			dm = 30;
		else if ((m == 2) && (y == 0))
			dm = 29;
		else if (m == 2)
			dm = 28;
		if ((d < 1) || (d > dm))
			throw new NumberFormatException("неверный день в \"" + val + "\"");
		// throw new NumberFormatException("неверный день в \""+d+"-"+dm+"\"");
		return val;
	}
	
	public static Calendar getDateFromUnixTime(long date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		return cal;
	}
	
	public static String dateToStr(Calendar date) {
		if (date==null)
			date = Calendar.getInstance();
		DateFormat sdf = DateFormat.getDateTimeInstance();
	    sdf.setTimeZone(TimeZone.getDefault());     
	    return sdf.format(date.getTime());   
	}
	
	
	public static String ISO8601encode(Calendar date) {
		if (date==null)
			return null;
		return (new DateISO8601(date)).get();
		/*
		DateFormat sdf = new SimpleDateFormat(ISO8601_FORMAT_STR, Locale.getDefault());
	    sdf.setTimeZone(TimeZone.getDefault());     
	    return sdf.format(date.getTime());
	    */ 
	}

	public static String ISO8601encode() {
	    return ISO8601encode(Calendar.getInstance()); 
	}
	
	public static Calendar ISO8601decode(String value) {
		return (new DateISO8601(value)).date;
/*		
		Calendar cl = null;
		if (value==null)
			return cl;
		DateFormat sdf = new SimpleDateFormat(ISO8601_FORMAT_STR, Locale.getDefault());
		try {
			Date res = sdf.parse(value);
			cl = Calendar.getInstance();
			cl.setTime(res);
		} catch (Exception e) {
			throw new NumberFormatException("Неверный формат даты \"" + value + "\"");
		}	
		return cl;
		*/
	}

	public static int ISO8601decodeDate(String value) {
		if (value==null || value.isEmpty())
			return -1;
		return (new DateISO8601(value)).getDateInt(); 
	}	

	public static int ISO8601decodeTime(String value) {
		if (value==null || value.isEmpty())
			return -1;
		return (new DateISO8601('T'+value)).getTimeInt(); 
	}	
	
	public static String getDateShort(int val) {
		if (val > 0)
			return String.format("%02d.%02d.%04d", val % 100, (val / 100) % 100, val / 10000);
		return "";
	}
	
	public static String getTime(int val) {
		if (val > 0)
			return String.format("%02d:%02d:%02d",  val / 10000, (val / 100) % 100, val % 100);
		return "";
	}
	
	public static String getTimeHourMin(int val) {
		if (val > 0)
			return String.format("%02d:%02d",  val / 10000, (val / 100) % 100);
		return "";
	}
	
	public static String getDateTimeHourMin(long val) {
		if (val > 0) {
			int dt = (int)(val / 1000000);
			int tm = (int)(val % 1000000);
			if (tm>0)
				return String.format("%02d.%02d.%04d %02d:%02d", dt % 100, (dt / 100) % 100, dt / 10000, tm / 10000, (tm / 100) % 100);
			else
				return String.format("%02d.%02d.%04d", dt % 100, (dt / 100) % 100, dt / 10000);
		}	
		return "";
	}
	
	public static String getDateTimeToISO8601(long val) {
		if (val > 0) {
			int tm = (int)(val % 1000000);
			return String.format("%08dT%02d%02d%02d", val / 1000000, tm / 10000, (tm / 100) % 100, tm % 100);
		}	
		return "";
	}
	
	
	public static int getTime(String val) {
		if (val==null || val.isEmpty())
			return -1;
		try {
			Integer.parseInt(val);
			if ((val.length() % 2)==1) 
				val = '0' + val;
		} catch (Exception e) {
		}
		if (val.toLowerCase().indexOf('t')<0)
			val = 'T' + val;
		DateISO8601 d = new DateISO8601(val);
		if (d.isTime)
			return d.getTimeInt();
		else
			return -1;
	}
	
	@Deprecated
	public static String DateToStr(int val) {
		return getDateShort(val);
	}
	
	public static String getPeriodShort(int val) {
		if (val > 0)
			return String.format("%02d.%02d", val % 100, (val / 100) % 100);
		return "";
	}
	
	public static String getPeriodLong(int val) {
		if (val > 0)
			return String.format("%02d %s", val % 100, getMonthName((val / 100) % 100));
		return "";
	}	
	
	@Deprecated	
	public static String PeriodToStr(int val) {
		return getPeriodShort(val);
	}
	
	@Deprecated
	public static String PeriodToStrFull(int val) {
		return getPeriodLong(val);
	}

	public static String getMonthName(int val) {
		final String[] month = { "", "января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа",
				"сентября", "октября", "ноября", "декабря" };
		if ((val >= 1) && (val <= 12))
			return month[val];
		return "";
	}

	public static String[] createArrayStr(String... val) {
		String[] res = new String[val.length];
		for (int i = 0; i < val.length; i++)
			res[i] = val[i];
		return res;
	}
	
	@Deprecated
	public static String[] StrArrayCreate(String... val) {
		return createArrayStr(val);
	}

	public static int compareStr(Object v1, Object v2) {
		if ((v1 != null) && (v2 != null))
			return v1.toString().compareTo(v2.toString());
		else if ((v1 == null) && (v2 != null))
			return -1;
		else if ((v1 != null) && (v2 == null))
			return 1;
		else
			return 0;
	}

	public static int compareInt(Object v1, Object v2) {
		if ((v1 != null) && (v2 != null))
			return getInt(v1) - getInt(v2);
		else if ((v1 == null) && (v2 != null))
			return -1;
		else if ((v1 != null) && (v2 == null))
			return 1;
		else
			return 0;
	}
	
	public static long compareLong(Object v1, Object v2) {
		if ((v1 != null) && (v2 != null))
			return getLong(v1) - getLong(v2);
		else if ((v1 == null) && (v2 != null))
			return -1;
		else if ((v1 != null) && (v2 == null))
			return 1;
		else
			return 0;
	}	

	public static class sortID implements Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			if ((o1 != null) && (o2 != null))
				return compareInt(o1.get("id"), o2.get("id"));
			else if ((o1 == null) && (o2 != null))
				return -1;
			else if ((o1 != null) && (o2 == null))
				return 1;
			else
				return 0;
		}
	}
	
	public static class sortIDlong implements Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			if ((o1 != null) && (o2 != null)) {
				long c = compareLong(o1.get("id"), o2.get("id"));
				if (c==0) return 0;
				else if (c<0) return -1;
				else return 1;
			} else if ((o1 == null) && (o2 != null))
				return -1;
			else if ((o1 != null) && (o2 == null))
				return 1;
			else
				return 0;
		}
	}	

	public static class sortName implements Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			if ((o1 != null) && (o2 != null))
				return compareStr(o1.get("name"), o2.get("name"));
			else if ((o1 == null) && (o2 != null))
				return -1;
			else if ((o1 != null) && (o2 == null))
				return 1;
			else
				return 0;
		}
	}

	public static class sortIDstr implements Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			if ((o1 != null) && (o2 != null))
				return compareStr(o1.get("id"), o2.get("id"));
			else if ((o1 == null) && (o2 != null))
				return -1;
			else if ((o1 != null) && (o2 == null))
				return 1;
			else
				return 0;
		}
	}

	public static class sortSeq implements Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			if ((o1 != null) && (o2 != null))
				return compareInt(o1.get("seq"), o2.get("seq"));
			else if ((o1 == null) && (o2 != null))
				return -1;
			else if ((o1 != null) && (o2 == null))
				return 1;
			else
				return 0;
		}
	}
	
	public static class sortInteger implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o1 - o2;
		}
	}	

	// Заменяет символы в val значениями из mask, в которых не стоит selector
	public static String concatStrMask(String val, String mask, char selector) {
		if (val == null)
			return "";
		if (mask == null)
			return val;
		mask = mask.trim();
		int l = val.length();
		int m = mask.length();
		if (l == 0)
			return "";
		if (m == 0)
			return val;
		char[] buf;
		char c;		
		boolean isSelector = false;
		for (int i = 0; i < m; i++) if (mask.charAt(i) == selector) {
			isSelector = true;
			break;
		}
		if (!isSelector) return mask;
		
		if (l >= m) {
			//Источник и маска равны, или источник больше маски 
			buf = new char[l];
			val.getChars(0, l, buf, 0);
			for (int i = 0; i < m; i++) {
				c = mask.charAt(i);
				if (c != selector)
					buf[i] = c;
			}
		} else {
			//Источник меньше маски
			buf = new char[m];
			mask.getChars(0, m, buf, 0);
			for (int i = 0; i < l; i++) {
				if (buf[i] == selector)
					buf[i] = val.charAt(i);
			}			
		}	
		
		return new String(buf);
	}
	
	// Заменяет цифры в val цифрами из from в тех разрядах, в которых в mask стоит не ноль
	public static long concatIntMask(long val, long from, long mask) {
		long res = 0;
		while (from>0 && mask>0) {
			res = res * 10;
			if ((mask % 10)!=0) 
				res = res + from % 10;  
			else	
				res = res + val % 10;
			mask = mask / 10;
			from = from / 10;
			val = val / 10;
		}
		return res;
	}
	
	public static double concatNumMask(double val, double from, double mask) {
		long res = (long)Math.rint(val);
		val = val - res;
		return val + concatIntMask(res, (long)Math.rint(from), (long)Math.rint(mask));
	}
	
	// Заменяет цифры в val цифрами из from_mask в тех разрядах, в которых в from_mask стоит не ноль
	public static long concatIntMask(long val, long from_mask) {
		long res = 0;
		while (from_mask>0) {
			res = res * 10;
			if ((from_mask % 10)!=0) 
				res = res + from_mask % 10;  
			else	
				res = res + val % 10;
			from_mask = from_mask / 10;
			val = val / 10;
		}
		return res;
	}	
	
	public static double concatNumMask(double val, double from_mask) {
		long res = (long)Math.rint(val);
		val = val - res;
		return val + concatIntMask(res, (long)Math.rint(from_mask));
	}	
	
	public static double concatNumMaskDecimal(double val, double from_mask) {
		long res = (long)Math.rint(val);
		val = val - res;
//		String s = new String.valueOf(val);
		return val + concatIntMask(res, (long)Math.rint(from_mask));
	}	


	public static String concatArrayStrMask(List<String> vals, String val, long id, char selector) {
		if (val == null)
			val = new String();
		if (vals == null)
			return val;
		int p;
		long d;
		for (String item : vals)
			if ((item != null) && (!item.isEmpty())) {
				p = item.indexOf("-");
				if (p <= 0)
					continue;
				d = Post.getLong(item.substring(0, p), 0);
				if (d == id)
					val = Post.concatStrMask(val, item.substring(p + 1).trim(), selector);
			}
		return val;
	}

	public static int getRequestInt(HttpServletRequest request, String paramName) {
		String val = request.getParameter(paramName);
		if (val == null)
			return -2;
		if ((val.length() == 0) || (val.trim().isEmpty()))
			return -1;
		try {
			return Integer.parseInt(val.trim());
		} catch (NumberFormatException е) {
			return -3;
		}
	}

	public static int getRequestInt(HttpServletRequest request, String paramName, String paramDest, ErrorList error,
			int minVal, int maxVal, int emptyVal) {
		String val = request.getParameter(paramName);
		if (val == null) {
			// if ((error!=null)&&(minVal>0)) error.add("Не задано значение
			// "+paramDest+".");
			return -1;
		}
		if ((val.length() == 0) || (val.trim().isEmpty())) {
			if ((error != null) && (emptyVal == 0))
				error.add("Не задано значение " + paramDest + ".");
			return emptyVal;
		} else
			try {
				int res = Integer.parseInt(val.trim());
				if ((res == 0) && (minVal > 0)) {
					if (error != null)
						error.add("Не задано значение " + paramDest + ".");
					return emptyVal;
				}
				if ((maxVal > 0) && ((res < minVal) || (res > maxVal))) {
					if (error != null)
						error.add("Неверное значение " + paramDest + " в параметре \"" + paramName + "=" + res + "\".");
					return emptyVal;
				}
				return res;
			} catch (NumberFormatException е) {
				if (error != null)
					error.add("Неверно задано значение " + paramDest + " в параметре \"" + paramName + "="
							+ val.toString() + "\".");
				return emptyVal;
			}
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Object> getArray(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof ArrayList)
			return (ArrayList<Object>) obj;
		ArrayList<Object> res = new ArrayList<Object>();
		res.add(obj);
		return res;
	}
	
	@Deprecated
	public static ArrayList<Object> objectToArrayList(Object obj) {
		return getArray(obj);
	}
	
	public static ArrayList<Integer> createArrayInteger(Integer val) {
		if (val==null) 
			return null;
		ArrayList<Integer> res = new ArrayList<Integer>(1);
		res.add(val);
		return res;
	}
	
	public static ArrayList<Integer> createArrayInteger(List<?> vals) {
		if (vals==null) 
			return new ArrayList<Integer>();		
		ArrayList<Integer> res = new ArrayList<Integer>(vals.size());
		Integer v;
		for (Object item: vals) {
			v = getIntNull(item);
			if (v!=null) res.add(v);
		}
		return res;
	}	
	
    public static ArrayList<Integer> getArrayInteger(Object val) throws NullPointerException, NumberFormatException {
        if (val == null) return null;
        else if (val instanceof List) return createArrayInteger((List<?>)val);
        else if (val instanceof Number) return createArrayInteger(((Number)val).intValue());
        else if (val instanceof Boolean) return createArrayInteger(((Boolean) val ? 1 : 0));
        else if (val.toString().trim().isEmpty()) return null;
        else try {
        	return createArrayInteger(Integer.parseInt(val.toString()));
        } catch (NumberFormatException e) {
       		return null;
        }        
    }
    
    @SuppressWarnings("unchecked")
	public static ArrayList<Integer> getArrayIntegerInstance(Object val) {
    	if (val == null || !(val instanceof ArrayList)) return null;
    	ArrayList<?> res = (ArrayList<?>)val;
    	if (!res.isEmpty() && (res.get(0) instanceof Integer))
    		return (ArrayList<Integer>)res;
    	return new ArrayList<Integer>();
    }
    
	@Deprecated
	public static ArrayList<Integer> objectToArrayListInteger(Object obj) {
		return getArrayInteger(obj);
	}
	
	public static ArrayList<Long> createArrayLong(Long val) {
		if (val==null) 
			return null;		
		ArrayList<Long> res = new ArrayList<Long>(1);
		res.add(val);
		return res;
	}
	
	public static ArrayList<Long> createArrayLong(List<?> vals) {
		if (vals==null) 
			return new ArrayList<Long>();		
		ArrayList<Long> res = new ArrayList<Long>(vals.size());
		Long v;
		for (Object item: vals) {
			v = getLongNull(item);
			if (v!=null) res.add(v);
		}
		return res;
	}			
		
    public static ArrayList<Long> getArrayLong(Object val) throws NullPointerException, NumberFormatException {
        if (val == null) return null;
        else if (val instanceof List) return createArrayLong((List<?>)val);
        else if (val instanceof Number) return createArrayLong(((Number)val).longValue());
        else if (val instanceof Boolean) return createArrayLong(((Boolean) val ? (long)1 : (long)0));
        else if (val.toString().trim().isEmpty()) return null;
        else try {
        	return createArrayLong(Long.parseLong(val.toString()));
        } catch (NumberFormatException e) {
       		return null;
        }        
    }   
    
	@Deprecated
	public static ArrayList<Long> objectToArrayListLong(Object obj) {
		return getArrayLong(obj);
	}	

	public static ArrayList<String> createArrayString(String val) {
		if (val==null) 
			return null;		
		ArrayList<String> res = new ArrayList<String>(1);
		res.add(val);
		return res;
	}
	
	public static ArrayList<String> createArrayString(List<?> vals) {
		if (vals==null) 
			return null;		
		ArrayList<String> res = new ArrayList<String>(vals.size());
		String v;
		for (Object item: vals) {
			v = getStrNull(item);
			if (v!=null) res.add(v);
		}
		return res;
	}		
	
    public static ArrayList<String> getArrayString(Object val) throws NullPointerException, NumberFormatException {
        if (val == null) return null;
        else if (val instanceof List) return createArrayString((List<?>)val);
        else if (val instanceof Number) return createArrayString(((Number)val).toString());
        else if (val instanceof Boolean) return createArrayString(((Boolean) val ? "1" : ""));
        else if (val instanceof String) return createArrayString((String)val);
        else return createArrayString(val.toString());
    }   
	
	@Deprecated
	public static ArrayList<String> objectToArrayListString(Object obj) {
		return getArrayString(obj);
	}		

	@SuppressWarnings("unchecked")
	public static ArrayList<Map<String, Object>> getArrayMap(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof ArrayList)
			return (ArrayList<Map<String, Object>>) obj;
		if (obj instanceof Map) {
			ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
			res.add((Map<String, Object>) obj);
			return res;
		}
		return null;
	}
	
	@Deprecated
	public static ArrayList<Map<String, Object>> objectToArrayListMap(Object obj) {
		return getArrayMap(obj);
	}		
	
	public static boolean containsKey(Map<String, Object> data, String... keys) {
		if (data == null || keys == null)
			return false;
		for (String key : keys)
			if (data.containsKey(key))
				return true;
		return false;
	}

	public static boolean containsKey(Map<String, Object> data, List<String> keys) {
		if (data == null || keys == null)
			return false;
		for (String key : keys)
			if (data.containsKey(key))
				return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public static String toText(Map<String, Object> data, String selector, String mask, String prefix, boolean escapeHTML) {
		Object val;
		String res = new String();
		if (prefix!=null && !prefix.isEmpty())
			prefix = prefix + ".";
		else
			prefix = "";
		if (data != null)
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				if (entry.getValue() == null)
					continue;
				val = entry.getValue();
				if (val == null)
					continue;
				if (!res.isEmpty())
					res = res + selector;
				if (val instanceof Map)
					res = res + toText((Map<String, Object>)val, selector, mask, prefix+entry.getKey(), escapeHTML);
				else if (val instanceof ArrayList) {
					String ar = "";
					for (Map<String, Object> item: getArrayMap(val)) if (item!=null) {
						if (!ar.isEmpty())
							ar = ar + selector;
						ar = ar + toText(item, selector, mask, null, escapeHTML);
					}
					res = res + prefix+entry.getKey() + ": [" + ar + "]"; 
				} else if (!escapeHTML)
					res = res + String.format(mask, prefix+entry.getKey(), val.toString());
				else
					res = res + String.format(mask, prefix+entry.getKey(), escapeHTML(val.toString()));
			}
		if (!res.isEmpty())
			return res;
		else
			return null;
	}	
	
	public static String toText(Map<String, Object> data) {
		return toText(data, ", ", "%s: %s", null, false);
	}
	
	public static String toHTML(Map<String, Object> data) {
		return toText(data, ", ", "<i>%s</i>: <b>%s</b>", null, true);
	}	
	
	public static String toText(List<Map<String, Object>> data, String paramName) {
		String el;
		String res = new String();
		if (data != null)
			for (Map<String, Object> item : data) {
				el = toText(item);
				if (el == null)
					continue;
				res = res + "\n(" + el + "), ";
			}
		if (res.isEmpty())
			return null;
		else if (paramName == null)
			return res;
		else
			return paramName + ": " + res;
	}

	public static String toHTML(List<Map<String, Object>> data, String paramName) {
		String el;
		String res = new String();
		if (data != null)
			for (Map<String, Object> item : data) {
				el = toHTML(item);
				if (el == null)
					continue;
				res = res + "<li>" + el + "</li>";
			}
		if (res.isEmpty())
			return null;
		else if (paramName == null)
			return "<ol>" + res + "</ol>";
		else
			return "<p><b><i>" + paramName + "</i></b>: <ol>" + res + "</ol></p>";
	}

	public static String replaceAll(String data, String what, String when) {
		int p = 0;
		if (data == null || what==null || data.isEmpty())
			return data;
		if (when == null)
			return null;
		int sz = what.length();
		int sw = when.length();
		if (sz==0)
			return data;
		do {
			p = data.indexOf(what, p);
			if (p<0)
				break;
			data = data.substring(0,p) + when + data.substring(p+sz);
			p = p + sw;
		} while (true);
		return data;
	}
	
	public static String escapeHTML(String data) {	
		if (data==null || data.isEmpty())
			return data;
		data = replaceAll(data, "&", "&amp;");
		data = replaceAll(data, "\"", "&quot;");
		data = replaceAll(data, "<", "&lt;");
		data = replaceAll(data, ">", "&gt;");
		return data;
	}
	
	public static String escapeJSON(String data) {
		if (data==null || data.isEmpty())
			return data;
		data = replaceAll(data, "\\", "\\\\");
		data = replaceAll(data, "\"", "\\\"");
		data = replaceAll(data, "\n", "\\n");
		data = replaceAll(data, "\r", "\\r");
		return data;
	}
	
	public static String toJSON(String data) {
		if (data==null || data.isEmpty())
			return data;
		int l = data.length();
		char[] in = data.toCharArray();
		int outmax = l;
		char [] out = new char [outmax];
		char [] outnew;
		int n = -1;
		boolean slash;
		char ch;
		for (int i = 0; i < l; i++) {
			ch = in[i];
			slash = false;
			switch (ch) {
			case '\n':
				ch = 'n';
				slash = true;
				break;
			case '\r':
				ch = 'r';
				slash = true;
				break;
			case '\"':
				slash = (i==0 || in[i-1]!='\\' || (i>1 && in[i-2]=='\\'));
				break;
			case '\\':				
				slash = (i==0 || in[i-1]!='\\') && ((i+1)>=l || (in[i+1]!='\\' && in[i+1]!='\"' && in[i+1]!='n' && in[i+1]!='r')) ;
				break;
			}
			if (slash) 
				n++;
			n++;
			if (n>=outmax) {
				outnew = new char [outmax+20];
				for (int j=0; j<outmax; j++) outnew[j] = out[j]; 
				outmax = outmax+20;
				out = outnew;
			}
			if (slash) 
				out[n-1] = '\\';  
			out[n] = ch;			
		}
		return new String(out, 0, n+1);
	}
	
	@SuppressWarnings("unchecked")
	public static String toJSON(Object data, boolean isPretty) {
		String res;
		String res1;
		Object vals;
		boolean iCR;
		if (data == null)
			return "null";
		else if (data instanceof String) 
			return "\"" + toJSON(data.toString()) + "\"";
		else if (data instanceof Number)
			return data.toString();
		else if (data instanceof Boolean)
			return ((Boolean) data ? "true" : "false");
		else if (data instanceof CalculateValue) {
			return ((CalculateValue) data).getJSON();
		} else if (data instanceof PostDictionary) {
			return toJSON(((PostDictionary) data).getBySeq(), isPretty);
		} else if (data instanceof Map) {
			Map<String, Object> mp = (Map<String, Object>) data;
			res = new String();
			boolean isCR = false;
			for (Map.Entry<String, Object> entry : mp.entrySet()) {
				vals = entry.getValue();
				if (vals == null)
					continue;
				res1 = toJSON(vals, isPretty);
				if (res1 == null)
					continue;
				iCR = (isPretty) && ((res1.indexOf('{') >= 0)||(res1.length()>80));
				if (iCR)
					isCR = true;
				if (!res.isEmpty())
					res = res + "," + (isCR ? "\n" : (isPretty ? " " : ""));
				if (!iCR)
					isCR = false;
				res = res + "\"" + entry.getKey() + "\":" + (isPretty ? " " : "") + res1;
			}
			if (!res.isEmpty())
				return "{" + res + "}";
			else
				return null;
		} else if (data instanceof ArrayList) {
			ArrayList<Object> arr = getArray(data);
			res = new String();
			for (Object arri : arr) {
				if (arri == null)
					continue;
				res1 = toJSON(arri, isPretty);
				if (res1 == null)
					continue;
				iCR = (isPretty) && (res1.indexOf('{') >= 0);
				if (!res.isEmpty())
					res = res + "," + (iCR ? "\n" : "");
				else
					res = "[" + (iCR ? "\n" : "");
				res = res + res1;
			}
			if (res.isEmpty())
				return null;
			else if ((isPretty) && (res.indexOf('{') >= 0))
				return res + "\n]";
			else
				return res + "]";
		} else
			return "\"" + data.toString() + "\"";
	}

	public static String toJSON(String paramName, Object data, boolean isPretty) {
		String res = toJSON(data, isPretty);
		if ((res == null) || (res.isEmpty()))
			return null;
		else
			return "\"" + paramName + "\":" + (isPretty?" ":"") + res;
	}

	public static String formatSuffixByNumeric(long val, String one, String little, String many) {
		val = Math.abs(val);
		int v = (int) val % 10;
		if (val <= 10 || val > 19)
			switch (v) {
			case 1:
				return one;
			case 2:
			case 3:
			case 4:
				return little;
			default:
				return many;
			}
		else
			return many;
	}

	public static String formatSuffixByNumeric(long val, List<String> units) {
		if (units == null)
			return new String();
		switch (units.size()) {
		case 0:
			return new String();
		case 1:
			return formatSuffixByNumeric(val, units.get(0), units.get(0), units.get(0));
		case 2:
			return formatSuffixByNumeric(val, units.get(0), units.get(1), units.get(0));
		default:
			return formatSuffixByNumeric(val, units.get(0), units.get(1), units.get(2));
		}
	}

	public static String formatSuffixByNumeric(long val, Object units) {
		return formatSuffixByNumeric(val, getArrayString(units));
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapStr(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Map)
			return (Map<String, Object>) obj;
		return null;
	}	
	
	//Добавление в массив list
	public static Map<String, Object> addToArrayMap(List<Map<String, Object>> list, int id, String name) {
		Map<String, Object> item = new LinkedHashMap<String, Object>();
		item.put("id", id);
		item.put("name", name);
		if (list!=null) list.add(item);
		return item;
	}
	
	//Поиск в массиве list по id
	public static Map<String, Object> getFromArrayMapByID(List<Map<String, Object>> list, int id) {
		if (list==null) return null;
		for (Map<String, Object> item: list) 
			if (item!=null && Post.getInt(item.get("id")) == id) return item;
		return null;
	}

	public static Map<String, Object> getFromArrayMapByID(List<Map<String, Object>> list, String id) {
		if (list==null || id == null || id.isEmpty()) 
			return null;
		for (Map<String, Object> item: list) 
			if (item!=null && Post.getStrEmpty(item.get("id")).equals(id)) 
				return item;
		return null;
	}	
	
	//Возвращает true, если хотя бы одно значение из arr1 есть в arr2
	public static boolean findAnyArrayInteger(List<Integer> arr1, List<Integer> arr2) {
		if (arr1==null || arr1.isEmpty() || arr2==null || arr2.isEmpty()) 
			return false;
		for (Integer item: arr1)  
			if (item!=null && arr2.indexOf(item)>=0) 
				return true;
		return false;
	}
	
	//Возвращает true, если все значения из arr1 есть в arr2
	public static boolean findStrictArrayInteger(List<Integer> arr1, List<Integer> arr2) {
		if (arr1==null || arr1.isEmpty() || arr2==null || arr2.isEmpty()) 
			return false;
		for (Integer item: arr1)  
			if (item!=null && arr2.indexOf(item)<0) 
				return false;
		return true;
	}		
	
	
	//Добавление в массив list уникального id
	public static Map<String, Object> addToArrayMapUnique(List<Map<String, Object>> list, int id, String name) {
		Map<String, Object> item = getFromArrayMapByID(list, id);
		if (item !=null ) return item;
		return addToArrayMap(list, id, name);
	}
	
	public static String encodeStr(String val, Charset toCharset) {
		if (val==null || val.isEmpty() || toCharset==null)
			return val;
		return new String(val.getBytes(toCharset));
	}

	public static String encodeStr(Object val, Charset toCharset) {
		return encodeStr(getStr(val), toCharset);
	}

	public static String encodeStr(String val, String toCharset) {
		return encodeStr(val, Charset.forName(toCharset));
	}

	public static String encodeStr(Object val, String toCharset) {
		return encodeStr(getStr(val), Charset.forName(toCharset));
	}
	
	public static String decodeStr(String val, Charset fromCharset) {
		if (val==null || val.isEmpty() || fromCharset==null)
			return val;
		return new String(val.getBytes(), fromCharset);
	}
	
	public static String decodeStr(Object val, Charset fromCharset) {
		return decodeStr(getStr(val), fromCharset);
	}	

	public static String decodeStr(String val, String fromCharset) {
		return decodeStr(val, Charset.forName(fromCharset));
	}	

	public static String decodeStr(Object val, String fromCharset) {
		return decodeStr(getStr(val), Charset.forName(fromCharset));
	}	
	
	public static String perecodeStr(String val, Charset fromCharset, Charset toCharset) {
		if (val==null || val.isEmpty() || (toCharset==null && fromCharset==null))
			return val;
		return new String(val.getBytes(toCharset), fromCharset);
	}
		
	public static String perecodeStr(Object val, Charset fromCharset, Charset toCharset) {
		return perecodeStr(getStr(val), fromCharset, toCharset);
	}
			
	public static String perecodeStr(String val, String fromCharset, String toCharset) {
		return perecodeStr(val, Charset.forName(fromCharset), Charset.forName(toCharset));
	}

	public static String perecodeStr(Object val, String fromCharset, String toCharset) {
		return perecodeStr(getStr(val), Charset.forName(fromCharset), Charset.forName(toCharset));
	}
	
	static class Finder extends SimpleFileVisitor<Path> {
		
		private final PathMatcher matcher;
		private final Path exceptFile;
		
		Finder(String pattern, String ExceptFile) {
		    matcher = FileSystems.getDefault()
		            .getPathMatcher("glob:" + pattern);
		    exceptFile = Paths.get(ExceptFile);	    
		}
		
		// Compares the glob pattern against
		// the file or directory name.
		void Find(Path file) {
		    Path name = file.getFileName();
		    if (name != null && matcher.matches(name) && !name.equals(exceptFile)) {
		    	java.io.File curFile = new java.io.File(file.toString());
				if (curFile.exists() && curFile.isFile()) {
					curFile.delete();
				}
		    }
		}
		
		// Invoke the pattern matching
		// method on each file.
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			Find(file);
		    return CONTINUE;
		}
		
		// Invoke the pattern matching
		// method on each directory.
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		    return CONTINUE;
		}
		
		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
		    System.err.println(exc);
		    return CONTINUE;
		}
		
	}	
	
	public static void DeleteFiles(String path, String pattern, String ExceptFile) throws IOException {
		Path dir = Paths.get(path);
		Finder finder = new Finder(pattern, ExceptFile); 
		Files.walkFileTree(dir, finder);
	}	
	
	//Возвращает true, если символ ch входит в интервалы intervals
	//В intervals на четных позициях присутствует символ начала интервала, затем символ конца интервала (может отсутсвовать)
	//Например, intervals="09" обозначает интервал от 0 до 9, intervals="09AZ" обозначает два интервала от 0 до 9 и от A до Z, intervals="0" обозначает интервал от 0 до последнего символа
	public static boolean charInInterval(char ch, String intervals) {
		if (intervals==null || intervals.isEmpty())
			return false;
		int l = intervals.length();
		char[] in = new char[l];
		intervals.getChars(0, l, in, 0);
		for (int i = 0; i < l; i=i+2) 
			if (ch>=in[i] && ((i+1)==l || ch<=in[i+1]))
				return true;
		return false;
	}
	
	//Возвращает true, если все символы val входят в интервалы intervals (см. описание charInInterval)
	public static boolean charsInInterval(String val, String intervals) {
		if (intervals==null || intervals.isEmpty() || val==null)
			return false;
		int l = val.length();
		char[] in = new char[l];
		val.getChars(0, l, in, 0);
		for (int i = 0; i < l; i++) 
			if (!charInInterval(in[i], intervals))
				return false;
		return true;
	}
	
	//Удаляет из where все символы, которые есть kill (если kill!=null) 
	//или если символ отсутствует в present (если present!=null)
	//или если символ есть в интервале intervals (если intervals!=null, описание формата intervals указано в описании charInInterval)
	//или если символ меньше min
	public static String killChars(String where, String kill, String present, String intervals, char min) {
		if (where==null)
			return null;
		int l = where.length();
		char[] in = new char[l];
		where.getChars(0, l, in, 0);
		int res = 0;
		char ch;
		for (int i = 0; i < l; i++) {
			ch = in[i];
			if (ch>=min 
			&& (kill==null || kill.indexOf(ch)<0) 
			&& (present==null || present.indexOf(ch)>=0) 
			&& (intervals==null || charInInterval(ch, intervals)))
			{
				in[res] = ch;
				res++;
			}
		}
		if (res<l)
			return new String(in, 0, res);
		else
			return where;
	}
	
	//Заменяет в строке where подстроку what на подсроку than 
	public static String replaceString(String where, String what, String than) {
		if (where==null || where.isEmpty() || what==null || what.isEmpty())
			return where;

		int l = where.length();
		char[] in = where.toCharArray();
		
		ArrayList<Character> out = new ArrayList<Character>(l); 

		int lw = what.length();		
		char[] ww = what.toCharArray();

		int lc = 0;
		if (than!=null)
			lc = than.length();
		char[] ch = null;
		if (lc>0) 
			ch = than.toCharArray();
		boolean find;
		for (int i = 0; i < l; i++) {
			find = (i<=(l-lw));
			if (find) for (int j=0; j<lw; j++) 
				if (ww[j]!=in[i+j]) {
					find = false;
					break;
				}
			if (!find) out.add(in[i]);
			else {
				i=i+lw-1;
				if (lc>0) 
					for (int j = 0; j < lc; j++) out.add(ch[j]);
			}
		}
		l = out.size();
		char [] oc = new char [l]; 
		for (int i = 0; i < l; i++) oc[i] = out.get(i); 
		return new String(oc);
	}
	

	public static double roundUp(double value, int places) {
		return new BigDecimal(value).setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
	}	
	
	public static long trunc(double value) {
		return new BigDecimal(value).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
	}	
	
	public static String htmlSplash(String value) {
		if (value==null || value.isEmpty())
			return value;
//		return StringEscapeUtils.escapeHtml(value);
		return killChars(replaceString(replaceString(replaceString(value, "<", "&lt;"), ">", "&gt;"), "\\", "/"), null, null, null, ' ');
	}
	
    static public String outFormat(Map<String, Object> data, FormatOutMode mode, String caption) {
    	if (mode==null)
    		mode = FormatOutMode.TEXT;
    	switch (mode) {
    	case TEXT:
    		return toText(data);
    	case HTML:
    		return (caption!=null?"<p>"+caption:"")+"<ul>"+toText(data, "\n","<li><i>%s</i>: <b>%s</b></li>", null, true)+"</ul>"+(caption!=null?"</p>":"");
    	case HTMLfull:
    		return "<html><head>"+(caption!=null?"<title>"+caption+"</title>":"")+"<meta charset=\"utf-8\"></head><body>"+outFormat(data, FormatOutMode.HTML, null)+"</body></html>";
    	case JSON:
    	case JSONtext:
    		if (caption!=null && !caption.isEmpty() && data.get("caption")==null)
    			data.put("caption", caption);
    		return toJSON(data, mode==FormatOutMode.JSONtext);
   	 	default:
   	 		return toText(data);
   	 	}
    }
 
	static public String encodeStringRUS(String inValue) throws CalculateException {
		if (inValue==null || inValue.isEmpty()
				//|| Post.charsInInterval(inValue, "09az--")
			) 
			return inValue;
		Locale lc = new Locale("ru", "RU");
		try {
			inValue = inValue.toLowerCase(lc);
			inValue = Post.killChars(inValue, null, null, "09azая--", ' ');
			inValue = URLEncoder.encode(inValue,"UTF-8").toLowerCase(lc);
			inValue = inValue.replace("%", "");
			return inValue;
		} catch (Exception e) {
			throw new CalculateException("Ошибка при указании значения строки \""+inValue+"\" ", 1340);
		}
	}
	
	static public String doubleToStr(double val) {
		long v = (long) Math.floor(val);
		if ((val-v)==0)
			return String.valueOf(v);
		else
			return String.valueOf(val);
	}
	
	static public double doubleNormalize(double val) {
		String v = String.valueOf(val);
		int p = v.indexOf('.');
		if (p<0)
			return val;
		int l = v.length();
		if ((l-p)<12)
			return val;
		char m = v.charAt(l-2);
		if (m!='9')
			return val;
		v = "%0" + String.valueOf(l-p-3) + "d";
		v = "0." + String.format(v, 0) + "1";
		val = val + Double.parseDouble(v);
		return val;
		
	}
	
	// Поиск максимальной даты из дат, меньше или равной требуемой
	static public Map<String, Object> getItemFromArrayByInt(int findVal, ArrayList<Map<String, Object>> source, String paramName) {
		if (source == null)
			return null;
		Map<String, Object> find = null;
		int v;
		int f = 0;
		for (Map<String, Object> item : source)
			if (item != null) {
				v = Post.getInt(item.get(paramName), 0);
				if (v == 0)
					continue;
				if ((v <= findVal) && (v > f)) {
					f = v;
					find = item;
				}
			}
		return find;
	}
	
	static public Map<String, Object> getItemArrayFromDate(int findDate, ArrayList<Map<String, Object>> vals) {
		return getItemFromArrayByInt(findDate, vals, "date");
	}
	
	
	static public UUID decodeUUID(String value) {
		if (value==null || value.isEmpty())
			return null;
		//BD85DCE8-DE61-489B-98CB-E818FB04F2D6		
		int l = value.length();		
		char[] v = value.toCharArray();
		int n = 2;
		int k = 0;
		long[] d = new long[2];
		char[] c = new char[18];
		c[0]='0';
		c[1]='x';
		for (int i = 0; i < l; i++) {
			if ((v[i]>='0' && v[i]<='F')||(v[i]>='a' && v[i]<='f')) {
				c[n] = v[i]; 
				n++;
				if (n==18) {
					d[k] = Long.decode(new String(c)).longValue();
					n = 2;
					k++;
					if (k==2)
						return new UUID(d[0], d[1]);
				}
			}
		}
		throw new IllegalArgumentException("Invalid UUID string: \""+value+"\"");
    }
	
	static public String encodeUUID(UUID value) {
		if (value==null || value.getMostSignificantBits()==0)
			return null;
		return String.format("%X%X", value.getMostSignificantBits(), value.getLeastSignificantBits()); 
	}
	
}