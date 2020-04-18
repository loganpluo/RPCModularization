package com.github.rpc.module_topic_api;

import com.github.rpc.modularization.RPCModuleService;

import java.util.List;

public interface TopicModuleService extends RPCModuleService {

    void getMyTopicList(GetMyTopicListCallBack getMyTopicListCallBack);

}
