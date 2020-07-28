package caits.utils;

import java.util.ArrayList;

import caits.utils.PostOfficeProperty.PostOfficeLoaded;

public class PostOfficePropertys extends ArrayList<PostOfficeProperty> {
	private static final long serialVersionUID = 1L;
	
	final PostOfficeLoaded loadPO;
	
	public PostOfficePropertys(PostOfficeLoaded loadPO, int capacity) {
		super();
		this.loadPO = loadPO;
		if (capacity > 0)
			for (int i = 0; i < capacity; i++)
				add(new PostOfficeProperty(loadPO));
	}

	public void clear() {
		for (PostOfficeProperty po : this)
			po.clear();
	}

	public PostOfficeProperty get(int index) {
		int cnt = size();
		if (index >= cnt)
			for (int i = cnt; i <= index; i++)
				add(new PostOfficeProperty(loadPO));
		return super.get(index);
	}

	public PostOfficeProperty set(int index, PostOfficeProperty val) {
		int cnt = size();
		if (index >= cnt)
			for (int i = cnt; i <= index; i++)
				add(new PostOfficeProperty(loadPO));
		return super.set(index, val);
	}

	public PostOfficeProperty indexOf(int PO) {
		for (PostOfficeProperty po : this)
			if ((po != null) && (po.PO == PO))
				return po;
		return null;
	}

	// Вывод в строку по маске
	public String toString(String mask, String prefix, String suffix, String separator, String selector, int fromPO, int toPO, int date) throws TariffException {
		String res = new String();
		boolean fl = false;
		String fd = null;
		for (PostOfficeProperty po : this) {
			if ((po == null) || (!po.isOK))
				continue;
			if (fl)
				res = res + separator;
			fl = separator != null;
			if ((fromPO != 0) && (po.PO == fromPO))
				fd = "отправления";
			else if ((toPO != 0) && (po.PO == toPO))
				fd = "назначения";
			else
				fd = "перегрузки";
			if ((prefix != null) && (!prefix.isEmpty()))
				res = res + String.format(prefix, po.PO, fd);
			res = res + po.toString(mask, selector, fd, date);
			if ((suffix != null) && (!suffix.isEmpty()))
				res = res + String.format(suffix, po.PO, fd);
		}
		return res;
	}
	

}
