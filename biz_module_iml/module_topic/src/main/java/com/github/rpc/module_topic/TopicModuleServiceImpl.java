package com.github.rpc.module_topic;

import com.github.rpc.modularization.RPCModuleServiceManager;
import com.github.rpc.module_login_api.LoginModuleService;
import com.github.rpc.module_topic_api.GetMyTopicListCallBack;
import com.github.rpc.module_topic_api.Topic;
import com.github.rpc.module_topic_api.TopicModuleService;

import java.util.ArrayList;
import java.util.List;

public class TopicModuleServiceImpl implements TopicModuleService {

    @Override
    public void getMyTopicList(GetMyTopicListCallBack getMyTopicListCallBack) {
        LoginModuleService loginModuleService = RPCModuleServiceManager.findService(LoginModuleService.class);

        //拦截器其实也是可以支持，采用动态代理来构建LoginModuleService， 统一判断是否需要登录不
        List<Topic> topics = new ArrayList<>();
        if(loginModuleService.isLogin()){
            //cost query
            for(int i=0; i<10; i++){
                Topic topic = new Topic();

                topics.add(topic);
            }
            getMyTopicListCallBack.result(0, "", topics);
        }else{
            getMyTopicListCallBack.result(0, "", topics);
        }


    }
}
