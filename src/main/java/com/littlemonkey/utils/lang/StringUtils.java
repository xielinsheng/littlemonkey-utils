package com.littlemonkey.utils.lang;

import com.google.common.base.Joiner;
import com.littlemonkey.utils.base.Constants;

import java.util.Map;


public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * @param map
     * @param separator
     * @param keyValueSeparator
     * @return
     */
    public static String join(Map map, final String separator, final String keyValueSeparator) {
        if (map == null || map.isEmpty()) {
            return Constants.BLANK;
        }
        return Joiner.on(separator).withKeyValueSeparator(keyValueSeparator).join(map);
    }
}
