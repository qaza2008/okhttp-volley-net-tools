package com.androidso.lib.net.api;



import org.json.JSONObject;

/**
 * Created by mac on 16/1/14 15:53.
 */
public interface  NetRequestListener {

    public static final int ERROR_CODE_NO_NETWORK = 99999;

    /**
     * 开始发起网络请求
     */
    public abstract void requestDidStart(String requestTag);

    /**
     * 网络请求中
     */

    public abstract void requestLoading(long totalCount, long current);

    /**
     * 网络请求成功
     */
    public abstract void requestDidSuccess(String requestTag, JSONObject response);

    /**
     * 网络请求失败
     */
    public abstract void requestDidFailed(String requestTag, Throwable t, int errorNo, String strMsg);

    /**
     * 网络请求失败
     */
    public abstract void requestDidCancel(String requestTag);



}
