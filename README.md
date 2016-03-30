### Okhttp-Volley net Tools

参照Okhttp 和 Volley 进行的封装工具.  
okhttp见：[https://github.com/square/okhttp](https://github.com/square/okhttp).

### 用法

* Android Studio 
  目前需要下载到本地,作为module library添加到项目中.
  
###  目前支持
* get请求
* post请求
* 文件下载及进度回调
* 支持添加公共参数和cookie
* 支持取消某个接口的批量请求
* 支持批量处理响应 
* 支持多线程下载
* 支持volley缓存

### 用法实例:

1. 在自定义的Application初始化  

```

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        RequestManager.getInstance(this);
        HttpConfig.DEBUG = BuildConfig.DEBUG;

    }
}  

```
2. 实现NetRequestListener接口

```
public class MainActivity extends FragmentActivity implements NetRequestListener {

 @Override
    public void requestDidStart(String requestTag) {


    }

    @Override
    public void requestLoading(long totalCount, long current) {

    }

    @Override
    public void requestDidSuccess(String requestTag, JSONObject response) {
        if ("query".equals(requestTag)) {

        }else {

        }

    }

    @Override
    public void requestDidFailed(String requestTag, Throwable t, int errorNo, String strMsg) {

    }

    @Override
    public void requestDidCancel(String requestTag) {

    }


}

```

3. GET 请求

```
DaoRequest.get(this, "query", "https://suggest.taobao.com/sug?code=utf-8&q=iphone", null, this, true, "utf8");
```

4. POST 请求

```
   Map<String, String> params = new HashMap<>();
        params.put("kw", "love");
        params.put("pi", "1");
        params.put("pz", "20");
        DaoRequest.post(this, "test", "http://v5.pc.duomi.com/search-ajaxsearch-searchall", params, this, true);
    }
    
```  

5. DOWNLOAD   

这里面后面的2个参数是可选项,是使用多线程下载,使用2个线程.

```
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

```  

6. 取消接口请求
   取消掉实现这个NetRequestListener的 所有接口.

```
  @Override
    protected void onStop() {
        super.onStop();
        RequestManager.getInstance(this).cancelRequests(this);
    }

```
  

7. 添加公共参数

```
  DaoRequest.addCommonParams("json", "1");
```