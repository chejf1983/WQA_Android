package com.naqing.io;

public class AndroidIO {
    private static AndroidIO instance;

    private AndroidIO(){
//        WQAPlatform.GetInstance().GetIOManager()
//        WQAPlatform.GetInstance().GetIOManager().SendReceive.RegeditListener(new EventListener<String>() {
//            @Override
//            public void recevieEvent(Event<String> event) {
//                System.out.println(event.GetEvent());
//            }
//        });
    }
    public static AndroidIO GetInstance() {
        if (instance == null) {
            instance = new AndroidIO();
        }
        return instance;
    }

    public void InitIO() throws Exception{
        this.GetComManager().InitIO();

        this.GetDevIO().initDevIO();
    }

    private DevIO devio = null;

    public DevIO GetDevIO() {
        if (devio == null) {
            this.devio = new DevIO();
        }
        return this.devio;
    }

    private ComManager comManager = null;

    public ComManager GetComManager() {
        if (comManager == null) {
            this.comManager = new ComManager();
        }
        return this.comManager;
    }
}
