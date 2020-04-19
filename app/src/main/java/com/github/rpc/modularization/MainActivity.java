package com.github.rpc.modularization;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.github.rpc.modularization.demo.R;
//import com.github.rpc.module_personalcenter_api.PersonalCenterModuleService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.my_topic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                RPCModuleServiceManager.findService(PersonalCenterModuleService.class).goToMyTopicListPage(MainActivity.this);
            }
        });
    }
}
