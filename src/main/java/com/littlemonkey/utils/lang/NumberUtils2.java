package com.littlemonkey.utils.lang;

import org.apache.commons.lang3.math.NumberUtils;

public class NumberUtils2 {

    public static boolean isNumber(String str) {
        return NumberUtils.isCreatable(str);
    }

    public static boolean isNumberClass(Class targetClass) {
        return int.class.equals(targetClass) || byte.class.equals(targetClass) ||
                double.class.equals(targetClass) || short.class.equals(targetClass) ||
                long.class.equals(targetClass) || float.class.equals(targetClass) ||
                Integer.class.equals(targetClass) || Double.class.equals(targetClass) ||
                Byte.class.equals(targetClass) || Short.class.equals(targetClass) ||
                Long.class.equals(targetClass) || Float.class.equals(targetClass);
    }

    public static <T> T parseNumber(String text, Class targetClass) {
        Class tClass = targetClass;
        if (int.class.equals(targetClass)) {
            tClass = Integer.class;
        } else if (byte.class.equals(targetClass)) {
            tClass = Byte.class;
        } else if (short.class.equals(targetClass)) {
            tClass = Short.class;
        } else if (long.class.equals(targetClass)) {
            tClass = Long.class;
        } else if (float.class.equals(targetClass)) {
            tClass = Float.class;
        }
        return (T) org.springframework.util.NumberUtils.parseNumber(text, tClass);
    }

}
