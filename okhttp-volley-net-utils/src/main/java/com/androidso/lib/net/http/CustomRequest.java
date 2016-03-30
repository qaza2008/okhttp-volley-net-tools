package com.androidso.lib.net.http;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.androidso.lib.net.api.HttpConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 16/1/14 17:25.
 */
public class CustomRequest<T> extends Request<T> {
    private static final String TAG = CustomRequest.class.getSimpleName();
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private Map<String, String> params;
    private String charset = HttpConfig.DEFAULT_CHARSET;


    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url    URL of the request to make
     * @param clazz  Relevant class object, for Gson's reflection
     * @param params Map of request params
     */
    public CustomRequest(String url, Class<T> clazz, Map<String, String> params,
                         Response.Listener<T> listener, Response.ErrorListener errorListener, String charset) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = null;
        this.params = params;
        this.listener = listener;
        this.charset = charset;
    }

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public CustomRequest(int method, String url, Class<T> clazz, Map<String, String> headers,
                         Map<String, String> params,
                         Response.Listener<T> listener, Response.ErrorListener errorListener, String charset) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
        this.charset = charset;
    }

    /**
     * @param builder requestBuilder
     */
    public CustomRequest(RequestBuilder builder) {
        super(builder.method, builder.url, builder.errorListener);
        clazz = builder.clazz;
        headers = builder.headers;
        listener = builder.successListener;
        params = builder.params;
        charset = builder.charset;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getParams();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {

            if (TextUtils.isEmpty(charset)) {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } else {
                parsed = new String(response.data, charset);
            }
            if (HttpConfig.DEBUG)
            Log.d(TAG + "requestTag:***" + getTag() + "***", "网络请求已返回  *****");
            if (HttpConfig.DEBUG)
            Log.d(TAG + "requestTag:***" + getTag() + "***", "response:\n" + parsed);

            JSONObject jsonObject = new JSONObject(parsed);

            if (clazz == null) {
                return (Response<T>) Response.success(jsonObject,
                        HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.success(gson.fromJson(parsed, clazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }

    }

    /**
     * requestBiulder  使用方法参见httpClientRequest
     */
    public static class RequestBuilder {
        private int method = Method.GET;
        private String url;
        private Class clazz;
        private Response.Listener successListener;
        private Response.ErrorListener errorListener;
        private Map<String, String> headers;
        private Map<String, String> params;
        private String charset;
        private boolean shouldCache = false;


        public RequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder clazz(Class clazz) {
            this.clazz = clazz;
            return this;
        }

        public RequestBuilder successListener(Response.Listener successListener) {
            this.successListener = successListener;
            return this;
        }

        public RequestBuilder errorListener(Response.ErrorListener errorListener) {
            this.errorListener = errorListener;
            return this;
        }

        public RequestBuilder post() {
            this.method = Method.POST;
            return this;
        }

        public RequestBuilder get() {
            this.method = Method.GET;
            return this;
        }

        public RequestBuilder method(int method) {
            this.method = method;
            return this;
        }

        public RequestBuilder addHeader(String key, String value) {
            if (headers == null)
                headers = new HashMap<>();
            headers.put(key, value);
            return this;
        }

        public RequestBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestBuilder params(Map<String, String> params) {
            post();
            this.params = params;
            return this;
        }

        public RequestBuilder addParams(String key, String value) {
            if (params == null) {
                params = new HashMap<>();
                post();
            }
            params.put(key, value);
            return this;
        }

        /**
         * 使用 Post方式
         *
         * @param method
         * @return
         */
        public RequestBuilder addMethodParams(String method) {
            if (params == null) {
                params = new HashMap<>();
                post();
            }
            params.put("method", method);
            return this;
        }

        public RequestBuilder charset(String charset) {
            this.charset = charset;
            return this;

        }


        public RequestBuilder shouldCache(boolean shouldCache){
            this.shouldCache = shouldCache;
            return this;
        }

        public CustomRequest build() {
            return new CustomRequest(this);
        }
    }

}