package com.naqing.common;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import com.naqing.wqa_android_ui_1.R;

public class Security {

    // <editor-fold desc="单例">
    private static Security instance;

    private Security() {
    }

    public static Security Instance() {
        if (instance == null) instance = new Security();
        return instance;
    }
    // </editor-fold>

    // <editor-fold desc="检查密码">
    public static void CheckPassword(Activity parent, Handler handler) {
        Security.Instance().checkpassword(parent, handler);
    }

    public static int CHECK_OK = 0x01;
    public static int START_BACKDOOR = -0x01;

    private void checkpassword(Activity parent, Handler handler) {
        if (!Security.Instance().security_need) {
            handler.sendEmptyMessage(CHECK_OK);
            return;
        }

        InputDialog.ShowInputDialog(parent, "请输入密码", "", (String passwd) -> {
            if (passwd.contentEquals(BACKDOOR)) {
                handler.sendEmptyMessage(START_BACKDOOR);
            } else if (comparepassword(passwd)) {
                handler.sendEmptyMessage(CHECK_OK);
            } else {
                ErrorExecutor.PrintErrorInfo("输入密码错误");
            }
        }, true);
    }

    private boolean comparepassword(String input) {
        return input.contentEquals("111111");
    }
    // </editor-fold>

    // <editor-fold desc="密码检查使能">
    private boolean security_need = true;

    private static String BACKDOOR = "12.34.56";

    public void EnableSecurity(boolean value) {
        this.security_need = value;
    }

    public boolean IsEnable() {
        return this.security_need;
    }
    // </editor-fold>
}
