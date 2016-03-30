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

1.在自定义的Application初始化  

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
2.实现NetRequestListener接口

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

3.GET 请求

```
DaoRequest.get(this, "query", "https://suggest.taobao.com/sug?code=utf-8&q=iphone", null, this, true, "utf8");
```

4.POST 请求

```

   Map<String, String> params = new HashMap<>();
        params.put("kw", "love");
        params.put("pi", "1");
        params.put("pz", "20");
        DaoRequest.post(this, "test", "http://v5.pc.duomi.com/search-ajaxsearch-searchall", params, this, true);
    }
    
```

5.DOWNLOAD   

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


6.取消接口请求

   取消掉实现这个NetRequestListener的 所有接口.
   

```

  @Override
    protected void onStop() {
        super.onStop();
        RequestManager.getInstance(this).cancelRequests(this);
    }

```
  

7.添加公共参数

```
  DaoRequest.addCommonParams("json", "1");
```
### 测试结果:

1. GET 

```

  03-30 21:04:03.031 27966-27966/com.androidso.okhttp_volley_net_tools D/RequestAgent-requestTag: ***** query *****: ********* 网络请求开始 Start *********
03-30 21:04:03.031 27966-27966/com.androidso.okhttp_volley_net_tools D/RequestAgent-requestTag: ***** query *****: URL: https://suggest.taobao.com/sug?code=utf-8&q=iphone
03-30 21:04:03.461 27966-27966/com.androidso.okhttp_volley_net_tools D/RequestAgent-requestTag: ***** query *****: ********* 网络请求成功 Success *********
03-30 21:04:03.461 27966-27966/com.androidso.okhttp_volley_net_tools D/RequestAgent-requestTag: ***** query *****: response:{"result":[["iphone6手机壳","3398535"],["iphone6s手机壳","5247220"],["iphone6plus手机壳","5572726"],["iphone5s手机壳","1298581"],["iphone6钢化膜","714654"],["iphone6s plus壳","2199991"],["iphone6数据线","300376"],["iphonese","44377"],["iphone6s钢化膜","261317"],["iphone5手机壳","1403709"]],"tmall":"iphone"}

```


2. POST
  

```
03-30 21:08:54.821 27966-27966/com.androidso.okhttp_volley_net_tools D/RequestAgent-requestTag: ***** test *****: ********* 网络请求开始 Start *********
03-30 21:08:54.821 27966-27966/com.androidso.okhttp_volley_net_tools D/RequestAgent-requestTag: ***** test *****: URL: http://v5.pc.duomi.com/search-ajaxsearch-searchall
03-30 21:08:54.821 27966-27966/com.androidso.okhttp_volley_net_tools D/RequestAgent-requestTag: ***** test *****: Params: pi=1&json=1&pz=20&kw=love
03-30 21:08:56.371 27966-27966/com.androidso.okhttp_volley_net_tools D/RequestAgent-requestTag: ***** test *****: ********* 网络请求成功 Success *********
03-30 21:08:56.371 27966-27966/com.androidso.okhttp_volley_net_tools D/RequestAgent-requestTag: ***** test *****: response:{"album_offset":0,"albums":[{"artists":[{"id":61435612,"name":"嵐","portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/04\/07\/71629906.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true}],"available":true,"company":"","cover":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/18\/08\/71229617.jpg&w=150&h=150&s=100&c=0&o=0&m=","id":2492417,"name":"Love","num_tracks":5,"release_date":"2013-10-23","type":"专辑"},{"artists":[{"id":51076381,"name":"AAA","portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/21\/29\/70892774.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true}],"available":true,"company":"","cover":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/23\/14\/71241371.jpg&w=150&h=150&s=100&c=0&o=0&m=","id":2521439,"name":"Love","num_tracks":1,"release_date":"2014-02-26","type":"EP\/单曲"},{"artists":[{"id":51079995,"name":"Inna","portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/17\/32\/70470674.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true}],"available":true,"company":"","cover":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/07\/26\/71191817.jpg&w=150&h=150&s=100&c=0&o=0&m=","id":2331169,"name":"Love","num_tracks":4,"release_date":"2011-07-03","type":"EP\/单曲"},{"artists":[{"id":61604428,"name":"Maco","portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/18\/08\/71266445.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true}],"available":true,"company":"","cover":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/25\/28\/71785423.jpg&w=150&h=150&s=100&c=0&o=0&m=","id":2627760,"name":"Love","num_tracks":3,"release_date":"2015-05-20","type":"EP\/单曲"},{"artists":[{"id":51032372,"name":"Peter Kater","portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/07\/27\/70775952.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true}],"available":true,"company":"","cover":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/26\/15\/71755974.jpg&w=150&h=150&s=100&c=0&o=0&m=","id":2623182,"name":"Love","num_tracks":13,"release_date":"2015-05-05","type":"专辑"}],"artist_offset":0,"artists":[{"id":51031253,"name":"Love","num_albums":21,"num_tracks":199,"portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/09\/09\/70154280.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true},{"id":61611513,"name":"Love & The Outcome","num_albums":0,"num_tracks":1,"portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/23\/22\/71513365.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true},{"id":60005603,"name":"G. Love","num_albums":2,"num_tracks":23,"portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/20\/25\/70577224.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true},{"id":61007763,"name":"In Love","num_albums":5,"num_tracks":51,"portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/03\/07\/70231960.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true},{"id":61006500,"name":"We Love","num_albums":3,"num_tracks":12,"portrait":"http:\/\/pic.cdn.duomi.com\/imageproxy2\/dimgm\/scaleImage?url=http:\/\/img.kxting.cn\/\/p1\/26\/18\/70582035.jpg&w=150&h=150&s=100&c=0&o=0&m=","valid":true}],"dm_error":0,"error_msg":"操作成功","recommend":0,"total_albums":6971,"total_artists":134,"total_tracks":109405,"track_offset":0,"tracks":[{"album":{"cover":"\/p1\/15\/15\/72099009.jpg","id":2680635,"name":"With L.O.V.E Brown Eyed Girls"},"artists":[{"id":61759342,"name":"브라운아이드걸스(Brown Eyed Girls)","num_albums":0,"num_tracks":4,"portrait":

```
  

3. DOWNLOAD 

```

03-30 21:10:17.231 27966-5212/com.androidso.okhttp_volley_net_tools D/DaoRequest: downLoad onResponse
03-30 21:10:17.231 27966-5212/com.androidso.okhttp_volley_net_tools D/DaoRequest: multhread  i= 0  RequestMap{name='url:http://7vihs8.com1.z0.glb.clouddn.com/fragment1.gif Range:bytes=0-225090', builder=com.squareup.okhttp.Request$Builder@2c5da353, callback=null, startPos=0, endPos=225090}
03-30 21:10:17.241 27966-5212/com.androidso.okhttp_volley_net_tools D/DaoRequest: multhread  i= 1  RequestMap{name='url:http://7vihs8.com1.z0.glb.clouddn.com/fragment1.gif Range:bytes=225091-450182', builder=com.squareup.okhttp.Request$Builder@10d40490, callback=null, startPos=225091, endPos=450182}
03-30 21:10:17.241 27966-5212/com.androidso.okhttp_volley_net_tools D/DaoRequest: while 1: RequestMap{name='url:http://7vihs8.com1.z0.glb.clouddn.com/fragment1.gif Range:bytes=0-225090', builder=com.squareup.okhttp.Request$Builder@2c5da353, callback=null, startPos=0, endPos=225090}
03-30 21:10:17.251 27966-5212/com.androidso.okhttp_volley_net_tools D/DaoRequest: while 1: RequestMap{name='url:http://7vihs8.com1.z0.glb.clouddn.com/fragment1.gif Range:bytes=225091-450182', builder=com.squareup.okhttp.Request$Builder@10d40490, callback=null, startPos=225091, endPos=450182}
03-30 21:10:17.281 27966-5219/com.androidso.okhttp_volley_net_tools D/DaoRequest: while 2: onResponse
03-30 21:10:17.281 27966-5218/com.androidso.okhttp_volley_net_tools D/DaoRequest: while 2: onResponse
03-30 21:10:17.701 27966-5218/com.androidso.okhttp_volley_net_tools D/FileCallBack: OkHttp http://7vihs8.com1.z0.glb.clouddn.com/fragment1.gif完成下载  ： 0 -- 225090
03-30 21:10:17.701 27966-5219/com.androidso.okhttp_volley_net_tools D/FileCallBack: OkHttp http://7vihs8.com1.z0.glb.clouddn.com/fragment1.gif完成下载  ： 225091 -- 450182
03-30 21:10:17.701 27966-5219/com.androidso.okhttp_volley_net_tools D/DaoRequest: /sdcard/download.gif
03-30 21:10:17.701 27966-5219/com.androidso.okhttp_volley_net_tools D/MainActivity: requestDidSuccess: 


```

done
===

Welcome To My Website
---

[http://androidso.com](http://androidso.com)
---