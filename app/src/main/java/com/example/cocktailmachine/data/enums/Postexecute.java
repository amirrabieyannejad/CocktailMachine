package com.example.cocktailmachine.data.enums;

public abstract class Postexecute {

    public abstract void post();

    public static Postexecute doNothing(){
        return new Postexecute() {
            @Override
            public void post() {
                return;
            }
        };
    }
}
