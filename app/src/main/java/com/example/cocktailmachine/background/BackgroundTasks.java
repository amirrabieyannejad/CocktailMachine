package com.example.cocktailmachine.background;


import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Johanna Reidt
 * @created Mo. 29.Jan 2024 - 16:07
 * @project CocktailMachine
 */
public class BackgroundTasks {
    private static BackgroundTasks singleton;
    private HashMap<Context, ExecutorService> pools;
    private BackgroundTasks(){}
    private static BackgroundTasks getSingleton() {
        if(singleton == null){
            singleton = new BackgroundTasks();
        }
        return singleton;
    }

    private ExecutorService getPool(Context context){
        if(pools.containsKey(context)){
            return pools.get(context);
        }
        return null;
    }

    private ExecutorService newIfTerminated(Context context){
        ExecutorService pool = getPool(context);
        if(pool == null||pool.isTerminated()||pool.isShutdown()){
            return Executors.newFixedThreadPool(1);
        }
        return pool;
    }

    public static void submit(Context context, Runnable runnable){
        getSingleton().newIfTerminated(context).submit(runnable);
    }

    public static void submit(Context context, List<Runnable> runnables){
        ExecutorService pool = getSingleton().newIfTerminated(context);
        for(Runnable r: runnables){
            pool.submit(r);
        }
    }

    public static void shutdown(Context context){
        ExecutorService pool = getSingleton().getPool(context);
        if(pool == null||pool.isTerminated()||pool.isShutdown()){
            return;
        }
        pool.shutdown();
        getSingleton().pools.remove(context);
    }
}
