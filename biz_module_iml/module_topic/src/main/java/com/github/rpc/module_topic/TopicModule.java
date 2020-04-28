package com.github.rpc.module_topic;

import android.content.Context;
import android.util.Log;

import com.github.rpc.modularization.RPCModule;
import com.github.rpc.modularization.RPCModuleServiceManager;
import com.github.rpc.module_topic_api.TopicModuleService;

public class TopicModule implements RPCModule {
    @Override
    public void onInit(Context context) {
//        RPCModuleServiceManager.getInstance().registerService(TopicModuleService.class,new TopicModuleServiceImpl());

        Log.d("TopicModule","TopicModule onInit");

    }
}
