package com.naqing.dev_views;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naqing.common.Security;
import com.naqing.common.TableElement;
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
    private LinearLayout index_layout;
    private TextView[] view_indexs;

    //初始化视图组件
    private void InitViewComponents() {
        LayoutInflater from = LayoutInflater.from(parentActivity);
        this.deviceView = from.inflate(R.layout.model_monitor, null);
        /** 初始化控件*/
        config = this.deviceView.findViewById(R.id.m_monitor_config_button);
        index_layout = this.deviceView.findViewById(R.id.m_monitor_index);
        md_main_name = this.deviceView.findViewById(R.id.m_monitor_data_name);
        md_name = this.deviceView.findViewById(R.id.m_monitor_name);
        md_main_data = this.deviceView.findViewById(R.id.m_monitor_data);
        md_range = this.deviceView.findViewById(R.id.m_monitor_range);
        md_temper = this.deviceView.findViewById(R.id.m_monitor_temper);

        md_main_name.setOnClickListener((View view) -> {
            view_indexs[index].setBackgroundColor(Color.GRAY);
            index = (index + 1) % data_name.length;
            view_indexs[index].setBackgroundColor(Color.BLACK);
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

        /** 添加翻页指示*/
        view_indexs = new TextView[data_name.length];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMarginStart(5);
        lp.setMarginEnd(5);
        for (int i = 0; i < this.data_name.length; i++) {
            view_indexs[i] = (TextView) TableElement.createTextView(this.parentActivity, "");
            view_indexs[i].setBackgroundColor(Color.GRAY);
            index_layout.addView(view_indexs[i], lp);
        }
        view_indexs[index].setBackgroundColor(Color.BLACK);
    }
    // </editor-fold>

    // <editor-fold desc="刷新界面">
    private SDisplayData lastdata;

    public void updateData(SDisplayData data) {
        this.lastdata = data;
        Refresh();
    }

    private void Refresh() {
        //检查数据
        if (lastdata == null) return;

        //设置设备名称
        md_name.setText(CDevDataTable.GetInstance().namemap.get(control.control.GetDevID().dev_type).dev_name);

        //设置数据名称
        md_main_name.setText(this.data_name[index]);

        SDataElement maindata = lastdata.GetDataElement(this.data_name[index]);
        //设置量程
        String range = maindata.range_info;
        if (range.length() > 12) {
            range += ("\n" + maindata.unit);
        } else {
            range += maindata.unit;
        }
        md_range.setText(range);

        //显示测量值
        String value = maindata.mainData + "";
        if (value.length() > 7) {
            md_main_data.setTextSize(40 * 7 / value.length());
        } else {
            md_main_data.setTextSize(40);
        }
        if (last_state == DevControl.ControlState.DISCONNECT) {
            //显示测量值
            md_main_data.setText("-.-");
            //设置温度
            md_temper.setText("-.- °C");
        } else {
            //显示测量值
            md_main_data.setText(value);
            //设置温度
            md_temper.setText(lastdata.GetDataElement("温度").mainData + "°C");
        }
    }
    // </editor-fold>

    private DevControl.ControlState last_state;

    public void initState(DevControl.ControlState state) {
        last_state = state;
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
                Refresh();
                break;
            case CONFIG:
                config.setEnabled(false);
                md_name.setTextColor(Color.GREEN);
                break;
        }
    }
}