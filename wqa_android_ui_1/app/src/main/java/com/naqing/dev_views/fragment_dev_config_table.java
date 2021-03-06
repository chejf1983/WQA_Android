package com.naqing.dev_views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.naqing.common.TableElement;
import com.naqing.wqa_android_ui_1.R;
import com.naqing.wqa_android_ui_1.model_dev_view_manager;

import wqa.control.common.DevControl;
import wqa.control.config.DevConfigTable;
import wqa.dev.intf.SConfigItem;

public class fragment_dev_config_table extends Fragment {
    private DevConfigTable config_table;
    private View root;
    private Activity parent;
    private SConfigItem[] configItems;

    public fragment_dev_config_table(DevConfigTable config_table) {
        this.config_table = config_table;
    }

    // <editor-fold desc="初始化">
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_config_table, container, false);

        /** 创建表格*/
        this.createTable(this.config_table);

        /** 刷新列表*/
        root.findViewById(R.id.dct_read).setOnClickListener((View view) -> {
            this.config_table.InitConfigTable();
            this.createTable(this.config_table);
        });

        /** 设置参数*/
        root.findViewById(R.id.dct_set).setOnClickListener((View view) -> {
            this.config_table.SetConfigList(configItems);
            for (SConfigItem item : configItems) {
                if (item.IsKey("设备地址") || item.IsKey("设备名称"))
                    model_dev_view_manager.Instance().SaveConfig();
            }
        });

        return root;
    }

    public void reset_calconfig(DevConfigTable cal_config) {
        this.config_table = cal_config;
    }
    // </editor-fold>

    // <editor-fold desc="创建动态列表">

    /**
     * 创建表格
     */
    private void createTable(DevConfigTable config_table) {
        /** 清除表格项*/
        LinearLayout layout = root.findViewById(R.id.dct_list);
        layout.removeAllViews();

        /** 添加行*/
        configItems = config_table.GetConfigList();
        /** 设置行高*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
        for (int i = 0; i < configItems.length; i++) {
            View view = TableElement.CreateLine(parent, configItems[i]);
            if (i % 2 == 0) {
                /** 偶数层替换背景*/
                view.setBackgroundColor(Color.parseColor("#001942"));
            }
            layout.addView(view, lp);
        }
    }
    // </editor-fold>
}
