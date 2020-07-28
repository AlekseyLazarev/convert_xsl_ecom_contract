package caits.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Свойства почтовой организации
public class PostOfficeProperty {
	
	public enum PostOfficeParamType {
		INT,
		BOOL,
		STR,		
		ARRAY_INT
	}
	
	public enum PostOfficeParam {
		INDEX("index", "Индекс", false, PostOfficeParamType.INT),
		USE("tp", "Назначение объекта", false, PostOfficeParamType.INT),
		NAME("name", "Наименование", false, PostOfficeParamType.STR),
		REGION("region", "Регион", false, PostOfficeParamType.INT),
		REGID("regid", "Код региона", false, PostOfficeParamType.INT),
		REGIONID("regionid", "Код ГАР региона", false, PostOfficeParamType.STR),
		REGIONO("regionido", "Логист.код региона ", false, PostOfficeParamType.INT),
		DISTRICT("district", "Район", false, PostOfficeParamType.INT),
		DISTRICTID("distrid", "Код ГАР района", false, PostOfficeParamType.STR),
		DISTRICTO("distrido", "Логист.код района", false, PostOfficeParamType.INT),
		PLACE("place", "Насел.пункт", false, PostOfficeParamType.INT),
		PLACEID("placeid", "Код ГАР насел.пункта", false, PostOfficeParamType.STR),
		PLACEO("placeido", "Логист.код насел.пункта", false, PostOfficeParamType.INT),
		PARENT("parent", "Подчинение", false, PostOfficeParamType.INT),
		HARD("hard", "Труднодоступный", false, PostOfficeParamType.BOOL),
		AVIA_PORT("aviaport", "Транспортный узел", false, PostOfficeParamType.ARRAY_INT),
		TYPE("type", "Тип объекта ПС", false, PostOfficeParamType.INT),
		PARTNER("partner", "Партнерский", false, PostOfficeParamType.BOOL),
		PVZ("pvz", "ПВЗ", false, PostOfficeParamType.BOOL),
		LIMIT("combo", "Есть ограничения по наземной пересылке", false, PostOfficeParamType.INT),
		CLOSED("closed", "Есть ограничения по доставке", false, PostOfficeParamType.INT),
		ITEM_OUT_PATH("item-out-path", "Частичная выдача", false, PostOfficeParamType.BOOL),
		ITEM_CHECK_WORK("item-check-work", "Проверка работоспособности", false, PostOfficeParamType.BOOL),
		ITEM_CHECK_MEN("item-check-men", "Возможность примерки", false, PostOfficeParamType.BOOL),
		ITEM_CHECK_VIEW("item-check-view", "Проверка вложений", false, PostOfficeParamType.BOOL),
		MOVE("move", "Является доставочным", false, PostOfficeParamType.BOOL),
		WORK_BEGIN("work-begin", "Время начала работы", true, PostOfficeParamType.INT),
		WORK_END("work-end", "Время окончания работы", true, PostOfficeParamType.INT),
		WEIGHT_MAX("weight-max", "Максимальный вес", false, PostOfficeParamType.INT),
		PACK_MAX("pack-max", "Максимальный размер", false, PostOfficeParamType.INT), 
		PAY_CARD("pay-card", "Оплата картой", false, PostOfficeParamType.BOOL), 
		PAY_MONEY("pay-money", "Оплата наличными", false, PostOfficeParamType.BOOL),
		WORK_COUNT("work-count", "Количество выходных", true, PostOfficeParamType.INT),
		CUT_OFF("cutoff", "Время обмена", false, PostOfficeParamType.INT),
		BOX_SERVICE("box", "Услуга BOX-сервис", false, PostOfficeParamType.BOOL); 
		
		public final String name;
		public final String desc;
		public final boolean funct;
		public final PostOfficeParamType type;
		
		PostOfficeParam(String name, String desc, boolean funct, PostOfficeParamType type) {
			this.name = name;
			this.desc = desc;
			this.funct = funct;
			this.type = type;
		}
		
		public static PostOfficeParam getByParam(String param) {
			if (param == null || param.isEmpty()) 
				return null;
			for (PostOfficeParam p : PostOfficeParam.values())
				if (p.name.equalsIgnoreCase(param))
					return p;
			return null;
		}		
		
		

	}
	
	public enum PostOfficePlaceType {
		OTHER(0, "Другое"),
		FROM(1, "Место приема"),
		TO(2, "Место вручения"),
		TRANSFER(3, "Место перегрузки");
		
		public final short code;
		public final String desc;

		PostOfficePlaceType(int code, String desc) {
			this.code = (short) code;
			this.desc = desc;
		}
		
		public static PostOfficePlaceType get(int val) {
			for (PostOfficePlaceType t : PostOfficePlaceType.values())
				if (t.code == val)
					return t;
			return OTHER;
		}		
	}		
	
	public enum PostLimitType {
		NONE(0, "", 0),
		COMBOAVIA(1, "АВИА", 3),
		COMBOGROUND(2, "НАЗЕМНО", 2),
		COMBOALL(3, "АВИА И НАЗЕМНО", 4),
		CLOSEDAVIA(4, "ТОЛЬКО АВИА", 1),
		CLOSEDALL(5, "ПОЛНЫЙ ЗАПРЕТ", 1);

		public final short id;
		public final String desc;
		public final short code;

		PostLimitType(int id, String desc, int code) {
			this.id = (short) id;
			this.desc = desc;
			this.code = (short) code;
		}

		public static PostLimitType get(int val) {
			for (PostLimitType t : PostLimitType.values())
				if (t.id == val)
					return t;
			return NONE;
		}

		public static PostLimitType get(String val) {
			if (val == null || val.isEmpty()) return NONE;
			else try {
				return get(Integer.parseUnsignedInt(val));
			} catch (NumberFormatException е) {
				return NONE;
			}
		}

		public String toString(String mask) {
			return String.format(mask, code, desc, id);
		}

		@Override
		public String toString() {
			return String.format("%1d: %s", code, desc);
		}		
	}

	public enum PostAddrType {
		NONE(0),
		REGION(1),
		DISTRICT(2),
		PLACE(3);

		public final short id;

		PostAddrType(int id) {
			this.id = (short) id;
		}

		public static PostAddrType get(int val) {
			for (PostAddrType t : PostAddrType.values())
				if (t.id == val)
					return t;
			return NONE;
		}

		public static PostAddrType get(String val) {
			if (val == null || val.isEmpty()) return NONE;
			else try {
				return get(Integer.parseUnsignedInt(val));
			} catch (NumberFormatException е) {
				return NONE;
			}
			
		}
		
		@Override
		public String toString() {
			return String.valueOf(id);
		}

	}

	// Режим работы ОПС
	static public class PostOfficeWorkDay {
		final public int date;
		final public int weekDay;
		final public int timeBegin;
		final public int timeEnd;
		
		public PostOfficeWorkDay(int date, int weekDay, int timeBegin, int timeEnd) {
			this.date = date;
			this.weekDay = weekDay;			
			this.timeBegin = timeBegin;
			this.timeEnd = timeEnd;
			
		}
		
		@Override
		public String toString() {
			return "[" + String.valueOf(weekDay) + "] " + Post.getTime(timeBegin) + "-" +Post.getTime(timeEnd);
		}
		
	}
	
	// Ограничение по доставке
	public class PostOfficeLimit {
		public int period;
		public PostLimitType type;
		public int baserate;
		public int basecoeff;
		public int transfcnt;
		public int transfer;
		public int ratezone;
		public int dateClosed;

		public PostOfficeLimit() {
			super();
			clear();
		}

		public void clear() {
			period = 0;
			type = PostLimitType.NONE;
			baserate = 0;
			basecoeff = 0;
			transfcnt = 0;
			transfer = 0;
			ratezone = 0;
			dateClosed = 0;
		}
		
		public void copy(PostOfficeLimit source) {
			if (source==null) {
				clear();
				return;
			}
			period = source.period;
			type = source.type;
			baserate = source.baserate;
			basecoeff = source.basecoeff;
			transfcnt = source.transfcnt;
			transfer = source.transfer;
			ratezone = source.ratezone;
			dateClosed = source.dateClosed;
		}
	}
	
	//Интерфейс загрузки ОПС
	public interface PostOfficeLoaded {
		public PostOfficeProperty loadPOdata(int PO, int date);
		public boolean loadPOworkdate(PostOfficeProperty po, int date, int weekDay);
		public String getTransUnitName(int transunit);
        public PostOfficePropertys usePostOffice();
		public void useClearPostOffice();
	}

	protected PostOfficeLoaded loadPO;
	//Дата, для которой данные
	public int lastDate;
	public int PO;
	//Сущесвует
	public boolean isOK;
	//доставочное
	public boolean isPostMove;	
	public String name;
	public long region;
	public long regiono;
	public UUID regionid;
	public int regid;
	public long district;
	public long districto;
	public UUID districtid;
	public long place;
	public long placeo;
	public UUID placeid;
	public int parent;
	public ArrayList<Integer> transunit;
	public PostOfficePlaceType use;
	//труднодоступное
	public boolean hard;
	public PostAddrType addrType;
	public PostOfficeLimit limit;
	public int limitActual;
	public int limitClosed;
	public int limitLoaded;
	//Партнерское
	public boolean isPartner;
	//Есть ПВЗ
	public boolean isPVZ;
	//Тип объекта
	public int type;
	//Проверка работоспособности
	public boolean isItemCheckWork;
	//Примерка
	public boolean isItemCheckMen;
	//С проверкой вложения
	public boolean isItemCheckView;
	//Частичная выдача
	public boolean isItemOutPath;
	//Максимальный вес
	public int maxWeight;
	//Максимальный размер
	public int maxSize;
	//Оплата картой
	public boolean isPayCard;
	//Оплата наличными
	public boolean isPayMoney;
	//Режим работы считан
	public Map<Integer, PostOfficeWorkDay> workDays;
	//CutOff
	public int cutoff;

	public PostOfficeProperty() {
		super();
		this.loadPO = null;
		transunit = new ArrayList<Integer>();
		clear();
	}

	public PostOfficeProperty(PostOfficeLoaded loadPO) {
		this();
		this.loadPO = loadPO;
	}

	public PostOfficeProperty loadPOdata(int PO, int date) {
		if (loadPO == null) return null;
		PostOfficeProperty res = loadPO.loadPOdata(PO, date);
		res.lastDate = date;
		return res;
	}
	
	public PostOfficeLoaded getLoadPO() {
		return loadPO;
	}
	
	public String limitClosedFrom() {
		if (limitClosed>0)
			return Post.getPeriodLong(limitClosed / 10000);
		else
			return new String();
	}
	
	public String limitClosedTo() {
		if (limitClosed>0)
			return Post.getPeriodLong(limitClosed % 10000);
		else
			return new String();
	}		

	@Override
	public String toString() {
		return String.format("%1d %s", PO, name);
	}
	
	public String getTransUnitName(int transUnitID) {
		if (loadPO == null) return null;
		return loadPO.getTransUnitName(transUnitID);
	}


	public PostOfficeLimit limitNew() {
		limit = new PostOfficeLimit();
		return limit;
	}

	public void limitClear() {
		limit = null;
	}

	public void clear() {
		PO = 0;
		isOK = false;
		isPostMove = false;
		region = 0;
		regiono = 0;
		regionid = null;
		regid = 0;
		district = 0;
		districto = 0;
		districtid = null;
		place = 0;
		placeo = 0;
		placeid = null;		
		parent = 0;
		transunit.clear();
		hard = false;
		limit = null;
		limitActual = 0;
		limitClosed = 0;
		limitLoaded = 0;
		addrType = PostAddrType.NONE;
		isPartner = false;
		isPVZ = false;
		type = 0;
		isItemCheckWork = false;
		isItemCheckMen = false;
		isItemCheckView = false;
		isItemOutPath = false;	
		workDays = null;
		maxWeight = 0;
		maxSize = 0;
		isPayCard = false;
		isPayMoney = false;
		cutoff = 0;
		use = PostOfficePlaceType.OTHER;
	}
	
	public void copy(PostOfficeProperty source) {
		if (source==null) {
			clear();
			return;
		}		
		PO = source.PO;
		isOK = source.isOK;
		isPostMove = source.isPostMove;
		region = source.region;
		regiono = source.regiono;
		if (source.regionid!=null)
			regionid = new UUID(source.regionid.getMostSignificantBits(), source.regionid.getLeastSignificantBits());
		else
			regionid = null;
		regid = source.regid;		
		district = source.district;
		if (source.districtid!=null)
			districtid = new UUID(source.districtid.getMostSignificantBits(), source.districtid.getLeastSignificantBits());
		else
			districtid = null;
		districto = source.districto;
		place = source.place;
		if (source.placeid!=null)
			placeid = new UUID(source.placeid.getMostSignificantBits(), source.placeid.getLeastSignificantBits());
		else
			placeid = null;
		placeo = source.placeo;
		parent = source.parent;
		transunit.clear();
		transunit.addAll(source.transunit);
		hard = source.hard;
		if (source.limit==null)
			limit = null;
		else
			limit.copy(source.limit);
		limitActual = source.limitActual;
		limitClosed = source.limitClosed;
		limitLoaded = source.limitLoaded;
		addrType = source.addrType;
		isPartner = source.isPartner;
		isPVZ = source.isPVZ;
		type = source.type;
		use = source.use; 
		isItemCheckWork = source.isItemCheckWork;
		isItemCheckMen = source.isItemCheckMen;
		isItemCheckView = source.isItemCheckView;
		isItemOutPath = source.isItemOutPath;	
		workDays = source.workDays;
		maxWeight = source.maxWeight;
		maxSize = source.maxSize;
		isPayCard = source.isPayCard;
		isPayMoney = source.isPayMoney;
		cutoff = source.cutoff;
	}	

	public Object getData(PostOfficeParam param) {
		if (param==null)
			return null;
		switch (param) {
			case INDEX:
				return PO;
			case USE:
				return use.code;
			case NAME:
				return name;				
			case REGION:
				return region;
			case REGIONID:
				return Post.encodeUUID(regionid);
			case REGID:
				return regid;
			case REGIONO:
				return regiono;
			case DISTRICT:
				return district;
			case DISTRICTID:
				return Post.encodeUUID(districtid);
			case DISTRICTO:
				return districto;
			case PLACE:
				return place;
			case PLACEID:
				return Post.encodeUUID(placeid);
			case PLACEO:
				return placeo;
			case PARENT:
				return parent;
			case HARD:
				return (hard ? 1 : 0);
			case AVIA_PORT: 
				return transunit;
			case TYPE:
				return type;
			case PARTNER:
				return (isPartner ? 1 : 0);
			case PVZ:
				return (isPVZ ? 1 : 0);
			case LIMIT:
				if (limit==null) return 0;
				else switch (limit.type) {
				case COMBOAVIA:
				case COMBOGROUND:
				case COMBOALL:
					return limit.period;
				default:
					return 0;
				}
			case CLOSED:
				return limitClosed;
			case ITEM_OUT_PATH:
				return (isItemOutPath ? 1 : 0);
			case ITEM_CHECK_WORK:
				return (isItemCheckWork ? 1 : 0);
			case ITEM_CHECK_MEN:
				return (isItemCheckMen ? 1 : 0);
			case ITEM_CHECK_VIEW:
				return (isItemCheckView ? 1 : 0);
			case MOVE:
				return (isPostMove ? 1 : 0);
			case WORK_BEGIN:
				return workDayBegin(lastDate);
			case WORK_END:				
				return workDayEnd(lastDate);
			case WEIGHT_MAX:
				return maxWeight;
			case PACK_MAX:
				return maxSize;
			case PAY_CARD:
				return (isPayCard ? 1 : 0);
			case PAY_MONEY:
				return (isPayMoney ? 1 : 0);
			case WORK_COUNT:
				return workDayCount(lastDate, lastDate, 0);
			case CUT_OFF:
				return cutoff;
			default:
				return 0;
		}
	}

	public Object getData(String paramName) {
		return getData(PostOfficeParam.getByParam(paramName));
	}

	public long getDataInt(String paramName, long defVal) {
		PostOfficeParam p = PostOfficeParam.getByParam(paramName);
		Object v = getData(p);
		if (v==null)
			return defVal;
		switch (p.type) {
		case STR:
			return defVal;
		case ARRAY_INT:
			switch (p) {
			case AVIA_PORT: 
				if (transunit.size()>0)
					return (long) transunit.get(0);
				else
					return defVal;
			default:
				return defVal;
			}	
		default:
			return ((Number)v).longValue();
		}
	}

	
	@Deprecated
	public long getDataInt(String paramName, long defVal, int indexElement) throws ArrayIndexOutOfBoundsException {
		PostOfficeParam p = PostOfficeParam.getByParam(paramName);
		Object v = getData(p);
		if (v==null)
			return defVal;
		switch (p.type) {
		case STR:
			return defVal;
		case ARRAY_INT:
			switch (p) {
			case AVIA_PORT: 
				if (indexElement >= transunit.size())
					throw new ArrayIndexOutOfBoundsException("Неверный индекс элемента при получении аэропорта почтового объекта.");
				return (long) transunit.get(indexElement);
			default:
				return defVal;
			}	
		default:
			return ((Number)v).longValue();
		}
	}
	
	@Deprecated
	public int getDataIntCount(String paramName) {
		PostOfficeParam p = PostOfficeParam.getByParam(paramName);
		if (p==null)
			return 0;
		switch (p.type) {
		case ARRAY_INT:
			switch (p) {
			case AVIA_PORT:
				return transunit.size();
			default:
				return 1;
			}
		default:
			return 1;
		}	
	}

	public void setData(String paramName, long val) {
		PostOfficeParam p = PostOfficeParam.getByParam(paramName);
		if (p==null || p.funct)
			return;		
		switch (p) {
		case INDEX:
			PO = (int) val;
			break;
		case USE:
			use = PostOfficePlaceType.get((int) val); 
						
		case REGION:
			region = val;
			break;
		case REGIONO:
			regiono = val;
			break;
		case DISTRICT:
			district = val;
			break;
		case DISTRICTO:
			districto = val;
			break;
		case PLACE:
			place = val;
			break;
		case PLACEO:
			placeo = val;
			break;
		case PARENT:
			parent = (int) val;
			break;
		case HARD:
			hard = val != 0;
			break;
		case AVIA_PORT:
			if (transunit.indexOf((int) val) == -1)
				transunit.add((int) val);
			break;
		case REGID:
			regid = (int) val;
			break;
		case TYPE:
			type = (int) val;
			break;
		case PARTNER:
			isPartner = val != 0;
			break;
		case PVZ:
			isPVZ = val != 0;
			break;
		case ITEM_OUT_PATH:
			isItemOutPath = val != 0;
			break;
		case ITEM_CHECK_WORK:
			isItemCheckWork = val != 0;
			break;
		case ITEM_CHECK_MEN:
			isItemCheckMen = val != 0;
			break;
		case ITEM_CHECK_VIEW:
			isItemCheckView = val != 0;
			break;
		case MOVE:
			isPostMove = val != 0;
			break;
		case WEIGHT_MAX:				
			maxWeight = (int) val;
			break;
		case PACK_MAX:				
			maxSize = (int) val;
			break;
		case PAY_CARD:				
			isPayCard = val != 0;
			break;
		case PAY_MONEY:				
			isPayMoney = val != 0;
			break;
		case CUT_OFF:
			cutoff = (int) val;
			break;
		default:
		}
	}

	public String toString(String mask, String selector, String description, int date) {
		Object v;
		String res = new String();
		String des;
		String val;
		String vali;
		String json;
		if (description == null)
			des = new String();
		else
			des = description.trim();
		boolean fl = false;
		for (PostOfficeParam p: PostOfficeParam.values()) if (!p.funct) {

			switch (p.type) {
			case INT:
			case BOOL:
				v = getData(p);
				if (v == null || ((Number) v).intValue()==0)
					continue;
				val = v.toString();
				/*long vi = getData(p, 0); 
				if (vi==0)
					continue;
				val = String.valueOf(vi);*/
				json = val;
				break;
			case STR:
				switch (p) {
				case REGIONID:
					v = regionid;					
					break;
				case DISTRICTID:
					v = districtid;					
					break;
				case PLACEID:
					v = placeid;					
					break;
				default:	
					v = getData(p); 
				}
				if (v == null || ((String) v).isEmpty())
					continue;
				val = v.toString();
				json = "\"" + val + "\"";
				break;
			case ARRAY_INT: 
				switch (p) {
				case AVIA_PORT:
					val = null;
					for (int tu : transunit) {
						if (val == null)
							val = new String();
						else
							val = val + ",";
						vali = getTransUnitName(tu);
						if (vali == null)
							val = val + String.valueOf(tu);
						else
							val = val + "\"" + vali + "\"";
					}
					if (val == null)
						continue;
					json = "[" + val + "]";
					break;
				default:
					continue;
				}
			default:
				continue;
			}	
			if (fl)
				res = res + selector;
			fl = selector != null;          // 1       2     3    4    5 
			res = res + String.format(mask, p.name, p.desc, val, des, json);
		}
		return res;
	}

	public PostLimitType getLimitType() {
		if (limit == null)
			return PostLimitType.NONE;
		else
			return limit.type;
	}

	public PostLimitType getLimitType(int date) {
		if (date <= 0)
			return PostLimitType.NONE;
		int dt = date % 10000;
		if (limitClosed != 0) {
			if ((dt >= (limitClosed / 10000)) && (dt <= (limitClosed % 10000)))
				return PostLimitType.CLOSEDALL;
		} else if (limit != null && limit.type != PostLimitType.NONE) {
			if ((dt >= (limit.period / 10000)) && (dt <= (limit.period % 10000)))
				return limit.type;
		}
		return PostLimitType.NONE;
	}

	public boolean getLimitCheckLoading(int date) {
		return (limitLoaded != 0) && (limitLoaded != date);
	}
	/*
	public PostOfficeProperty.PostLimitType  getLimitFromDate(int date) throws TariffException {
		if (!isMove)
			return PostOfficeProperty.PostLimitType.NONE;
		PostOfficeProperty.PostLimitType res = getLimitType(date);
		if (res==PostOfficeProperty.PostLimitType.NONE)
			return res;
		if (limitClosed > 0) {
			out.transType = TariffTransType.CLOSED;
			out.transTypeName = "Доставка " + (!fromto ? "из" : "в") + " " + PO + " " + po.name
					+ " в период с " + Post.getPeriodLong(po.limitClosed / 10000) + " по "
					+ Post.getPeriodLong(po.limitClosed % 10000) + " запрещена.";
			if (!in.calculateClosed) { 
				// Запрет считать запрещенную доставку
				error.setErrorCode(403);
				error.add(out.transTypeName, 1350);
			}
		}	
		return res;
	}
	*/		

	public String getPostOfficeName(boolean withIndex) {
		if (!withIndex) {
			if (name == null)
				return new String();
			else
				return name;
		} else {
			if (PO == 0)
				return new String();
			else if ((name == null) || (name.isEmpty()))
				return String.valueOf(PO);
			else
				return PO + " \"" + name + "\"";
		}
	}

	public String getPostOfficeName(int PO, int date, boolean withIndex) {
		PostOfficeProperty res = loadPOdata(PO, date);
		if (res != null)
			return res.getPostOfficeName(withIndex);
		else
			return new String();
	}
	
	public String toJSON() {
		return toString("\"%1$s\":%5$s", ",", null, 0);
	}
	
	public void workDaySet(PostOfficeWorkDay val) {
		if (val==null)
			return;
		if (workDays==null)
			workDays = new HashMap<Integer, PostOfficeWorkDay>();
		workDays.put(val.date*10 + val.weekDay, val);
	}	
	
	public void workDaySet(int date, int weekDay, int timeBegin, int timeEnd) {
		if (date>0)
			workDaySet(new PostOfficeWorkDay(date, weekDay, timeBegin, timeEnd));
	}		
	
	public PostOfficeWorkDay getWorkDay(int date, int weekDay) {
		PostOfficeWorkDay res = null;
		if (date<=0)
			return null;		
		if (workDays!=null)
			res = workDays.get(date*10 + weekDay);
		if (res!=null || loadPO == null || !loadPO.loadPOworkdate(this, date, weekDay))
			return res;
		if (workDays!=null)
			return workDays.get(date*10 + weekDay);
		return null;
	}
	
	public PostOfficeWorkDay getWorkDay(int date) {
		if (date<=0)
			return null;
		int w = Post.decodeDate(date).get(Calendar.DAY_OF_WEEK) - 1;
		if (w<=0) w = 7;
		return getWorkDay(date, w);
	}
	
	public int workDayBegin(int date) {
		PostOfficeWorkDay res = getWorkDay(date);
		if (res!=null) 
			return res.timeBegin;
		else
			return 0;
	}
	
	public int workDayEnd(int date) {
		PostOfficeWorkDay res = getWorkDay(date);
		if (res!=null) 
			return res.timeEnd;
		else
			return 0;
	}
	
	public int workDayCount(int date, int dayDate, int dayTime) {
		int cnt = 0;
		int w = Post.decodeDate(dayDate).get(Calendar.DAY_OF_WEEK) - 1;
		if (w<=0) w = 7;
		PostOfficeWorkDay res;
		do {
			res = getWorkDay(date, w);
			if (res!=null && res.timeBegin<res.timeEnd) {
				//Рабочий день
				if (cnt>0 || dayTime<=res.timeEnd) //Если день не первый или в первый день время до конца работы, то возврат
					return cnt;
			}
			cnt++;
			w++;
			if (w>7)
				w = 1;
		} while (cnt<7);
		return -1;
	}


}
