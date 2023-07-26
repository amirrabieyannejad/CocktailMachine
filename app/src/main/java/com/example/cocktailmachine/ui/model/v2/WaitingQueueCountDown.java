package com.example.cocktailmachine.ui.model.v2;

import android.os.Handler;
import android.os.Message;

/**
 * @author Johanna Reidt
 * @created Mi. 26.Jul 2023 - 13:08
 * @project CocktailMachine
 */
public abstract class WaitingQueueCountDown {

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
    private int tick;

    private long previousTick;

     /**
       * @param countDownInterval The interval along the way to receive
       */
     public WaitingQueueCountDown(long countDownInterval) {
          //mMillisInFuture = millisInFuture;
          mCountdownInterval = countDownInterval;
     }

     /**
       * Cancel the countdown.
       */
     public synchronized final void cancel() {
          mCancelled = true;
          mHandler.removeMessages(MSG);
     }

     /**
       * Start the countdown.
       */
     public synchronized final WaitingQueueCountDown start() {
          mCancelled = false;

          if (tick <= 0) {
              onFinish();
              return this;
          }
         if (tick <= 1) {
             onNext();
         }
          mHandler.sendMessage(mHandler.obtainMessage(MSG));
          return this;
     }


     /**
       * Callback fired on regular interval.
       */
     public abstract void onTick();

     public abstract void reduceTick();

     public void setTick(int tick){
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
         return tick >0;
     }

    public boolean isNext(){
        return tick ==1;
    }

    public boolean isUsersTurn(){
         return tick ==0;
    }


     private static final int MSG = 1;


     // handles counting down
     private Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                synchronized (WaitingQueueCountDown.this) {
                    if (mCancelled) {
                        return;
                    }

                    reduceTick();

                    if(tick ==1){
                        onNext();
                    }

                    if (tick <= 0) {
                        onFinish();
                    } else {
                        onTick();
                        sendMessageDelayed(obtainMessage(MSG), mCountdownInterval);
                    }
                }
            }
     };

}
