package caits.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PostAttributes {
	
	private Map<String, PostAttribute> byID;
	private Map<String, ArrayList<PostAttribute>> byParam;
	private Map<String, ArrayList<String>> params;
	private Map<Integer, PostAttribute> byIDint;
	
	public PostAttributes(ArrayList<Map<String, Object>> source) {
		super();
		params = null;
		byID = new HashMap<String, PostAttribute>();
		byIDint = new HashMap<Integer, PostAttribute>();
		byParam = new HashMap<String, ArrayList<PostAttribute>>();
		if (source==null) return;
		for (Map<String, Object> item: source) add(new PostAttribute(item));
		prepareParams();
	}
	
	public PostAttributes() {
		this(null);
	}
	
	public PostAttribute get(String id) {
		if (id==null || id.isEmpty()) return null;
		return byID.get(id);
	}
	
	public PostAttribute get(int id) {
		if (id<=0) return null;
		return byIDint.get(id);
	}	

	public ArrayList<PostAttribute> getByParam(String id) {
		if (id==null || id.isEmpty()) return null;
		return byParam.get(id);
	}
	
	public String getName(String id) {
		if (id==null) return new String(); 
		PostAttribute v = get(id);
		if (v==null || v.isEmpty()) return id.toString();
		return v.getName();
	}

	public PostAttribute get(String id, Map<String, Object> values) throws NullPointerException {
		PostAttribute a = get(id);
		if (a==null || a.isEmpty())
			throw new NullPointerException("Непподерживаемый атрибут " + id + ".");
		if (values==null)
			throw new NullPointerException(a.getName() + ": не указано значение.");
		return a;
	}	
	
	public PostAttribute get(int id, Map<String, Object> values) throws NullPointerException {
		PostAttribute a = get(id);
		if (a==null || a.isEmpty())
			throw new NullPointerException("Непподерживаемый атрибут " + id + ".");
		if (values==null)
			throw new NullPointerException(a.getName() + ": не указано значение.");
		return a;
	}		
	
	public long getLong(String id, Map<String, Object> values, boolean autoCreate) throws NullPointerException {
		return get(id, values).getLong(values, autoCreate);
	}	
	
	public Object setLong(String id, Map<String, Object> values, long val) throws NullPointerException {
		return get(id, values).setLong(values, val);
	}	

	public int getInt(String id, Map<String, Object> values, boolean autoCreate) throws NullPointerException {
		return get(id, values).getInt(values, autoCreate);
	}	

	public Object setInt(String id, Map<String, Object> values, int val) throws NullPointerException {
		return get(id, values).setInt(values, val);
	}	
	
	public String getStr(String id, Map<String, Object> values, boolean autoCreate) throws NullPointerException {
		return get(id, values).getStr(values, autoCreate);
	}	
	
	public Object setStr(String id, Map<String, Object> values, String val) throws NullPointerException {
		return get(id, values).setStr(values, val);
	}	
	
	public boolean getBool(String id, Map<String, Object> values, boolean autoCreate) throws NullPointerException {
		return get(id, values).getBool(values, autoCreate);
	}	
	
	public Object setBool(String id, Map<String, Object> values, boolean val) throws NullPointerException {
		return get(id, values).setBool(values, val);
	}	
	
	public ArrayList<?> getSet(String id, Map<String, Object> values, boolean autoCreate) throws NullPointerException {
		return get(id, values).getSet(values, autoCreate);
	}
	
	public Object setSet(String id, Map<String, Object> values, ArrayList<Integer> val) throws NullPointerException {
		return get(id, values).setSet(values, val);
	}	
	
	public PostAttribute add(PostAttribute item) {
		if (item==null || item.isEmpty()) return null;
		PostAttribute v = get(item.getID());
		if (v != null) return v;
		String param = item.getParam();
		byID.put(item.getID(), item);
		if (item.getIDint()>0)
			byIDint.put(item.getIDint(), item);
		ArrayList<PostAttribute> at = byParam.get(param);
		if (at == null) at = new ArrayList<PostAttribute>();
		at.add(item);
		byParam.put(param, at);		
		params = null;
		return item;
	}
	

	public boolean remove(PostAttribute item) {
		if (item==null) return false;
		PostAttribute v = byID.remove(item.getID());
		byIDint.remove(item.getIDint());
		byParam.remove(item.getParam());
		if (v==null) return false;
		params = null;
		return true;
	}	
	
	public boolean remove(String id) {
		return remove(get(id));
	}
	
	public boolean remove(int id) {
		return remove(get(id));
	}			
	
	public int size() {
		return byID.size();
	}	
	
	public boolean isEmpty() {
		return byID.isEmpty();
	}	
	
	public void clear() {
		params = null;
		byID.clear();
		byParam.clear();
	}		
	
	public Set<String> idSet() {
		return byID.keySet(); 
	}	
	
	public Set<String> paramSet() {
		return byParam.keySet(); 
	}	

	public Collection<PostAttribute> values() {
		return byID.values(); 
	}		
	
	public static Map<String, ArrayList<String>> prepareParams(PostAttributes source) {
		Map<String, ArrayList<String>> res = new HashMap<String, ArrayList<String>>();
		if (source == null) return res;
		ArrayList<String> ps;
		ArrayList<String> el;
		for (PostAttribute item : source.values()) if (item != null && !item.isEmpty()) {
			ps = item.getParams();
			if (ps == null)	ps = new ArrayList<String>();
			ps.add(0, item.getID()); //Добавление собственного id
			for (String p : ps)	if (p != null && !p.isEmpty()) {
				el = res.get(p);
				if (el == null) {
					el = new ArrayList<String>();
					res.put(p, el);
				}
				el.add(item.getID());
			}
		}
		return res;
	}
	

	public void prepareParams() {
		params = prepareParams(this);
	}

	public Map<String, ArrayList<String>> getParams() {
		if (params==null) prepareParams();
		return params;
	}	
	
	public ArrayList<String> getAttribytesByParams(String paramID) {
		return getParams().get(paramID);
	}
	
	public Object fromString(String id, Object source, Map<String, Object> dest, boolean isEmpty) throws NullPointerException, NumberFormatException {
		return get(id, dest).fromString(source, dest, isEmpty);
	}

	public String toString(String id, Map<String, Object> values, String mask, String listName) {
		if (values==null) return null;
		PostAttribute a = get(id);
		if (a==null) return null;
		return a.toString(values.get(id), mask, listName);
	}
	
	public ArrayList<Map<String, Object>> toMap(boolean isListData) {
		ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>(); 
		for (PostAttribute attr: values())
			if (attr!=null && !attr.isEmpty()) 
				res.add(attr.toMap(isListData));
		return res;
	}
	
}
