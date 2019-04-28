package com.hoho.android.usbserial.examples;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


//调试信息管理
public class LogInfoManager {


	public void saveExceptionLog(String log)
	{
//		File dir =  AppConfig.getExceptionLogFileDir();
		File dir = new File("/mnt/sdcard/LOG_INFO/");
		dir.mkdirs();
		File file = new File(dir, DateUtils.getCurrTime());

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			fos.write(log.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public File[] getExceptionLogFileInfo()
	{
		File dir =  AppConfig.getExceptionLogFileDir();
		return dir.listFiles();
	}

	public void saveLog(String log)
	{
		File dir =  AppConfig.getCommonLogFileDir();
		File file = new File(dir, DateUtils.getCurrTime());

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			fos.write(log.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public File[] getLogFileInfo()
	{
		File dir =  AppConfig.getCommonLogFileDir();
		return dir.listFiles();
	}

	public void clearExceptionLog()
	{
		FileUtils.deleteDirectoryAllFile(AppConfig.getExceptionLogFileDir());
	}

	public void clearCommonLog()
	{
		FileUtils.deleteDirectoryAllFile(AppConfig.getCommonLogFileDir());
	}

	public void clear()
	{
		clearExceptionLog();
		clearCommonLog();
	}

}
