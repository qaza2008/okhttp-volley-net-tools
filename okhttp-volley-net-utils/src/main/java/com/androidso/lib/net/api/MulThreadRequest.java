package com.androidso.lib.net.api;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by wangs on 16/3/30 10:16.
 */
public class MulThreadRequest {
    public static final int THREAD_COUNT_DEFAULT = 5;
    public static final int PART_DESIRE_SIZE_DEFAULT = 1 * 1024 * 1024;

    private RequestMap parentBuilder;
    private Vector<RequestMap> children = new Vector<>();

    public static class RequestMap implements Callback {
        private String name;
        private Builder builder;
        private Callback callback;
        private long startPos;
        private long endPos;

        public long getStartPos() {
            return startPos;
        }

        public void setStartPos(long startPos) {
            this.startPos = startPos;
        }

        public long getEndPos() {
            return endPos;
        }

        public void setEndPos(long endPos) {
            this.endPos = endPos;
        }

        public RequestMap add(RequestMap map) {
            this.name = map.name;
            this.builder = map.builder;
            this.callback = map.callback;
            return this;
        }

        public String getName() {
            return name;
        }

        public RequestMap setName(String name) {
            this.name = name;
            return this;
        }

        public Builder getBuilder() {
            return builder;
        }

        public RequestMap setBuilder(Builder builder) {
            this.builder = builder;
            return this;
        }

        public Callback getCallback() {
            return callback;
        }

        public RequestMap setCallback(Callback callback) {
            this.callback = callback;
            return this;
        }

        @Override
        public void onFailure(Request request, IOException e) {
            if (callback != null) {
                callback.onFailure(request, e);

            }
        }

        @Override
        public void onResponse(Response response) throws IOException {
            if (callback != null) {
                callback.onResponse(response);

            }

        }

        @Override
        public String toString() {
            return "RequestMap{" +
                    "name='" + name + '\'' +
                    ", builder=" + builder +
                    ", callback=" + callback +
                    ", startPos=" + startPos +
                    ", endPos=" + endPos +
                    '}';
        }
    }


    public RequestMap getParentBuilder() {
        return parentBuilder;
    }

    public void setParentBuilder(RequestMap parentBuilder) {
        this.parentBuilder = parentBuilder;
    }

    public void add(RequestMap requestMap) {
        children.add(requestMap);
    }

    public void remove(RequestMap requestMap) {
        children.remove(requestMap);
    }

    public Enumeration<RequestMap> getChildren() {
        return children.elements();
    }
}
