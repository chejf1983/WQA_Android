package com.naqing.dev_views;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.naqing.common.Security;
import com.naqing.wqa_android_ui_1.R;

import wqa.adapter.factory.CDevDataTable;
import wqa.control.common.DevControl;
import wqa.control.common.SDisplayData;
import wqa.dev.data.SDataElement;

public class model_monitor_holder {
    private Activity parentActivity;
    private View deviceView;
    public String[] data_name;
    private int index = 0;
    private model_dev_view control;
    public boolean isVisable = true;

    public model_monitor_holder(Activity parent, model_dev_view control, String[] data_name) {
        this.parentActivity = parent;
        this.data_name = data_name;
        this.control = control;
        this.InitView(parent);
    }

    public void InitView(Activity parent) {
        this.parentActivity = parent;
        /* Init Device View */
        InitViewComponents();
    }

    public View getView() {
        return this.deviceView;
    }

    // <editor-fold desc="初始化界面">
    private View config;
    private TextView md_name;
    private TextView md_main_name;
    private TextView md_main_data;
    private TextView md_range;
    private TextView md_temper;

    //初始化视图组件
    private void InitViewComponents() {
        LayoutInflater from = LayoutInflater.from(parentActivity);
        this.deviceView = from.inflate(R.layout.model_monitor, null);
        /** 初始化控件*/
        config = this.deviceView.findViewById(R.id.m_monitor_config_button);
        md_main_name = this.deviceView.findViewById(R.id.m_monitor_data_name);
        md_name = this.deviceView.findViewById(R.id.m_monitor_name);
        md_main_data = this.deviceView.findViewById(R.id.m_monitor_data);
        md_range = this.deviceView.findViewById(R.id.m_monitor_range);
        md_temper = this.deviceView.findViewById(R.id.m_monitor_temper);

        md_main_name.setOnClickListener((View view) -> {
            index = (index + 1) % data_name.length;
            Refresh();
        });
        /** 显示配置界面*/
        config.setOnClickListener((View view) -> {
            Security.CheckPassword(parentActivity, new Handler() {
                public void handleMessage(Message msg) {
                    control.showConfigActivity(parentActivity);
                }
            });
        });
    }
    // </editor-fold>

    // <editor-fold desc="刷新界面">
    private SDisplayData lastdata;

    public void updateData(SDisplayData data) {
        this.lastdata = data;
        Refresh();
    }

    private void Refresh() {
        if (lastdata == null) return;

//        DevID id = this.control.GetDevID();
        md_name.setText( CDevDataTable.GetInstance().namemap.get(control.control.GetDevID().dev_type).dev_name);
//        md_name.setText(this.data_name);
        md_main_name.setText(this.data_name[index]);
        SDataElement maindata = lastdata.GetDataElement(this.data_name[index]);

        //设置量程
        String range = maindata.range_info;
        if(range.length() > 12){
            range += ("\n" + maindata.unit);
        }else {
            range += maindata.unit;
        }
        md_range.setText(range);

        //显示测量值
        String value = maindata.mainData + "";
        if(value.length() > 7){
            md_main_data.setTextSize(40 * 7 / value.length());
        }else{
            md_main_data.setTextSize(40);
        }

        md_main_data.setText(value);
        md_temper.setText(lastdata.GetDataElement("温度").mainData + "°C");
    }
    // </editor-fold>

    public void initState(DevControl.ControlState state) {
        switch (state) {
            case CONNECT:
                config.setEnabled(true);
                md_name.setTextColor(Color.WHITE);
                break;
            case ALARM:
                config.setEnabled(true);
                md_name.setTextColor(Color.YELLOW);
                break;
            case DISCONNECT:
                config.setEnabled(false);
                md_name.setTextColor(Color.RED);
                break;
            case CONFIG:
                config.setEnabled(false);
                md_name.setTextColor(Color.GREEN);
                break;
        }
    }
}