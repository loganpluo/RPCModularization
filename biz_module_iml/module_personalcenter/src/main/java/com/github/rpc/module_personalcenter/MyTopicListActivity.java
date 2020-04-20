package com.github.rpc.module_personalcenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.github.rpc.modularization.RPCModuleServiceManager;
import com.github.rpc.module_topic_api.GetMyTopicListCallBack;
import com.github.rpc.module_topic_api.Topic;
import com.github.rpc.module_topic_api.TopicModuleService;

import java.util.List;

public class MyTopicListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_topic_list);
        final TextView topicsView = findViewById(R.id.my_topic_list);
//        RPCModuleServiceManager.findService(TopicModuleService.class).getMyTopicList(new GetMyTopicListCallBack() {
//            @Override
//            public void result(int code, String msg, List<Topic> topics) {
//                if(code == 0){
//                    StringBuilder stringBuilder = new StringBuilder();
//                    for(Topic topic : topics){
//                        stringBuilder.append(topic.getName());
//                        stringBuilder.append("\n");
//                    }
//                    topicsView.setText(stringBuilder.toString());
//                }
//
//            }
//        });

    }
}
