package com.androidso.lib.net.api;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.androidso.lib.net.utils.DialogUtils;

public class DialogImp implements DialogInterface {

    private Dialog dialog;

    public DialogImp(Context context, String dialogMsg) {
        this.dialog = new DialogUtils().dialog(context, dialogMsg);


    }

    public DialogImp(Context context, Dialog dialog) {
        if (dialog != null) {
            this.dialog = dialog;
        } else {
            this.dialog = new DialogUtils().dialog(context, null);
        }
    }

    public static DialogInterface getnewInstance(@NonNull Context context, Dialog dialog) {
        return new DialogImp(context, dialog);

    }

    public static DialogInterface getnewInstance(@NonNull Context context, String msg) {
        return new DialogImp(context, msg);

    }

    @Override
    public Dialog showDialog() {
        if (dialog != null)
            dialog.show();
        return dialog;
    }

    @Override
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
