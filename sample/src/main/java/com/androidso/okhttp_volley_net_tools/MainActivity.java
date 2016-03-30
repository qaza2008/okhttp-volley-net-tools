package com.androidso.okhttp_volley_net_tools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.androidso.lib.net.api.FileCallBack;
import com.androidso.lib.net.api.NetRequestListener;
import com.androidso.lib.net.controller.DaoRequest;
import com.androidso.lib.net.http.RequestManager;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NetRequestListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void click1(View view) {

        DaoRequest.get(this, "query", "https://suggest.taobao.com/sug?code=utf-8&q=iphone", null, this, true, "utf8");
        //please fix this fail/
//        DaoRequest.get(this, "test", "http://www.weather.com.cn/data/sk/101010100.html", null, this, true, "utf8");
    }

    public void click2(View view) {

        Map<String, String> params = new HashMap<>();
        params.put("kw", "love");
        params.put("pi", "1");
        params.put("pz", "20");
        DaoRequest.post(this, "test", "http://v5.pc.duomi.com/search-ajaxsearch-searchall", params, this, true);
    }

    public void click3(View view) {
        DaoRequest.download(this, "http://7vihs8.com1.z0.glb.clouddn.com/fragment1.gif", new FileCallBack("sdcard", "download.gif") {
            @Override
            public void inProgress(int progress) {

            }

            @Override
            public File parseNetWorkResponse(Response response) throws Exception {
                return null;
            }

            @Override
            public void requestDidStart(String requestTag) {

            }

            @Override
            public void requestLoading(long totalCount, long current) {

            }

            @Override
            public void requestDidSuccess(String requestTag, JSONObject response) {
                Log.d(TAG, "requestDidSuccess: ");

            }

            @Override
            public void requestDidFailed(String requestTag, Throwable t, int errorNo, String strMsg) {

            }

            @Override
            public void requestDidCancel(String requestTag) {

            }
        }, true, 2);

    }

    @Override
    public void requestDidStart(String requestTag) {


    }

    @Override
    public void requestLoading(long totalCount, long current) {

    }

    @Override
    public void requestDidSuccess(String requestTag, JSONObject response) {
        if ("query".equals(requestTag)) {

        } else {

        }

    }

    @Override
    public void requestDidFailed(String requestTag, Throwable t, int errorNo, String strMsg) {

    }

    @Override
    public void requestDidCancel(String requestTag) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        RequestManager.getInstance(this).cancelRequests(this);
    }
}
