package com.hoho.android.usbserial.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class StringUtil {
	public static boolean isEmpty(String s) {
		if (s == null) {
			return true;
		} else if (s.trim().equals("")) {
			return true;
		} else if (s.trim().equals("null")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNumber(String s) {

		try {
			Float.parseFloat(s);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	/**
	 * 比较第一个数组中的 元素是否和 bt2 中相同 （从srcPos开始比较）
	 * 
	 * @param src
	 *            被比较的byte数组
	 * @param srcPos
	 *            开始点
	 * @param bt2
	 *            目标数组
	 * @param length
	 *            长度
	 * @return
	 */
	public static boolean isEqualsByte(byte[] src, int srcPos, byte[] bt2,
			int length) {

		byte[] temp = new byte[length];
		System.arraycopy(src, srcPos, temp, 0, length);

		return Arrays.equals(temp, bt2);

	}

	/**
	 * 字符串格式化为日期时间格式
	 * 
	 * @param format
	 *            原来格式 yyyyMMdd HHmmss
	 * @param toformat
	 *            目标格式 yyyy-MM-dd HH:mm:ss
	 * @param time
	 *            时间或日期
	 * @return 目标日期时间字符串
	 */
	public static String str2DateTime(String format, String toformat,
                                      String time) {
		String str = "";
		Date date;

		try {
			date = new SimpleDateFormat(format).parse(time);
			str = new SimpleDateFormat(toformat).format(date);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return str;

	}

	/**
	 * short转换为字节
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] shortToByteArray(short s) {
		byte[] targets = new byte[2];
		// for (int i = 0; i < 2; i++) {
		// int offset = (targets.length - 1 - i) * 8;
		// targets[i] = (byte) ((s >>> offset) & 0xff);
		// }
		targets[0] = (byte) (s & 0x00ff);
		targets[1] = (byte) ((s & 0xff00) >> 8);
		return targets;
	}

	/**
	 * short转换为字节
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] shortToByteArrayTwo(short s) {
		byte[] targets = new byte[2];
		// for (int i = 0; i < 2; i++) {
		// int offset = (targets.length - 1 - i) * 8;
		// targets[i] = (byte) ((s >>> offset) & 0xff);
		// }
		targets[1] = (byte) (s & 0x00ff);
		targets[0] = (byte) ((s & 0xff00) >> 8);
		return targets;
	}

	/**
	 * short[]转换为字节[]
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] shortArrayToByteArray(short[] s) {
		byte[] targets = new byte[s.length * 2];
		for (int i = 0; i < s.length; i++) {
			byte[] tmp = shortToByteArray(s[i]);

			targets[2 * i] = tmp[0];
			targets[2 * i + 1] = tmp[1];
		}
		return targets;
	}

	/**
	 * byte[]到Short
	 * 
	 * @param buf
	 * @return
	 */
	public static short[] byteArraytoShort(byte[] buf) {
		short[] targets = new short[buf.length / 2];
		short vSample;
		int len = 0;
		for (int i = 0; i < buf.length; i += 2) {
			vSample = (short) (buf[i] & 0x00FF);
			vSample |= (short) ((((short) buf[i + 1]) << 8) & 0xFF00);
			targets[len++] = vSample;
		}
		return targets;
	}

	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @param String
	 *            str 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			//sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * 十六进制转换字符串
	 * 
	 * @param String
	 *            str Byte字符串(Byte之间无分隔符 如:[616C6B])
	 * @return String 对应的字符串
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		try {
			return new String(bytes, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return "";
	}

	/**
	 * bytes转换成十六进制字符串
	 * 
	 * @param byte[] b byte数组
	 * @return String 每个Byte值之间空格分隔
	 */
	public static String byte2HexStr(byte[] b) {
		if(b == null)
			return "";
		String stmp = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
			// sb.append(" ");
		}
		return sb.toString().toUpperCase().trim();
	}

	/**
	 * bytes字符串转换为Byte值
	 * 
	 * @param String
	 *            src Byte字符串，每个Byte之间没有分隔符
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		if((src.length()%2)!=0)
			src = "0"+src;
		int l = src.length() / 2;
		//System.out.println(l);
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = Integer.decode(
					"0x" + src.substring(i * 2, m) + src.substring(m, n))
					.byteValue();
		}
		return ret;
	}

	/**
	 * String的字符串转换成unicode的String
	 * 
	 * @param String
	 *            strText 全角字符串
	 * @return String 每个unicode之间无分隔符
	 * @throws Exception
	 */
	public static String strToUnicode(String strText) throws Exception {
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128)
				str.append("\\u" + strHex);
			else
				// 低位在前面补00
				str.append("\\u00" + strHex);
		}
		return str.toString();
	}

	/**
	 * unicode的String转换成String的字符串
	 * 
	 * @param String
	 *            hex 16进制值字符串 （一个unicode为2byte）
	 * @return String 全角字符串
	 */
	public static String unicodeToString(String hex) {
		int t = hex.length() / 6;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++) {
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 高位需要补上00再转
			String s1 = s.substring(2, 4) + "00";
			// 低位直接转
			String s2 = s.substring(4);
			// 将16进制的string转为int
			int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
			// 将int转换为字符
			char[] chars = Character.toChars(n);
			str.append(new String(chars));
		}
		return str.toString();
	}

	/**
	 * @author Junhua Wu
	 * @param src
	 *            byteêy×é 3¤?èó|μ±?a4
	 * @return ×a??oóμ???Dí?á1? ×a??°′??D???×??úDòà′?D?¨
	 */
	public static int byteToInt(byte[] src) {
		int tmp = 0;
		for (int i = 0; i < src.length; i++) {
			tmp += ((int) src[i] << (i * 8)) & (0xFF << (i * 8));
		}

		return tmp;
	}

	/**
	 * @author Junhua Wu
	 * @param src
	 *            ??Díêy?Y
	 * @return ×a??oóμ?byteêy×é?á1? ×a??°′??D???×??úDòà′?D?¨
	 */
	public static byte[] intToByte(int src) {
		byte[] tmp = new byte[4];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = (byte) (((int) src >> (i * 8)) & 0xFF);
		}
		return tmp;
	}

	public static byte[] intToByte1024(int src) {
		byte[] tmp = new byte[1024];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = (byte) (((int) src >> (i * 8)) & 0xFF);
		}
		return tmp;
	}


	public static String byteTostrGBK(byte[] data) {
		String result = "";
		try {

			result = new String(data, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			Log.e("StringUtil", "byteTostrGBK", e);

		}
		return result;
	}
    /* *
     * 
     * 把16进制字符串转换成字节数组 @param hex @return 
     */  
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);  
        byte[] result = new byte[len];  
        char[] achar = hex.toCharArray();  
        for (int i = 0; i < len; i++) {  
            int pos = i * 2;  
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));  
        }  
        return result;  
    }  
  
    private static byte toByte(char c) {  
        byte b = (byte) "0123456789ABCDEF".indexOf(c);  
        return b;  
    }  
  
    /** 
     * 把字节数组转换成16进制字符串 
     *  
     * @param bArray 
     * @return 
     */  
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {  
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)  
                sb.append(0);  
            sb.append(sTemp.toUpperCase());  
        }  
        return sb.toString();  
    }  
    public static byte[] intToByteArray(int i) {
		byte[] targets = new byte[4];
		targets[0] = (byte) (i & 0xFF);
		targets[1] = (byte) ((i>>8) & 0xFF);
		targets[2] = (byte) ((i>>16) & 0xFF);
		targets[3] = (byte) ((i>>24) & 0xFF);
		return targets;
	}
    
   /**
    * 把字节数组转化成int类型，小端模式
    * @param b
    * @return
    */
   public static int byteArrayToInt(byte[] b) {
		int result = 0;
		result = (b[0]&0xFF)|(b[1]<<8&0xFFFF)|(b[2]<<16&0xFFFFFF)|(b[3]<<24&0xFFFFFFFF);
		return result;
	}
}
