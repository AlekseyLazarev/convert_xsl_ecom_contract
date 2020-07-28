package caits.utils;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Класс для преобразования даты и времени в строку ISO-8601 в обоих направлениях
 * пример преобразования даты в строку: String str = (new DateISO8601(date)).get());
 * пример преобразования строки в дату: Calendar date = (new DateISO8601(str)).date;
 *  */	
public class DateISO8601 {

	//признак наличия даты в строке
	public boolean isDate;
	//признак наличия времени в строке
	public boolean isTime;
	//признак наличия временной зоны в строке	
	public boolean isTimeZone;
	//признак наличия милисекунд в строке
	public boolean isTimeMillisecond;
	
	//дата и время в Calendar
	public Calendar date; 
	//часы временной зоны (знак указывает, положительная или отрицательная зона)
	public int timeZoneHour;
	//минуты временной зоны (знак указывает, положительная или отрицательная зона)
	public int timeZoneMinute;
	//признак отрицательной временной зоны
	public boolean timeZoneNegative;
	
	//Кол-во полей
	protected static final int Dmax = 8;
	//Максимальный размер полей
	//0-год, 1-месяц, 2-день, 3-час, 4-мин, 5-сек, 6-мсек, 7-пояс час, 8-пояс мин
	protected static final int[] Dcnt = {4,2,2,2,2,2,3,2,2};	
	
	public DateISO8601() {
		super();
		clear();
	}

	public DateISO8601(String value) {
		super();
		set(value);
	}
	
	public DateISO8601(Calendar date) {
		super();
		clear();
		this.date = date;
		this.isDate = true;
		this.isTime = true;
		this.isTimeZone = false;
	}
	
	public DateISO8601(Calendar date, int timeZoneHour, int timeZoneMinute) {
		super();
		clear();
		this.date = date;
		this.isDate = true;
		this.isTime = true;
		this.isTimeZone = true;
		this.timeZoneHour = timeZoneHour;
		this.timeZoneMinute = timeZoneMinute;
		this.timeZoneNegative = timeZoneHour<0 || timeZoneMinute<0;
	}	
	
	public void clear() {
		date = null;
		timeZoneHour = 0;
		timeZoneMinute = 0;
		timeZoneNegative = false;
		isDate = false;
		isTime = false;
		isTimeZone = false;
		isTimeMillisecond = false;
	}	

	/**
	 * set Преобразует строку ISO-8601 в любой форме в дату.
	 * Дата должна следовать в последовательности год-месяц-день.
	 * Месяц и день может отсутствовать (они принимаются равными единице).
	 * Разделитель для даты может быть любой (в т.ч. пробел), либо отсутствовать.
	 * 
	 * Время от даты разделяется символом 'T' в любом регистре.
	 * Время должно следовать в последовательности час-мин-секунда-милисекунда.
	 * Час дожен быть указан в 24 часовом формате (значения 0..23).
	 * Минута, секунда и милисекунда могут отсутствовать (они принимаются равными нулю).
	 * Разделитель для времени может быть любой (в т.ч. пробел), кроме '-', либо отсутствовать
	 * 
	 * После времени может присутствовать часовой пояс. Разделителем часового пояса является знак '+' или '-'.
	 * Разделитель для часов и минут часового пояса может быть любой (в т.ч. пробел), кроме '+' и '-', либо отсутствовать
	 * Часовой пояс дожен быть указан в 24 часовом формате (значения 0..23).
	 * Минуты часового пояса могут отсутствовать (они принимаются равными нулю).
	 * 
	 * Дата, время и часовой пояс могут отсутствовать. При отсутствии времени часовой пояс игнорируется.
	 * 
	 * Если разделителя нет, то:
	 *  • для даты допустимы 4 цифры года и 2 цифры месяца и дня, 
	 *  • для времени допустимы 2 цифры для часа, минут, секунд и 3 цифры для милисекунд,
	 *  • для часового пояса допустимы 2 цифры для часа и минут.
	 * Если разделить есть (любой, возможен пробел), то:
	 *  • для даты допустимы 2-4 цифры года, 1-2 цифры месяца, 1-2 цифры дня,
	 *  • для времени допустимы 1-2 цифры для часа, минут, секунд и 3 цифры для милисекунд,
	 *  • для часового пояса допустимы по 2 цифры для часа и минут.
	 *  
	 * @param value дата и время в строке в формате ISO-8601
	 * @return date - дата и время в Calendar
	 * @return timeZoneHour - временная зона в часах (знак указывает, положительная или отрицательная зона)
	 * @return timeZoneMinute - временная зона в минутах (знак указывает, положительная или отрицательная зона)
	 * @return timeZoneNegative - признак отрицательной временной зоны 
	 * @return isDate - признак наличии даты в строке
	 * @return isTime - признак наличии времени в строке
	 * @return isTimeZone - признак наличии временной зоны в строке
	 */		
	
	public void set(String value) {
		clear();
		if (value!=null && value.isEmpty()) 
			return;
		char[] num = new char[8];
		int l = value.length();
		if (l>30)
			l = 30;
		char[] val = new char[l];
		value.getChars(0, l, val, 0);			
		int n = 0; //Счетчик цифр
		int m = 0; //Счетчик полей
		int[] D = new int[Dmax+1]; //Значения полей
		
		for (byte i=0; i<=Dmax; i++) D[i] = 0;
		boolean isEnd = false;
		
		for (int i=0; i<l; i++) {
			switch (val[i]) {
			case '0': case '1': case '2': case '3':
			case '4': case '5': case '6': case '7':
			case '8': case '9':
				isEnd = true;
				if (m>=3 && !isTime)
					break;
				if (m>=7 && !isTimeZone)
					break;
				num[n] = val[i];
				n++;
				if (n==Dcnt[m]) {
					D[m] = Integer.parseUnsignedInt(new String(num, 0, n));
					if (m==6 && isTime)
						isTimeMillisecond = true;					
					n = 0;
					m++;
					if (m>Dmax)
						break;
				}			
				isEnd = false;
				break;
			case 'T': case 't':
				if (!isTime) {
					m = 3;
					n = 0;
					isTime = true;
				}
				break;
			case '+': 
				isEnd = true;
				if (isTimeZone) 
					break;
				m = 7;
				n= 0;
				isTimeZone = true;
				timeZoneNegative = false;
				isEnd = false;
				break;
			case '-': 
				isEnd = true;
				if (isTime) {
					if (isTimeZone)
						break;
					m = 7;
					n = 0;
					isTimeZone = true;
					timeZoneNegative = true;
				}
				isEnd = false;
				break;
			default:
				break;
			}
			if (isEnd)
				break;
		}
		isDate = D[0]>9;
		if (!isDate && !isTime) {
			isTimeZone = false;
			isTimeMillisecond = false;
			timeZoneNegative = false;		
			return;
		}

		date = new GregorianCalendar();
		date.clear();
		if (isDate) {
			if (D[1]<=12 || D[0]<100) {
				if (D[1]==0) 
					D[1] = 1;
				if (D[2]==0)
					D[2] = 1;
				if (D[0]<100)
					D[0] = 2000 + D[0];
			} else {
				int tmp = D[1]*100 + D[2];
				D[1] = (D[0] % 100);
				D[2] = D[0] / 100;
				D[0] = tmp;
			}
			if (D[0]>9999 || D[0]<1000)
				throw new DateTimeException("Неверное значение года " + D[0] + " в значении даты \"" + value+ "\"");
			date.set(Calendar.YEAR, D[0]);
			if (D[1]>12 || D[1]<1)
				throw new DateTimeException("Неверное значение месяца " + D[1] + " в значении даты \"" + value+ "\"");
			date.set(Calendar.MONTH, D[1]-1);
			if (D[2]>date.getActualMaximum(Calendar.DAY_OF_MONTH) || D[2]<1)
				throw new DateTimeException("Неверное значение дня месяца " + D[2] + " в значении даты \"" + value+ "\"");
			date.set(Calendar.DAY_OF_MONTH, D[2]);
		}
		
		if (D[3]>23 || D[3]<0)
			throw new DateTimeException("Неверное значение часа " + D[3] + " в значении времени \"" + value+ "\"");
		date.set(Calendar.HOUR_OF_DAY, D[3]);
		if (D[4]>59 || D[4]<0)
			throw new DateTimeException("Неверное значение минут " + D[4] + " в значении времени \"" + value+ "\"");
		date.set(Calendar.MINUTE, D[4]);
		if (D[5]>59 || D[5]<0)
			throw new DateTimeException("Неверное значение секунд " + D[5] + " в значении времени \"" + value+ "\"");
		date.set(Calendar.SECOND, D[5]);
		if (D[6]>999 || D[6]<0)
			throw new DateTimeException("Неверное значение миллисекунд " + D[6] + " в значении времени \"" + value+ "\"");
		date.set(Calendar.MILLISECOND, D[6]);
		
		if (isTimeZone) {
			timeZoneHour = D[7];
			timeZoneMinute = D[8];
			if (timeZoneNegative) {
				timeZoneHour = - timeZoneHour;
				timeZoneMinute = - timeZoneMinute;
			}	
		}
	}

	/**
	 * get Форматирует дату и время в строку ISO-8601 
	 * @param date - дата и время в Calendar
	 * @param timeZoneHour - временная зона в часах (знак указывает, положительная или отрицательная зона)
	 * @param timeZoneMinute - временная зона в минутах
	 * @param isDate - признак вывода даты в строку
	 * @param isTime - признак вывода времени в строку
	 * @param isTimeZone - признак вывода временной зоны в строку
	 * @param separatorDate - разделитель в дате
	 * @param separatorTime - разделитель во времени
	 * @return срока формата ISO-8601
	 */		
	public static String get(Calendar date, int timeZoneHour, int timeZoneMinute, boolean isDate, boolean isTime,  boolean isTimeMillisecond, boolean isTimeZone, String separatorDate, String separatorTime) {
		String res = new String();
		if (date==null)
			return res;
		String sep;
		if (isDate) {
			if (separatorDate==null || separatorDate.isEmpty())
				sep = "";
			else
				sep = "'" + separatorDate + "'";
			res = res + "yyyy" + sep + "MM" + sep + "dd";
		}	
		if (isTime) {
			if (separatorTime==null || separatorTime.isEmpty())
				sep = "";
			else
				sep = "'" + separatorTime + "'";			

			res = res + "'T'HH" + sep + "mm" + sep + "ss";
			if (isTimeMillisecond)
				res = res + sep + "Z";
		}	
		if (res.isEmpty())
			return res;
		SimpleDateFormat df = new SimpleDateFormat(res);
		res = df.format(date.getTime());
		if (!isTime || !isTimeZone)
			return res;
		if (timeZoneMinute<0)
			timeZoneMinute = -timeZoneMinute;
		if (timeZoneHour>=0)
			res = res + "+" + String.format("%02d", timeZoneHour);
		else
			res = res + "-" + String.format("%02d", -timeZoneHour);
		return res + (separatorTime!=null?separatorTime:"") + String.format("%02d", timeZoneMinute);
	}

	/**
	 * get Возвращает дату в числе по ISO-8601 используюя переменные класса
	 * @return число даты в формате ISO-8601
	 */		
	public int getDateInt() {
		return date.get(Calendar.YEAR)*10000 + (date.get(Calendar.MONTH)+1)*100 + date.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * get Возвращает часы, минуты и секунды в числе, используюя переменные класса
	 * @return число часов и минут
	 */		
	public int getTimeInt() {
		if (date!=null && isTime)
			return date.get(Calendar.HOUR_OF_DAY)*10000 + date.get(Calendar.MINUTE)*100 + date.get(Calendar.SECOND);
		else
			return -1;
	}
	
	/**
	 * get Форматирует дату и время в строку ISO-8601 в сокращенной форме без разделителей
	 * @param date - дата и время в Calendar
	 * @param timeZoneHour - временная зона в часах (знак указывает, положительная или отрицательная зона)
	 * @param timeZoneMinute - временная зона в минутах
	 * @param isDate - признак вывода даты в строку
	 * @param isTime - признак вывода времени в строку
	 * @param isTimeZone - признак вывода временной зоны в строку
	 * @return срока формата ISO-8601
	 */		
	public static String get(Calendar date, int timeZoneHour, int timeZoneMinute, boolean isDate, boolean isTime, boolean isTimeZone) {
		return get(date, timeZoneHour, timeZoneMinute, isDate, isTime, false, isTimeZone, null, null);	
	}
	
	/**
	 * get Форматирует дату и время в строку ISO-8601 в сокращенной форме без разделителей и без часового пояса
	 * @param date - дата и время в Calendar
	 * @param isDate - признак вывода даты в строку
	 * @param isTime - признак вывода времени в строку
	 * @return срока формата ISO-8601
	 */				
	public static String get(Calendar date, boolean isDate, boolean isTime) {
		return get(date, 0, 0, isDate, isTime, false, false, null, null);
	}		

	/**
	 * get Форматирует дату и время в строку ISO-8601 в сокращенной форме без разделителей используюя переменные класса
	 * @return срока формата ISO-8601
	 */				
	public String get() {
		return get(date, timeZoneHour, timeZoneMinute, isDate, isTime, isTimeMillisecond, isTimeZone, null, null);
	}		
	
	/**
	 * getLong Форматирует дату и время в строку ISO-8601 в полную форму с разделителями '-' для даты и ':' для времени используюя переменные класса
	 * @return срока формата ISO-8601
	 */	
	public String getLong() {
		return get(date, timeZoneHour, timeZoneMinute, isDate, isTime, isTimeMillisecond, isTimeZone, "-", ":");
	}		

	public Date getTime() {
		if (date!=null && isTime)
			return date.getTime();
		else
			return null;
	}	
	
	public void test(String[] dv) {
		String vs;
		String vl;
		for (String v: dv) {
			set(v);
			vs = get();
			vl = getLong();
			set(vl);
			System.out.print(v + " - " + vs + " - " + vl + " - " + get() + " - ");
			set(vs);
			System.out.println(get());
		}
	}
	
}
