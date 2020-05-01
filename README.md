# RPCModularization
* 模块结构图(来自美团组件化)
<br>
![](https://github.com/loganpluo/RPCModularization/blob/master/pic/module-service.png)
<br>
和美团的组件化结构类似 https://tech.meituan.com/2018/12/20/modular-event.html  <br>
RPCModule(模块初始化) + RPCModuleService（模块暴露的接口服务）

## step1: 模块接口服务中心，支持自动注册（done）
```
    //根目录build.gradle引入 自动化注册的插件
    buildscript {
     dependencies {
            classpath 'com.github.rpc.modularization:plugin-modularization:1.0.0'
      }    
    }

    
    // app/build.gradle引入modularization.gradle 配置，实现
    apply from: rootProject.file('modularization.gradle')
    

    // 初始化组件
    public class MyApplication extends Application {

        @Override
        public void onCreate() {
            super.onCreate();
        	RPCModuleServiceManager.init(getApplicationContext());
        }
    }

    //TopicModuleService对外接口 ，暂时手动放到 biz_module_api/module_topic_api模块接口工程里面
    public interface TopicModuleService  {

        void getMyTopicList(GetMyTopicListCallBack getMyTopicListCallBack);

    }

    //biz_module_iml/module_topic 模块接口实现工程里面实现TopicModuleServiceImpl, 
    //注解@ModuleService自动注册到RPCModuleServiceManager， 字节码asm 到initModuleServices方法里面，调用registerModuleService(Class<?> serviceImpl)
    @ModuleService
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
    
    //其他模块获取 getMyTopicList
    //build.gradle 配置依赖 模块接口工程
    api project(':module_topic_api')

    //从模块接口服务中心 获取TopicModuleService
    RPCModuleServiceManager.findService(TopicModuleService.class).getMyTopicList(new GetMyTopicListCallBack() {
        @Override
        public void result(int code, String msg, List<Topic> topics) {
        }
    }    

    // 模块初始化 注册模块对外接口实现 到 模块接口服务中心， plugin-modularization插件 asm自动注册到initModules(Context context)
    public class TopicModule implements RPCModule {
        @Override
        public void onInit(Context context) {
            
        }
    }



```
## step2: 模块支持单独debug run (stop， 共用一个配置 两个mainfest merge问题)
```
module_personalcenter模块为可以单独debug调试模块, 配置如下

(1) 根目录gradle.properties 配置主工程名, plugin-modularization用来支持library可以run
    mainAppName = app

(2) app主工程的build.gradle ,把com.android.application替换成如下插件，能够动态依赖单独运行的模块
    apply plugin: 'com.github.rpc.modularization'
(3) app主工程的build.gradle, 动态添加业务实现模块(config阶段，)
    addModule 'module_personalcenter'

(4) module_personalcenter 模式支持单独debug调试 build.gradle 配置
    apply plugin: 'com.github.rpc.modularization' 替换 apply plugin: 'com.android.library'

(5) module_personalcenter 模式支持单独debug调试 gradle.properties 配置
    isDebugAlone=true
    moduleApplicationId=com.github.rpc.module_personalcenter.run

(6) module_personalcenter 模式支持单独debug调试， 测试debug目录调试代码 todo 调用还有些问题
    新建 src/main/debug 目录, 相当于个application工程目录
    src/main/debug/java 新建对应包名，测试activity
    src/main/debug/res 资源，主要加前缀module_personalcenter
    src/main/debug/AndroidManifest.xml 同样的包名, 定义 application 和指定测试入口activity

```

### 字节码修改自动注入模块初始化和接口服务绑定代码
插件介绍：
https://github.com/loganpluo/RPCModularization/blob/master/plugin-modularization/README.md

## step3: 模块接口工程自动生成（todo）
准备初步采用setting里面调用函数 来动态copy library里面 .api 文件 生成library工程，然后include进来, 实现微信的.api<br>
微信.api 不支持kotlin、资源文件，是直接从模块特定接口目录(支持识别)copy 生成接口工程，最好是增量生成aar包


和美团的组件化结构类似 https://tech.meituan.com/2018/12/20/modular-event.html
1、RPCModule(模块初始化) + RPCModuleService（模块暴露的接口服务）<br>
RPCModule需要感知生命周期不<br>
