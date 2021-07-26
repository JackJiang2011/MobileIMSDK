package net.x52im.mobileimsdk.android.utils;

import android.os.Handler;
import android.util.Log;

public abstract class MBSimpleTimer {

    private Handler handler = null;
    private Runnable runnable = null;
    private int interval = -1;

    public MBSimpleTimer(int interval){
        this.interval = interval;
    }

    public void init() {
        handler = new Handler();
        runnable = () -> {
            try {
                doAction();
            } catch (Exception e) {
                Log.w(MBSimpleTimer.class.getSimpleName(), e);
            }

            handler.postDelayed(runnable, interval);
        };
    }

    protected abstract void doAction();

    public void start(boolean immediately) {
        stop();
        onStart();
        handler.postDelayed(runnable, immediately ? 0 : interval);
    }

    protected void onStart(){
        // default do nothing
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        onStop();
    }

    protected void onStop(){
        // default do nothing
    }
}