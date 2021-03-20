package com.douyuehan.doubao.enumeration;

import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * 公共枚举
 *
 * @author liyonghai
 * @version 0.1
 * @date 2021/3/14 23:56
 */
public enum CommParamEnum {
    COMMON_PARAM_LATEST("latest", "最新"),
    COMMON_PARAM_HOT("hot", "最热");

    public static Map<String, String> keyValue;
    public String key;
    public String value;

    static {
        keyValue = new TreeMap<String, String>();
        for (CommParamEnum enumData : EnumSet.allOf(CommParamEnum.class)) {
            keyValue.put(enumData.key, enumData.value);
        }
    }

    private CommParamEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public final String value() {
        return (String) keyValue.get(this.key);
    }

    public static String lookup(String key) {
        return (String) keyValue.get(key);
    }

    public static String key(String dataValue) {
        String key = null;
        for (CommParamEnum enumData : values()) {
            if (enumData.value.equals(dataValue)) {
                key = enumData.key;
                break;
            }
        }
        return key;
    }
}
