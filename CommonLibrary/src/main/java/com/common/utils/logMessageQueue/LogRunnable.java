package com.common.utils.logMessageQueue;

import com.socks.library.KLog;

//线程池中工作的线程
public class LogRunnable implements Runnable {
 
    private String msg;
     
    public LogRunnable() {
        super();
    }
 
    public LogRunnable(String msg) {
        this.msg = msg;
    }
 
    public String getMsg() {
        return msg;
    }
 
    public void setMsg(String msg) {
        this.msg = msg;
    }
 
    @Override
    public void run() {
        try {
            Thread.sleep(2000);                 //1000 毫秒，也就是1秒.
            KLog.e(msg);
            KLog.json(msg);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
 
}