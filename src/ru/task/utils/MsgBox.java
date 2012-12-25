package ru.task.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 18.12.12
 * Time: 22:23
 * To change this template use File | Settings | File Templates.
 */
public class MsgBox {
    ActivityMsgBox activity;
    String title;

    public MsgBox(ActivityMsgBox activity, String title) {
        this.activity = activity;
        this.title = title;
    }

    public void runMsgBox(String message, String button, final int type) {

        AlertDialog.Builder dlgAlert = new AlertDialog.Builder((Context) activity);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);
        dlgAlert.setPositiveButton(button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                activity.action(type);
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}
