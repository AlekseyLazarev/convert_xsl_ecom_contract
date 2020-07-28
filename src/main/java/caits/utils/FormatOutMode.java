package caits.utils;

import java.io.PrintWriter;
import java.util.Map;

public enum FormatOutMode {
	
    TEXT("text", "text/plain; charset=utf-8"), 
    HTML("html", "text/html; charset=utf-8"), 
    HTMLfull("htmlfull", "text/html; charset=utf-8"), 
    JSON("json", "application/json; charset=utf-8"), 
    JSONtext("jsontext", "text/plain; charset=utf-8"), 
    EASY("easy", "text/plain; charset=utf-8");
	
	public final String paramName;
	public final String contentType;

	FormatOutMode(String paramName, String contentType) {
		this.paramName = paramName.toLowerCase();
		this.contentType = contentType.toLowerCase();
	}
	
	public static FormatOutMode get(String val) {
		if (val==null || val.isEmpty()) return null;
		String v = val.toLowerCase().trim();
		for (FormatOutMode m : FormatOutMode.values())
			if (m.paramName.equals(v))
				return m;
		return null;
	}
	
	public static FormatOutMode get(Map<String, ?> param) {
		if (param==null || param.isEmpty()) return null;
		FormatOutMode f;
		FormatOutMode f1 = null;
		for (String m : param.keySet()) {
			f = get(m);
			if (f==null) ;
			else if (f1 == null) f1 = f;
			else if ((f1==FormatOutMode.TEXT && f==FormatOutMode.JSON)||(f1==FormatOutMode.JSON && f==FormatOutMode.TEXT)) return FormatOutMode.JSONtext;
			else return f1;
		}
		return f1;
	}
	
	public static FormatOutMode get(Map<String, ?> param, FormatOutMode defaultMode) {
		FormatOutMode f = get(param);
		if (f!=null) return f;
		return defaultMode;
	}
	
	public void write(PrintWriter out, ResultFormatOut obj) throws Exception {
		if (obj==null || out==null)
			return;
		
		switch (this) {
		case HTML:	
			obj.outHTML(out, false);
			break;
		case HTMLfull:
			obj.outHTML(out, true);
			break;
		case JSON:
			obj.outJSON(out, false);
			break;
		case JSONtext:
			obj.outJSON(out, true);
			break;
		case TEXT:
			obj.outText(out, false);
			break;			
		case EASY:
			obj.outText(out, true);
			break;
		}
	}
}	
