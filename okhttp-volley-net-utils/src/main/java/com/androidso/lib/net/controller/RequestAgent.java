package com.androidso.lib.net.controller;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidso.lib.net.api.HttpConfig;
import com.androidso.lib.net.api.NetRequestListener;
import com.androidso.lib.net.api.NetStartListener;
import com.androidso.lib.net.http.AjaxParams;
import com.androidso.lib.net.http.RequestManager;
import com.androidso.lib.net.utils.NetUtil;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangs on 16/1/15 11:34.
 */
public class RequestAgent<T> implements Response.Listener<JSONObject>, Response.ErrorListener, NetStartListener {
    private static final String TAG = RequestAgent.class.getSimpleName();
    private String requestTag = TAG;
    private RequestCallBack callBack;
    private String url;
    private AjaxParams ajaxParams;

    public RequestAgent(String requestTag, String url, RequestCallBack callBack, Map<String, String> params) {
        this.requestTag = requestTag;
        this.callBack = callBack;
        this.url = url;
        if (params != null) {
            ajaxParams = new AjaxParams(params);
        }
    }

    public RequestAgent(String requestTag, String url, RequestCallBack callBack, String stringParams) {
        this.requestTag = requestTag;
        this.callBack = callBack;
        this.url = url;
        if (!TextUtils.isEmpty(stringParams)) {
            ajaxParams = new AjaxParams(stringParams);
        }
    }

    @Override
    public void onStart() {

        if (HttpConfig.DEBUG)
        Log.d(TAG + "-requestTag: ***** " + this.requestTag + " *****", "********* 网络请求开始 Start *********");
        if (HttpConfig.DEBUG)
        Log.d(TAG + "-requestTag: ***** " + this.requestTag + " *****", "URL: " + this.url);
        try {
            if (ajaxParams != null) {
                if (HttpConfig.DEBUG)
                Log.d(TAG + "-requestTag: ***** " + this.requestTag + " *****", "Params: " + URLDecoder.decode(this.ajaxParams.getParamString(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {

            if (HttpConfig.DEBUG)
            Log.d(TAG + "-requestTag:***** " + this.requestTag + " *****", "params Decode出错");
            if (HttpConfig.DEBUG)
            Log.d(TAG + "-requestTag: ***** " + this.requestTag + " *****", "Params: " + this.ajaxParams.getParamString());

        }
        if (callBack != null)
            callBack.requestDidStart(requestTag);
    }

    @Override
    public void onResponse(JSONObject response) {

        if (HttpConfig.DEBUG)
        Log.d(TAG + "-requestTag: ***** " + requestTag + " *****", "********* 网络请求成功 Success *********");
        if (response == null) {
            if (HttpConfig.DEBUG)
            Log.e(TAG + "-requestTag: ***** " + requestTag + " *****", "*********  解析后的实体为空 *********");

        }
        if (HttpConfig.DEBUG)
        Log.d(TAG + "-requestTag: ***** " + requestTag + " *****", "response:" + response.toString());
        if (callBack != null)
            callBack.requestDidSuccess(requestTag, response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (HttpConfig.DEBUG)
        Log.d(TAG, error.toString());
        if (HttpConfig.DEBUG)
        Log.d(TAG + "-requestTag: ***** " + requestTag + " *****", "********* 网络请求结束 requestDidFinished *********");

        if (error.getCause() instanceof UnsupportedEncodingException) {

            if (HttpConfig.DEBUG)
            Log.d(TAG + "-requestTag: ***** " + requestTag + " *****", "********* UnsupportedEncodingException 不支持的类型 解析失败 *********");

        } else if (error.getCause() instanceof JsonSyntaxException) {

            if (HttpConfig.DEBUG)
            Log.d(TAG + "-requestTag: ***** " + requestTag + " *****", "*********JsonSyntaxException Gson Json 解析失败 *********");

        } else {

            if (HttpConfig.DEBUG)
            Log.d(TAG + "-requestTag: ***** " + requestTag + " *****", "*********Exception 解析失败 *********");
        }

        if (HttpConfig.DEBUG)
        Log.d(TAG + "-requestTag: ***** " + requestTag + " *****", "Exception:\n" + error.getLocalizedMessage());

        if (error.networkResponse == null) {
            callBack.requestDidFailed(requestTag, error.getCause(), NetRequestListener.ERROR_CODE_NO_NETWORK, error.getMessage());
            return;
        }
        int errorNo = error.networkResponse.statusCode;
        switch (errorNo) {
            case 404:
                // Toast.makeText(CoreConstants.CONTEXT, "网络请求出错，找不到页面-404", Toast.LENGTH_SHORT).show();
                Toast.makeText(RequestManager.mCtx, "您的手机网络不太顺畅哦～", Toast.LENGTH_SHORT).show();
                break;
            case 500:
                // Toast.makeText(CoreConstants.CONTEXT, "网络请求出错，内部服务器错误-500", Toast.LENGTH_SHORT).show();
                Toast.makeText(RequestManager.mCtx, "您的手机网络不太顺畅哦～", Toast.LENGTH_SHORT).show();
                break;
            case 0:
                if (!NetUtil.isNetworkAvailable(RequestManager.mCtx)) {
                    // Toast.makeText(CoreConstants.CONTEXT, "网络异常，请检查您的网络", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        String strMsg = error.getMessage();
        if (strMsg != null && strMsg.contains("unknownHost")) {
            ConnectivityManager connectMgr = (ConnectivityManager) RequestManager.mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if ((wifiNetInfo == null || !wifiNetInfo.isConnected()) && (mobileNetInfo == null || !mobileNetInfo.isConnected())) {
                errorNo = NetRequestListener.ERROR_CODE_NO_NETWORK;
                if (isRunningForeground(RequestManager.mCtx)) {
                    if (!HAS_SHOW_NET_ERROR) {
                        setHasShowNetError(true);
                        Toast.makeText(RequestManager.mCtx, "您的手机网络不太顺畅哦～", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (isRunningForeground(RequestManager.mCtx)) {
                    if (!HAS_SHOW_NET_ERROR) {
                        setHasShowNetError(true);
                        Toast.makeText(RequestManager.mCtx, "您的手机网络不太顺畅哦～", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        if (callBack != null)
            callBack.requestDidFailed(requestTag, error.getCause(), error.networkResponse.statusCode, error.getMessage());

    }


    private boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();

        return !TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName());
    }

    /**
     * 是否已经弹出网络异常的提醒flag
     */
    public static boolean HAS_SHOW_NET_ERROR = false;

    static Timer timer;

    public static void setHasShowNetError(boolean hasShowNetError) {
        HAS_SHOW_NET_ERROR = hasShowNetError;
        if (HAS_SHOW_NET_ERROR) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    HAS_SHOW_NET_ERROR = false;
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                }
            }, 1000);
        }
    }

    public RequestCallBack getCallBack() {
        return callBack;
    }

}
