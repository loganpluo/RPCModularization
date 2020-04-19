# RPCModularization

![](https://github.com/loganpluo/RPCModularization/blob/master/pic/%E6%A8%A1%E5%9D%97%E6%9A%B4%E9%9C%B2%E7%9A%84%E6%9C%8D%E5%8A%A1.png)<br>
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;;&emsp;&emsp;&emsp;&emsp;模块结构图(来自美团组件化)

## step1: 模块接口服务中心（done）
```
    // 暂时手动初始化模块
    public class MyApplication extends Application {

        @Override
        public void onCreate() {
            super.onCreate();

            RPCModuleHelper.newInstanceAndInit(getApplicationContext(),"com.github.rpc.module_login.LoginModel");
            RPCModuleHelper.newInstanceAndInit(getApplicationContext(),"com.github.rpc.module_topic.TopicModule");
            RPCModuleHelper.newInstanceAndInit(getApplicationContext(),"com.github.rpc.module_personalcenter.PersonalCenterModule");

        }
    }

    //TopicModuleService对外接口 ，暂时收到你放到 biz_module_api/module_topic_api模块接口工程里面
    public interface TopicModuleService extends RPCModuleService {

        void getMyTopicList(GetMyTopicListCallBack getMyTopicListCallBack);

    }

    //biz_module_iml/module_topic 模块接口实现工程里面实现TopicModuleServiceImpl
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
                    topic.setName("话题"+i);
                    topics.add(topic);
                }
                getMyTopicListCallBack.result(0, "", topics);
            }else{
                getMyTopicListCallBack.result(0, "", topics);
            }


        }
    }

    // 模块初始化 注册模块对外接口实现 到 模块接口服务中心
    public class TopicModule implements RPCModule {
        @Override
        public void onInit(Context context) {
            RPCModuleServiceManager.getInstance().registerService(TopicModuleService.class,new TopicModuleServiceImpl());
        }
    }

    //其他模块获取 getMyTopicList
        //build.gradle 配置依赖 模块接口工程
        api project(':module_topic_api')

        //从模块接口服务中心 获取TopicModuleService
        RPCModuleServiceManager.findService(TopicModuleService.class).getMyTopicList(new GetMyTopicListCallBack() {
            @Override
            public void result(int code, String msg, List<Topic> topics) {
            }
        }

```
## step2: 模块支持单独debug run (doing)
addModule 'module_personalcenter' 插件脚本实现最后问题<br>
app运行的时候，引入module_personalcenter模块；run module_personalcenter模块的时候，不引入module_personalcenter模块<br>

## step3: 模块接口实现支持自动注册(todo)

## step4: 模块接口工程自动生成（todo, time-consuming）
	 微信.api 不支持kotlin、资源文件，是直接从模块特定接口目录(支持识别)copy 生成接口工程，最好是增量生成aar包


和美团的组件化结构类似 https://tech.meituan.com/2018/12/20/modular-event.html
1、RPCModule(模块初始化) + RPCModuleService（模块暴露的接口服务）<br>
RPCModule需要感知生命周期不<br>