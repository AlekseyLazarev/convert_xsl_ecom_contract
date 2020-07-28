package caits.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PostDictionary implements Iterable<Map<String, Object>> {
	
	private int from;
	private int to;
	private boolean isIDstr;
	private boolean isSeq;
	private String id;
	private String name;
	private int idint;
	private ArrayList<Map<String, Object>> data;
	private int seqnum;

	// Интерфейс получения справочиника по его имени
	public interface PostDictionaryGet {
		public PostDictionary getPostDictionary(String name);
	}
	
	public static class FindResult {
		public final boolean ok;
		public final int index;
		
		public FindResult(boolean ok, int index) {
			super();
			this.ok = ok;
			this.index = index;
		}
		
	}
	
	public PostDictionary(PostDictionary source) {
	  	data = new ArrayList<Map<String, Object>>();
		this.isIDstr = false;	  	
	  	copyFrom(source); 
	}
	
	public PostDictionary() {
	  	this(null);
	}
	
	public PostDictionary(boolean isIDstr) {
		this(null);
		this.isIDstr = isIDstr;
	}	

	public PostDictionary(boolean isIDstr, ArrayList<Map<String, Object>> data) {
		this.isIDstr = isIDstr;
		if (data==null) {
			this.data = new ArrayList<Map<String, Object>>();
			clear();
		} else {
			this.data = null;
			clear();
			if (!isIDstr)
				data.sort(new Post.sortIDlong());
			else
				data.sort(new Post.sortIDstr());
			this.data = data;
		}	
	}	
	
	@Override
	public String toString() { 
		return data.toString();
	}
	
	public void loadFromBinary(ByteBufferManager source, int checkSize, short checkBlockID, int checkDictID) throws InvalidPropertiesFormatException {
		clear();
		ByteBufferManager.Head head = source.head(checkSize, checkBlockID);
		ByteBufferManager.Element el = new ByteBufferManager.Element();
		int valID = 0;
		String valName = null;
		Map<String, Object> valData = new HashMap<String, Object>();
		String[] valsName = new String[3];
		while (source.element(el, head)) 
			if (el.type==ByteBufferManager.ElementType.SEPARATOR) {
				if (valID>0) 
					set(String.valueOf(valID), valName, valData);
				valData.clear();
				valID = 0;
				valName = null;
			} else switch (el.id) {
			case 1:
				this.idint = el.getInt();
				if (this.idint!=checkDictID)
					throw new InvalidPropertiesFormatException("Различные коды справочников");
				break;
			case 2:
				this.id = el.getStr();
				break;
			case 3:
				this.name = el.getStr();
				break;
			case 4:
			case 5:
			case 6:
				valsName[el.id-4] = el.getStr();
				break;
			case 10:
				if (!el.isArray())
					throw new InvalidPropertiesFormatException("Неверный формат справочника");
				break;	
			case 11:
				valID = el.getInt();
				break;
			case 12:	
				valName = el.getStr();
				break;
			case 14:
			case 15:
			case 16:
				if (valsName[el.id-14]!=null && !valsName[el.id-14].isEmpty())
					valData.put(valsName[el.id-14], el.value());
				break;
			}
	}
	
		


	
	@Override
	public Iterator<Map<String, Object>> iterator() {
		return data.iterator();
	}	
	
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}	
	
	public int intID() {
		return idint;
	}

	public void intID(int id) {
		this.idint = id;
	}	
	
    public int dateFrom() {
    	return from; 
    }    
	    
    public int dateTo() {
    	return to; 
    }    

    public boolean idStr() {
    	return isIDstr; 
    }   

    public boolean isSeq() {
    	return isSeq; 
    }   
        
    public void clear() {
	  	from = 0; 
	  	to = 0;
	  	id = null;
	  	idint = 0;
	  	name = null;
	  	isSeq = false;
	  	seqnum = 0;
	  	if (data!=null)
	  		data.clear();
    }
	
	public void copyFrom(PostDictionary source) {
		clear();
		if (source==null) return;
		this.data.addAll(source.data);
		this.from = source.from; 
		this.to = source.to;
		this.id = source.id;
		this.name = source.name;
		this.idint = source.idint;
	  	this.isIDstr = source.isIDstr;
	}	
	
    public boolean loaded() {
    	return (from>0)&&(to>=from); 
    }
	    
    public boolean loaded(int date) {
    	return (from>0)&&(to>=from)&&(date>=from)&&(date<=to); 
    }

	//Бинарный поиск
    protected FindResult binarySearch(String valID) {
		if (valID==null || valID.isEmpty()) 
			return null;
		int first = 0;
        int last = data.size()-1;
        int position = first + (last - first) / 2;
        Map<String, Object> v;
        Object s;
        int comp;
        while (first <= last) {
        	v = data.get(position);
        	if (v!=null) s = v.get("id"); 
        	else s = null;
        	if (s!=null) comp = valID.compareTo(s.toString());
        	else comp = 1;
        	if (comp==0) 
        		return new FindResult(true, position); //Найдено
        	else if (comp>0) 
        		first = position + 1;
        	else 
        		last = position - 1;
        	position = first + (last - first) / 2;
        }
        return new FindResult(false, first); //Не найдено		
	}		

	
	public Map<String, Object> set(String valID, Object valName, Map<String, Object> valData) {
		if (valID==null || valID.isEmpty()) return null;
		Map<String, Object> item = new LinkedHashMap<String, Object>();
		int seq;
    	if (!isIDstr) {
    		Integer id = new Integer(valID);
    		if (id<0 || id>=data.size()) return null;
    		item.put("id", id);
        	if (valName!=null) 
        		item.put("name", valName);
        	seq = Post.getInt(valData.get("seq"), -1);
        	if (seq < 0) {
        		seqnum++;
        		seq = seqnum;
        		valData.put("seq", seq);
        	} else if (seq > seqnum) {
        		seqnum = seq;
        		isSeq = true;
        	}
        	item.putAll(valData);
        	data.set(id, item);
    	} else {
    		FindResult res = binarySearch(valID);
    		if (res==null) return null; //Невозможно искать
    		if (res.ok) return data.get(res.index); //Найдено, повтор
    		item.put("id", valID);
        	if (valName!=null) item.put("name", valName);
        	item.putAll(valData);
        	data.add(res.index, item); //Добавление перед res
    	}
    	return item;
	}	

	public Map<String, Object> get(int valID) {
		if (isIDstr) return get(String.valueOf(valID));
		if ((valID<0)||(valID>=data.size())) return null;
		//Прямое позиционирование по индексу
		return data.get(valID);
	}
	
	public boolean contains(int valID) {
		return get(valID)!=null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> get(String valID) {
		if (!isIDstr) return get(Integer.valueOf(valID));
		FindResult res = binarySearch(valID);
		if (res==null || !res.ok) //Не найдено
			return null;
		return data.get(res.index);
	}		

	public ArrayList<Map<String, Object>> getInterval(String valID) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> it;
		if (!isIDstr) {
			it = get(Integer.valueOf(valID));
			if (it==null)
				return null;
			list.add(it);
			return list;
		}
		FindResult find = binarySearch(valID);
		if (find==null) //Невозможно искать 
			return null;
		int i;
		i = find.index;
		if (!find.ok) i--;
		if (i<0) //Перед первым 
			return null;
		list.add(data.get(i));
		i++;
		if (i>=data.size()) 
			return list; //Последний элемент
		list.add(data.get(i));
		return list;
	}	
	
	public boolean contains(String valID) {
		return get(valID)!=null;
	}	
	
	public ArrayList<Map<String, Object>> getByID() {
		if (isIDstr) return data;
		else {
			ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> el: data) 
				if (el!=null) list.add(el);
			list.sort(new Post.sortID());
			return list;
		} 
	}	
	
	public ArrayList<String> getIDlist() {
		ArrayList<String> list = new ArrayList<String>();
		Object v;
		for (Map<String, Object> el: data) 
			if (el!=null) {
				v = el.get("id");
				if (v!=null && !v.toString().isEmpty())
					list.add(v.toString());
			}
		return list;
	}		
	
	public ArrayList<Map<String, Object>> getByName() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (isIDstr) list.addAll(data);
		else for (Map<String, Object> el: data)	if (el!=null) list.add(el);
		list.sort(new Post.sortName());
		return list;
	}	
	
	public ArrayList<Map<String, Object>> getBySeq() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (isIDstr) list.addAll(data);
		else for (Map<String, Object> el: data)	if (el!=null) list.add(el);
		list.sort(new Post.sortSeq());
		return list;
	}	
	
	public ArrayList<Map<String, Object>> getList(boolean byName) {
		if (byName)
			return getByName();
		else if (isSeq)
			return getBySeq();
		else
			return getByID();
	}		
	
    public void loadBegin(boolean idStr, int maxID) {
    	clear();
	  	this.isIDstr = idStr;
	  	if (!isIDstr) {
	  		data.ensureCapacity(maxID+1);
	  		for (int i = 0; i<=maxID; i++) data.add(null);
	  	}	
    }
    
    public void loadBegin() {
    	loadBegin(true,0);
    }
    
    public void loadBegin(int maxID) {
    	loadBegin(false,maxID);
    }
    
    
    public void loadEnd(int dateFrom, int dateTo) {
	  	this.from = dateFrom; 
	  	this.to = dateTo;
    }
    
    public void loadEnd() {
    	loadEnd(20000101, 29991231);
    }
    
    public void loadFrom(ArrayList<Map<String, Object>> source, boolean idStr) {
    	clear();
    	this.isIDstr = idStr;
    	if (source == null) return;
    	int maxID = 0;
    	int valid;
    	String id;
    	if (!idStr) for (Map<String, Object> item: source) {
   	    	valid = Post.getInt(item.get("id"), -1);
   	    	if (valid > maxID) maxID = valid;
    	}
    	loadBegin(idStr, maxID);
    	try {
    		for (Map<String, Object> item: source) {
    			id = Post.getStrNull(item.get("id"));
    			if (id == null) continue;
    			set(id, item.get("name"), item);
    		}
    	} finally {
    		loadEnd();
    	}
	  	 
	  	
    }
        
    
    public String getName(int valID) {
    	Map<String, Object> v = get(valID);
       	if (v!=null) return Post.killDublSpace(Post.getStrEmpty(v.get("name")));
    	return null;
    }  	
    
    public String getName(String valID) {
    	Map<String, Object> v = get(valID);
    	if (v!=null) return Post.killDublSpace(Post.getStrEmpty(v.get("name")));
    	return null;
    }
    
	public String getName(int valID, String mask) {
		Map<String, Object> v = get(valID);
		if (v==null)
			return String.valueOf(valID);
		String n = Post.getStrNull(v.get("name"));
		if (n==null || n.isEmpty())
			return String.valueOf(valID);
		n = Post.killDublSpace(n.trim());
		if (n.isEmpty())
			return String.valueOf(valID);
		if (mask==null)
			return n;
		return String.format(mask, n, valID);
	}
	
	public String getName(String valID, String mask) {
		Map<String, Object> v = get(valID);
		if (v==null)
			return valID;
		String n = Post.getStrNull(v.get("name"));
		if (n==null || n.isEmpty())
			return valID;
		n = Post.killDublSpace(n.trim());
		if (n.isEmpty())
			return valID;
		if (mask==null)
			return n;
		return String.format(mask, n, valID);
	}	

	public Map<String, Object> toMap(boolean withData) {
		Map<String, Object> res = new LinkedHashMap<String, Object>();
		res.put("id", id);
		res.put("name", name);
		res.put("id_string", isIDstr);
		res.put("idcode", idint);
		if (withData)
			res.put("data", data);
		return res; 
	}
}
