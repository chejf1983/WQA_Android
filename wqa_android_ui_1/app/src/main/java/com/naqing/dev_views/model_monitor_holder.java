package com.naqing.dev_views;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

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
    private String md_main_alarm;
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

        //切换翻页
        md_main_name.setOnClickListener((View view) -> {
            view_indexs[index].setBackgroundColor(Color.GRAY);
            index = (index + 1) % data_name.length;
            view_indexs[index].setBackgroundColor(Color.BLACK);
            Refresh();
        });

        //显示报警信息
        md_name.setOnClickListener((View view) -> {
            ShowAlarm();
        });
    }
    // </editor-fold>

    // <editor-fold desc="刷新界面数据">
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

        if (lastdata.alarm != 0) {
            SetAlarm(lastdata.alram_info);
        }
    }
    // </editor-fold>

    // <editor-fold desc="更新状态">
    private DevControl.ControlState last_state;

    public void initState(DevControl.ControlState state) {
        last_state = state;
        switch (state) {
            case CONNECT:
                config.setEnabled(true);
                md_name.setTextColor(Color.WHITE);
                SetAlarm(null);
                break;
            case ALARM:
                config.setEnabled(true);
                md_name.setTextColor(Color.YELLOW);
                break;
            case DISCONNECT:
                config.setEnabled(false);
                md_name.setTextColor(Color.RED);
                SetAlarm("连接中断");
                Refresh();
            case CONFIG:
                config.setEnabled(false);
                md_name.setTextColor(Color.GREEN);
                break;
        }
    }
    // </editor-fold>

    // <editor-fold desc="报警信息">
    private void SetAlarm(String info) {
        if (info == null) {
            md_main_alarm = ("");
        } else {
//            int width = 3;
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < info.length(); i += width) {
//                if (info.length() - i > width)
//                    sb.append(info.substring(i, i + width));
//                else
//                    sb.append(info.substring(i));
//
//                sb.append("\n");
//            }
            md_main_alarm = info;
        }
    }

    private void ShowAlarm() {
        if (this.md_main_alarm.contentEquals("")) return;
        PopupMenu popupMenu = new PopupMenu(this.parentActivity, this.md_name);
        popupMenu.getMenuInflater().inflate(R.menu.menu_nqmain, popupMenu.getMenu());

        popupMenu.getMenu().findItem(R.id.action_settings).setTitle(md_main_alarm);
        popupMenu.show();
    }
    // </editor-fold>
}