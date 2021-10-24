package com.hangliebe.medianeo.tool;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class AlertTool {
    public static void longToast(Context context,String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void shortToast(Context context,String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
