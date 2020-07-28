package caits.utils;

import java.io.PrintWriter;

public interface ResultFormatOut {
	 public void outHTML(PrintWriter out, boolean fullout) throws Exception;
	 public void outJSON(PrintWriter out, boolean isPretty) throws Exception;
	 public void outText(PrintWriter out, boolean isEasy) throws Exception;
}
