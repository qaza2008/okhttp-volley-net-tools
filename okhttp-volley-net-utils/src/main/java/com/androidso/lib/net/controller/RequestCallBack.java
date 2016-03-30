package com.androidso.lib.net.controller;

import android.content.Context;
import android.util.Log;

import com.androidso.lib.net.api.DialogImp;
import com.androidso.lib.net.api.DialogInterface;
import com.androidso.lib.net.api.HttpConfig;
import com.androidso.lib.net.api.NetRequestListener;

import org.json.JSONObject;


/**
 * 请实现这个类在Activity和Fragment
 */
public class RequestCallBack implements NetRequestListener {

    private Context context;
    private NetRequestListener listener;
    private boolean showDialog;
    private String dialogMsg;
    private DialogInterface d;

    public RequestCallBack(Context context, NetRequestListener listener, boolean showDialog) {
        super();
        this.context = context;
        this.listener = listener;
        this.showDialog = showDialog;
    }

    public RequestCallBack(Context context, NetRequestListener listener, String dialogMsg) {
        super();
        this.context = context;
        this.listener = listener;
        this.showDialog = true;
        this.dialogMsg = dialogMsg;
    }

    @Override
    public void requestDidStart(String requestTag) {

        if (showDialog) {
            d = new DialogImp(context, dialogMsg);
            d.showDialog();
        }

        if (listener != null) {
            listener.requestDidStart(requestTag);
        }

    }

    @Override
    public void requestLoading(long totalCount, long current) {
        if (listener != null) {
            listener.requestLoading(totalCount, current);
        }

    }

    @Override
    public void requestDidSuccess(String requestTag, JSONObject response) {
        if (d != null)
            d.dismissDialog();
        context = null;

        if (listener != null) {
            listener.requestDidSuccess(requestTag, response);
        }

    }

    @Override
    public void requestDidFailed(String requestTag, Throwable t, int errorNo, String strMsg) {
        if (d != null)
            d.dismissDialog();
        if (listener != null/* && errorNo != ERROR_CODE_NO_NETWORK*/) {
            listener.requestDidFailed(requestTag, t, errorNo, strMsg);
        }
        context = null;

    }

    @Override
    public void requestDidCancel(String requestTag) {
        if (listener != null) {
            listener.requestDidCancel(requestTag);
        }
        if (HttpConfig.DEBUG)
            Log.d("取消网络请求", this.getClass().toString());
        if (d != null)
            d.dismissDialog();

    }

    public NetRequestListener getListener() {
        return listener;
    }
}
