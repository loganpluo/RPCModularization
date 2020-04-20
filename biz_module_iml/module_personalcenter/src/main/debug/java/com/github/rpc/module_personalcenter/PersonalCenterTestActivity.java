package com.github.rpc.module_personalcenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.github.rpc.modularization.RPCModuleServiceManager;
import com.github.rpc.module_personalcenter_api.PersonalCenterModuleService;

public class PersonalCenterTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center_test);
        findViewById(R.id.my_topic_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo fix
                RPCModuleServiceManager.findService(PersonalCenterModuleService.class).goToMyTopicListPage(PersonalCenterTestActivity.this);
            }
        });
    }
}
