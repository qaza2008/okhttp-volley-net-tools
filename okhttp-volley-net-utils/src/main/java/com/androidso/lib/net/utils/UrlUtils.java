package com.androidso.lib.net.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 15/10/16.
 */
public class UrlUtils {

    /**
     * 解析出url请求的路径，包括页面
     *
     * @param strURL url地址
     * @return url路径
     */
    public static String UrlPage(String strURL) {
        String strPage = null;
        String[] arrSplit = null;
        strURL = strURL.trim();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 0) {
            if (arrSplit.length > 1) {
                if (arrSplit[0] != null) {
                    strPage = arrSplit[0];
                }
            }
        }
        return strPage;
    }

    /**
     * 获取去掉参数的url
     *
     * @param strURL
     * @return
     */

    public static String getUrl(String strURL) {
        String strPage = null;
        String[] arrSplit = null;
        strURL = strURL.trim();
        arrSplit = strURL.split("[?]");
        return arrSplit[0];

    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;
        strURL = strURL.trim();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }
        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> URLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
//每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
//解析出键值
            if (arrSplitEqual.length > 1) {
//正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
//只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 目的向url注入参数,如果发现存在相同的key,替换之value
     *
     * @param strURL
     * @param params
     * @return
     */
    public static String getURLByAddParams(String strURL, Map<String, String> params) {

        StringBuffer buffer = new StringBuffer();
        if (params == null || params.size() == 0) {
            return strURL;
        }

        //
        Map<String, String> oldParams = URLRequest(strURL);
        for (Map.Entry<String, String> me : oldParams.entrySet()) {
            if (!params.containsKey(me.getKey())) {
                params.put(me.getKey(), me.getValue());
            }
        }


        String[] arrSplit = null;
        strURL = getUrl(strURL.trim());
        buffer.append(strURL);
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                //包含参数
                for (Map.Entry<String, String> me : params.entrySet()) {
                    buffer.append("&");
                    buffer.append(me.getKey() + "=" + me.getValue());
                }
            } else {
                //不包含参数
                buffer.append("?");
                int i = 0;
                for (Map.Entry<String, String> me : params.entrySet()) {

                    if (i != 0) {
                        buffer.append("&");
                    }
                    buffer.append(me.getKey() + "=" + me.getValue());
                    i++;
                }

            }
        }
        return buffer.toString();


    }

    public static void main(String[] args) {


        Map<String, String> params = new HashMap<>();
        params.put("uin", "wangsh");
        params.put("appToken", "KSOIEJ");
//        System.out.print(getURLByAddParams("www.baidu.com?uin=zhangsan&appToken=KKK", params));
        System.out.print(getURLByAddParams("www.baidu.com?uin=zhangsan&appToken=KKK", null));

    }

}
