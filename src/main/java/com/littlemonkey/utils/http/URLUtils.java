package com.littlemonkey.utils.http;

import com.google.common.base.Joiner;
import com.littlemonkey.utils.base.Constants;
import com.littlemonkey.utils.lang.StringUtils;

import java.util.Map;

/**
 * url工具类
 */
public class URLUtils {

    /**
     * <p>拼接get请求参数到请求url</p>
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return
     */
    public static String join(String url, Map<String, String> param) {
        if (StringUtils.indexOf(url, Constants.QUERY) > -1) {
            return Joiner.on(Constants.BLANK).join(url, Constants.AD, StringUtils.join(param, Constants.AD, Constants.QUERY));
        } else {
            return Joiner.on(Constants.BLANK).join(url, Constants.QUERY, StringUtils.join(param, Constants.AD, Constants.QUERY));
        }
    }
}
