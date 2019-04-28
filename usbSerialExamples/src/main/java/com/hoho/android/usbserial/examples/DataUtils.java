package com.hoho.android.usbserial.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtils {

    public static boolean ByteComp(byte[] x,int xPos, byte[] y,int yPos, int len)
    {
        if (x.length - xPos >= len && y.length - yPos >= len)
        {
            for (int i = 0; i < len; i++)
            {
                if (x[xPos + i] != y[yPos + i])
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static byte[] subBytes(byte[] src, int begin, int count)
    {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }


    public static long power(int x,int n)
    {
        int i =0;
        int tmp = 1;
        for(i = 0;i<n;i++){
            tmp = tmp*x;
        }
        return tmp;
    }

    public static long  BCDtoDec(byte bcd[],int offset, int length)
    {
        int i, tmp;
        long dec = 0;
        for(i = 0 + offset; i < length + offset; i++)
        {
            tmp = (( bcd[i] >> 4 ) & 0x0F ) * 10 + ( bcd[i] & 0x0F );


            dec += tmp * power(100, length + offset - 1 - i);
        }
        return dec;
    }

    public static int DectoBCD(int Dec, byte[] Bcd, int length)
    {
        int i;
        int temp;
        for(i = length-1; i >= 0; i--)
        {
            temp = Dec%100;
            Bcd[i] = (byte) (((temp/10)<<4) + ((temp%10) & 0x0F));
            Dec /= 100;
        }
        return 0;
    }
    public static int DectoBCD(long Dec, byte[] Bcd, int length)
    {
        int i;
        long temp;
        for(i = length-1; i >= 0; i--)
        {
            temp = Dec%100;
            Bcd[i] = (byte) (((temp/10)<<4) + ((temp%10) & 0x0F));
            Dec /= 100;
        }
        return 0;
    }

    public static int DectoBCD(long Dec, byte[] Bcd,int offset, int length)
    {
        int i;
        long temp;
        for(i = length-1; i >= 0; i--)
        {
            temp = Dec%100;
            Bcd[i + offset] = (byte) (((temp/10)<<4) + ((temp%10) & 0x0F));
            Dec /= 100;
        }
        return 0;
    }
    //十六进制字符串转换成十六进制byte数组
    public static byte[] hexStr2HexByte(String hexStr)
    {
        if(null == hexStr)
            return null;
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toUpperCase().toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++)
        {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return bytes;
    }

    //十六进制byte数组转换成十六进制字符串
    public static String hexByte2HexStr(byte[] bs)
    {

        if(bs == null)
            return null;
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
//            sb.append(' ');
        }
        return sb.toString().trim();
    }

    //十六进制byte数组转换成十六进制字符串
    public static String hexByte2HexStr(byte[] bs, int offst, int len)
    {
        if(bs == null)
            return null;
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        int bit;

        for (int i = offst; i < offst + len; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
//            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static int asc2bcd(byte[] ascStr, int ascOffset, int asclen, byte[] bcdStr, int bcdOffset, int align)
    {
        int i = 0;
        byte ch;

        for (i = 0; i < (asclen + 1) / 2; i++)
            bcdStr[bcdOffset + i] = 0x00;

        if ((asclen & 0x01) > 0 && align > 0)
            align = 1;
        else
            align = 0;

        for (i = align; i < asclen + align; i ++) {
            ch = ascStr[ascOffset + i - align];
            if (ch >= 'a'&& ch <= 'f')
                ch -= ('a' - 0x0A);
            else if (ch >= 'A'&& ch <= 'F')
                ch -= ('A' - 0x0A);
            else if (ch >= '0' && ch <= '9')
                ch -= '0';
            else
                return -1;
            bcdStr[bcdOffset + (i / 2)] |= (ch << (((i + 1) % 2) * 4));
        }

        return 0;
    }
    /*    功能:        转换BCD码为ASCII码。如果有10->15数值，将转
    　　　　换为大写字母: A->F。
     *    参数:        #bcd        输入的BCD码字符
                     #asclen        输出的ASCII码字符串长度
                     #asc        输出的ASCII码字符串
                     #align        1        BCD左填充0x00
                                 0        BCD右填充0x00
     *    返回值:        0        成功
     *                -1        失败
     */
    public static int bcd2asc(byte[] bcd,int bcdOffset, int asclen, byte[] asc, int align)
    {
        int cnt;
        int pbcd = bcdOffset;
        int pasc = 0;

        if (((asclen & 0x01)> 0) && (align > 0)) {  /*判别是否为奇数以及往那边对齐 */
            cnt = 1;
            asclen ++;
        } else {
            cnt = 0;
        }
        for (; cnt < asclen; cnt++, pasc++) {

            asc[pasc] = (byte) ((cnt & 0x01)>0 ? (bcd[pbcd ++] & 0x0f) : ((bcd[pbcd]&0xff) >> 4));
            asc[pasc] += ((asc[pasc] > 9) ? ('A' - 10) : '0');
        }
        asc[pasc] = 0;
        return 0;
    }

    public static int bytes2int(byte[] bytes) {
        int val = 0;
        for (int i = 3; i >= 0; i--) {
            val += ((int)bytes[i]&0xFF)<<(8*i);
        }
        return val;
    }


    public static byte calcLrc( byte[] data, int offset,int dataLen )
    {
        byte bcc = 0;
        int i;

        for( i = 0; i < dataLen; ++i ){
            bcc ^= data[offset + i];
        }

        return bcc;
    }

    public static byte[] ToGbkByteArray(String data)
    {
        byte[] array = null;
        if(data == null)
            return null;
        try {
            array  = data.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return array;
    }

    public static String ToGbkString(byte[] array)
    {
        String tmp = null;
        try {
            tmp = new String( array,"GBK");

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tmp;
    }
    public static String ToGbkString(byte[] array, int offset, int len)
    {
        String tmp = null;
        try {
            tmp = new String( array, offset, len,"GBK");

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tmp;
    }

    public static String toFormatNumeric(String val, int len)
    {

        char[] c = new char[len];
        char[] x = val.toCharArray();
        if (x.length > len) {
            throw new IllegalArgumentException("Numeric value is larger than intended length: " + len + " LEN " + len);
        }
        int lim = c.length - x.length;
        for (int i = 0; i < lim; i++) {
            c[i] = '0';
        }
        System.arraycopy(x, 0, c, lim, x.length);
        return new String(c);
    }

    public static String toFormatNumeric(long val, int len)
    {

        char[] c = new char[len];
        char[] x = ("" + val).toCharArray();
        if (x.length > len) {
            throw new IllegalArgumentException("Numeric value is larger than intended length: " + len + " LEN " + len);
        }
        int lim = c.length - x.length;
        for (int i = 0; i < lim; i++) {
            c[i] = '0';
        }
        System.arraycopy(x, 0, c, lim, x.length);
        return new String(c);
    }

    public static String toFormatAlpha(String val, int len)
    {

        byte[] tmp = ToGbkByteArray(val);
        if (val == null) {
            val = "";
        }
        if (tmp.length > len)
            return ToGbkString( tmp, 0, len);
        if (tmp.length == len) {
            return val;
        }

        String space = "";
        for(int i = 0; i < len - tmp.length; i++ )
        {
            space += " ";
        }

        return val + space;
    }

    public static long  AsctoDec(byte asc[],int pos, int length)
    {
        long l = 0L;
        long power = 1L;
        for (int i = pos + length - 1; i >= pos; i--) {
            l += (asc[i] - '0') * power;
        }
        return l;
    }

    public static Double toFormtAmt(String val)
    {

        return toFormtAmt(Long.parseLong(val));
    }

    public static Double toFormtAmt(long cash)
    {

        return Double.parseDouble(String.format("%d.%02d", cash/100, cash%100));
    }

    public static Double toFormtAmt(byte[] val)
    {
        long cash = BCDtoDec(val, 0, val.length);

        return Double.parseDouble(String.format("%d.%02d", cash/100, cash%100));
    }

    public static Double toFormtAmt(byte[] val, int offset, int len)
    {
        long cash = BCDtoDec(val, offset, len);

        return Double.parseDouble(String.format("%d.%02d", cash/100, cash%100));
    }

    public static String leftPad(String str, int length, char ch){
        char[] chs = new char[length];
        Arrays.fill(chs, ch);//把数组chs填充成ch
        char[] src = str.toCharArray();//把字符串转换成字符数组
        System.arraycopy(src, 0, chs,
                length-src.length,src.length);
        //从src的0位置开始复制到chs中从length-src.length到src.lengtth
        return new String(chs);
    }

    public static String rightPad(String str, int length, char ch){
        char[] chs = new char[length];
        Arrays.fill(chs, ch);//把数组chs填充成ch
        char[] src = str.toCharArray();//把字符串转换成字符数组
        System.arraycopy(src, 0, chs,
                0,src.length);
        //从src的0位置开始复制到chs中从length-src.length到src.lengtth
        return new String(chs);
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整�?    *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整�?    *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 将一个InputStream流转换成字符
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line);
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }


    public static boolean isAmtFormat(String text)
    {
        if(text == null)
            return true;
        int posDot = text.indexOf(text);
        int len = text.length();

        if(posDot <= 0)
        {
            if(len > 12)
                return false;
        }else
        {
            if(len - posDot - 1 > 2)
                return false;
        }
        if(len > 13)
            return false;

        return true;
    }


    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr
     *            身份证号
     * @return 有效：返回"" 无效：返回String信息
     * @throws ParseException
     */
    public static boolean IDCardValidate(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
            return false;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            return false;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效。";
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(
                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "身份证生日不在有效范围。";
                return false;
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return false;
        }
        // =====================(end)=====================  

        // ================ 地区码时候有效 ================  
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误。";
            return false;
        }
        // ==============================================  

        // ================ 判断最后一位的值 ================  
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                errorInfo = "身份证无效，不是合法的身份证号码";
                return false;
            }
        } else {
            return true;
        }
        // =====================(end)=====================  
        return true;
    }

    /**
     * 功能：判断字符串是否为数字 
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：设置地区编码 
     *
     * @return Hashtable 对象 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 验证日期字符串是否是YYYY-MM-DD格式 
     *
     * @param str
     * @return
     */
    private static boolean isDataFormat(String str) {
        boolean flag = false;
        // String  
        // regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";  
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1- 9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1- 2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }


    /**
     * 描述：是否是邮箱.
     *
     * @param str 指定的字符串
     * @return 是否是邮箱:是为true，否则false
     */
    public static Boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }

    /**
     * 判断是否是银行卡号
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId
                .substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N  
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }


    /**
     * 判断是否是手机号
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        Pattern pattern = Pattern
                .compile("^(13[0-9]|15[0-9]|153|15[6-9]|180|18[23]|18[5-9])\\d{8}$");
        Matcher matcher = pattern.matcher(phone);

        if (matcher.matches()) {
            return true;
        }
        return false;
    }


    /**
     * 描述：判断一个字符串是否为null或空值.
     *
     * @param str 指定的字符串
     * @return true or false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 描述：是否是中文.
     *
     * @param str 指定的字符串
     * @return  是否是中文:是为true，否则false
     */
    public static Boolean isChinese(String str) {
        Boolean isChinese = true;
        String chinese = "[\u0391-\uFFE5]";
        if(!isEmpty(str)){
            //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                //获取一个字符
                String temp = str.substring(i, i + 1);
                //判断是否为中文字符
                if (temp.matches(chinese)) {
                }else{
                    isChinese = false;
                }
            }
        }
        return isChinese;
    }

    /**
     * 描述：是否包含中文.
     *
     * @param str 指定的字符串
     * @return  是否包含中文:是为true，否则false
     */
    public static Boolean isContainChinese(String str) {
        Boolean isChinese = false;
        String chinese = "[\u0391-\uFFE5]";
        if(!isEmpty(str)){
            //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                //获取一个字符
                String temp = str.substring(i, i + 1);
                //判断是否为中文字符
                if (temp.matches(chinese)) {
                    isChinese = true;
                }else{

                }
            }
        }
        return isChinese;
    }

}
