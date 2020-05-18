package com.github.rpc.modularization.test;

import android.util.Log;

public class Test {

    static {
        Log.d("test","static");
    }

    public Test(){

    }

    public void test(){
        Log.d("test","msg");
    }

}
