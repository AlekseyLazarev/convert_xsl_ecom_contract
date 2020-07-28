package caits.utils;

import caits.utils.Post.DataType;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/*
 * Переменная
 */
public class CalculateValue {

	// Интерфейс работы с переменными
	public interface Operation {
		// Получение переменной по её имени
		public CalculateValue getCalculateValue(String name, boolean optional) throws CalculateException;
		// Установка переменной по её имени
		public boolean setCalculateValue(String name, Object value) throws CalculateException;
	}
	
	//Фабрика переменной в зависимости от требуемого типа
	static public CalculateValue createValue(Post.DataType type, int date, PostOfficeProperty.PostOfficeLoaded loadPO, PostDictionary list) {
		switch (type) {
		case INT: 
			return new CalculateInt(Post.DataType.INT);
		case STR: 
			return new CalculateStr();
		case NUM: 
			return new CalculateNum();
		case DATE: 
			return new CalculateDate();
		case BOOL: 
			return new CalculateBool();
		case TIME: 
			return new CalculateTime();
		case SUM: 
			return new CalculateSum();
		case DAY: 
			return new CalculateDay();
		case MONTH: 
			return new CalculateMonth();
		case WEIGHT: 
			return new CalculateWeight();
		case SIZE: 
			return new CalculateSize();
		case POST: 
			return new CalculatePost(loadPO, date);
		case LIST: 
			return new CalculateList(list);
		case SET: 
			return new CalculateSet(list);
		case BIT: 
			return new CalculateBit();
		case ARRAY: 
			return new CalculateArray();
		case GUID: 
			return new CalculateGUID();
		default:	
			return new CalculateValue(Post.DataType.NONE);
		}
	}
	
	static public CalculateValue valueOf(Post.DataType type) {
		return createValue(type, 0, null, null);
	}

	static public CalculateValue valueOf(Object val) {
		if (val == null) 
			return null;
		if (val instanceof CalculateValue) 
			switch (((CalculateValue)val).type) {
			case INT: 
				return new CalculateInt((CalculateInt)val);
			case STR: 
				return new CalculateStr((CalculateStr)val);
			case NUM: 
				return new CalculateNum((CalculateNum)val);
			case DATE: 
				return new CalculateDate((CalculateDate)val);
			case BOOL: 
				return new CalculateBool((CalculateBool)val);
			case TIME: 
				return new CalculateTime((CalculateTime)val);
			case SUM: 
				return new CalculateSum((CalculateSum)val);
			case DAY: 
				return new CalculateDay((CalculateDay)val);
			case MONTH: 
				return new CalculateMonth((CalculateMonth)val);
			case WEIGHT: 
				return new CalculateWeight((CalculateWeight)val);
			case SIZE: 
				return new CalculateSize((CalculateSize)val);
			case POST: 
				return new CalculatePost((CalculatePost)val);
			case LIST: 
				return new CalculateList((CalculateList)val);
			case SET: 
				return new CalculateSet((CalculateSet)val);
			case BIT: 
				return new CalculateBit((CalculateBit)val);
			case ARRAY: 
				return new CalculateArray((CalculateArray)val);
			default:	
				return new CalculateValue((CalculateValue)val);
			}
		if (val instanceof Long) 
			return valueOf((Long)val);
		if (val instanceof Integer) 
			return valueOf((Integer)val);
		if (val instanceof String) 
			return valueOf((String)val);		
		if (val instanceof Boolean) 
			return valueOf((Boolean)val);
		if (val instanceof Number) 
			return valueOf((Number)val);
		if (val instanceof ArrayList) 
			return valueOf((ArrayList<?>)val);
		return null;
	}
	
	static public CalculateValue valueOf(double val) {
		CalculateValue res = new CalculateNum();
		res.set(null, val);
		return res;
	}

	static public CalculateValue valueOf(long val) {
		CalculateValue res = new CalculateInt(Post.DataType.INT);
		res.set(null, val);
		return res;
	}
	
	static public CalculateValue valueOf(int val) {
		CalculateValue res = new CalculateInt(Post.DataType.INT);
		res.set(null, val);
		return res;
	}
	
	static public CalculateValue valueOf(Long val) {
		CalculateValue res = new CalculateInt(Post.DataType.INT);
		res.set(null, val);
		return res;
	}
	
	static public CalculateValue valueOf(Integer val) {
		CalculateValue res = new CalculateInt(Post.DataType.INT);
		res.set(null, val);
		return res;
	}		
	
	static public CalculateValue valueOf(Number val) {
		CalculateValue res = new CalculateNum();
		res.set(null, val);
		return res;
	}			

	static public CalculateValue valueOf(String val) {
		CalculateValue res = new CalculateStr();
		res.set(null, val);
		return res;
	}	
	
	static public CalculateValue valueOf(Boolean val) {
		CalculateValue res = new CalculateBool();
		res.set(null, val);
		return res;
	}
	
	static public CalculateValue valueOf(boolean val) {
		CalculateValue res = new CalculateBool();
		res.set(null, val);
		return res;
	}	
	
	static public CalculateValue valueOf(ArrayList<?> val) {
		CalculateValue res = new CalculateArray();
		res.set(null, val);
		return res;
	}	
	
	static public CalculateValue valueOf(PostOfficeProperty val, int date, PostOfficeProperty.PostOfficeLoaded loadPO) {
		CalculateValue res = new CalculatePost(loadPO, date);
		res.set(null, val);
		return res;
	}	

	//Тип переменной
	final public Post.DataType type;
	//Количесво использования переменной, управляется внешним кодом
	private int cntRead;
	private int cntWrite;
	public static final char selectorValue = '\'';
	public static final char selectorFormat = ':';
	
	protected CalculateValue(Post.DataType type) {
		super();
		this.type = type;
		this.cntRead = 0;
		this.cntWrite = 0;
		clear(null);
	}
	
	protected CalculateValue(CalculateValue source) {
		this(source.type);
		copy(null, source);
	}	
	
	//Очистка счетчика чтений
	public void clearRead() {
		cntRead = 0;
	}
	
	//Увеличение счетчика чтений
	public void incRead() {
		cntRead++;
	}	
	
	//Получение счетчика чтений
	public int getRead() {
		return cntRead;
	}	
	
	//Очистка счетчика записи
	public void clearWrite() {
		cntWrite = 0;
	}
	
	//Увеличение счетчика записи
	public void incWrite() {
		cntWrite++;
	}	
	
	//Получение счетчика записи
	public int getWrite() {
		return cntWrite;
	}	
	
	//Получение объекта значения
	public Object getVal(String subname) {
		return this;
	}
	
	//Получение дробного значения
	public double getNum(String subname) {
		return 0;
	}

	//Получение целого значения
	public long getInt(String subname) {
		return 0;
	}
	
	public int intValue() {
		return (int)getInt(null);
	}
	
	public long longValue() {
		return getInt(null);
	}	

	public double doubleValue() {
		return getNum(null);
	}	
	
	//Округление
	public void round(int newScale, int roundingMode) {
		long res = new BigDecimal(getInt(null)).setScale(newScale, roundingMode).longValue();
		set(null, res);
 	}
	
	//Получение строки
	public String getStr(String subname) {
		return new String();
	}
	
	//Получение списка возможных встроенных переменных (м.б. не полным)
	public ArrayList<String> getSubnames() {
		return new ArrayList<String>();
	}	
	
	//Получение списка значений
	public void getValues(Map<String, Object> dest) {
		for (String nm: getSubnames()) dest.put(nm, getVal(nm));
	}

	public Map<String, Object> getValues() {
		Map<String, Object> res = new HashMap<String, Object>();
		getValues(res);
		return res;
	}
	
	//Получение отображаемого значения
	@Override
	public String toString() {
		return getStr(null);
	}
	
	//Получение значения для JSON
	public String getJSON() {
		return getStr(null);
	}	
	
	//Получение единицы измерения
	public static String getDisplayUnit(String units) {
		if (units == null) return new String();
		else if (!units.isEmpty()) return " " + units;
		else return units;
	}
	
	//Получение единицы измерения
	public static String getDisplayUnit(ArrayList<String> units, Long val) {
		if (units == null || units.isEmpty()) return new String();
		else if (val==null) return getDisplayUnit(units.get(0));
		else return getDisplayUnit(Post.formatSuffixByNumeric(val, units));
	}	
	
	//Получение отображаемого значения с единицей измерения
	public String toString(ArrayList<String> units) {
		return toString() + getDisplayUnit(units, null);
	}	
	
	//Получение признака "пусто"
	public boolean isEmpty(String subname) {
		return true;
	}			
	
	public boolean isEmpty() {
		return isEmpty(null);
	}			
	
	//Установка признака "пусто"
	public void clear(String subname) {
		
	}
	
	//Установка значения
	public void set(String subname, Object value) {
		if (value == null) clear(subname);
		else if (value instanceof CalculateValue) {
			copy(subname, (CalculateValue) value);
			format(subname);
		} else if (value instanceof Number) {
			copy(subname, (Number) value);
			format(subname);
		} else if (value instanceof Boolean) {
			if (!(Boolean)value) 
				clear(subname);
			else {
				copy(subname, new Integer(1));
				format(subname);
			}	
		} else if (value.toString().trim().isEmpty()) clear(subname);
		else {
			copy(subname, value.toString());
			format(subname);
		}
	}
	
	//Установка строкового значения
	public void set(String subname, String value) {
		if (value == null) 
			clear(subname);
		else {
			copy(subname, value);
			format(subname);
		}	
	}	
	
	//Установка числового значения
	public void set(String subname, Number value) {
		if (value == null) 
			clear(subname);
		else {
			copy(subname, value);
			format(subname);
		}	
	}	
	
	//Установка значения из другой переменой
	public void set(String subname, CalculateValue value) {
		if (value == null) 
			clear(subname);
		else { 
			copy(subname, value);
			format(subname);
		}	
	}		

	//Проверка на равенство значения
	public boolean equals(String subname, CalculateValue value) {
		if (value==this)
			return true;
		else
			return false;
	}
	
	public boolean equals(CalculateValue value) {
		return equals(null, value); 
	}	
	
	//Сравнение значения с переменной
	public int compare(String subnameLeft, CalculateValue valueRight, String subnameRight) {
		if (valueRight==this)
			return 0;
		if (valueRight==null)
			return 1;
		String v1 = getStr(subnameLeft);
		String v2 = valueRight.getStr(subnameRight);
		if (v1==null && v2==null) return 0;
		else if (v1==null && v2!=null) return -1;
		else if (v1!=null && v2==null) return 1;
		return v1.compareTo(v2);
	}
	
	public int compare(CalculateValue valueRight) {
		return compare(null, valueRight, null); 
	}
	
	public int compare(long valueRight) {
		CalculateValue v = new CalculateValue(DataType.INT);
		v.set(null, valueRight);
		return compare(null, v, null); 
	}	
	
	public int compare(double valueRight) {
		CalculateValue v = new CalculateValue(DataType.NUM);
		v.set(null, valueRight);
		return compare(null, v, null); 
	}		
	
	//Суммирование this=this+value
	public void sum(String subname, CalculateValue value) {
		if (value!=null)
			set(subname, getInt(subname) + value.getInt(subname));
	}
	
	//Вычитание this=this-value
	public void sub(String subname, CalculateValue value) {
		if (value!=null)
			set(subname, getInt(subname) - value.getInt(subname));
	}
	
	//Умножение this=this*value
	public void multi(String subname, CalculateValue value) {
		if (value!=null)
			set(subname, getInt(subname) * value.getInt(subname));
	}

	//Деление this=this/value
	public void divide(String subname, CalculateValue value) {
		if (value==null)
			return;
		long res = getInt(subname) / value.getInt(subname);
		set(subname, res);
	}	
	
	//Проверка нахождения значения в диапазоне
	public boolean checkMinMax(String subname, long min, long max) {
		return true;
	}
	
	//Проверка значения на минимум
	public boolean checkMin(String subname, long min) {
		return true;
	}		

	//Проверка значения на максимум
	public boolean checkMax(String subname, long max) {
		return true;
	}		
	
	
	protected void copy(String subname, CalculateValue value) {
		
	}
	
	protected void copy(String subname, Number value) {
		
	}	
	
	protected void copy(String subname, String value) {
		
	}	
	
	protected void format(String subname) {
		
	}
	
	static public String getName(String name) {
		if (name==null || name.isEmpty())
			return name;
		int p = name.indexOf(".");
		if (p<0)
			return name;
		return name.substring(0, p).trim();
	}

	static public String getSubName(String name) {
		if (name==null || name.isEmpty())
			return null;
		int p = name.indexOf(".");
		if (p<0)
			return null;
		return name.substring(p+1).trim();
		
	}
	
	static public String getSubName(Object name) {
		if (name==null || !(name instanceof String))
			return null;
		return getSubName((String)name); 
	}		
	
	//Получение форматированного текста значения переменной
	public String formatValue(String format, String subname) {
		if (format==null) 
			return getStr(subname);
		format = format.trim().toLowerCase();
		if (format.isEmpty()) 
			return getStr(subname);
		if (format.equals("num")) 
			return String.valueOf(Post.doubleToIntegerNoDecimal(getNum(subname)));
		if (format.equals("int")) 
			return String.valueOf(getInt(subname));
		if (format.equals("str")) 
			return String.valueOf(getStr(subname));
		if (format.equals("currency")) 
			return Post.getCurrecyShort(getInt(subname));
		if (format.equals("date")) 
			return Post.getDateShort((int)getInt(subname));
		return getStr(subname);
	}	
	
	//Получение значения по имени (в имени литерал обрабатывается) посредсвом variables 
	static public Object getValue(Operation variables, String name, boolean isRound, boolean optional) throws CalculateException {
		if (name == null || name.isEmpty())
			return null;
		if (name.indexOf('\'')==0) 
			return name.substring(1, name.length()-1);
		
		CalculateValue vv = variables.getCalculateValue(name, optional);
		if (vv==null) 
			return null;
		String sn = getSubName(name);
		if (sn==null)
			return vv;
		Object res = vv.getVal(sn);
		if (res==null) 
			return null;
		if (isRound && (res instanceof Double))
			return ((Double)res).longValue();
		else 
			return res;
	}
	
	// Получение значения из литерала или из переменной посредсвом variables
	static public Object getValue(Operation variables, Object val, boolean optional) throws CalculateException {
		if (val == null)
			return null;
		if (val instanceof String) 
			return getValue(variables, (String)val, false, optional);
		if (val instanceof Integer) 
			return (Integer)val;
		if (val instanceof Long)
			return (Long)val;
		if (val instanceof Number) 
			return (Number)val;
		if (val instanceof Boolean) 
			return ((Boolean) val ? 1 : 0);
		return null;
	}	
	
	// Получение значения типа CalculateValue из литерала или из переменной посредсвом variables
	static public CalculateValue getCalculateValue(Operation variables, Object val, boolean optional) throws CalculateException {
		Object v = getValue(variables, val, optional);
		if (v==null)
			return null;
		else if (v instanceof CalculateValue)
			return (CalculateValue)v;
		else
			return CalculateValue.valueOf(v);
	}
	
	// Получение значения типа Long из литерала или из переменной посредсвом variables
	static public Long getValueInt(Operation variables, Object val, boolean optional) throws CalculateException {
		Object v = getValue(variables, val, optional);
		if (v==null)
			return null;
		if (v instanceof CalculateValue) 
			return ((CalculateValue)v).getInt(CalculateValue.getSubName(val));
		if (v instanceof Long)
			return (Long)v;
		if (v instanceof Number)
			return ((Number)v).longValue();
		return null;
	}	
	
	// Получение значения типа Double из литерала или из переменной посредством variables
	static public Double getValueNum(Operation variables, Object val, boolean optional) throws CalculateException {
		Object v = getValue(variables, val, optional);
		if (v==null)
			return null;
		if (v instanceof CalculateValue) 
			return ((CalculateValue)v).getNum(CalculateValue.getSubName(val));
		if (v instanceof Double)
			return (Double)v;
		if (v instanceof Number)
			return ((Number)v).doubleValue();
		return null;
	}
	
	// Получение значения типа String из литерала или из переменной посредством variables
	static public String getValueStr(Operation variables, Object val, boolean optional) throws CalculateException {
		Object v = getValue(variables, val, optional);
		if (v==null)
			return null;
		if (v instanceof CalculateValue)
			return ((CalculateValue)v).getStr(CalculateValue.getSubName(val));
		if (v instanceof String)
			return (String)v;
		return v.toString();
	}	

	//Получение текста значения переменной
	static public String formatValue(String format, CalculateValue.Operation variables, String name) throws CalculateException {
		if (name == null || name.isEmpty())
			return null;
		CalculateValue vv = variables.getCalculateValue(getName(name), false);
		if (vv==null) 
			return null;
		return vv.formatValue(format, getSubName(name));
	}		
	
	// Форматирование строки с подстановкой значений переменных 
	static public String formatValues(CalculateValue.Operation variables, String source) throws CalculateException {
		String res = new String();
		if (source == null || source.isEmpty())
			return res;
		boolean inVar = false;
		int pos = 0;
		int p;
		String s;
		String val;
		while (true) {
			p = source.indexOf(selectorValue, pos);
			if (p<0) 
				break;
			s = source.substring(pos, p);
			pos = p + 1;			
			if (!inVar) res = res + s;
			else if (s.isEmpty()) res = res + selectorValue;
			else {
				p = s.indexOf(selectorFormat);
				if (p>=0) 
					val = formatValue(s.substring(p+1), variables, s.substring(0, p));
				else 
					val = formatValue(null, variables, s);
				if (val!=null) 
					res = res + val;
			}
			inVar = !inVar;
		}
		if (!inVar)
			res = res + source.substring(pos);
		return res;
	}
	
	static public void CalculateNone(CalculateValue.Operation variables, String errorMsg, int codeDefault) throws CalculateException {
		if (errorMsg==null)
			return;
		int p = errorMsg.indexOf('#');
		if (p>0) try {
			codeDefault = Integer.parseInt(errorMsg.substring(0,p).trim());
			errorMsg = errorMsg.substring(p+1).trim();
		} catch (Exception e) {
		}
		throw new CalculateNone(formatValues(variables, errorMsg), codeDefault);	
	}
	
	protected static class CalculateInt extends CalculateValue {

		protected long val;
		
		public CalculateInt(Post.DataType datatype) {
			super(datatype);
			this.val = 0;
		}
		
		public CalculateInt(CalculateInt source) {
			this(source.type);
			this.val = source.getInt(null);
		}		
	
		@Override
		public Object getVal(String subname) {
			return getInt(subname);
		}
		
		@Override
		public double getNum(String subname) {
			return (double)getInt(subname);
		}

		@Override
		public long getInt(String subname) {
			return val;
		}

		@Override
		public String getStr(String subname) {
			return String.valueOf(getInt(subname));
		}
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString() + getDisplayUnit(units, val);
		}	
		
		@Override
		public boolean isEmpty(String subname) {
			return getInt(subname)==0;
		}		
		
		@Override
		public void clear(String subname) {
			val = 0;
		}

		@Override
		protected void copy(String subname, CalculateValue value) {
			val = value.getInt(null);
		}
		
		@Override
		protected void copy(String subname, Number value) {
			if (value instanceof Long) {
				val = (Long) value;
			} else if (value instanceof Integer) {	
				val = (Integer) value;
			} else {
				val = new BigDecimal(value.doubleValue()).setScale(0, BigDecimal.ROUND_DOWN).longValue();
			}
		}	
		
		@Override
		protected void copy(String subname, String value) {
			try {
				copy(subname, Long.parseLong(value));
			} catch (Exception e) {
				throw new NumberFormatException("значение \"" + value + "\" не целочисленное");
			}	
		}			
		
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			return getInt(subname)>=min && ((max<=min || getInt(subname)<=max));
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			return getInt(subname)>=min;
		}	

		@Override
		public boolean checkMax(String subname, long max) {
			return getInt(subname)<=max;
		}
		
		//Проверка на равенство значения
		@Override
		public boolean equals(String subname, CalculateValue value) {
			if (subname==null)
				return val==value.getInt(null);
			else
				return getInt(subname)==value.getInt(subname);
		}		
		
		//Сравнение значения с переменной
		@Override
		public int compare(String subnameLeft, CalculateValue valueRight, String subnameRight) {
			if (valueRight==this)
				return 0;
			if (valueRight==null)
				return 1;
			long v1 = getInt(subnameLeft);
			long v2 = valueRight.getInt(subnameRight);
			if (v1>v2) return 1;
			else if (v1<v2) return -1;
			else return 0;
		}
		
	}
	
	protected static class CalculateStr extends CalculateValue {

		public CalculateStr() {
			super(Post.DataType.STR);
		}
		
		public CalculateStr(CalculateStr source) {
			this();
			copy(null, source.getStr(null));
		}
	
		protected String val;
		
		@Override
		public Object getVal(String subname) {
			return val;
		}		
		
		@Override
		public double getNum(String subname) {
			if (val.isEmpty())
				return 0;
			try {
				return Double.parseDouble(val);
			} catch (Exception e) {
				throw new NumberFormatException("значение \"" + val + "\" не дробное.");
			}					
		}

		@Override
		public long getInt(String subname) {
			if (val.isEmpty())
				return 0;
			try {
				return Long.parseLong(val);
			} catch (Exception e) {
				throw new NumberFormatException("значение \"" + val + "\" не целочисленное.");
			}			
		}

		@Override
		public String getStr(String subname) {
			return val;
		}
	
		@Override
		public String getJSON() {
			return "\""+Post.toJSON(val)+"\"";
		}			
		
		@Override
		public boolean isEmpty(String subname) {
			return val.isEmpty();
		}			
		
		@Override
		public void clear(String subname) {
			val = new String();
		}

		@Override
		protected void copy(String subname, CalculateValue value) {
			val = value.getStr(null);
		}
		
		@Override
		protected void copy(String subname, Number value) {
			val = String.valueOf(value);
		}	
		
		@Override
		protected void copy(String subname, String value) {
			val = value;
		}			
		
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			int v = val.length();
			return v>=min && ((max<=min || v<=max));
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			return val.length()>=min;
		}	

		@Override
		public boolean checkMax(String subname, long max) {
			return val.length()<=max;
		}
		
		//Проверка на равенство значения
		@Override
		public boolean equals(String subname, CalculateValue value) {
			if (subname==null)
				return val.equals(value.getStr(null));
			else
				return getStr(subname).equals(value.getStr(subname));
		}		
		
		//Суммирование this=this+value
		@Override
		public void sum(String subname, CalculateValue value) {
			if (value==null)
				return;
			String v = value.getStr(subname);
			if (v!=null)
				set(subname, getStr(subname) + v);
		}
		
	}
	
	protected static class CalculateNum extends CalculateValue {

		public CalculateNum() {
			super(Post.DataType.NUM);
		}
		
		public CalculateNum(CalculateNum source) {
			this();
			copy(null, source.getNum(null));
		}			
	
		protected double val;
		
		@Override
		public Object getVal(String subname) {
			return val;
		}		
		
		@Override
		public double getNum(String subname) {
			return val;
		}

		@Override
		public long getInt(String subname) {
			return (long)val;
		}

		@Override
		public String getStr(String subname) {
			long v = (long) Math.floor(val);
			if ((val-v)==0)
				return String.valueOf(v);
			else
				return String.valueOf(val);
		}
	
		@Override
		public String getJSON() {
			return String.valueOf(val);
		}			
		
		@Override
		public String toString() {
			return getStr(null);
		}	
		
		@Override
		public String toString(ArrayList<String> units) {
			return getStr(null) + getDisplayUnit(units, (long)val);
		}	
		
		@Override
		public boolean isEmpty(String subname) {
			return val==0;
		}			

		@Override
		public void clear(String subname) {
			val = 0;
		}

		@Override
		protected void copy(String subname, CalculateValue value) {
			val = value.getNum(null);
		}
		
		@Override
		protected void copy(String subname, Number value) {
			val = value.doubleValue();
		}	
		
		@Override
		protected void copy(String subname, String value) {
			try {
				val = Double.parseDouble(value);
			} catch (Exception e) {
				throw new NumberFormatException("значение \"" + value + "\" не дробное");
			}	
		}			
		
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			long v = getInt(null);
			return v>=min && ((max<=min || v<=max));
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			return getInt(null)>=min;
		}	

		@Override
		public boolean checkMax(String subname, long max) {
			return getInt(null)<=max;
		}

		@Override		
		public void round(int newScale, int roundingMode) {
			double res = new BigDecimal(getNum(null)).setScale(newScale, roundingMode).doubleValue();
			set(null, res);
		}
		
		//Проверка на равенство значения
		@Override
		public boolean equals(String subname, CalculateValue value) {
			if (subname==null)
				return val==value.getNum(null);
			else
				return getNum(subname)==value.getNum(subname);
		}	
		
		//Сравнение значения с переменной
		@Override
		public int compare(String subnameLeft, CalculateValue valueRight, String subnameRight) {
			if (valueRight==this)
				return 0;
			if (valueRight==null)
				return 1;
			double v1 = getNum(subnameLeft);
			double v2 = valueRight.getNum(subnameRight);
			if (v1>v2) return 1;
			else if (v1<v2) return -1;
			else return 0;
		}
		
		//Суммирование this=this+value
		@Override
		public void sum(String subname, CalculateValue value) {
			if (value!=null)
				set(subname, getNum(subname) + value.getNum(subname));
		}
		
		//Вычитание this=this-value
		@Override
		public void sub(String subname, CalculateValue value) {
			if (value!=null)
				set(subname, getNum(subname) - value.getNum(subname));
		}
		
		//Умножение this=this*value
		@Override
		public void multi(String subname, CalculateValue value) {
			if (value!=null)
				set(subname, getNum(subname) * value.getNum(subname));
		}

		//Деление this=this/value
		@Override
		public void divide(String subname, CalculateValue value) {
			if (value==null)
				return;
			set(subname, getNum(subname) / value.getNum(subname));
		}			
		
	}
	
	protected static class CalculateDate extends CalculateInt {

		public CalculateDate() {
			super(Post.DataType.DATE);
		}
		
		public CalculateDate(CalculateDate source) {
			super(source);
		}			
		
		@Override
		public String toString() {
			return Post.getDateShort((int)val);
		}		
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}
		
		@Override
		public long getInt(String subname) {
			if (subname==null) 
				return super.getInt(null);
			if (subname.equalsIgnoreCase("d")) return val % 100;
			if (subname.equalsIgnoreCase("m")) return (val / 100) % 100;
			if (subname.equalsIgnoreCase("y")) return val / 10000;
			return super.getInt(subname);
		}
		
		@Override
		public ArrayList<String> getSubnames() {
			ArrayList<String> res = super.getSubnames();
			res.add("d");
			res.add("m");
			res.add("y");
			return res;
		}
		
		@Override
		protected void copy(String subname, String value) {
			try {
				val = Post.ISO8601decodeDate(value);
				if (val < 0)
					val = 0;
			} catch (Exception e) {
				throw new NumberFormatException("значение \"" + value + "\" не является датой");
			}	
		}			
		
	}
	
	protected static class CalculateTime extends CalculateInt {

		public CalculateTime() {
			super(Post.DataType.TIME);
		}
		
		public CalculateTime(CalculateTime source) {
			super(source);
		}			
		
		@Override
		public String toString() {
			return Post.getTime((int)val);
		}		
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}	
		
		
		@Override
		public long getInt(String subname) {
			if (subname==null) 
				return super.getInt(null);
			if (subname.equalsIgnoreCase("hm")) return val / 100;
			if (subname.equalsIgnoreCase("h")) return val / 10000;
			if (subname.equalsIgnoreCase("m")) return (val / 100) % 100;
			if (subname.equalsIgnoreCase("s")) return val % 100;
			return super.getInt(subname);
		}
		
		@Override
		public ArrayList<String> getSubnames() {
			ArrayList<String> res = super.getSubnames();
			res.add("hm");
			res.add("h");
			res.add("m");
			res.add("s");
			return res;
		}
		
		@Override
		protected void copy(String subname, String value) {
			try {
				val = Post.ISO8601decodeTime(value);
			} catch (Exception e) {
				throw new NumberFormatException("значение \"" + value + "\" не является временем");
			}
		}		
		
	}
	
	
	protected static class CalculateBool extends CalculateInt {

		public CalculateBool() {
			super(Post.DataType.BOOL);
		}
		
		public CalculateBool(CalculateBool source) {
			super(source);
		}				
	
		@Override
		public String getStr(String subname) {
			return String.valueOf(val);
		}
		
		@Override
		public String toString() {
			return (val!=0?"да":"нет");
		}
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}			
		
		@Override
		protected void copy(String subname, String value) {
			if (value.isEmpty())
				clear(subname);
			else if (value.equalsIgnoreCase("да")||value.equalsIgnoreCase("true")||value.equalsIgnoreCase("yes"))
				val = 1;
			else try {
				long v = Long.parseUnsignedLong(value);
				val = (v!=0?1:0);
			} catch (Exception e) {
				val = 1;
			}	
		}
		
		@Override
		protected void format(String subname) {
			if (val!=0)
				val = 1;
		}		
		
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			return true;
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			return true;
		}	

		@Override
		public boolean checkMax(String subname, long max) {
			return true;
		}
		
		//Проверка на равенство значения
		@Override
		public boolean equals(String subname, CalculateValue value) {
			if (value==null)
				return false;
			return (getInt(subname)!=0)==(value.getInt(subname)!=0);
		}	
		
		//Сравнение значения с переменной
		@Override
		public int compare(String subnameLeft, CalculateValue valueRight, String subnameRight) {
			if (valueRight==this)
				return 0;
			if (valueRight==null)
				return 1;
			boolean v1 = getInt(subnameLeft)!=0;
			boolean v2 = valueRight.getInt(subnameRight)!=0;
			if (v1==v2) return 0;
			else if (!v1 && v2) return -1;
			else return 1;
		}
		
		//Суммирование this=this+value
		@Override
		public void sum(String subname, CalculateValue value) {
			if (value==null)
				return;
			if ((getInt(subname)!=0)||(value.getInt(subname)!=0))
				set(subname, 1);
			else
				set(subname, 0);
		}
		
		//Умножение this=this*value
		@Override
		public void multi(String subname, CalculateValue value) {
			if (value==null)
				return;
			if ((getInt(subname)!=0)&&(value.getInt(subname)!=0))
				set(subname, 1);
			else
				set(subname, 0);

		}

	}
	
	protected static class CalculateSum extends CalculateInt {

		public CalculateSum() {
			super(Post.DataType.SUM);
		}
		
		public CalculateSum(CalculateSum source) {
			super(source);
		}			
	
		@Override
		public String toString() {
			return Post.getCurrecyShort(val);
		}			
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString() + getDisplayUnit(units, val);
		}			
		
	}
	
	protected static class CalculateDay extends CalculateInt {

		public CalculateDay() {
			super(Post.DataType.DAY);
		}
		
		public CalculateDay(CalculateDay source) {
			super(source);
		}		
	
		@Override
		protected void format(String subname) {
			if (val<=0) val = 0;
			else if (val>31) val = 31;
		}	
		
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			return val>=1 && val<=31;
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			return val>=1;
		}	

		@Override
		public boolean checkMax(String subname, long max) {
			return val<=31;
		}
		
	}	
	
	protected static class CalculateMonth extends CalculateInt {

		public CalculateMonth() {
			super(Post.DataType.MONTH);
		}
		
		public CalculateMonth(CalculateMonth source) {
			super(source);
		}			
		
		@Override
		public String toString() {
			if (val==0)
				return new String();
			else
				return Post.getMonthName((int)val);
		}
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}			
	
		@Override
		protected void format(String subname) {
			if (val<=0) val = 0;
			else if (val>12) val = 12;
		}	
		
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			return val>=1 && val<=12;
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			return val>=1;
		}	

		@Override
		public boolean checkMax(String subname, long max) {
			return val<=12;
		}		
		
	}	
	
	protected static class CalculateWeight extends CalculateInt {

		public CalculateWeight() {
			super(Post.DataType.WEIGHT);
		}
		
		public CalculateWeight(CalculateWeight source) {
			super(source);
		}	
		
		@Override
		public String toString() {
			if (val==0)
				return new String();
			else
				return Post.WeightToStr(val, true);
		}	
		
		@Override
		public String toString(ArrayList<String> units) {
			if (val==0) 
				return new String();
			else if (units == null || units.isEmpty() || units.get(0).equalsIgnoreCase("г") || units.get(0).equalsIgnoreCase("грамм"))
				return Post.WeightToStr(val, true);
			else
				return String.valueOf(val) + getDisplayUnit(units, val);
		}			
		
	}		
	
	protected static class CalculateSize extends CalculateValue {

		public CalculateSize() {
			super(Post.DataType.SIZE);
		}
		
		public CalculateSize(CalculateSize source) {
			this();
			copy(null, source);
		}		
	
		protected double x;
		protected double y;
		protected double z;
		
		public int subname(String subname) {
			if (subname==null || subname.isEmpty())
				return 0;
			String sn = subname.substring(0,1);
			if (sn.equalsIgnoreCase("x")||sn.equalsIgnoreCase("w"))
				return 1;
			else if (sn.equalsIgnoreCase("y")||sn.equalsIgnoreCase("h"))
				return 2;
			else if (sn.equalsIgnoreCase("z")||sn.equalsIgnoreCase("d"))
				return 3;
			else if (sn.equalsIgnoreCase("s"))
				return 4;
			else if (sn.equalsIgnoreCase("p"))
				return 5;
			else if (sn.equalsIgnoreCase("m")) {
				if (subname.equalsIgnoreCase("max"))
					return 6;
				else if (subname.equalsIgnoreCase("mid"))
					return 7;
				else if (subname.equalsIgnoreCase("min"))
					return 8;
				else 
					return 0;
			}	
			else
				return 0;
		}
		
		@Override
		public Object getVal(String subname) {
			if (subname==null)
				return this;			
			switch (subname(subname)) {
			case 1:
				return x;
			case 2:
				return y;
			case 3:
				return z;
			case 4:
				return x*y*z;
			case 5:
				return x+y+z;
			case 6:
				if (x>y) {
					if (x>z) return x;
					else return z;
				} else {
					if (y>z) return y;
					else return z;
				}
			case 7:
				if (x>y) {
					if (x>z) return z;
					else return x;
				} else {
					if (y>z) return z;
					else return y;				
				}
			case 8:
				if (x<y) {
					if (x<z) return x;
					else return z;
				} else {
					if (y<z) return y;
					else return z;
				}				
			default:
				return x+y+z;
			}			
		}
		
		@Override
		public double getNum(String subname) {
			switch (subname(subname)) {
			case 1:
				return x;
			case 2:
				return y;
			case 3:
				return z;
			case 4:
				return x*y*z;
			case 5:
				return x+y+z;
			case 6:
				if (x>y) {
					if (x>z) return x;
					else return z;
				} else {
					if (y>z) return y;
					else return z;
				}
			case 7:
				if (x>y) {
					if (x<z) return x;
					else if (y>z) return y;
					else return z;
				} else {
					if (x>z) return x;
					else if (y<z) return y;
					else return z;				
				}
			case 8:
				if (x<y) {
					if (x<z) return x;
					else return z;
				} else {
					if (y<z) return y;
					else return z;
				}					
			default:
				return x+y+z;
			}
		}
		
		@Override
		public ArrayList<String> getSubnames() {
			ArrayList<String> res = super.getSubnames();
			res.add("x");
			res.add("y");
			res.add("z");
			res.add("s");
			res.add("p");
			res.add("max");
			res.add("mid");
			res.add("min");
			return res;
		}
		
		@Override
		public long getInt(String subname) {
			Double d = new Double(getNum(subname)); 
			return d.longValue();
		}

		@Override
		public String getStr(String subname) {
			if (subname==null || subname.isEmpty()) {
				return toString();
			} else
				return String.valueOf(getNum(subname));
		}
		
		@Override
		public String getJSON() {
			return String.format("{\"x\":"+Post.doubleToStr(x)+",\"y\":"+Post.doubleToStr(y)+",\"z\":"+Post.doubleToStr(z)+"}", x, y, z);
		}			

		@Override
		public String toString() {
			String res = Post.doubleToStr(x);
			if (y!=0) res = res + "x" + Post.doubleToStr(y);
			if (z!=0) res = res + "x" + Post.doubleToStr(z);
			return res;
		}	
		
		@Override
		public String toString(ArrayList<String> units) {
			String unit;
			if (units==null || units.isEmpty()) 
				unit = new String();
			else
				unit = units.get(0);
			String res = Post.doubleToStr(x) + unit;
			if (y!=0) res = res + " X " + Post.doubleToStr(y) + unit;
			if (z!=0) res = res + " X " + Post.doubleToStr(z) + unit;
			return res;
		}			
		
		@Override
		public boolean isEmpty(String subname) {
			if (subname==null || subname.isEmpty())
				return x==0 && y==0 && z==0;
			switch (subname(subname)) {
			case 1:
				return x==0;
			case 2:
				return y==0;
			case 3:
				return z==0;
			default:
				return true;
			}
		}			

		@Override
		public void clear(String subname) {
			x = 0;
			y = 0;
			z = 0;
		}
		
		@Override
		protected void copy(String subname, CalculateValue value) {
			if (value.type == Post.DataType.SIZE) {
				CalculateSize v = (CalculateSize)value;
				x = v.x;
				y = v.y;
				z = v.z;
			} else
				copy(subname, value.getStr(null));
		}
		
		@Override
		public void copy(String subname, String value) {
			clear(subname);
			if (value==null || value.isEmpty()) return;
			int p;
			try {
				if (subname==null || subname.isEmpty()) for (int i = 0 ; i<3; i++) {
					p = value.indexOf("x");
					if (p<0) p = value.indexOf("X");
					if (p<0) p = value.indexOf("*");
					if (p<0) p = value.indexOf("х");
					if (p<0) p = value.indexOf("Х");
					switch (i) {
					case 0:
						if (p>=0) 
							x = Double.parseDouble(value.substring(0,p).trim());
						else
							x = Double.parseDouble(value.trim());
						break;
					case 1:
						if (p>=0)
							y = Double.parseDouble(value.substring(0,p).trim());
						else
							y = Double.parseDouble(value.trim());					
						break;
					case 2:
						if (p>=0)
							z = Double.parseDouble(value.substring(0,p).trim());
						else
							z = Double.parseDouble(value.trim());				
						break;
					}
					if (p<0)
						break;
					value = value.substring(p+1).trim();
				} else switch (subname(subname)) {
				case 1:
					x = Double.parseDouble(value.trim());
					break;
				case 2:
					y = Double.parseDouble(value.trim());
					break;
				case 3:
					z = Double.parseDouble(value.trim());
					break;
					
				}
			} catch (Exception e) {
				throw new NumberFormatException("значение \"" + value + "\" не является размером");
			}			
			
		}	
		
		@Override
		protected void copy(String subname, Number value) {
			if (subname==null || subname.isEmpty()) {
				x = (double) value;
				y = 0;
				z = 0;
			} else switch (subname(subname)) { 
				case 1:
					x = (double) value;
					break;
				case 2:
					y = (double) value;
					break;
				case 3:
					z = (double) value;
					break;
			}	
		}	
		
		@Override
		public boolean equals(String subname, CalculateValue value) {
			if (value.type == Post.DataType.SIZE) {
				CalculateSize v = (CalculateSize)value;
				if (subname==null || subname.isEmpty()) return x == v.x && y == v.y && z == v.z;
				else switch (subname(subname)) {
				case 1: 
					return x == v.x;
				case 2: 
					return y == v.y;
				case 3:
					return z == v.z;
				default:
					return false;
				}	
			} else return getStr(subname).equalsIgnoreCase(value.getStr(subname));
		}		
		
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			if (subname==null || subname.isEmpty()) {
				double[] val = {x, y, z};
				for (int i = 0 ; i<3; i++) {
					if (val[i]>=min && (max<=min || val[i]<=max)) continue;
					return false;
				}
			} else switch (subname(subname)) {
			case 1: 
				return x>=min && (max<=min || x<=max);
			case 2:
				return y>=min && (max<=min || y<=max);
			case 3:
				return z>=min && (max<=min || z<=max);
			}
			return true;
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			if (subname==null || subname.isEmpty()) {
				double[] val = {x, y, z};
				for (int i = 0 ; i<3; i++) {
					if (val[i]>=min) continue;
					return false;
				}
			} else switch (subname(subname)) {
			case 1: 
				return x>=min;
			case 2:
				return y>=min;
			case 3:
				return z>=min;
			}	
			return true;
		}		

		@Override
		public boolean checkMax(String subname, long max) {
			if (subname==null || subname.isEmpty()) {			
				double[] val = {x, y, z};
				for (int i = 0 ; i<3; i++) {
					if (val[i]<=max) continue;
					return false;
				}
			} else switch (subname(subname)) {
			case 1: 
				return x<=max;
			case 2:
				return y<=max;
			case 3:
				return z<=max;
			}	
		return true;
		}			

	}

	protected static class CalculatePost extends CalculateValue {

		protected PostOfficeProperty val;	
		protected int date;
		
		public CalculatePost(PostOfficeProperty.PostOfficeLoaded loadPO, int date) {
			super(Post.DataType.POST);
			this.val = new PostOfficeProperty(loadPO);			
			this.date = date;
		}
		
		public CalculatePost(CalculatePost source) {
			this(source.val.getLoadPO(), source.date);
			val.copy(source.val);
		}			
		
		@Override
		public Object getVal(String subname) {
			if (subname==null) 
				return this;
			else if (subname.equalsIgnoreCase("name"))
				return val.getPostOfficeName(false);
			else if (subname.equalsIgnoreCase("fullname"))
				return val.getPostOfficeName(true);
			else if (subname.indexOf(".")>0) {
				String[] d = subname.split("\\.");
				if (d.length<2)
					return getInt(subname);
				if (d[0].equalsIgnoreCase("workday-count") && d.length==3) {
					return val.workDayCount(date, Post.getInt(d[1], 0), Post.getInt(d[2], 0));
				} else 
					return getInt(subname);
			}
			else
				return getInt(subname);
		}
		
		@Override
		public double getNum(String subname) {
			return getInt(subname);
		}

		@Override
		public long getInt(String subname) {
			if (subname==null || subname.isEmpty())
				return val.PO;
			return val.getDataInt(subname, 0);
		}

		@Override
		public String getStr(String subname) {
			if (subname==null) 
				return String.valueOf(val.PO);
			else if (subname.equalsIgnoreCase("name"))
				return val.getPostOfficeName(false);
			else if (subname.equalsIgnoreCase("fullname"))
				return val.getPostOfficeName(true); 
			else {
				Object v = val.getData(subname);
				if (v==null)
					return null;
				return v.toString();
			}
		}
		
		@Override
		public ArrayList<String> getSubnames() {
			ArrayList<String> res = super.getSubnames();
			for (PostOfficeProperty.PostOfficeParam p: PostOfficeProperty.PostOfficeParam.values())
				if (!p.funct)
					res.add(p.name);
			return res;
		}
		
		@Override
		public void getValues(Map<String, Object> dest) {
			if (dest!=null)
				for (PostOfficeProperty.PostOfficeParam p: PostOfficeProperty.PostOfficeParam.values())
					if (!p.funct)
						dest.put(p.name, val.getData(p));
		}			
		
		@Override
		public String getJSON() {
			return String.valueOf(val.PO);
			//if (val.PO>0)
			//	return "{" + val.toJSON() + "}";
			//return "null";
		}			

		@Override
		public String toString() {
			return val.getPostOfficeName(true);
		}
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}			
		
		@Override
		public boolean isEmpty(String subname) {
			if (subname==null || subname.isEmpty())
				return (val.PO==0);
			else {
				Object v = val.getData(subname);
				if (v==null)
					return true;
				return (v instanceof Number) && ((Number)v).longValue()==0;
			}	
		}			
		
		@Override
		public void clear(String subname) {
			if (val==null) ;
			else if (subname==null || subname.isEmpty()) val.clear();
			else val.setData(subname, 0);
		}

		@Override
		protected void copy(String subname, CalculateValue value) {
			if (val==null) return;
			if (value.type == Post.DataType.POST) {
				CalculatePost v = (CalculatePost)value;
				if (subname==null || subname.isEmpty()) 
					val.PO = v.val.PO;
				else
					val.setData(subname, v.getInt(subname));
				
			} else if (subname==null || subname.isEmpty()) copy(subname, value.getStr(null));
			else val.setData(subname, value.getInt(subname));
		}
		
		@Override
		protected void copy(String subname, Number value) {
			if (subname==null || subname.isEmpty()) 
				val.PO = value.intValue();
			else
				val.setData(subname, value.longValue());
		}	
		
		@Override
		protected void copy(String subname, String value) {
			try {
				if (subname==null || subname.isEmpty()) 
					val.PO = Integer.parseUnsignedInt(value);
				else
					val.setData(subname, Long.parseLong(value));
			} catch (Exception e) {
				throw new NumberFormatException("значение \"" + value + "\" не является параметром почтового объекта");
			}				
		}	
		
		@Override
		protected void format(String subname) {
			if (subname==null || subname.isEmpty()) 
				val = val.loadPOdata(val.PO, date);
		}

		@Override
		public boolean equals(String subname, CalculateValue value) {
			if (value.type == Post.DataType.POST) {
				CalculatePost v = (CalculatePost)value;
				if (subname==null || subname.isEmpty()) 
					return val.PO == v.val.PO;
				else
					return val.getData(subname) == v.val.getData(subname);
			} else return getStr(subname).equalsIgnoreCase(value.getStr(subname));
		}	
				
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			if (subname==null || subname.isEmpty()) 
				return val.PO>=100000 && val.PO<=999999;
			else {
				Object vo = val.getData(subname);
				if (vo==null || !(vo instanceof Number))
					return false;
				long v = ((Number)vo).longValue();
				return v>=min && (max<=min || v<=max);
			}
				
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			if (subname==null || subname.isEmpty()) 
				return val.PO>=100000;
			else 
				return val.getDataInt(subname,min-1)>=min;
		}	

		@Override
		public boolean checkMax(String subname, long max) {
			if (subname==null || subname.isEmpty()) 
				return val.PO<=999999;
			else 
				return val.getDataInt(subname,max+1)<=max;
		}			

	}
		
	protected static class CalculateList extends CalculateInt {

		final protected PostDictionary list;
		
		public CalculateList(PostDictionary list) {
			super(Post.DataType.LIST);
			this.list = list;
		}
		
		public CalculateList(CalculateList source) {
			super(source);
			this.list = source.list;
		}			
		
		@Override
		public String toString() {
			return list.getName((int)val, "%1$s (%2$d)");
		}
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}	
		
		@Override
		public String getStr(String subname) {
			if (subname!=null && subname.equalsIgnoreCase("name"))
				return list.getName((int)val, null);
			return super.getStr(subname);
		}			
		
		@Override
		protected void format(String subname) {
			if (list==null || val==0)
				return;
			if (list.get((int)val)==null)
				val = 0;
		}
		
		@Override
		public ArrayList<String> getSubnames() {
			return list.getIDlist();
		}		
		
		@Override
		public void getValues(Map<String, Object> dest) {
			ArrayList<Map<String, Object>> res = list.getByID();
			Object v;
			for (Map<String, Object> it: res) if (it!=null) {
				v = it.get("id");
				if (v!=null && !v.toString().isEmpty()) 
					dest.put(v.toString(), it.get("name"));
			}
		}		
		
		@Override
		public String getJSON() {
			Map<String, Object> v = list.get((int)val);
			String n = null;
			if (v!=null)
				n = Post.getStrNull(v.get("name"));
			if (n!=null)
				n = Post.killDublSpace(n.trim());
			if (n==null || n.isEmpty())
				return String.format("{\"id\":%1$d}", val);
			return String.format("{\"id\":%1$d,\"name\":\"%2$s\"}", val, Post.toJSON(n));
		}		
		
	}
	
	protected static class CalculateSet extends CalculateValue {

		protected ArrayList<Integer> vals;	
		final protected PostDictionary list;
		
		public CalculateSet(PostDictionary list) {
			super(Post.DataType.SET);
			this.list = list;
			this.vals = new ArrayList<Integer>();	
		}
		
		public CalculateSet(CalculateSet source) {
			this(source.list);
			this.vals.addAll(source.vals);
		}		
		
		@Override
		public Object getVal(String subname) {
			if (subname==null)
				return this;
			return getInt(subname);
		}
		
		@Override
		public double getNum(String subname) {
			return getInt(subname);
		}

		public int find(int value) {
			if (value<=0)
				return -1;
			for (Integer i = 0; i<vals.size(); i++) if (vals.get(i) != null && vals.get(i).equals(value))
				return i;
			return -1;
		}		
		
		@Override
		public ArrayList<String> getSubnames() {
			ArrayList<String> res = super.getSubnames();
			for (Integer it: vals) res.add(String.valueOf(it));
			return res;
		}
		
		@Override
		public void getValues(Map<String, Object> dest) {
			if (dest!=null)
				for (Integer it: vals) dest.put(String.valueOf(it), it);
		}	
		
		@Override
		public long getInt(String subname) {
			if (subname==null || subname.isEmpty())
				return 0;
			else {
				int i = find(Post.getInt(subname,0));
				if (i>=0) 
					 return vals.get(i);
				return 0;
			}
		}

		@Override
		public String getStr(String subname) {
			if (subname==null || subname.isEmpty())
				return Post.arrayToString(vals, ",");
			else {
				int i = find(Post.getInt(subname,0));
				if (i>=0) 
					 return vals.get(i).toString();
				return new String();
			}
		}
		
		@Override
		public String toString() {
			String res;
			res = new String();
			for (Integer v : vals) if (v!=null && v!=0) {
				if (!res.isEmpty()) 
					res = res + ", ";
				if (list==null)
					res = res + String.valueOf(v);
				else 
					res = res + list.getName(v, "%1$s (%2$d)");
			}
			return "[" + res + "]";
		}
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}	
		
		@Override
		public String getJSON() {
			String res;
			res = new String();
			for (Integer v : vals) if (v!=null && v!=0) {
				if (!res.isEmpty()) 
					res = res + ",";
				res = res + v;
			}
			return "[" + res + "]";
		}
		
		@Override
		public boolean isEmpty(String subname) {
			if (subname==null || subname.isEmpty())
				return vals.isEmpty();
			else 
				return find(Post.getInt(subname,0))<0;
		}			
		
		@Override
		public void clear(String subname) {
			if (vals==null)
				return;
			if (subname==null || subname.isEmpty())
				vals.clear();
			else {
				int i = find(Post.getInt(subname,0));
				if (i>=0) vals.remove(i);
			}
		}

		@Override
		protected void copy(String subname, CalculateValue value) {
			switch (value.type) {
			case SET:
				CalculateSet v = (CalculateSet)value;
				if (subname==null || subname.isEmpty()) {
					vals.clear();
					vals.addAll(v.vals);
				} else if (subname.equalsIgnoreCase("add")) vals.addAll(v.vals);
				else if (subname.equalsIgnoreCase("del")) {
					vals.removeAll(v.vals);
				}
				break;
			case STR:
				copy(subname, value.getStr(null));
				break;
			default:
				copy(subname, value.getInt(null));
			}
		}
		
		@Override
		protected void copy(String subname, Number value) {
			int v;
			if (value!=null) 
				v = value.intValue();
			else
				v = 0;
			if (subname==null || subname.isEmpty()) {
				vals.clear();
				if (v>0)
					vals.add(v);
			} else if (subname.equalsIgnoreCase("add")) {
				if (v>0 && find(v)==-1) 
					vals.add(v);
			} else if (subname.equalsIgnoreCase("del")) {
				if (v>0) {
					int i = find(v);
					if (i!=-1)
						vals.remove(i);
				}
			}
		}	
		
		@Override
		protected void copy(String subname, String value) {
			try {
				if (subname==null || subname.isEmpty()) {
					Post.stringToArrayIntegerPositive(vals, value, ",");
				} else if (subname.equalsIgnoreCase("add")) {
					vals.addAll(Post.stringToArrayIntegerPositive(value, ","));
				} else if (subname.equalsIgnoreCase("del")) {
					vals.removeAll(Post.stringToArrayIntegerPositive(value, ","));
				}
			} catch (Exception e) {
				throw new NumberFormatException("значение \"" + value + "\" не является списком целых чисел");
			}				
		}	
		
		@Override
		protected void format(String subname) {
			/*
			if (list==null || vals.isEmpty())
				return;
			Integer v; 
			int i = 0;
			int size = vals.size();
			while (i<size) {
				v = vals.get(i);
				if (v!=null && (v==0 || (list!=null && list.get(v)==null)))
					v = null;
				if (v!=null) i++;
				else {
					vals.remove(i);
					size--;
				}
			}
			*/
		}

		@Override
		public boolean equals(String subname, CalculateValue value) {
			if (value.type == Post.DataType.SET) {
				CalculateSet v = (CalculateSet)value;
				return vals.equals(v.vals);
			} else return getStr(null).equals(value.getStr(null));
		}	
				
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			if (subname==null || subname.isEmpty()) {
				for (Integer v: vals) if (v!=null) {
					if (v>=min && ((max<=min || v<=max))) continue;
					return false;
				}
				return true;
			} else {
				int i = find(Post.getInt(subname,0));
				if (i<0)
					return true;
				int v = vals.get(i);
				return (v>=min && ((max<=min || v<=max)));				
			}
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			if (subname==null || subname.isEmpty()) {
				for (Integer v: vals) if (v!=null) {
					if (v>=min) continue;
					return false;
				}
				return true;
			} else {
				int i = find(Post.getInt(subname,0));
				if (i<0)
					return true;
				int v = vals.get(i);
				return v>=min;				
			}				
		}		

		@Override
		public boolean checkMax(String subname, long max) {
			if (subname==null || subname.isEmpty()) {
				for (Integer v: vals) if (v!=null) {
					if (v<=max) continue;
					return false;
				}
				return true;
			} else {
				int i = find(Post.getInt(subname,0));
				if (i<0)
					return true;
				int v = vals.get(i);
				return v<=max;				
			}				
		}			
	}

	protected static class CalculateBit extends CalculateInt {

		public CalculateBit() {
			super(Post.DataType.BIT);
		}
		
		public CalculateBit(CalculateBit source) {
			super(source);
		}			
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}	
		
	}
	
	protected static class CalculateArray extends CalculateValue {

		protected Map<String, Object> vals;	
		
		public CalculateArray() {
			super(Post.DataType.ARRAY);
			this.vals = new LinkedHashMap<String, Object>();	
		}
		
		public CalculateArray(CalculateArray source) {
			this();
			vals.putAll(source.vals);	
		}		
		
		@Override
		public Object getVal(String subname) {
			if (subname==null)
				return this;
			return vals.get(subname);
		}		
		
		@Override
		public double getNum(String subname) {
			return Post.getNum(vals.get(subname), 0.0);
		}

		@Override
		public long getInt(String subname) {
			return Post.getLong(vals.get(subname), 0);
		}

		@Override
		public String getStr(String subname) {
			return Post.getStr(vals.get(subname));
		}
		
		@Override
		public ArrayList<String> getSubnames() {
			ArrayList<String> res = super.getSubnames();
			for (String it: vals.keySet()) res.add(it);
			return res;
		}	
		
		@Override
		public void getValues(Map<String, Object> dest) {
			if (dest!=null)
				dest.putAll(vals);
		}		
		
		@Override
		public Map<String, Object> getValues() {
			return vals;
		}			

		@Override
		public String toString() {
			String res = new String();
			for (Entry<String, Object> v : vals.entrySet()) if (v.getKey()!=null && v.getValue()!=null) {
				if (!res.isEmpty()) 
					res = res + ", ";
				res = res + "\"" + v.getKey() + "\":" + Post.toJSON(v.getValue(),false);
			}
			return "{" + res + "}";
		}
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}	
		
		@Override
		public String getJSON() {
			return Post.toJSON(vals, false);
		}
		
		@Override
		public boolean isEmpty(String subname) {
			if (subname==null || subname.isEmpty()) {
				for (Object v : vals.values()) 
					if (v!=null)
						return false;
				return true;
			} else {
				return vals.get(subname)==null;
			}
		}			
		
		@Override
		public void clear(String subname) {
			if (vals==null)
				return;
			if (subname==null || subname.isEmpty()) vals.clear();
			else {
				Object v = vals.get(subname);
				if (v!=null)
					vals.put(subname, null);
			}
			 
		}

		@Override
		protected void copy(String subname, CalculateValue value) {
			if (value==null) 
				return; 
			if (subname!=null && !subname.isEmpty()) switch (value.type) {
			case ARRAY:
				vals.put(subname, ((CalculateArray)value).vals.get(subname));
				break;
			case STR:
				vals.put(subname, value.getStr(null));
				break;
			case NUM:
				vals.put(subname, value.getNum(null));
				break;
			case SET:
			case SIZE:
			case POST:
				vals.put(subname, value);
				break;
			default:
				vals.put(subname, value.getInt(null));
			} else switch (value.type) {
			case ARRAY:
				vals.clear();
				vals.putAll(((CalculateArray)value).vals);
				break;
			default:	
				vals.clear();
			}
		}
		
		@Override
		protected void copy(String subname, Number value) {
			if (subname!=null && !subname.isEmpty()) 
				vals.put(subname, value);
			else
				vals.clear();
		}	
		
		@Override
		protected void copy(String subname, String value) {
			if (subname!=null && !subname.isEmpty()) 
				vals.put(subname, value);
			else
				vals.clear();
		}	
		
		@Override
		public boolean equals(String subname, CalculateValue value) {
			if (value.type == Post.DataType.ARRAY) {
				CalculateArray v = (CalculateArray)value;
				if (subname==null || subname.isEmpty()) 
					return vals.equals(v.vals);
				else
					return vals.get(subname).equals(v.vals.get(subname));
			} else return getStr(subname).equals(value.getStr(subname));
		}	
				
		@Override
		public boolean checkMinMax(String subname, long min, long max) {
			long vi;
			if (subname==null || subname.isEmpty()) {
				for (Object v : vals.values()) if (v!=null) {
					vi = Post.getLong(v);
					if (vi>=min && (max<=min || vi<=max)) continue;
					return false;
				}	
			} else {
				vi = getInt(subname);
				return vi>=min && (max<=min || vi<=max);
			}
			return true;
		}
		
		@Override
		public boolean checkMin(String subname, long min) {
			long vi;
			if (subname==null || subname.isEmpty()) {
				for (Object v : vals.values()) if (v!=null) {
					vi = Post.getLong(v);
					if (vi>=min) continue;
					return false;
				}	
			} else {
				vi = getInt(subname);
				return vi>=min;
			}
			return true;
		}		

		@Override
		public boolean checkMax(String subname, long max) {
			long vi;
			if (subname==null || subname.isEmpty()) {
				for (Object v : vals.values()) if (v!=null) {
					vi = Post.getLong(v);
					if (vi<=max) continue;
					return false;
				}	
			} else {
				vi = getInt(subname);
				return vi<=max;
			}
			return true;			
		}

	}

	protected static class CalculateGUID extends CalculateValue {

		protected UUID val;	
		
		public CalculateGUID() {
			super(Post.DataType.GUID);
			this.val = null;	
		}
		
		public CalculateGUID(CalculateGUID source) {
			this();
			if (source!=null && source.val!=null)
				this.val = new UUID(source.val.getMostSignificantBits(), source.val.getLeastSignificantBits());
		}		
		
		@Override
		public long getInt(String subname) {
			if (subname==null || val==null)
				return 0;
			else if (subname.equalsIgnoreCase("most"))
				return val.getMostSignificantBits();
			else if (subname.equalsIgnoreCase("least"))
				return val.getLeastSignificantBits();
			else
				return 0;
			
		}
		
		@Override
		public double getNum(String subname) {
			return getInt(subname);
		}
		

		@Override
		public String getStr(String subname) {
			if (val==null)
				return null;
			else if (subname==null)
				return Post.encodeUUID(val);
			else if (subname.equalsIgnoreCase("most"))
				return String.valueOf(val.getMostSignificantBits());
			else if (subname.equalsIgnoreCase("least"))
				return String.valueOf(val.getLeastSignificantBits());
			else
				return null;
		}
		
		@Override
		public ArrayList<String> getSubnames() {
			ArrayList<String> res = super.getSubnames();
			res.add("most");
			res.add("least");
			return res;
		}	
		
		@Override
		public void getValues(Map<String, Object> dest) {
			if (dest==null || val==null) 
				return;
			dest.put("", Post.encodeUUID(val));
			dest.put("most", val.getMostSignificantBits());
			dest.put("least", val.getLeastSignificantBits());
		}		
		
		@Override
		public String toString() {
			if (val!=null)
				return val.toString();
			return null;
		}	
		
		@Override
		public String toString(ArrayList<String> units) {
			return toString();
		}	
		
		@Override
		public String getJSON() {
			return Post.toJSON(getStr(null));
		}
		
		@Override
		public boolean isEmpty(String subname) {
			return val==null;
		}			
		
		@Override
		public void clear(String subname) {
			val = null;
		}

		@Override
		protected void copy(String subname, CalculateValue value) {
			if (value==null) 
				val = null; 
			else switch (value.type) {
			case GUID:
				val = new UUID(((CalculateGUID)value).val.getMostSignificantBits(), ((CalculateGUID)value).val.getLeastSignificantBits());
				break;
			default:
				val = Post.decodeUUID(value.getStr(subname));
			}
		}
		
		@Override
		protected void copy(String subname, String value) {
			val = Post.decodeUUID(value);
		}	
		
		@Override
		public boolean equals(String subname, CalculateValue value) {
			if (value==null)  
				return val==null;
			if (val==null)
				return false;
			switch (value.type) {
			case GUID:
				return val.equals(((CalculateGUID)value).val);
			default:
				return val.equals(Post.decodeUUID(value.getStr(subname)));
			}
		}

	}
	
}
