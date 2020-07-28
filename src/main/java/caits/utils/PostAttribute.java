package caits.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PostAttribute {

	public enum CheckType {ON, OFF};
	
	private String id;
	private int idint;
	private String name;
	private String param;
	private Post.DataType type;
	private ArrayList<String> params;
	private ArrayList<String> unit;
	private String listname;
	private PostDictionary listdata;
	private String listparam;
	private long min;
	private long max;
	private long def;
	private Map<String, Long> multi;
	private int seq;
	private Post.BoolExt direction;
	private Map<Integer, CheckType> check;
	private Map<Integer, String> checkList;
	private boolean optional;

	public PostAttribute(Map<String, Object> data) {
		super();
		clear();
		if (data == null) 
			return;
		loadFromMap(data);		
	}
	
	public PostAttribute(PostAttribute data) {
		super();
		id = null;
		if (data == null) return;
		id = data.id;
		name = data.name;
		param = data.param;
		listname = data.listname;
		listdata = data.listdata;
		listparam = data.listparam;
		type = data.type;
		params = data.params;
		unit = data.unit;
		min = data.min;
		max = data.max;
		def = data.def;
		seq = data.seq;
		multi = data.multi;
		direction = data.direction;
		check = data.check;
		checkList = data.checkList;
		optional = false;
	}	
	
	public PostAttribute() {
		super();
		clear();
	}
	
	public void clear() {
		id = null;
		idint = 0;
		name = null;
		param = null;
		type = null;
		params = null;
		unit = null;
		listname = null;
		listdata = null;
		min = 0;
		max = 0;
		def = 0;
		multi = null;
		seq = 0;
		direction = Post.BoolExt.NONE;
		check = null;
		checkList = null;
		optional = false;
	}
	
	public void loadFromBinary(ByteBufferManager source, int checkSize, short checkBlockID, int checkAttrID) throws InvalidPropertiesFormatException {
		clear();
		ByteBufferManager.Head head = source.head(checkSize, checkBlockID);
		ByteBufferManager.Element el = new ByteBufferManager.Element();
		int valID = 0;
		String valName = null;
		while (source.element(el, head)) 
			if (el.type==ByteBufferManager.ElementType.SEPARATOR) {
				if (valID>0 && this.listdata!=null && valName!=null) 
					this.listdata.set(String.valueOf(valID), valName, null);
				valID = 0;
				valName = null;
			} else switch (el.id) {
			case 1:
				this.idint = el.getInt(); 
				if (this.idint!=checkAttrID)
					throw new InvalidPropertiesFormatException("Различные коды атрибутов");
				break;
			case 2:
				this.id = el.getStr();
				break;
			case 3:
				this.name = el.getStr();
				break;
			case 4:
				this.param = el.getStr();
				break;
			case 5:
				this.type = Post.DataType.get(el.getInt());
				break;
			case 6:
				this.min = el.getLong();
				break;
			case 7:
				this.max = el.getLong();
				break;
			case 8:
				this.def = el.getLong();
				break;
			case 9:
				this.listname = el.getStr();
				break;					
			case 10:
				if (!el.isArray())
					throw new InvalidPropertiesFormatException("Неверный формат единиц измерения атрибута");
				this.unit = new ArrayList<String>(el.getArrayCount());
				break;					
			case 11:
				this.unit.add(el.getStr());
				break;	
			case 12:
				this.seq = el.getInt();
				break;	
			case 13:
				this.listparam = el.getStr();
				break;					
			case 20:	
				if (!el.isArray())
					throw new InvalidPropertiesFormatException("Неверный формат списка атрибута");
				this.listdata = new PostDictionary();
				break;
			case 21:
				switch (el.type) {
				case VALUE_STR:
					valName = el.getStr();
					break;
				case VALUE_INT:
					valID = el.getInt();
					break;
				default:
					break;
				}
				break;
			case 22:
				this.optional = el.getBoolean();
				break;
			}
	}
	
	public void loadFromMap(Map<String, Object> data) {
		if (data == null) 
			return;
		id = Post.getStr(data.get("id"), id);
		if (id==null)
			id = new String();
		name = Post.getStr(data.get("name"), name);
		if (name==null) 
			name = id;
		param = Post.getStr(data.get("param"), param);
		if (param==null) 
			param = id;
		Object v = data.get("datatype");
		if (v!=null)
			type = Post.DataType.get(v);
		listname = Post.getStr(data.get("listname"), listname);
		listparam = Post.getStr(data.get("listparam"), listparam);
		v = data.get("list");
		if (v == null) ;
		else if (v instanceof String) listname = Post.getStrNull(v);
		else listdata = new PostDictionary(true, Post.getArrayMap(v));
		if (listdata != null) listname = null;
		if ((listdata != null || listname != null) && type != Post.DataType.SET) 
			type = Post.DataType.LIST;
		
		v = data.get("params");
		if (v!=null)
			params = Post.getArrayString(v);
		v = data.get("unit");
		if (v!=null)
			unit = Post.getArrayString(v);
		min = Post.getLong(data.get("min"), min);
		max = Post.getLong(data.get("max"), max);
		def = Post.getLong(data.get("def"), def);
		seq = Post.getInt(data.get("seq"), seq);
		optional = Post.getBool(data.get("optional"), optional);
		v = data.get("direction");
		if (v!=null)
			direction =  Post.BoolExt.get(v);
	}
	
	//Объединение с приоритетом source (перезапись)
	public void copy(PostAttribute source) throws CalculateException {
		if (source == null || source == this) return;
		if (source.type!=null)
			type = source.type;		
		if (source.name!=null && !source.name.equalsIgnoreCase(source.param))
			name = source.name;
		if (source.param!=null && !source.param.equalsIgnoreCase(id))
			param = source.param;		
		if (source.min>0)
			min = source.min;
		if (source.max>0 && source.max>source.min)
			max = source.max;		
		if (source.def>0)
			def = source.def;
		if (source.check!=null) {
			if (check==null) {
				check = new HashMap<Integer, CheckType>(1);
				checkList = new HashMap<Integer, String>(1);
			}
			for (Entry<Integer, CheckType> ct : source.check.entrySet()) if (check.get(ct.getKey())==null) {
				check.put(ct.getKey(), ct.getValue());
				checkList.put(ct.getKey(), source.checkList.get(ct.getKey()));
			}
		}	
		if (source.unit!=null)
			unit = source.unit;
		if (source.listdata!=null) 
			listdata = source.listdata;
		else if (source.listname!=null) 
			listname = source.listname;
		if (source.optional)
			optional = source.optional;
	}

	//Объединение равноправное (анализ)
	public void merge(PostAttribute source) throws CalculateException {
		if (source == null || source == this) return;
		if (source.name!=null && !source.name.equalsIgnoreCase(source.param) && (name==null || name.equalsIgnoreCase(param)))
			name = source.name;
		if (source.param!=null && !source.param.equalsIgnoreCase(id) && (param==null || param.isEmpty() || param.equalsIgnoreCase(id)))
			param = source.param;		
		if (source.min>0 && source.min>min)
			min = source.min;
		if (source.max>0 && source.max>source.min && source.max<max)
			max = source.max;		
		if (source.def>0 && (def<min || (max>min && def>max)))
			def = source.def;
		if (source.check!=null) {
			if (check==null) {
				check = new HashMap<Integer, CheckType>(1);
				checkList = new HashMap<Integer, String>(1);
			}
			for (Entry<Integer, CheckType> ct : source.check.entrySet()) if (check.get(ct.getKey())==null) {
				check.put(ct.getKey(), ct.getValue());
				checkList.put(ct.getKey(), source.checkList.get(ct.getKey()));
			}
		}	
		if (source.unit!=null && unit==null)
			unit = source.unit;
		if (source.listparam!=null && listparam==null)
			listparam = source.listparam;
		if (source.listdata!=null) {
			if (listdata!=null && listdata!=source.listdata)
				throw new CalculateException("Разные списки в одном параметре " + getDisplayNameQuote() + ".");
			listdata = source.listdata;
		} else if (source.listname!=null) { 
			if (listname!=null && listname!=source.listname)
				throw new CalculateException("Разные справочнки в одном параметре " + getDisplayNameQuote() + ".");
			listname = source.listname;
		}	
		if (!source.optional)
			optional = source.optional;		
	}
	
	public boolean isEmpty() {
		return id == null;
	}
	
	public boolean equal(Object id) {
		return (this.id!=null)&&(id!=null)&&(this.id.equals(id));
	}	

	public boolean equal(String... val) {
		if (this.id==null) return false;
		for (int i = 0; i < val.length; i++) if (this.id.equalsIgnoreCase(val[i])) 
			return true;
		return false;
	}	
	
	public String getID() {
		return id;
	}
	
	public boolean isID(String... ids) {
		int cnt = 0;
		if (ids!=null)
			cnt = ids.length;
		for (int i=0; i<cnt; i++) 
			if (id.equalsIgnoreCase(ids[i])) 
				return true;
		return false;
	}	
	
	public int getIDint() {
		return idint;
	}	
	
	public String getName() {
		return name;
	}
	
	public void setProperty(int tariffID, Map<String, Object> property, int seq) {
		if (property == null) return;
		String v = Post.getStrNull(property.get("name"));
		if (v!=null) name = v;
		v = Post.getStrNull(property.get("p"));
		if (v!=null) param = v;
		v = Post.getStrNull(property.get("d"));
		if (v!=null) type = Post.DataType.get(v);
		v = Post.getStrNull(property.get("min"));
		if (v!=null) min = Post.getLong(v);
		v = Post.getStrNull(property.get("max"));
		if (v!=null) max = Post.getLong(v);
		v = Post.getStrNull(property.get("def"));
		if (v!=null) def = Post.getLong(v);
		setCheck(tariffID, property);		
		ArrayList<String> u = Post.getArrayString(property.get("u"));
		if (u!=null) unit = u;
		listparam = Post.getStrNull(property.get("listparam")); 
		v = Post.getStrNull(property.get("listname"));
		if (v!=null) listname = v;
		ArrayList<Map<String, Object>> l = Post.getArrayMap(property.get("list"));
		if (l!=null && !l.isEmpty()) setListData(l);
		if (seq>0)
			this.seq = seq;
		if (property.containsKey("optional"))
			optional = Post.getBool(property.get("optional"), optional);
	}
	
	public String getParam() {
		if (param!=null && !param.isEmpty())
			return param;
		return id;
	}	
	
	public String getDisplayParamName(boolean isQuote) {
		String n = name.trim();
		if (isQuote)
			n = "\"" + n + "\"";
		String p = getParam();
		if (p == null || p.isEmpty() || p.equalsIgnoreCase(name)) 
			return n;
		return n + " (" + p + ")";
	}
	
	
	public String getDisplayName() {
		return getDisplayParamName(false);
	}	
	
	public String getDisplayNameQuote() {
		return getDisplayParamName(true);
	}		
	
	public String getDisplayName(String value) {
		return getDisplayParamName(false) + " " + value;
	}		

	public String getDisplayNameQuote(String value) {
		return getDisplayParamName(true) + " \"" + value + "\"";
	}	
	
    public String getDisplayValue(CalculateValue value) {
        if (value == null) return null;
        return value.toString(getUnits());
    }    

    public String getDisplayValue(Map<String, CalculateValue> values) {
        return getDisplayValue(values.get(id));
    }
    
    public String getDisplayNameValue(CalculateValue value, String mask, String nameNoUse) {
        if (value == null) return null;
        boolean isUse = value.getRead()>0;
        String use;
        if (nameNoUse==null)
            use = new String();
        else if (nameNoUse.isEmpty())
            use = (isUse?"да":"нет");
        else
            use = (isUse?"":nameNoUse);
        //								1			2					3							4			5
        return String.format(mask, getParam(), getDisplayName(), value.getJSON(), value.toString(getUnits()), use);
    }    

	public Post.DataType getType() {
		return type;
	}
	
	/*
	public void addListData(Map<String, Object> item) {
		if (item == null)
			return;
		if (listdata == null)
			listdata = new ArrayList<Map<String, Object>>();
		listdata.add(item);
		if (this.type == null || this.type != Post.DataType.SET)
			this.type = Post.DataType.LIST;
		this.unit = null;
		this.listname = null;
		this.def = 0;
		this.min = 0;
		this.max = 0;
	}
	*/
	
	public void setListData(ArrayList<Map<String, Object>> items) {
		if (items == null) 
			return;
		this.listdata = new PostDictionary(true, items);
		if (this.type == null || this.type != Post.DataType.SET)
			this.type = Post.DataType.LIST;
		this.unit = null;
		this.listname = null;
		this.def = 0;
		this.min = 0;
		this.max = 0;
	}
	
	
	public String getListName() {
		return listname;
	}
	
	public void setListName(String listname) {
		this.listname = listname;
	}	
	
	public String getListParam() {
		return listparam;
	}	
	
	public ArrayList<Map<String, Object>> getListData() {
		if (listdata==null)
			return null;
		return listdata.getByID();
	}	
	
	public PostDictionary getList() {
		return listdata;
	}		

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	public int getIndex() {
		return seq;
	}

	public void setIndex(int seq) {
		this.seq = seq;
	}
	
	public boolean getOptional() {
		return optional;
	}
	
	public void setMulti(String multiID, long val) {
		if (multiID==null || multiID.isEmpty())
			return;
		if (multi==null) multi = new HashMap<String, Long>();
		Long v = multi.get(multiID);
		if (v==null || !v.equals(val))
			multi.put(multiID, val);
	}
	
	public void delMulti(String multiID) {
		if (multiID==null || multiID.isEmpty())
			return;
		multi.remove(multiID);
	}	

	public boolean getMulti(String multiID) {
		return multi!=null && multi.get(multiID)!=null;	
	}

	public long getMulti(String multiID, long val) {
		if (multi==null) 
			 return 1; 
		Long m = multi.get(multiID);
		if (m==null)
			return 1;
		m = val + m;
		if (m<0) m = (long)0;
		return m;
	}
	
	public CheckType setCheck(int tariffID, Map<String, Object> data) {
		String coff = Post.getStrNull(data.get("listblack"));
		String con = Post.getStrNull(data.get("listwhite"));
		if (coff==null && con==null)
			return null;
		if (check==null) 
			check = new HashMap<Integer, CheckType>(1);
		if (checkList==null) 		
			checkList = new HashMap<Integer, String>(1);
		if (coff!=null) {
			check.put(tariffID, CheckType.OFF);
			checkList.put(tariffID, coff);
			return CheckType.OFF;
		} else if (con!=null) {
			check.put(tariffID, CheckType.ON);
			checkList.put(tariffID, con);
			return CheckType.ON;			
		} else return null;
	}
	
	public CheckType getCheck(int tariffID) {
		if (check==null || checkList==null) return null;
		else return check.get(tariffID);
	}
	
	public String getCheckList(int tariffID) {
		if (check==null || checkList==null) return null;
		else return checkList.get(tariffID);
	}	
	
	public Post.BoolExt getDirection() {
		if (direction==null)
			return Post.BoolExt.NONE;
		return direction;
	}	
	
	public boolean checkExternal(Post.BoolExt external) {
		if (external == null 
				|| external == Post.BoolExt.NONE 
				|| direction == null 
				|| direction == Post.BoolExt.NONE) 
			return true;
		return external == direction;
	}	
	
	public boolean checkMinMax(Object val) {
		if (val==null) return true;
		else if (val instanceof Size) return ((Size)val).checkMinMax((int)min, (int)max);
		else {
			long v = Post.getLong(val);
			return v>=min && ((max<=min || v<=max));
		}
	}
	
	public boolean checkMin(Object val) {
		if (val==null) return true;
		else if (val instanceof Size) return ((Size)val).checkMin((int)min);
		else return Post.getLong(val)>=min;
	}	

	public boolean checkMax(Object val) {
		if (val==null) return true;
		else if (val instanceof Size) return ((Size)val).checkMax((int)max);
		else return max<=min || Post.getLong(val)<=max;
	}
	
	public ArrayList<String> getParams() {
		return params;
	}

	public ArrayList<String> getUnits() {
		return unit;
	}

	public String getUnit() {
		if (unit == null || unit.isEmpty())
			return new String();
		return unit.get(0);
	}
	
	public String getUnit(String prefix) {
		if (unit == null || unit.isEmpty())
			return new String();
		if (prefix!=null && !prefix.isEmpty())
			return prefix + unit.get(0);
		else
			return unit.get(0);
	}	
	
	public static Object setLong(long val) {
		return new Long(val);
	}

	public static Object setInt(int val) {
		return new Long(val);
	}
	
	public static Object setStr(String val) {
		return new String(val);
	}	

	public static Object setBool(boolean val) {
		return new Boolean(val);
	}		

	public Object setNum(double val) {
		return new Double(val);
	}
	
	public Object setSet(ArrayList<Integer> val) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		if (val!=null) res.addAll(val);
		return res;
	}
	
	public Object setSize(String val) throws NumberFormatException {
		return new Size(val);
	}		
	
	protected Object newValue(long vl, double vd, String vs, ArrayList<Integer> va) throws NullPointerException {
		switch (type) {
		case NONE:
			return null;
		case STR:
			return setStr(vs);
		case NUM:
			return setNum(vd);
		case SIZE:
			return setSize(vs);
		case BOOL:
			return setBool(vl!=0);
		case SET:
			return setSet(va);
		default:
			return setLong(vl);
		}
	}

	public Object getValue(Map<String, Object> dest, boolean autoCreate) throws NullPointerException {
		if (dest==null) 
			return null;
		Object v = dest.get(id);
		if (v!=null) return v;
		if (!autoCreate)
			throw new NullPointerException(name + ": не указано значение");
		v = newValue(0, 0, null, null);
		dest.put(id, v);
		return v;		
	}		
	
	public static long getLong(Object val) throws NullPointerException, NumberFormatException  {
		if (val == null) return -1;
		else if (val instanceof Number) return ((Number) val).longValue();
		else if (val instanceof Boolean) return ((Boolean) val ? 1 : 0);
		else if (val instanceof Size) return ((Size) val).longValue();
		else if (val.toString().trim().isEmpty()) return 0;
		else try {
			return Long.parseLong(val.toString());
		} catch (NumberFormatException e) {
			throw new NumberFormatException("значение \"" + val + "\" не целочисленное");
		}		
	}	
	
	public long getLong(Map<String, Object> dest, boolean autoCreate) throws NullPointerException{
		return getLong(getValue(dest, autoCreate));
	}

	public Object setLong(Map<String, Object> dest, long val) throws NullPointerException {
		Object v = setLong(val);
		if (dest!=null) dest.put(id, v);
		return v;
	}
	
	public static int getInt(Object val) throws NullPointerException, NumberFormatException {
		if (val == null) return -1;
		else if (val instanceof Number) return ((Number) val).intValue();
		else if (val instanceof Boolean) return ((Boolean) val ? 1 : 0);
		else if (val instanceof Size) return ((Size) val).intValue();		
		else if (val.toString().trim().isEmpty()) return 0;
		else try {
			return Integer.parseInt(val.toString());
		} catch (NumberFormatException e) {
			throw new NumberFormatException("неверно задано целочисленное значение \"" + val + "\"");
		}		
	}	
	
	public int getInt(Map<String, Object> dest, boolean autoCreate) throws NullPointerException {
		return getInt(getValue(dest, autoCreate));
	}	
	
	public Object setInt(Map<String, Object> dest, int val) throws NullPointerException {
		Object v = setInt(val);
		if (dest!=null) dest.put(id, v);
		return v;
	}
	
	public static boolean getBool(Object val) throws NullPointerException {
		if (val == null) return false;
		else if (val instanceof Boolean) return (Boolean) val;
		else if (val instanceof Number) return ((Number) val).longValue()!=0;
		else if (val instanceof Size) return !((Size) val).isEmpty();
		else if (val.toString().trim().isEmpty()) return false;
		else if (val.toString().equals("0")) return false;
		return true;
	}	
	
	public boolean getBool(Map<String, Object> dest, boolean autoCreate) throws NullPointerException {
		return getBool(getValue(dest, autoCreate));
	}	
	
	public Object setBool(Map<String, Object> dest, boolean val) throws NullPointerException {
		Object v = setBool(val);
		if (dest!=null) dest.put(id, v);
		return v;
	}	
	
	public static String getStr(Object val) throws NullPointerException {
		if (val == null) return null;
		else return val.toString();
	}	
	
	public String getStr(Map<String, Object> dest, boolean autoCreate) throws NullPointerException {
		return getStr(getValue(dest, autoCreate));
	}	
	
	public Object setStr(Map<String, Object> dest, String val) throws NullPointerException {
		Object v = setStr(val);
		if (dest!=null) dest.put(id, v);
		return v;
	}		
	
	public static ArrayList<Integer> getSet(Object val) throws NullPointerException, NumberFormatException {
		return Post.getArrayInteger(val);
	}	
	
	public ArrayList<Integer> getSet(Map<String, Object> dest, boolean autoCreate) throws NullPointerException {
		return getSet(getValue(dest, autoCreate));
	}	
	
	public Object setSet(Map<String, Object> dest, ArrayList<Integer> val) throws NullPointerException {
		Object v = setSet(val);
		if (dest!=null) dest.put(id, v);
		return v;
	}	
	
	public static double getNum(Object val) throws NullPointerException, NumberFormatException {
		if (val == null) return -1;
		else if (val instanceof Number) return ((Number) val).doubleValue();
		else if (val instanceof Boolean) return ((Boolean) val ? 1 : 0);
		else if (val instanceof Size) return ((Size) val).doubleValue();	
		else if (val.toString().trim().isEmpty()) return 0;
		else try {
			return Double.parseDouble(val.toString());
		} catch (NumberFormatException e) {
			throw new NumberFormatException("неверно задано дробное числовое значение \"" + val + "\"");
		}		
	}	
	
	public double getNum(Map<String, Object> dest, boolean autoCreate) throws NullPointerException{
		return getNum(getValue(dest, autoCreate));
	}

	public Object setNum(Map<String, Object> dest, double val) throws NullPointerException {
		Object v = setNum(val);
		if (dest!=null) dest.put(id, v);
		return v;
	}	
	
	
	public static Size getSize(Object val) throws NullPointerException {
		if (val == null) return null;
		else if (val instanceof Size) return (Size) val;
		else return null;
	}	
	
	public Size getSize(Map<String, Object> dest, boolean autoCreate) throws NullPointerException{
		return getSize(getValue(dest, autoCreate));
	}

	public Object setSize(Map<String, Object> dest, String val) throws NullPointerException {
		Object v = setSize(val);
		if (dest!=null) dest.put(id, v);
		return v;
	}		
	
	public static int getIntParam(Map<String, String[]> source, String paramName) {
		if (source==null || paramName==null || paramName.isEmpty())
			return -1;
		try {
			String[] pp = source.get(paramName);
			if (pp!=null && pp.length>0) 
				return PostAttribute.getInt(pp[pp.length-1]);
			else
				return -1;
		} catch (Exception e) {
			return -1;
		}	
	}
	
	public static int getIntParam(Map<String, String[]> source, String... paramName) {
		int res = -1;
		if (source==null || paramName==null) 
			return res;
		String[] pp;
		for (String pn : paramName) if (pn!=null && !pn.isEmpty()) {
			pp = source.get(pn);
			if (pp!=null) 
				for (String p : pp) 
					if (p!=null && !p.isEmpty())
						try {
							res = PostAttribute.getInt(p);
						} catch (Exception e) {
							res = -1;
						}	
			if (res>=0)
				 return res;
		}	
		return res;
	}	
	
	public static long getLongParam(Map<String, String[]> source, String... paramName) {
		long res = -1;
		if (source==null || paramName==null) 
			return res;
		String[] pp;
		for (String pn : paramName) if (pn!=null && !pn.isEmpty()) {
			pp = source.get(pn);
			if (pp!=null) 
				for (String p : pp) 
					if (p!=null && !p.isEmpty())
						try {
							res = PostAttribute.getLong(p);
						} catch (Exception e) {
							res = -1;
						}	
			if (res>=0)
				 return res;
		}	
		return res;
	}		
	
	public static String getStrParam(Map<String, String[]> source, String paramName) {
		if (source==null) 
			return null;
		try {
			String[] pp = source.get(paramName);
			if (pp!=null && pp.length>0) 
				return pp[pp.length-1];
			else
				return null;
		} catch (Exception e) {
			return null;
		}	
	}	
	
	public static ArrayList<Integer> getSetParam(Map<String, String[]> source, String paramName) {
		if (source==null) return null;
		try {
			String[] pp = source.get(paramName);
			if (pp!=null && pp.length>0) 
				return Post.stringToArrayIntegerPositive(pp[pp.length-1], ",");
			else
				return null;
		} catch (Exception e) {
			return null;
		}	
	}	

	
	public static boolean isEmptyValue(Object val) throws NullPointerException {
		return !getBool(val);
	}	
	
	public Object fromString(Object source, Map<String, Object> dest, boolean isEmpty) throws NullPointerException, NumberFormatException {
		Object v;
		String val = Post.getStrEmpty(source);
		if (source==null || val==null || val.isEmpty() || val.trim().isEmpty()) {
			//Пустая строка
			if (type==Post.DataType.BOOL) //Параметр имеется в наличии 
				v = setBool(true);
			else {
			 	if (!isEmpty)
			 		throw new NullPointerException(getDisplayName() + ": не указано значение в параметре \"" + param + "\"");
				v = newValue(0, 0, null, null);
			}
			if (dest!=null) dest.put(id, v);
			return v;			
		}
		switch (type) {
		case NONE:
			return null;
		case STR:
			return setStr(dest, val);
		case NUM:
			try {
				double vd = Double.parseDouble(val.trim());
			 	if (vd == 0 && !isEmpty)
			 		throw new NullPointerException(getDisplayName() + ": не указано значение в параметре \"" + param + "\"");
				if (vd != 0 && !checkMin(Math.round(vd)))
					throw new NumberFormatException(getDisplayName() + ": значение \"" + param + "=" + vd + "\" не может быть менее " + min + getUnit(" "));
				if (vd != 0 && !checkMax(Math.round(vd)))
					throw new NumberFormatException(getDisplayName() + ": значение \"" + param + "=" + vd + "\" не может быть более " + max + getUnit(" "));
				return setNum(dest, vd);
			} catch (NumberFormatException e) {
				throw new NumberFormatException(getDisplayName() + ": неверно задано числовое значение в параметре \"" + param + "=" + val + "\"");
			}
		case BOOL:
			return setBool(dest, !val.equals("0"));
		case DATE:
			try {
				int dt = Post.checkDate(Integer.parseUnsignedInt(val.trim()));
			 	if (dt ==0 && !isEmpty)
			 		throw new NullPointerException(getDisplayName() + ": не указано значение в параметре \"" + param + "\"");
				return setInt(dest, dt);
			} catch (NumberFormatException e) {
				throw new NumberFormatException(getDisplayName() + ": неверное значение даты в параметре \"" + param + "=" + val + "\"");
			}		
		case SUM:
			long vs = 0;
			try {
				vs = Long.parseUnsignedLong(val.trim());
			} catch (NumberFormatException e) {
				throw new NumberFormatException(getDisplayName() + ": неверно задано значение суммы в параметре \"" + param + "=" + val + "\"");
			}
		 	if (vs ==0 && !isEmpty)
		 		throw new NullPointerException(getDisplayName() + ": не указано значение суммы в параметре \"" + param + "\"");
			if (vs != 0 && !checkMin(vs))
				throw new NumberFormatException(getDisplayName() + ": значение суммы \"" + param + "=" + vs + "\" не может быть менее " + Post.getCurrecyLong(min) + getUnit(" "));
			if (vs != 0 && !checkMax(vs))
				throw new NumberFormatException(getDisplayName() + ": значение суммы \"" + param + "=" + vs + "\" не может быть более " + Post.getCurrecyLong(max) + getUnit(" "));
			
			return setLong(dest, vs);
		case SIZE:
			try {
				return setSize(dest, val);
			} catch (NumberFormatException e) {
				throw new NumberFormatException(getDisplayName() + ": неверно задано значение размера в параметре \"" + param + "=" + val + "\"");
			}
				
		case SET:
			try {
				return setSet(dest, Post.stringToArrayIntegerPositive(val, ","));
			} catch (NumberFormatException e) {
				throw new NumberFormatException(getDisplayName() + ": неверно задано целочисленное значение в параметре \"" + param + "=" + val + "\"");
			}			
		default:
			long vi = 0;
			try {
				vi = Long.parseUnsignedLong(val.trim());
			} catch (NumberFormatException e) {
				throw new NumberFormatException(getDisplayName() + ": неверно задано целочисленное значение в параметре \"" + param + "=" + val + "\"");
			}
		 	if (vi ==0 && !isEmpty)
		 		throw new NullPointerException(getDisplayName() + ": не указано значение в параметре \"" + param + "\"");
			if (vi != 0 && !checkMin(vi))
				throw new NumberFormatException(getDisplayName() + ": значение \"" + param + "=" + vi + "\" не может быть менее " + min + getUnit(" "));
			if (vi != 0 && !checkMax(vi))
				throw new NumberFormatException(getDisplayName() + ": значение \"" + param + "=" + vi + "\" не может быть более " + max + getUnit(" "));
			
			return setLong(dest, vi);
		}
	}	
	
	public Object fromString(Map<String, String[]> source, Map<String, Object> dest, boolean isEmpty) throws NullPointerException, NumberFormatException {
		if (source == null) return null;
		String[] pp = source.get(param);
	 	if (pp!=null && pp.length>0) 
	 		return fromString(pp[pp.length-1], dest, isEmpty);
	 	else
	 		return fromString(null, dest, isEmpty);
	}	

	public static String getDisplayUnit(String units) {
		if (units == null) return new String();
		else if (!units.isEmpty()) return " " + units;
		else return units;
	}
	
	public String getDisplayUnit(long val) {
		if (unit==null || unit.isEmpty())
			return new String();
		return getDisplayUnit(Post.formatSuffixByNumeric(val, unit));	
	}
	
	public String getDisplayUnit() {
		if (unit==null || unit.isEmpty())
			return new String();
		return getDisplayUnit(unit.get(0));	
	}	
	
	@Override
	public String toString() {
		String res = id;
		if (param!=null && !param.isEmpty())
			res = res + " (" + param + ")";
		//if (name!=null && !name.isEmpty())
		//	res = res + " (" + name + ")";
		return res;
	}	
	
	public String toString(Object val, String mask, String listValue) {
		if (val == null || (isEmptyValue(val) && min > 0)) return null;
		String nm;
		if ((type == Post.DataType.BOOL || type == Post.DataType.SET) && !param.startsWith("is"))
			nm = new String("is" + param);
		else
			nm = new String(param);
		
		if (listdata != null && !listdata.isEmpty()) {
			Map<String, Object> vl = listdata.get(getInt(val));
			if (vl == null)
				return null;
			return String.format(mask, nm, name, getInt(val), Post.getStr(vl.get("name")));
		} else if (listname != null && !listname.isEmpty() && listValue != null && type!=Post.DataType.SET)
			return String.format(mask, nm, name, getInt(val), listValue);
		else
			switch (type) {
			case NONE:
				return null;
			case INT:
				return String.format(mask, nm, name, getLong(val), String.format("%,d", getLong(val)) + getDisplayUnit(getLong(val)));
			case DAY:
			case MONTH:
				return String.format(mask, nm, name, getInt(val), String.valueOf(getInt(val)) + getDisplayUnit(getInt(val)));
			case BIT:
				return String.format(mask, nm, name, getLong(val), getLong(val));
			case BOOL:
				return String.format(mask, nm, name, getInt(val), (getBool(val) ? "да" : "нет"));
			case STR:
				return String.format(mask, nm, name, "\""+Post.htmlSplash(getStr(val)) + "\"", "\"" + Post.htmlSplash(getStr(val)) + getDisplayUnit() + "\"");
			case NUM:
				return String.format(mask, nm, name, getNum(val), String.valueOf(getNum(val)) + getDisplayUnit(getLong(val)));
			case DATE:
				return String.format(mask, nm, name, getInt(val), Post.getDateShort(getInt(val)));
			case SUM:
				return String.format(mask, nm, name, getLong(val), Post.getCurrecyLong(getLong(val)) + getDisplayUnit(getLong(val)));
			case WEIGHT:
				if (unit == null || unit.isEmpty() || unit.get(0).equalsIgnoreCase("г") || unit.get(0).equalsIgnoreCase("грамм"))
					return String.format(mask, nm, name, getLong(val), Post.WeightToStr(getLong(val), true));
				else
					return String.format(mask, nm, name, getLong(val), String.valueOf(getLong(val)) + getDisplayUnit(getLong(val)));
			case SIZE:
				if (unit == null || unit.isEmpty() || unit.get(0).isEmpty())
					return String.format(mask, nm, name, "\"" + getSize(val).toString() + "\"", getSize(val).toString());
				else
					return String.format(mask, nm, name, "\"" + getSize(val).toString() + "\"", getSize(val).toString(" "+unit.get(0)));
			case POST:
				return String.format(mask, nm, name, getInt(val), String.valueOf(getInt(val)));
			case LIST:
				return null;				
			case SET:
				String vvv = Post.arrayToString(val, ",");
				if (vvv==null || vvv.isEmpty())
					return null;
				return String.format(mask, nm, name, "["+vvv+"]", (listValue!=null?listValue:vvv));
			default:
				return null;
			}
	}

	public String toDisplayValue(Object value, String listValue) {
		if (value == null) 
			return new String();
		Object val = value;
		if (value instanceof CalculateValue)
			val = ((CalculateValue)value).getVal(null);
		
		if (val == null || (isEmptyValue(val) && min > 0)) 
			return new String();
		
		if (listdata != null && !listdata.isEmpty()) {
			Map<String, Object> vl = listdata.get(getInt(val));
			if (vl == null) 
				return null;
			return Post.getStr(vl.get("name"));
		} else if (listname != null && !listname.isEmpty() && listValue != null && type!=Post.DataType.SET)
			return listValue;
		else		
			switch (type) {
			case NONE:
				return null;
			case INT:
				return String.format("%,d", getLong(val)) + getDisplayUnit(getLong(val));
			case DAY:
			case MONTH:
				return String.valueOf(getInt(val)) + getDisplayUnit(getInt(val));
			case STR:
				return "\"" + Post.htmlSplash(getStr(val)) + getDisplayUnit() + "\"";
			case BIT:
				return String.valueOf(getLong(val));
			case BOOL:
				return (getBool(val) ? "да" : "нет");
			case NUM:
				return String.valueOf(getNum(val)) + getDisplayUnit(getLong(val));
			case DATE:
				return Post.getDateShort(getInt(val));
			case SUM:
				return Post.getCurrecyLong(getLong(val)) + getDisplayUnit(getLong(val));
			case WEIGHT:
				if (unit == null || unit.isEmpty() || unit.get(0).equalsIgnoreCase("г") || unit.get(0).equalsIgnoreCase("грамм"))
					return Post.WeightToStr(getLong(val), true);
				else
					return String.valueOf(getLong(val)) + getDisplayUnit(getLong(val));
			case SIZE:
				if (unit == null || unit.isEmpty() || unit.get(0).isEmpty())
					return getSize(val).toString();
				else
					return getSize(val).toString(" "+unit.get(0));
			case POST:
				return String.valueOf(getInt(val));
			case LIST:
				return null;
			case SET:
				if (listValue!=null)
					 return listValue;
				String vvv = Post.arrayToString(val, ",");
				if (vvv==null || vvv.isEmpty())
					return null;
				return vvv;
			default:
				return new String();
			}
	}
	
	public String toDisplayValue(Object val) {
		return toDisplayValue(val, null);
	}
	
	public void toMap(Map<String, Object> dest, ArrayList<Map<String, Object>> overrideList, boolean isListData) {
		if (dest == null) return;
		dest.put("id", id);
		dest.put("name", name);
		dest.put("datatype", type.index);
		dest.put("param", param);
		//if (params!=null && !params.isEmpty()) dest.put("params", params);
		if (unit!=null && !unit.isEmpty()) dest.put("unit", unit);
		if (min>0) dest.put("min", min);
		if (max>0) dest.put("max", max);
		if (def>0) dest.put("def", def);
		if (multi!=null) dest.put("multi", multi);
		if (direction!=null && direction != Post.BoolExt.NONE) 
			dest.put("direction", direction.index);
		if (overrideList!=null) 
			dest.put("list", overrideList);
		else if (listdata != null && isListData)
			dest.put("list", listdata.getBySeq());
		else if (listname!=null && !listname.isEmpty()) 
			dest.put("list", listname);
		if (listparam!=null && !listparam.isEmpty())
			dest.put("listparam", listparam);
		dest.put("seq", seq);
		if (optional)
			dest.put("optional", optional);
	}	
	
	public void toMap(Map<String, Object> dest, ArrayList<Map<String, Object>> overrideList) {
		toMap(dest, overrideList, true);
	}
	
	 public Map<String, Object> toMap(ArrayList<Map<String, Object>> overrideList) {
		 Map<String, Object> res = new LinkedHashMap<String, Object>();
		 toMap(res, overrideList);
		 return res;
	 } 	
	 
	 public Map<String, Object> toMap(boolean isListData) {
		 Map<String, Object> res = new LinkedHashMap<String, Object>();
		 toMap(res, null, isListData);
		 return res;
	 } 			 
	 
}

