package com.github.rpc.module_topic_api;

import java.util.List;

public interface GetMyTopicListCallBack {

    void result(int code, String msg, List<Topic> topics);

}
