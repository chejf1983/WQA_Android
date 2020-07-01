package com.naqing.dev_views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.naqing.common.TableElement;
import com.naqing.wqa_android_ui_1.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import wqa.bill.log.DevLog;
import wqa.control.DB.AlarmRecord;
import wqa.control.data.IMainProcess;
import wqa.system.WQAPlatform;

public class fragment_dev_cal_info extends Fragment {
    private model_dev_view config_table;
    private ArrayList<String> cal_log = new ArrayList<>();
    private View root;
    private Activity parent;

    public fragment_dev_cal_info(model_dev_view config_table) {
        this.config_table = config_table;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (Activity) context;
    }

    public void reset_calconfig(model_dev_view config_table) {
        this.config_table = config_table;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_config_table, container, false);

        if (this.cal_log.isEmpty())
            this.ReadAlarm();
        else
            InitTable();

        /** 刷新列表*/
        root.findViewById(R.id.dct_read).setOnClickListener((View view) -> {
            ReadAlarm();
        });

        /** 设置参数*/
        TextView view = root.findViewById(R.id.dct_set);
        view.setText("删除");
        root.findViewById(R.id.dct_set).setOnClickListener((View v) -> {
            DelAlarm();
        });
        return root;
    }

    private boolean signal = false;

    private void DelAlarm() {
        if (signal) {
            return;
        }
        signal = true;
        DevLog.Instance().DelFile(config_table.control.GetDevID());
        cal_log.clear();
        messagehandler.sendEmptyMessage(READ);
    }

    public void ReadAlarm() {
        if (signal) {
            return;
        }
        signal = true;
        WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            cal_log = DevLog.Instance().ReadLog(config_table.control.GetDevID());
            messagehandler.sendEmptyMessage(READ);
        });
    }

    // <editor-fold desc="消息中心">
    //activity 消息
    private int READ = 0x01;
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == READ) {
                InitTable();
            }
        }
    };
    // </editor-fold>

    // <editor-fold desc="显示界面">
    private void InitTable() {
        /** 清除表格项*/
        LinearLayout layout = root.findViewById(R.id.dct_list);
        layout.removeAllViews();

        /** 设置行高*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);

        /** 添加行*/
        for (int i = 0; i < this.cal_log.size(); i++) {
            TextView view = (TextView) TableElement.createTextView(parent, cal_log.get(i));
            view.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

            if (i % 2 == 0) {
                /** 偶数层替换背景*/
                view.setBackgroundColor(Color.parseColor("#001942"));
            }
            layout.addView(view, lp);
        }

        signal = false;
    }
    // </editor-fold>
}
