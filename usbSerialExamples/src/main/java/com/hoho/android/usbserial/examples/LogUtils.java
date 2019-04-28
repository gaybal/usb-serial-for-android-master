package com.hoho.android.usbserial.examples;

import android.util.Log;

public class LogUtils {
	
	private static String TAG = "LOG";
	public static boolean DEBUG = true;
	
	private static String generateLable(StackTraceElement stack){
		
		String tag = "%s(L:%d): ";
		String className = stack.getClassName();
		className = className.substring(className.lastIndexOf(".")+1);
		tag = String.format(tag,className,stack.getLineNumber());
		
		return tag;
	}

	public static void i()
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        Log.i(TAG, generateLable(caller));
	}
	
	public static void i(String msg)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        Log.i(TAG, generateLable(caller)+ msg);
	}
	
	public static void d(String msg)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        Log.d(TAG, generateLable(caller) + msg);
	}
	
	public static void v(String msg)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        Log.v(TAG, generateLable(caller) + msg);
	}
	
	public static void w(String msg)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        Log.w(TAG, generateLable(caller) + msg);
	}
	
	public static void e(String msg)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        Log.e(TAG, generateLable(caller) + msg);
	}
	
	public static void i(String msg, Throwable e)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		Log.i(TAG, generateLable(caller)+ msg, e);
	}
	
	public static void d(String msg, Throwable e)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		Log.d(TAG,  generateLable(caller)+ msg, e);
	}
	
	public static void v(String msg, Throwable e)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		Log.v(TAG,  generateLable(caller)+ msg, e);
	}
	
	public static void w(String msg, Throwable e)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		Log.w(TAG, generateLable(caller)+ msg, e);
	}
	
	public static void e(String msg, Exception e)
	{
        if (!DEBUG) {
            return ;
        }
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		Log.e(TAG, generateLable(caller)+ msg, e);
	}
	
}
