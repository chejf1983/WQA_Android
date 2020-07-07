/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.naqing.io;
import java.util.ArrayList;
import java.util.EventListener;
import android_serialport_api.SerialPortFactory;
import android_serialport_api.SerialPortFinder;
import wqa.bill.io.SIOInfo;
import wqa.bill.io.ShareIO;
import wqa.system.WQAPlatform;

/**
 * @author chejf
 */
public class ComManager implements EventListener {
    private ArrayList<ShareIO> localio = new ArrayList();
//    SerialPortFinder io_finder = new SerialPortFinder();

    //刷新IO口
    public void InitIO() throws Exception {
        this.localio.clear();

        //找到所有串口
        for (String com : SerialPortFactory.ListAllCom()) {
            com = com.split(" ")[0];
//            System.out.println("************************************" + com);
            //根据串口名称，查看配置
            SIOInfo sioInfo = WQAPlatform.GetInstance().GetIOManager().GetIOConfig(com);
            //如果没有找到，添加一个默认配置
            if (sioInfo == null) {
                sioInfo = new SIOInfo("COM", com, "9600");
//                WQAPlatform.GetInstance().GetIOManager().SaveIOConfig(com, sioInfo);
            }

            //查找是否已经注册到IO服务器了
            ShareIO io = WQAPlatform.GetInstance().GetIOManager().FindIO(sioInfo);
            //如果没有就注册到IO服务器当中
            if (io == null) {
                io = WQAPlatform.GetInstance().GetIOManager().ADDShareIO(SerialPortFactory.BuildCom(sioInfo));
            }
            //本地保存缓存
            this.localio.add(io);
        }
    }

    public void ChangeBandRate(ShareIO io, String bandrate) {
        SIOInfo info = io.GetConnectInfo();
        if (info.iotype.contentEquals(SIOInfo.COM)) {
            info.par[1] = bandrate;
            io.SetConnectInfo(info);
            WQAPlatform.GetInstance().GetIOManager().SaveIOConfig(GetKey(io), info);
        }
    }

    public String GetKey(ShareIO io) {
        if (io.GetConnectInfo().iotype.contentEquals(SIOInfo.COM))
            return io.GetConnectInfo().par[0];
        else
            return "";
    }

    public static String[] BandRate = new String[]{"4800", "9600", "19200", "38400", "57600", "115200"};
}
