package com.zanclick.prepay.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author duchong
 * @description
 * @date
 */
public class H5CityCode {

    public static Map<String,String> cityCodeMap = new HashMap<>();
    static {
        cityCodeMap.put("440100","");
        cityCodeMap.put("440200","");
        cityCodeMap.put("440300","");
        cityCodeMap.put("440400","");
        cityCodeMap.put("440500","");
        cityCodeMap.put("440600","");
        cityCodeMap.put("440700","");
        cityCodeMap.put("440800","");
        cityCodeMap.put("440900","");
        cityCodeMap.put("441200","");
        cityCodeMap.put("441300","");
        cityCodeMap.put("441400","");
        cityCodeMap.put("441500","");
        cityCodeMap.put("441600","");
        cityCodeMap.put("441700","");
        cityCodeMap.put("441800","");
        cityCodeMap.put("441900","");
        cityCodeMap.put("442000","");
        cityCodeMap.put("445100","");
        cityCodeMap.put("445200","");
        cityCodeMap.put("445300","");
    }


    public static String getCityCode(String code){
        return cityCodeMap.get(code);
    }
}
