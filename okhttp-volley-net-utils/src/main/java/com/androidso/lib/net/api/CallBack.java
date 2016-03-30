package com.androidso.lib.net.api;

import com.squareup.okhttp.Response;

/**
 * Created by wangs on 16/1/29 15:30.
 */
public abstract class CallBack<T> implements NetRequestListener {
    /**
     * 进度条
     *
     * @param progress
     */
    public void inPrgress(float progress) {
    }

    public abstract T parseNetWorkResponse(Response response) throws Exception;


}
