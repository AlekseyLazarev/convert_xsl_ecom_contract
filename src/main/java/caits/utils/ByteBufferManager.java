package caits.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InvalidPropertiesFormatException;

public class ByteBufferManager {
	
	final ByteBuffer data;
	private Charset fcharset;
	private int fsize;

	public ByteBufferManager(ByteBuffer data) {
		this.data = data;
		this.fsize = 0;
	}
	
	public ByteBufferManager(ByteBuffer data, int size, Charset charset) {
		this.data = data;
		this.fcharset = charset;
		this.fsize = size;
		data.rewind();
	}	
	
	public Charset charset() {
		return fcharset;
	}
	
	public void charset(Charset charset) {
		this.fcharset = charset;
	}	
	
	public void charset(String charset) {
		this.fcharset = Charset.forName(charset);
	}	
	
	public int size() {
		return fsize;
	}	
	
	public short getByte() {
		if ((data.position()+1)>=fsize)
			return 0;
		return (short) (data.get() & 0xff);
	}
	
	public long getNum(int size) {
		if ((data.position() + size)>=fsize) 
			size = fsize - data.position();		
		if (size<=0) 
			return 0;
		byte[] v = new byte[size];
		data.get(v);
		int val = (v[0] & 0xff);
		int m = 0;
		for (int i=1; i<size; i++) {
			m = m + 8;
			val = val | ((int)(v[i] & 0xff)<<m);
		}	
		return val;  
	}
	
	public int getInt() {
		return (int)getNum(4);
	}

	public long getLong() {
		return (int)getNum(8);
	}
	
	public String getStrLength(int lengthSize, Charset charset) {
		int size = (int)getNum(lengthSize);
		if (size<=0) 
			return new String();		
		if ((data.position() + size)>=fsize) 
			size = fsize - data.position();
		if (size<=0) 
			return new String();
		byte[] str = new byte[size];
		data.get(str);
		return new String(str, charset);
	}
	
	public String getStrLength() {
		return getStrLength(4, fcharset);
	}	

	public String getStrFixed(int size) {
		if (size <=0 ) return new String();
		byte[] str = new byte[size];
		data.get(str);
		int sz = size;
		for (int i=0; i<size; i++)
			if (str[i]==0) {
				sz = i;
				break;
			}
		return new String(str, 0, sz, fcharset);
	}
	
	public int positionAdd(int delta) {
		int pos = data.position();
		int newpos = pos + delta;
		if (newpos>=fsize)
			newpos = fsize - pos;
		data.position(newpos);
		return pos;
	}
	
	public int position() {
		return data.position();
	}
	
	public int position(int position) {
		int pos = data.position();
		if (position>=fsize)
			position = fsize;
		data.position(position);
		return pos;
	}	
	
	public static String decodeBarcode(long data) {
		return String.valueOf(data);
	}
	
	public static Calendar decodeDateTime(long data) {
		if (data==0)
			return null;
		
		int sec = (int)(data % 60);
		int v = (int)(data / 60);
		int min = v % 60;
		v = v / 60;
		int hour = v % 24;
		v = v / 24;
		int day = v % 366;
		v = v / 366 + 2000;
		
		Calendar res = new GregorianCalendar();
		res.set(Calendar.YEAR, v);
		res.set(Calendar.DAY_OF_YEAR, day);
		res.set(Calendar.HOUR_OF_DAY, hour);
		res.set(Calendar.MINUTE, min);
		res.set(Calendar.SECOND, sec);
		return res;
	}	
	
	public static Calendar decodeDate(int data) {
		if (data==0)
			return null;		
		int y = data / 366;
		data = data - y*366;
		Calendar res = new GregorianCalendar();
		res.set(Calendar.YEAR, y+2000);
		res.set(Calendar.DAY_OF_YEAR, data);
		return res;
	}
	
	public static Calendar decodeTime(int data) {
		if (data==0)
			return null;		
		data--;
		int hour = data / 60;
		int min = data - hour*60;
		Calendar res = new GregorianCalendar();
		res.set(Calendar.HOUR_OF_DAY, hour);
		res.set(Calendar.MINUTE, min);
		return res;
	}	
	
	public static double decodeDouble(long data) {
		return data;
	}
	
	public static class Head {
		public short sign;
		public short id;
		public Charset charset;
		public int sizeBody;
		public int sizeHead;
		public int sizeFull;
		public int position_first;
		public int position_last;

		
		public Head() {
			super();
			clear();
		}	
		
		public void clear() {
			sign = 0;
			id = 0;
			charset = null;
			sizeFull = 0;
			sizeBody = 0;
			sizeHead = 0;
		}		
		
		public void read(ByteBufferManager source) throws InvalidPropertiesFormatException {
			position_first = source.position();
			sign = source.getByte();
			if (sign!=0x8b)
				throw new InvalidPropertiesFormatException("Непподерживаемый тип блока.");
			id = source.getByte();
			short ch = source.getByte();			
			if (ch!=0)
				throw new InvalidPropertiesFormatException("Шифрование и упаковка не поддерживается");
			ch = source.getByte();
			switch (ch) {
			case 1:
				charset = Charset.forName("windows-1251");
				break;
			case 200: 
				charset = Charset.forName("UTF-8");
				break;
			default:
				charset =null;				
			}
			source.positionAdd(4);
			sizeBody = source.getInt();
			source.positionAdd(8);
			sizeHead = 20;
			sizeFull = sizeBody + sizeHead;
			position_last = position_first + sizeFull;
		}
	}
	
	public enum ElementType {
		VALUE_NULL,
		VALUE_STR,
		VALUE_INT,
		VALUE_LONG,
		VALUE_BOOLEAN,
		VALUE_DOUBLE,
		VALUE_DATETIME,
		VALUE_DATE,
		VALUE_TIME,
		VALUE_BINARY,
		VALUE_BARCODE,
		ARRAY,
		ARRAYEXT,
		SEPARATOR
	}
	
	public static class Element {
		public short id;
		protected long data;
		protected int size;
		protected String str;
		public ElementType type;
		
		public Element() {
			super();
			clear();
		}		
		
		public void clear() {
			type = null;
			id = 0;
			data = 0;
			size = 0;	
			str = null;
		}
		
		public void read(ByteBufferManager source, Head head) throws InvalidPropertiesFormatException {
			clear();
			int pos = source.data.position();
			if (pos>=source.fsize || pos>=head.position_last) 
				return;
			short macrotype = source.getByte(); 
			if (macrotype==255) {
				type = ElementType.SEPARATOR;
				return;
			}
			if (source.data.position()>=source.fsize) 
				return;			
			id = source.getByte();
			if (macrotype>=1 && macrotype<=230) { 
				str = source.getStrFixed(macrotype);
				type = ElementType.VALUE_STR;
			} else if (macrotype>=232 && macrotype<=239) {
				size = (int)source.getNum(2);
				data = (long)source.positionAdd(size);
				type = ElementType.VALUE_BINARY;
			} else switch (macrotype) {
			case 0: 
				type = ElementType.VALUE_NULL;
				break;
			case 231: 
				str = source.getStrLength(2, head.charset);
				type = ElementType.VALUE_STR;
				break;
			case 240:
				data = 1;
				type = ElementType.VALUE_BOOLEAN;
				break;
			case 241:
				data = source.getNum(1);
				type = ElementType.VALUE_INT;
				break;
			case 242:
				data = source.getNum(2);
				type = ElementType.VALUE_INT;
				break;
			case 243:
				data = source.getNum(3);
				type = ElementType.VALUE_INT;
				break;
			case 244:
				data = source.getNum(4);
				type = ElementType.VALUE_INT;
				break;	
			case 245:
				data = source.getNum(8);
				type = ElementType.VALUE_LONG;
				break;	
			case 246:
				//Штрихкод
				str = ByteBufferManager.decodeBarcode(source.getNum(7));
				type = ElementType.VALUE_BARCODE;
				break;
			case 247:
				data = source.getNum(4);
				type = ElementType.VALUE_DATETIME;				
			case 248:
				data = source.getNum(2);
				type = ElementType.VALUE_DATE;
				break;
			case 249:
				data = source.getNum(2);
				type = ElementType.VALUE_TIME;
				break;				
			case 250:
				data = source.getNum(8);
				type = ElementType.VALUE_DOUBLE;
				break;
			case 251:
			case 252:
			case 254:
				data = macrotype-250;
				size = (int)source.getNum((int)data);
				type = ElementType.ARRAY;
				break;				
			case 253:				
				size = (int)source.getNum(4);
				type = ElementType.ARRAYEXT;				
				break;				
			}
		}
		
		public int getArrayCount() {
			switch (type) {
			case ARRAY:
			case ARRAYEXT:
				return (int)size;
			default:	
				return 0;
			}	
		}
		
		public boolean isArrayExt() {
			return type==ElementType.ARRAYEXT;
		}
		
		public boolean isArray() {
			return type==ElementType.ARRAY || type==ElementType.ARRAYEXT;
		}			

		public int getInt() {
			switch (type) {
			case VALUE_STR:
			case VALUE_BARCODE:	
				return Post.getInt(str, 0);
			case VALUE_INT:	
			case VALUE_LONG:				
			case VALUE_BOOLEAN:
				return (int)data;
			case VALUE_DATETIME:
			case VALUE_DATE:
			case VALUE_TIME:
				return Post.encodeDate(getDateTime());
			case VALUE_DOUBLE:	
				return (int)Post.trunc(getNum());
			default:	
				return 0;
			}	
		}

		public long getLong() {
			switch (type) {
			case VALUE_STR:
			case VALUE_BARCODE:				
				return Post.getLong(str, 0);
			case VALUE_INT:
			case VALUE_LONG:				
			case VALUE_BOOLEAN:	
				return data;
			case VALUE_DATETIME:
			case VALUE_DATE:
			case VALUE_TIME:
				return Post.encodeDate(getDateTime());
			case VALUE_DOUBLE:	
				return Post.trunc(getNum());
			default:	
				return 0;
			}	
		}
		
		public boolean getBoolean() {
			switch (type) {
			case VALUE_STR:
			case VALUE_BARCODE:					
				return str!=null && !str.isEmpty();
			case VALUE_INT:
			case VALUE_LONG:				
			case VALUE_BOOLEAN:	
			case VALUE_DATETIME:
			case VALUE_DATE:
			case VALUE_TIME:
				return data!=0;
			case VALUE_BINARY:
				return size>0;
			case VALUE_DOUBLE:	
				return getNum()!=0;
			default:	
				return false;
			}
		}	
		
		public String getStr() {
			switch (type) {
			case VALUE_STR:
			case VALUE_BARCODE:
				return str;
			case VALUE_INT:
			case VALUE_LONG:				
				return String.valueOf(data);
			case VALUE_BOOLEAN:	
				return (data==0?"":"1");
			case VALUE_DATETIME:
				return DateFormat.getDateTimeInstance().format(getDateTime());
			case VALUE_DATE:
				return DateFormat.getDateInstance().format(getDateTime());
			case VALUE_TIME:
				return DateFormat.getTimeInstance().format(getDateTime());
			case VALUE_DOUBLE:	
				return String.valueOf(getNum());
			default:	
				return null;
			}			
		}
		
		public Calendar getDateTime() {
			switch (type) {
			case VALUE_STR:
				return Post.decodeDate(str);
			case VALUE_INT:
			case VALUE_LONG:				
				return Post.decodeDate((int)data);
			case VALUE_BOOLEAN:	
				return  (data==0?null:new GregorianCalendar());
			case VALUE_DATETIME:
				return ByteBufferManager.decodeDateTime(data);
			case VALUE_DATE:
				return ByteBufferManager.decodeDate((int)data);
			case VALUE_TIME:
				return ByteBufferManager.decodeTime((int)data);
			default:	
				return null;
			}
		}	
		
		public double getNum() {
			switch (type) {
			case VALUE_STR:
			case VALUE_BARCODE:	
				return Double.valueOf(str);
			case VALUE_INT:
			case VALUE_LONG:				
			case VALUE_BOOLEAN:
			case VALUE_DATETIME:
			case VALUE_DATE:
			case VALUE_TIME:
				return Double.valueOf(data);
			case VALUE_DOUBLE:
				return ByteBufferManager.decodeDouble(data);
			default:	
				return 0;
			}				
		}
		
		public ByteBuffer getBinary() {
			return null;
		}		
		
		public Object value() {
			switch (type) {
			case VALUE_STR:
			case VALUE_BARCODE:	
				return str;
			case VALUE_INT:
				return new Integer((int)data);
			case VALUE_LONG:
				return new Long(data);
			case VALUE_BOOLEAN:
				return new Boolean(getBoolean());
			case VALUE_DATETIME:
			case VALUE_DATE:
			case VALUE_TIME:
				return getDateTime();
			case VALUE_DOUBLE:
				return getNum();
			case VALUE_BINARY:
				return getBinary();
			default:	
				return null;
			}
		}
		
	}
		
	public Head head(int checkSize, short checkBlockID) throws InvalidPropertiesFormatException {
		Head res = new Head();
		res.read(this);
		if (checkBlockID!=0 && checkBlockID!=res.id)
			throw new InvalidPropertiesFormatException("Неверный идентификатор блока");
		if (checkSize!=0 && checkSize!=res.sizeFull)
			throw new InvalidPropertiesFormatException("Неверный размер блока");
		if (res.charset==null)
			res.charset = fcharset;
		return res;
	}
	
	public Element element(Head head) throws InvalidPropertiesFormatException {
		Element res = new Element();
		res.read(this, head);
		if (res.type!=null)
			return res;
		else
			return null;
	}

	public boolean element(Element dest, Head head) throws InvalidPropertiesFormatException {
		dest.read(this, head);
		return dest.type!=null;
	}	
	
	
	
	
}
