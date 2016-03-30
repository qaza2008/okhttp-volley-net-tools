package com.androidso.lib.net.http;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AjaxParams {
    private static String ENCODING = "UTF-8";

    protected ConcurrentHashMap<String, String> urlParams;
    protected ConcurrentHashMap<String, FileWrapper> fileParams;

    public AjaxParams() {
        init();
    }

    public AjaxParams(Map<String, String> source) {
        init();

        for (Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public AjaxParams(String key, String value) {
        init();
        put(key, value);
    }

    public AjaxParams(Object... keysAndValues) {
        init();
        int len = keysAndValues.length;
        if (len % 2 != 0)
            throw new IllegalArgumentException("Supplied arguments must be even");
        for (int i = 0; i < len; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    public void put(String key, File file, String contentType) throws FileNotFoundException {
//        put(key, new FileInputStream(file), file.getName());
        fileParams.put(key, new FileWrapper(file, file.getName(), contentType));
    }

//    public void put(String key, InputStream stream) {
//        put(key, stream, null);
//    }
//
//    public void put(String key, InputStream stream, String fileName) {
//        put(key, stream, fileName, null);
//    }

//    /**
//     * 添加 inputStream 到请求中.
//     *
//     * @param key         the key name for the new param.
//     * @param stream      the input stream to add.
//     * @param fileName    the name of the file.
//     * @param contentType the content type of the file, eg. application/json
//     */
//    public void put(String key, InputStream stream, String fileName, String contentType) {
//        if (key != null && stream != null) {
//            fileParams.put(key, new FileWrapper(stream, fileName, contentType));
//        }
//    }

    public void remove(String key) {
        urlParams.remove(key);
        fileParams.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        return result.toString();
    }

    /**
     * Returns an HttpEntity containing all request parameters
     */
    public RequestBody getEntity() {
        RequestBody entity = null;

        if (!fileParams.isEmpty()) {
//            MultipartEntity multipartEntity = new MultipartEntity();
            MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

            // Add string params
            for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }

            // Add file params
            int currentIndex = 0;
            int lastIndex = fileParams.entrySet().size() - 1;
            for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
                FileWrapper fileWrapper = entry.getValue();
                if (fileWrapper.file != null) {
                    boolean isLast = currentIndex == lastIndex;
                    if (fileWrapper.contentType != null) {

                        RequestBody body = RequestBody.create(MediaType.parse(fileWrapper.contentType), fileWrapper.file);

                        builder.addFormDataPart(fileWrapper.getFileName(), fileWrapper.getFileName(), body);
//                        builder.add(entry.getKey(), file.getFileName(), file.inputStream, file.contentType, isLast);
                    } else {
//                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, isLast);
                        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), fileWrapper.file);
//                        builder.addPart(Headers.of(
//                                "Content-Disposition",
//                                "form-data; name=\"" + fileWrapper.getFileName() + "\";filename=\"" + fileWrapper.getFileName() + "\""), fileBody);
                        builder.addFormDataPart(fileWrapper.getFileName(), fileWrapper.getFileName(), fileBody);

                    }
                }
                currentIndex++;
            }

            entity = builder.build();

        }

        return entity;
    }

    private void init() {
        urlParams = new ConcurrentHashMap<String, String>();
        fileParams = new ConcurrentHashMap<String, FileWrapper>();
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return lparams;
    }

    public String getParamString() {
        return URLEncodedUtils.format(getParamsList(), ENCODING);
    }


    private static class FileWrapper {
        public File file;
        public String contentType;

        public FileWrapper(File file, String fileName, String contentType) {
            this.file = file;
            this.contentType = contentType;
        }

        public String getFileName() {
            if (file.getName() != null) {
                return file.getName();
            } else {
                return "nofilename";
            }
        }
    }
}
