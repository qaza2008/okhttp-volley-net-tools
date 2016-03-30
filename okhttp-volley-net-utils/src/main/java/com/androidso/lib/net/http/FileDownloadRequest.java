package com.androidso.lib.net.http;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by wangs on 16/1/29 11:44.
 */
public class FileDownloadRequest extends Request<Void> {
    private File mStorFile;
    private File mTempFile;
    /**
     * Decoding lock so that we don't decode more than one image at a time (to avoid OOM's)
     */
    private static final Object sDecodeLock = new Object();

    public FileDownloadRequest(String storeFilePath, String url, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        super(url, null);
        mStorFile = new File(storeFilePath);
        mTempFile = new File(storeFilePath + ".tmp");
        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 200, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    protected Response<Void> parseNetworkResponse(NetworkResponse response) {
        synchronized (sDecodeLock) {
            try {
                doParse(response);

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return null;
    }

    //支持断点续传
    private void doParse(NetworkResponse response) throws IOException {
        byte[] data = response.data;

        //content-length
        long fileSize = TextUtils.isEmpty(response.headers.get("Content-Length")) ? 0 : Long.parseLong(response.headers.get("Content-Length"));
        if (fileSize <= 0) {
            Log.d("FileDownloadRequest", "Response doesn't present Content-Length!");
        }
        long downloadedSize = mTempFile.length();
        boolean isSupportRange = isSupportRange(response);
        if (isSupportRange) {
            fileSize += downloadedSize;
            // Verify the Content-Range Header, to ensure temporary file is part of the whole file.
            // Sometime, temporary file length add response content-length might greater than actual file length,
            // in this situation, we consider the temporary file is invalid, then throw an exception.
            String realRangeValue = response.headers.get("Content-Range");
            // response Content-Range may be null when "Range=bytes=0-"
            if (!TextUtils.isEmpty(realRangeValue)) {
                String assumeRangeValue = "bytes " + downloadedSize + "-" + (fileSize - 1);
                if (TextUtils.indexOf(realRangeValue, assumeRangeValue) == -1) {
                    throw new IllegalStateException(
                            "The Content-Range Header is invalid Assume[" + assumeRangeValue + "] vs Real[" + realRangeValue + "], " +
                                    "please remove the temporary file [" + mTempFile + "].");
                }


            }


        }
        // Compare the store file size(after download successes have) to server-side Content-Length.
        // temporary file will rename to store file after download success, so we compare the
        // Content-Length to ensure this request already download or not.
        if (fileSize > 0 && mStorFile.length() == fileSize) {
            mStorFile.renameTo(mTempFile);

        }

        RandomAccessFile tmpFileRaf = new RandomAccessFile(mTempFile, "rw");

        if (isSupportRange) {
            tmpFileRaf.seek(downloadedSize);

        } else {
            tmpFileRaf.setLength(0);
            downloadedSize = 0;
        }
        tmpFileRaf.write(data);

    }

    @Override
    protected void deliverResponse(Void response) {

    }

    public static boolean isSupportRange(NetworkResponse response) {
        if (TextUtils.equals(response.headers.get("Accept-Ranges"), "bytes")) {
            return true;

        }
        String value = response.headers.get("Content-Range");
        return value != null && value.startsWith("bytes");
    }
}
