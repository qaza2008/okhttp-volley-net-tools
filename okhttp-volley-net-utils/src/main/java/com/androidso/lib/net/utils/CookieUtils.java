/*
 *                               _ooOoo_
 *                              o8888888o
 *                              88" . "88
 *                              (| -_- |)
 *                               O\ = /O
 *                           ____/`---'\____
 *                         .   ' \\| |// `.
 *                          / \\||| : |||// \
 *                        / _||||| -:- |||||- \
 *                          | | \\\ - / | |
 *                        | \_| ''\---/'' | |
 *                         \ .-\__ `-` ___/-. /
 *                      ___`. .' /--.--\ `. . __
 *                   ."" '< `.___\_<|>_/___.' >'"".
 *                  | | : `- \`.;`\ _ /`;.`/ - ` : | |
 *                    \ \ `-. \_ __\ /__ _/ .-` / /
 *            ======`-.____`-.___\_____/___.-`____.-'======
 *                               `=---='
 *
 *            .............................................
 *                     佛祖保佑             永无BUG
 *             佛曰:
 *                     写字楼里写字间，写字间里程序员；
 *                     程序人员写程序，又拿程序换酒钱。
 *                     酒醒只在网上坐，酒醉还来网下眠；
 *                     酒醉酒醒日复日，网上网下年复年。
 *                     但愿老死电脑间，不愿鞠躬老板前；
 *                     奔驰宝马贵者趣，公交自行程序员。
 *                     别人笑我忒疯癫，我笑自己命太贱；
 *                     不见满街漂亮妹，哪个归得程序员？
 */

package com.androidso.lib.net.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.jd.paipai.net.BuildConfig;

/**
 * cookie持久化
 *
 * Created by zhenguo on 1/28/16.
 */
public class CookieUtils {

    private static final String COOKIES_FILE = "cookies_file";

    public synchronized static void saveCookies(Context context, String cookies) {
        if (cookies == null) {
            if (BuildConfig.DEBUG) Log.d("CookieUtils", "cookies == null");
            return;
        }
        if (context==null){
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(COOKIES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
            editor.putString("cookies", cookies);
        editor.commit();

    }

    public synchronized static String  getCookies(Context context) {
        if(context==null){
            return "";
        }
        SharedPreferences preferences = context.getSharedPreferences(COOKIES_FILE, Context.MODE_PRIVATE);
        String cookies = preferences.getString("cookies", "");
        return cookies;
    }

}
