package com.androidso.lib.net.http;

import android.content.Context;

import android.support.annotation.Nullable;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.androidso.lib.net.api.HttpConfig;
import com.androidso.lib.net.api.NetRequestListener;
import com.androidso.lib.net.controller.RequestAgent;
import com.jd.paipai.net.BuildConfig;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import java.io.File;


/**
 * 把这个放到Application中 初始化
 *
 * @author wangs
 */
public class RequestManager {

    private final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;

    private static RequestManager mInstance;
    public static Context mCtx;
    public RequestQueue mRequestQueue;

    private RequestManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }


    public static synchronized RequestManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestManager(context);
        }
        return mInstance;
    }

    /**
     * Returns a Volley request queue for creating network requests
     *
     * @return {@link com.android.volley.RequestQueue}
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
//            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            OkHttpClient okHttpClient = new OkHttpClient();
//            final File baseCacheDir = mCtx.getCacheDir();
//            if (baseCacheDir != null) {
//                Log.d("RequestManager", "baseCacheDir != null");
//                final File cacheDir = new File(baseCacheDir, "HttpResponseCache");
//                Cache cache = new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
//                okHttpClient.setCache(cache);
//            } else {
//                Log.d("RequestManager", "baseCacheDir == null");
//            }
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), new OkHttpStack(okHttpClient));
        }
        return mRequestQueue;
    }

    /**
     * Adds a request to the Volley request queue
     *
     * @param request is the request to add to the Volley queue
     */
    public <T> void addRequest(Request<T> request) {
        getRequestQueue().add(request);
    }

    /**
     * Adds a request to the Volley request queue
     *
     * @param request is the request to add to the Volley queuest
     * @param tag     is the tag identifying the request
     */
    public <T> void addRequest(Request<T> request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    /**
     * Cancels all the request in the Volley queue for a given tag
     *
     * @param tag associated with the Volley requests to be cancelled
     */
    public void cancelAllRequests(String tag) {
        if (getRequestQueue() != null) {
            getRequestQueue().cancelAll(tag);
        }
    }

    public void cancelRequests(final NetRequestListener tag) {
        if (getRequestQueue() != null) {
            // getRequestQueue().cancelAll(tag);
            getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    // 获取listener和tag对比 如果一样则取消
                    if (request != null && request.getErrorListener() != null) {
                        if (request.getErrorListener() instanceof RequestAgent) {
                            RequestAgent requestAgent = (RequestAgent) request.getErrorListener();
                            if (requestAgent.getCallBack() != null
                                    && requestAgent.getCallBack().getListener() != null) {

                                if (requestAgent.getCallBack().getListener().equals(tag)) {
                                    if (HttpConfig.DEBUG)
                                        Log.d("RequestManager", "cancel request tag : " + request.getTag());
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                }
            });
        }
    }

    /**
     * 使用和参数配置范例
     *
     * @param param1
     * @param param2
     * @param listener
     * @param errorListener
     */
//
//    public void getDemoData(String param1,
//                            String param2,
//                            Response.Listener listener,
//                            Response.ErrorListener errorListener, String tag) {
//        Map<String, String> params = new HashMap<>();
//        params.put("param1", param1);
//        params.put("param2", param2);
//
//        CustomRequest request = new CustomRequest().RequestBuilder()
////                .post()//不设置的话默认GET 但是设置了参数就不需要了。。。
//                .url("")//url会统一配置到requestUrl类中
//                .addMethodParams("") //请求的方法名
//                        // 添加参数方法1 适用参数比较多的情况下
////                .params(params)
//                        // 添加参数方法2
//                .addParams("param1", param1)//添加参数1
//                .addParams("param2", param2)//添加参数2
////                .clazz(Test.calss) //如果设置了返回类型，会自动解析返回model 如果不设置会直接返回json数据;
//                .successListener(listener)//获取数据成功的listener
//                .errorListener(errorListener)//获取数据异常的listener
//
//                .build();
////        addRequest(request, tag);
//
//        //将请求add到队列中。并设置tag  并需要相应activity onStop方法中调用cancel方法
//    }


}