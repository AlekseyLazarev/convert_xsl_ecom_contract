package caits.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ErrorList implements ResultFormatOut {

	public class ErrorListItem {
		final private int code;
		final private String msg;
		
		public ErrorListItem(int code, String msg) {
			super();
			this.code = code;
			this.msg = msg;
		}
		
		
		public boolean isEmpty() {
			return msg==null || msg.isEmpty();
		}
		
		@Override
		public String toString() {
			if (isEmpty()) return new String();
			if (code==0) return msg;
			return msg+" (код ошибки "+code+")";
		}
		
		public String toJSON(boolean withCode) {
			if (isEmpty()) 
				return new String("null");
			String res = Post.escapeJSON(msg);
			if (withCode && code !=0)
				res = res + " (" + code + ")";
			return "\"" + res + "\"";
		}		

		public String toJSONgroup() {
			String res = new String();
			if (isEmpty()) return res;
			res = "{\"msg\":" + toJSON(false);
			if (code!=0) 
				res = res + ",\"code\":" + code;
			res = res + "}";
			return res;
		}		
		
		public int getCode() {
			if (isEmpty()) return 0;
			return code;
		}

	}
	
	private ArrayList<ErrorListItem> list;
	private int errorCode;
	private String caption;
	private String version;	 
	private Map<String, Object> params;
	protected Boolean outErrorOnlyArray;
	 
	public ErrorList() {
		list = new ArrayList<ErrorListItem>();
		outErrorOnlyArray = false;
		clear();
	}

	public ErrorListItem get(String msg, int errorCode) {
		 for (ErrorListItem it: list)
		 if (it.code==errorCode && it.msg.equalsIgnoreCase(msg)) 
			 return it;
		 return null;
	}

	public ErrorListItem get(String msg) {
		 return get(msg, 0);
	}
	
	public void add(String msg) {
		if (get(msg)==null)
			list.add(new ErrorListItem(0, msg)); 
	}
	 
	public void add(String msg, int errorCode) {
		if (get(msg, errorCode)==null)
			list.add(new ErrorListItem(errorCode, msg)); 
	} 
	
	public void add(ErrorList msg) {
		if (msg==null || msg.list==null) 
			return;
		for (ErrorListItem it: msg.list)
			list.add(it); 
	}	

	public void clear() {
		list.clear();
		errorCode = 0;
		caption = null;
		version = null;
	}
	 
	public boolean empty() {
		return list.isEmpty(); 
	} 
	 
	public int count() {
		return list.size(); 
	}  
	 
	public int getErrorCode() {
		return errorCode;	 
	}
	 
	public void setErrorCode(int val) {
		errorCode = val;	
	}
	
	public void setErrorCodeIfEmpty(int val) {
		if (errorCode==0) 
			errorCode = val;
	} 
	 
	public String getCaption() {
		return caption;
	}


	public void setCaption(String caption) {
		this.caption = caption;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}		
	
	public void setOutErrorOnlyArray(Boolean outErrorOnlyArray) {
		this.outErrorOnlyArray = outErrorOnlyArray;
	}

	public String toString(String selector) {
		String res = new String();
		for (ErrorListItem it: list) if (!it.isEmpty()) {
			if (!res.isEmpty()) res = res + selector;
			res = res + it.toString();
		}
		return res;
	}
	
	public String toString() {
		return toString("\r\n"); 
	}
	
	public String toJSON(boolean isGroup, boolean isPretty) {
		String res = new String();
		if (empty()) return res;
		boolean isSel = false;
		String sel = ",";
		if (isPretty)
			sel = sel + "\n";		
		for (ErrorListItem it: list) if (!it.isEmpty()) { 
			if (isSel) res = res + sel; 
			if (isGroup)
				res = res + it.toJSONgroup();
			else
				res = res + it.toJSON(true);
			isSel = true;
		}			
		return res;
	}
	
	public ArrayList<Map<String, Object>> toMap(String paramID, String paramMsg) {
		if (empty()) 
			return null;
		ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		Map<String, Object> e;
		for (ErrorListItem it: list) if (!it.isEmpty()) {
			e = new LinkedHashMap<String, Object>(2);
			if (paramMsg!=null)
				e.put(paramMsg, it.msg);
			if (it.code!=0 && paramID!=null)
				e.put(paramID, it.code);
			res.add(e); 
		}			
		return res;
	}		
	
	public ArrayList<Map<String, Object>> toMap() {
		return toMap("code", "msg");	
	}
	
	@Override
	public void outHTML(PrintWriter out, boolean fullout) throws IOException {
		if (fullout) {
			out.println("<html>");
			out.println("<head>");
			if ((caption != null) && (!caption.isEmpty()))
				out.println("<title>" + caption + "</title>");
			out.println("</head>");
			out.println("<body>");
		}
		out.println("<p>");
		if (caption != null && !caption.isEmpty())
			out.print("<b>" + caption + "</b>");
		else
			out.print("<b>Ошибки</b>");
		if (version != null && !version.isEmpty())
			out.print(" <i>(версия <b>" + version + "</b>)</i>");
		out.println("<b>:</b>");
		out.println("<ul>");
		for (ErrorListItem it: list) 
			 if (!it.isEmpty()) out.print("<li>" + Post.htmlSplash(it.toString()) + "</li>");
		out.println("</ul></p>");
		if (fullout) {
			out.println("</body>");
			out.println("</html>");
		}
	}
	
	@Override
	public void outText(PrintWriter out, boolean isEasy) throws IOException {
		if (!empty()) 
			out.println((caption!=null && !caption.isEmpty()?caption:"Ошибки") + ": " + (isEasy?"":"\r\n") + toString(isEasy?". ":"\r\n"));
	}

	@Override
	public void outJSON(PrintWriter out, boolean isPretty) throws IOException {
		String end = ",";
		if (isPretty)
			end = end + "\n";
		out.print("{");
		if (caption != null && !caption.isEmpty())
			out.print("\"caption\": \"" + caption + "\"" + end);
		if (version != null && !version.isEmpty())
			out.print("\"version\": \"" + version + "\"" + end);
		if (params!=null && !params.isEmpty())
			out.print("\"data\": " + Post.toJSON(params, isPretty) + end);
		if (!outErrorOnlyArray)
			out.print("\"error\": [" + toJSON(false, isPretty) + "]" + end);
		out.print("\"errors\": [" + toJSON(true, isPretty) + "]}");
	}
	
	public boolean out(FormatOutMode mode, PrintWriter out) throws Exception {
		if (empty())
			return false;
		mode.write(out, this);
		return true;
	}

}
