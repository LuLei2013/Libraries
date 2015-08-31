package com.code.lib.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class Log {
	private static String getStackTraceString(Throwable e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);

		Throwable cause = e;
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		final String stacktraceAsString = result.toString();
		printWriter.close();

		return stacktraceAsString;
	}

	public static void e(String tag, Throwable e) {
		android.util.Log.e(tag, getStackTraceString(e));
	}

	public static void e(String tag, String infor) {
		android.util.Log.e(tag, infor);
	}

}
