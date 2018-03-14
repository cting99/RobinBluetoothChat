package com.cting.support.robinbt.threads;


import android.os.Handler;

public abstract class BaseThread extends Thread {

    protected Handler handler;

    public BaseThread(Handler handler) {
        this.handler = handler;
    }

    public abstract void cancel();
}
