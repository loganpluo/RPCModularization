package com.github.rpc.module_personalcenter_api;

import android.content.Context;

import com.github.rpc.modularization.RPCModuleService;

public interface PersonalCenterModuleService extends RPCModuleService {

    //实例而已，跳转拉起页面用路由方式最好
    void goToMyTopicListPage(Context context);

}
