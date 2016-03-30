package com.androidso.lib.net.api;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by wangs on 16/1/29 15:18.
 */
public abstract class FileCallBack extends CallBack<File> {
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;
    int progress = 0;
    Handler handler = new Handler(Looper.getMainLooper());

    public abstract void inProgress(int progress);

    private File file;

    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
        File dir = new File(destFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file = new File(dir, destFileName);
    }


    public File parseNetworkResponse(Response response) throws Exception {

        return saveFile(response);
    }

    public File saveFile(Response response) throws IOException {
        progress = 0;
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;

            Log.d("FileCallBack", total + "");


            fos = new FileOutputStream(file);

            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("FileCallBack", "finalSum::" + finalSum + "  total::" + total + "");
                        if (Math.floor(finalSum * 100.0f / total) - progress > 0) {
                            progress = (int) Math.floor(finalSum * 100.0f / total);
                            inProgress(progress);

                        }
                    }
                });
            }
            fos.flush();

            return file;

        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    public File parseNetworkResponse(Response response, boolean mulThreadFlag) throws Exception {

        return saveFile(response, mulThreadFlag);
    }

    public File saveFile(Response response, boolean mulThreadFlag) throws IOException {

        if (!mulThreadFlag || !isSupportRange(response)) {
            return saveFile(response);
        } else {

            long fileSize = getFileSize(response);
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.setLength(fileSize);
            raf.close();
            return file;
        }
    }

    public File parseNetworkResponse(Response response, Long startPos, Long endPos) {
        try {

            /**
             * 代表服务器已经成功处理了部分GET请求
             */
            if (response.code() == 206) {
                InputStream is = response.body().byteStream();
                int len = 0;
                byte[] buf = new byte[1024];

                RandomAccessFile raf = new RandomAccessFile(file,
                        "rwd");
                raf.seek(startPos);
                while ((len = is.read(buf)) != -1) {
                    raf.write(buf, 0, len);
                }
                raf.close();
                is.close();
                Log.d("FileCallBack", Thread.currentThread().getName()
                        + "完成下载  ： " + startPos + " -- " + endPos);
                return file;
            } else {
                throw new RuntimeException(
                        "url that you conneted has error ...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
    }

    public long getFileSize(Response response) {
        long fileSize = TextUtils.isEmpty(response.headers().get("Content-Length")) ? 0 : Long.parseLong(response.headers().get("Content-Length"));
        if (fileSize <= 0) {
            Log.d("FileDownloadRequest", "Response doesn't present Content-Length!");
        }
        return fileSize;
    }

    public static boolean isSupportRange(Response response) {
        if (TextUtils.equals(response.headers().get("Accept-Ranges"), "bytes")) {
            return true;
        }
        String value = response.headers().get("Content-Range");
        return value != null && value.startsWith("bytes");
    }
}
