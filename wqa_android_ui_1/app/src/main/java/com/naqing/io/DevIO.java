package com.naqing.io;


import wqa.bill.io.SIOInfo;
import wqa.bill.io.ShareIO;
import wqa.system.WQAPlatform;

public class DevIO {
    private ShareIO io_instance;
    private final String DEVIO = "DEVIO";

    // <editor-fold defaultstate="collapsed" desc="设备IO列表">
    public void initDevIO() throws Exception{
        /**读取配置*/
        String iokey = WQAPlatform.GetInstance().GetConfig().getProperty(DEVIO, "NON");

        if(!iokey.contentEquals("NON")) {
            SIOInfo sioInfo = WQAPlatform.GetInstance().GetIOManager().GetIOConfig(DEVIO);
            /**查找IO*/
            io_instance = WQAPlatform.GetInstance().GetIOManager().FindIO(sioInfo);
            /**如果找不到，设置默认串口*/
            if (io_instance == null) {
                SetDevConfigIO(WQAPlatform.GetInstance().GetIOManager().GetAllIO(SIOInfo.COM)[6]);
            }
        }else{
            SetDevConfigIO(WQAPlatform.GetInstance().GetIOManager().GetAllIO(SIOInfo.COM)[6]);
        }
        io_instance.Close();
        io_instance.Open();
    }

    public ShareIO GetDevConfigIO() {
        return io_instance;
    }

    public void SetDevConfigIO(ShareIO iolist) {
        if(iolist != null){
            WQAPlatform.GetInstance().GetManager().DelAutoSearchIO(io_instance);
            /**如果找不到，设置第一个IO口为设备口*/
            io_instance = iolist;

            WQAPlatform.GetInstance().GetManager().AddAutoSearchIO(io_instance);
            WQAPlatform.GetInstance().GetConfig().setProperty(DEVIO, AndroidIO.GetInstance().GetComManager().GetKey(io_instance));
        }
    }
    // </editor-fold>
}
