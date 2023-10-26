package com.example.cocktailmachine.ui.model.helper;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Johanna Reidt
 * @created Mi. 26.Jul 2023 - 13:08
 * @project CocktailMachine
 */
public abstract class WaitingQueueCountDown {
    private static final String TAG = "WaitingQueueCountDown";

    /**
     * Millis since epoch when alarm should stop.
     */
    //private final long mMillisInFuture;
     /**
      * The interval in millis that the user receives callbacks
      */
     private final long mCountdownInterval;
     //private long mStopTimeInFuture;

     /**
       * boolean representing if the timer was cancelled
       */
     private boolean mCancelled = false;

    /**
     * number of next users
     */
    private int tick = 5;

    //private long previousTick;

     /**
       * @param countDownInterval The interval along the way to receive
       */
     public WaitingQueueCountDown(long countDownInterval) {
          //mMillisInFuture = millisInFuture;
          mCountdownInterval = countDownInterval;
          Log.v(TAG, "WaitingQueueCountDown");
     }

     /**
       * Cancel the countdown.
       */
     public synchronized final void cancel() {
         Log.v(TAG, "cancel");
          mCancelled = true;
          mHandler.removeMessages(MSG);
     }

     /**
       * Start the countdown.
       */
     public synchronized final WaitingQueueCountDown start() {
         Log.v(TAG, "start");
          mCancelled = false;
          reduceTick();
         Log.v(TAG, "start: reduceTick");

          if (tick <= 0) {
              onFinish();
              Log.v(TAG, "start: onFinish");
              return this;
          }
         if (tick <= 1) {
             onNext();
             Log.v(TAG, "start: onNext");
         }
          mHandler.sendMessage(mHandler.obtainMessage(MSG));
         Log.v(TAG, "start: sendMessage");
          return this;
     }


     /**
       * Callback fired on regular interval.
       */
     public abstract void onTick();

     public abstract void reduceTick();

     public void setTick(int tick){
         Log.v(TAG, "setTick: "+tick);
         this.tick = tick;
     }

    public int getTick(){
        return this.tick;
    }


    /**
     * Callback fired when the time is up.
     */
    public abstract void onNext();
     /**
       * Callback fired when the time is up.
       */
     public abstract void onFinish();

     public boolean isWaiting(){
         Log.v(TAG, "isWaiting?");
         return tick >0;
     }

    public boolean isNext(){
        Log.v(TAG, "isNext?");
        return tick ==1;
    }

    public boolean isUsersTurn(){
         Log.v(TAG, "isUsersTurn?");

        return tick ==0;
    }


     private static final int MSG = 1;


     // handles counting down
     private final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                synchronized (WaitingQueueCountDown.this) {
                    if (mCancelled) {
                        Log.v("Handler: WaitingQueueC", "cancelled");
                        return;
                    }

                    reduceTick();
                    Log.v("Handler: WaitingQueueC", "reduced");

                    if(tick ==1){
                        onNext();
                        Log.v("Handler: WaitingQueueC", "onNext");
                    }

                    if (tick <= 0) {
                        onFinish();
                        Log.v("Handler: WaitingQueueC", "onFinish");
                    } else {
                        onTick();
                        Log.v("Handler: WaitingQueueC", "onTick");
                        sendMessageDelayed(obtainMessage(MSG), mCountdownInterval);
                        Log.v("Handler: WaitingQueueC", "sendMessageDelayed");
                    }
                }
            }
     };

}
