package com.github.rpc.module_personalcenter;

import android.content.Context;
import android.content.Intent;

import com.github.rpc.module_personalcenter_api.PersonalCenterModuleService;

public class PersonalCenterModuleServiceImpl implements PersonalCenterModuleService {

    @Override
    public void goToMyTopicListPage(Context context) {

        Intent intent = new Intent(context,MyTopicListActivity.class);
        context.startActivity(intent);

    }
}
