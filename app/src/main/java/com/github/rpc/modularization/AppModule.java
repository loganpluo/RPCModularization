package com.github.rpc.modularization;

import android.content.Context;
import android.util.Log;

public class AppModule implements RPCModule {//
    @Override
    public void onInit(Context context) {
        Log.d("AppModule","AppModule onInit");
    }
}
