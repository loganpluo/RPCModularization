package com.github.rpc.module_personalcenter;

import android.content.Context;

import com.github.rpc.modularization.RPCModule;
import com.github.rpc.modularization.RPCModuleServiceManager;
import com.github.rpc.module_personalcenter_api.PersonalCenterModuleService;

public class PersonalCenterModule implements RPCModule {
    @Override
    public void onInit(Context context) {
        //todo 自动注册
        RPCModuleServiceManager.getInstance().registerService(PersonalCenterModuleService.class, new PersonalCenterModuleServiceImpl());

    }
}
