package com.hoho.android.usbserial.examples;


import java.io.File;


/**
 * 应用程序配置类：用于保存用户相关信息及设
 */
public class AppConfig {

    //图片存储文件路径
    public final static String ROOT_IMAGE = "ROOT_IMAGE";
    public final static String ICON_IMAGE = "ICON_IMAGE"; //...\ROOT_IMAGE\ICON_IMAGE
    public final static String GUIDE_IMAGE = "GUIDE_IMAGE"; //...\ROOT_IMAGE\GUIDE_IMAGE
    public final static String BATCH_IMAGE = "BATCH_IMAGE"; //...\ROOT_IMAGE\BATCH_IMAGE
    public final static String COMMON_IMAGE = "COMMON_IMAGE"; //...\ROOT_LOG\COMMON_IMAGE
    //异常文件存储路径
    public final static String ROOT_LOG = "ROOT_LOG";
    public final static String EXCEPTION_LOG = "EXCEPTION_LOG"; //...\ROOT_LOG\EXCEPTION_LOG
    //终端信息、配置文件路径
    public final static String ROOT_TERMINFO = "ROOT_TERMINFO";
    public final static String TERMINFO_CONFIG = "TERMINFO_CONFIG";
//    短信验证
    public final static String SMSSDK_APP_KEY = "191525f3382a4";
    public final static String SMSSDK_APP_SECRET = "a3e75eadb150058c08131e9fba0e451a";
 // 默认使用中国区号
 	public static final String DEFAULT_COUNTRY_ID = "42";
 	

    public static File getImageRootDir() {
    	return FileUtils.getDiskCacheDir(MainApplication.getContextObject(), ROOT_IMAGE);
    }
    
    public static File getIconRootDir() {
    	return FileUtils.getDiskCacheDir(MainApplication.getContextObject(),  ROOT_IMAGE + File.separator + ICON_IMAGE);
    }
    
    public static File getIconDir(String name) {
    	return FileUtils.getDiskCacheDir(MainApplication.getContextObject(),  ROOT_IMAGE + File.separator + ICON_IMAGE + File.separator + name);
    }
    
    public static File getGuideImageDir() {
    	return FileUtils.getDiskCacheDir(MainApplication.getContextObject(),  ROOT_IMAGE + File.separator + GUIDE_IMAGE);
    }
    
    public static File getBatchImageRootDir() {
    	return FileUtils.getDiskCacheDir(MainApplication.getContextObject(),  ROOT_IMAGE + File.separator + BATCH_IMAGE);
    }
    
    public static File getBatchImageDir(String key) {
    	return FileUtils.getDiskCacheDir(MainApplication.getContextObject(),  ROOT_IMAGE + File.separator + BATCH_IMAGE + File.separator + key);
    }
    
    public static File getLogRootDir() {
    	return FileUtils.getDiskCacheDir(MainApplication.getContextObject(),  ROOT_LOG);
    }

    public static File getExceptionLogFileDir() {
    	return FileUtils.getDiskCacheDir(MainApplication.getContextObject(),  ROOT_LOG + File.separator + EXCEPTION_LOG);
    }
    
    public static File getCommonLogFileDir() {
    	return FileUtils.getDiskCacheDir(MainApplication.getContextObject(),  ROOT_LOG + File.separator + COMMON_IMAGE);
    }
    
    public static File getTermInfoFile() {
    	return FileUtils.getDiskCacheFile( ROOT_TERMINFO, TERMINFO_CONFIG);
    }
    
}


