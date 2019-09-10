package com.zanclick.prepay.common.utils;/**
 * Created by lvlu on 2018/1/15.
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author lvlu
 * @date 2018-01-15 10:13
 **/
public class StringUtils {

    private static  final Pattern BLANKPATTERN = Pattern.compile("\\s*|\t|\r|\n");

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Matcher m = BLANKPATTERN.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    static Pattern NUMBERIC_PATTERN = Pattern.compile("^[0-9]+(.[0-9]+)?$");

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {

        Matcher isNum = NUMBERIC_PATTERN.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串中是否含有中文
     */
    public static boolean isCNChar(String s){
        if (DataUtil.isEmpty(s)) return false;
        boolean booleanValue = false;
        for(int i=0; i<s.length(); i++){
            char c = s.charAt(i);
            if(c > 128){
                booleanValue = true;
                break;
            }
        }
        return booleanValue;
    }

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 获取订单号
     * */
    public static String getOrderNo() {
        StringBuffer sb = new StringBuffer();
        sb.append(sdf.format(new Date()));
        sb.append(stringToAscii("zanclick"));
        sb.append(createRandom(true,4));
        return sb.toString();
    }

    /**
     * 获取商户号
     * */
    public static String getMerchantNo() {
        StringBuffer sb = new StringBuffer();
        sb.append(sdf.format(new Date()));
        sb.append(stringToAscii("xs"));
        sb.append(createRandom(true,4));
        return sb.toString();
    }

    /**
     * 获取平台ID
     * */
    public static String getAppId() {
        StringBuffer sb = new StringBuffer();
        sb.append(sdf.format(new Date()));
        sb.append(stringToAscii("x"));
        sb.append(createRandom(true,4));
        return sb.toString();
    }

    /**
     * 获取平台ID
     * */
    public static String getQrCodeNo() {
        StringBuffer sb = new StringBuffer();
        sb.append("QR");
        sb.append(sdf.format(new Date()));
        sb.append(createRandom(true,4));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.err.println(getAppId());
    }

    /**
     * 字符串转 ASCII码
     * @param value
     * */
    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append((int)chars[i]);
        }
        return sbu.toString();
    }


    /**
     * 交易单号创建
     */
    public static String getTradeNo(){
        return System.currentTimeMillis() + createRandom(true, 8);
    }

    public static String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;

        do {
            retStr = "";
            int count = 0;

            for(int i = 0; i < length; ++i) {
                double dblR = Math.random() * (double)len;
                int intR = (int)Math.floor(dblR);
                char c = strTable.charAt(intR);
                if ('0' <= c && c <= '9') {
                    ++count;
                }

                retStr = retStr + strTable.charAt(intR);
            }

            if (count >= 2) {
                bDone = false;
            }
        } while(bDone);

        return retStr;
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }


    public static String substringAfter(Object target, String substr) {
        Assert.notNull(target, "Cannot apply substringAfter on null");
        Assert.notNull(substr, "Parameter substring cannot be null");
        String str = target.toString();
        int index = str.indexOf(substr);
        return index < 0 ? null : new String(str.substring(index + substr.length()));
    }


    public static boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            return isMatch;
        }
    }
    /**
     * 支付结果中的支付渠道格式化
     * @param channels
     * */
    public static String getChannels(String channels) {
        JSONArray array = JSONArray.parseArray(channels);
        if (DataUtil.isEmpty(array)){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0;i<array.size();i++){
            JSONObject object = array.getJSONObject(i);
            if (!object.containsKey("fundChannel")){
                continue;
            }
            sb.append(object.get("fundChannel"));
            sb.append(",");
        }

        return sb.substring(0,sb.length()-1);
    }

    public static String getMethodName(String method) {
        String[] arr = method.split("\\.");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            if (i > 0) {
                sb.append(upperCaseFirst(s));
            } else {
                sb.append(s);
            }
        }
        if (DataUtil.isNotEmpty(sb)) {
            sb.append("Resolver");
        }
        return sb.toString();
    }

    static String upperCaseFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }


    public static final String EMPTY = "";

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }
    public static boolean hasText(String str) {
        return hasText((CharSequence) str);
    }

    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }
}
