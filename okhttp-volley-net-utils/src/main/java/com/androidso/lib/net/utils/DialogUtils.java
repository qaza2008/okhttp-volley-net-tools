package com.androidso.lib.net.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidso.lib.net.view.PageLoadingView;
import com.jd.paipai.net.R;


public class DialogUtils {

    private PageLoadingView mPageLoadingView;

    private Dialog mDialog;

    public Dialog dialog(Context context, String msg) {
        mPageLoadingView = new PageLoadingView(context);
        Dialog mDialog = new Dialog(context, R.style.Theme_Light_Dialog);
        View view = LayoutInflater.from(context).inflate(
                R.layout.progress_bar, null);
        //提示内容
        TextView tv_update_dialog_version = (TextView) view
                .findViewById(R.id.tv_pro_text);
        PageLoadingView pro = (PageLoadingView) view.findViewById(R.id.loading_bar);
        pro.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(msg)) {
            tv_update_dialog_version.setText(msg);
        } else {
            tv_update_dialog_version.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(view, params);
        mDialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(android.content.DialogInterface dialogInterface) {
                if (mPageLoadingView != null) {
                    mPageLoadingView.stopAnimation();
                }
            }
        });

        return mDialog;
    }
}
