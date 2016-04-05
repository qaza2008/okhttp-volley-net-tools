package com.androidso.lib.net.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidso.lib.net.api.DialogInterface;
import com.androidso.lib.net.api.FileCallBack;
import com.androidso.lib.net.api.HttpConfig;
import com.androidso.lib.net.api.MulThreadRequest;
import com.androidso.lib.net.api.NetRequestListener;
import com.androidso.lib.net.http.AjaxParams;
import com.androidso.lib.net.http.CustomRequest;
import com.androidso.lib.net.http.RequestManager;
import com.androidso.lib.net.utils.CookieUtils;
import com.androidso.lib.net.utils.UrlUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 16/1/14 16:37.
 * //请使用此类进行网络请求
 * //TODO
 * 上传待优化
 * <p/>
 * <p/>
 * 整体带测试
 */
public class DaoRequest {
    private static final String TAG = DaoRequest.class.getSimpleName();

    private static Map<String, String> headers;
    private static Map<String, String> cookies;
    private static Map<String, String> commonParams;

    private DaoRequest() {

    }


    public static DialogInterface dialogInterface;


    /**
     * @param context
     * @param requestTag
     * @param url
     * @param params
     * @param netRequestListener //     * @param clazz              为空的话 返回String
     * @param <T>
     */
    public static <T> void get(@NonNull Context context, @NonNull final String requestTag, @NonNull String url, Map<String, String> params, @NonNull NetRequestListener netRequestListener) {
        get(context, requestTag, url, params, netRequestListener, false, HttpConfig.DEFAULT_CHARSET);

    }

    public static <T> void get(@NonNull Context context, @NonNull final String requestTag, @NonNull String url, Map<String, String> params, @NonNull NetRequestListener netRequestListener, boolean showDialog, String charset) {
        get(context, requestTag, url, params, netRequestListener, showDialog, charset, false);

    }

    /**
     * @param context
     * @param requestTag         tag
     * @param url                url
     * @param params
     * @param netRequestListener
     * @param showDialog         默认false
     * @param <T>
     */
    public static <T> void get(@NonNull Context context, @NonNull final String requestTag, @NonNull String url, Map<String, String> params, @NonNull NetRequestListener netRequestListener, boolean showDialog, String charset, boolean shouCache) {
        params = getFinalParams(params);
        final RequestCallBack callBack = new RequestCallBack(context, netRequestListener, showDialog);
        RequestAgent<T> agent = new RequestAgent<T>(requestTag, url, callBack, params);

        //将url和params进行封装
        url = UrlUtils.getURLByAddParams(url, params);

        CustomRequest request = new CustomRequest.RequestBuilder()
                .get()
                .url(url)//url会统一配置到requestUrl类中
//                .clazz(clazz) //如果设置了返回类型，会自动解析返回model 如果不设置会直接返回json数据;
                .successListener(agent)//获取数据成功的listener
                .errorListener(agent)//获取数据异常的listener
                .charset(charset)
                .headers(getHeader(context))
                .shouldCache(shouCache)
                .build();
        agent.onStart();
        RequestManager.getInstance(context).addRequest(request, requestTag);
    }

    public static <T> void post(@NonNull final Context context, @NonNull final String requestTag, @NonNull String url, Map<String, String> params, @NonNull NetRequestListener netRequestListener, boolean showDialog) {
        post(context, requestTag, url, params, netRequestListener, showDialog, false);

    }

    public static <T> void post(@NonNull final Context context, @NonNull final String requestTag, @NonNull String url, Map<String, String> params, @NonNull NetRequestListener netRequestListener, boolean showDialog, boolean shouCache) {
        params = getFinalParams(params);
        final RequestCallBack callBack = new RequestCallBack(context, netRequestListener, showDialog);
        RequestAgent<T> agent = new RequestAgent<T>(requestTag, url, callBack, params);

        CustomRequest request = new CustomRequest.RequestBuilder()
                .post()
                .url(url)
                .params(params)
//                .clazz(clazz) //如果设置了返回类型，会自动解析返回model 如果不设置会直接返回json数据;
                .successListener(agent)//获取数据成功的listener
                .errorListener(agent)//获取数据异常的listener
                .headers(getHeader(context))
                .shouldCache(shouCache)
                .build();
        agent.onStart();
        RequestManager.getInstance(context).addRequest(request, requestTag);
    }

    /**
     * @param context
     * @param url
     * @param fileCallBack 返回filePath为key 的jsonObject
     */
    public static void download(Context context, final String url, final FileCallBack fileCallBack) {

        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = null;
        try {
            requestBuilder = new Request.Builder()
                    .get()
                    .url(url);
        } catch (Exception e) {
            e.printStackTrace();
            fileCallBack.requestDidFailed("downLoad=" + url, e.getCause(), HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage());
            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            return;

        }
        final Request request = requestBuilder.build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

                if (HttpConfig.DEBUG)
                    Log.d(TAG, "downLoad onFailure");
                fileCallBack.requestDidFailed("downLoad=" + url, e.getCause(), 999, e.getMessage());

            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                if (HttpConfig.DEBUG)
                    Log.d(TAG, "downLoad onResponse");
                try {
                    if (response.code() >= 400 && response.code() <= 599) {
                        fileCallBack.requestDidFailed("downLoad=" + url, new RuntimeException(response.body().string()), response.code(), "网络请求异常");
                        return;
                    }

                    File file = fileCallBack.parseNetworkResponse(response);


                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("filePath", file.getAbsolutePath());
                    Log.d("DaoRequest", file.getAbsolutePath());
                    fileCallBack.requestDidSuccess("downLoad=" + url, jsonObject);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }


    public static void upLoad(final Context context, final String requestTag, final String url,
                              @NonNull AjaxParams params, String contentType,
                              final NetRequestListener netRequestListener, final String dialogMsg) {
        final RequestCallBack callBack = new RequestCallBack(context, netRequestListener, dialogMsg);


        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callBack.requestDidSuccess(requestTag, response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callBack.requestDidFailed(requestTag, error.getCause(), error.networkResponse.statusCode, error.getMessage());

            }

        };
        params.getEntity();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .tag(requestTag)
                .post(params.getEntity())
                .build();
        callBack.requestDidStart(requestTag);
        try {
            com.squareup.okhttp.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                errorListener.onErrorResponse(new VolleyError(response.message()));
            } else {
                listener.onResponse(new JSONObject(response.message()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (
                JSONException e
                )

        {
            e.printStackTrace();
        }

    }

    public static void addCookie(String key, String value) {
        if (TextUtils.isEmpty(key) && TextUtils.isEmpty(value)) {
            if (HttpConfig.DEBUG) Log.d(TAG, "can not add empty header(key and value both empty)");
            return;
        }
        if (cookies == null) {
            cookies = new HashMap<>();
        }
        cookies.put(key, value);
    }

    public static String getCookies() {
        StringBuffer sb = new StringBuffer();
        if (cookies != null && cookies.size() > 0) {
            for (String key : cookies.keySet()) {
                sb.append(",").append(key).append("=").append(cookies.get(key));
            }
            sb.replace(0, 1, "");
        }
        if (HttpConfig.DEBUG) Log.d(TAG, "cookies : " + sb.toString());
        return sb.toString();
    }

    public static void addHeader(String key, String value) {
        if (TextUtils.isEmpty(key) && TextUtils.isEmpty(value)) {
            if (HttpConfig.DEBUG) Log.d(TAG, "can not add empty header(key and value both empty)");
            return;
        }
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
    }

    public static Map<String, String> getHeader(Context context) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        String cookies = getCookies();
        if (TextUtils.isEmpty(cookies)) {
            cookies = CookieUtils.getCookies(context);
        } else {
            CookieUtils.saveCookies(context, cookies);
        }
        headers.put("Cookie", getCookies());
        return headers;
    }

    public static void addCommonParams(String k, String value) {
        if (commonParams == null) {
            commonParams = new HashMap<>();
        }
        commonParams.put(k, value);
    }

    /**
     * 获取最终的参数，添加公共参数之后的
     *
     * @return
     */
    private synchronized static Map<String, String> getFinalParams(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        if (commonParams != null && commonParams.size() > 0) {
            params.putAll(commonParams);
        }
        return params;
    }


    //组合模式 适配器模式...
    //支持多线程 和断点续传
    public static void download(Context context, final String url, final FileCallBack fileCallBack, final boolean mulThreadFlag, final int mulThreadCount) {

        final OkHttpClient client = new OkHttpClient();
        Request.Builder builder;
        try {
            builder = new Request.Builder().url(url);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            fileCallBack.requestDidFailed("downLoad=" + url, e.getCause(), HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage());
            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            return;

        }
        final MulThreadRequest mulThreadRequest = new MulThreadRequest();
        final MulThreadRequest.RequestMap parentReq = new MulThreadRequest.RequestMap();
        parentReq.setName("url:" + url);
        parentReq.setBuilder(builder);
        parentReq.setCallback(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (HttpConfig.DEBUG)
                    Log.d(TAG, "downLoad onFailure");
                fileCallBack.requestDidFailed("downLoad=" + url, e.getCause(), 999, e.getMessage());
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                if (HttpConfig.DEBUG)
                    Log.d(TAG, "downLoad onResponse");
                try {
                    if (response.code() >= 400 && response.code() <= 599) {
                        fileCallBack.requestDidFailed("downLoad=" + url, new RuntimeException(response.body().string()), response.code(), "网络请求异常");
                        return;
                    }
                    //单线程
                    if (!mulThreadFlag) {
                        File file = fileCallBack.parseNetworkResponse(response);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("filePath", file.getAbsolutePath());
                        Log.d("DaoRequest", file.getAbsolutePath());
                        fileCallBack.requestDidSuccess("downLoad=" + url, jsonObject);
                    } else {
                        //多线程
                        final int threadCount = mulThreadCount <= 0 ? MulThreadRequest.THREAD_COUNT_DEFAULT : mulThreadCount;
                        final File file = fileCallBack.parseNetworkResponse(response, mulThreadFlag);
                        final String filePath = file.getAbsolutePath();
                        long fileSize = fileCallBack.getFileSize(response);

                        long block = fileSize % threadCount == 0 ? fileSize / threadCount : fileSize / threadCount + 1;
                        for (int i = 0; i < threadCount; i++) {
                            long start = i * block;
                            long end = start + block >= fileSize ? fileSize : start + block - 1;
                            MulThreadRequest.RequestMap childRequestMap = new MulThreadRequest.RequestMap();
                            childRequestMap.setName(parentReq.getName() + " Range:bytes=" + start + "-" + end);
                            Request.Builder childBuilder = new Request.Builder();

                            childRequestMap.setBuilder(
                                    childBuilder.url(url).addHeader("Range", "bytes=" + start + "-" + end)
                            );
                            childRequestMap.setStartPos(start);
                            childRequestMap.setEndPos(end);
                            Log.d(TAG, "multhread " + " i= " + i + "  " + childRequestMap.toString());

                            mulThreadRequest.add(childRequestMap);

                        }
                        final Callback callbackAgent = new Callback() {
                            int responseCount = 0;

                            @Override
                            public void onFailure(Request request, IOException e) {
                                if (HttpConfig.DEBUG)
                                    Log.d(TAG, "downLoad onFailure");
                                fileCallBack.requestDidFailed("downLoad=" + url, e.getCause(), 999, e.getMessage());
                                while (mulThreadRequest.getChildren().hasMoreElements()) {
                                    MulThreadRequest.RequestMap childRequestMap = mulThreadRequest.getChildren().nextElement();
                                    client.cancel(childRequestMap.getBuilder().build());
                                }
                            }

                            @Override
                            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                                responseCount++;
                                if (responseCount == threadCount) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("filePath", filePath);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("DaoRequest", file.getAbsolutePath());
                                    fileCallBack.requestDidSuccess("downLoad=" + url, jsonObject);
                                }
                            }
                        };

                        Enumeration<MulThreadRequest.RequestMap> iterator = mulThreadRequest.getChildren();
                        while (iterator.hasMoreElements()) {
                            MulThreadRequest.RequestMap childRequestMap = iterator.nextElement();
                            Log.d(TAG, "while 1: " + childRequestMap.toString());
                            client.newCall(childRequestMap.getBuilder().build()).enqueue(new ChildeCallback(childRequestMap) {

                                @Override
                                public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                                    super.onResponse(response);
                                    Log.d(TAG, "while 2: onResponse");
                                    requestMap.getStartPos();
                                    fileCallBack.parseNetworkResponse(response, requestMap.getStartPos(), requestMap.getEndPos());
                                    callbackAgent.onResponse(response);
                                }

                                @Override
                                public void onFailure(Request request, IOException e) {
                                    super.onFailure(request, e);
                                    Log.d(TAG, "while 3: onFailure");
                                    callbackAgent.onFailure(request, e);
                                }
                            });
                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mulThreadRequest.setParentBuilder(parentReq);
        client.newCall(parentReq.getBuilder().build()).enqueue(parentReq.getCallback());


    }

    static class ChildeCallback implements Callback {
        MulThreadRequest.RequestMap requestMap;

        public ChildeCallback(MulThreadRequest.RequestMap requestMap) {
            this.requestMap = requestMap;
        }

        @Override
        public void onFailure(Request request, IOException e) {
        }

        @Override
        public void onResponse(com.squareup.okhttp.Response response) throws IOException {

        }
    }


}
