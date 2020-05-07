# RPCModularization
* 模块结构图(来自美团组件化)
<br>
![](https://github.com/loganpluo/RPCModularization/blob/master/pic/module-service.png)
<br>
和美团的组件化结构类似 https://tech.meituan.com/2018/12/20/modular-event.html  <br>
RPCModule(模块初始化) + RPCModuleService（模块暴露的接口服务）


# 快速接入
## 自动生成对外接口的api工程
* 我们的目标是，读取模块里面特定目录下.api文件， copy并重命名.java文件 到 自动生成.api工程

### step1: 声明插件
* 工程根目录下的build.gradle
```
buildscript {
    repositories {
        maven{url 'https://dl.bintray.com/loganpluo/maven/'}//暂时这样引用，审核通过之后jceneter之后就不需要了
    }
    dependencies {
        classpath 'com.github.rpc.modularization:plugin-modularization:1.0.0'
    }
}

```

### stpe2: 模块的build.gradle引入 modularization插件
* 该插件在syc阶段识别下面  module_iml_api_src = src/main/api/src/目录，能把.api文件当作java编辑,运行时会剔除这个目录
* 还有个作用就是会扫描需要注册的接口 和 模块 完成自动注册，会在下面解说

```
apply plugin: 'com.github.rpc.modularization'

```

### step2: 在根目录gradle.properties 里面配置 自动生成.api工程的信息

```

#---------------auto_create_api_library.gradle的配置-----------------------
#实现接口的模块放的父目录
module_iml_base_path = ./
#接口模块放的父目录
module_api_base_path = ./
#实现接口的模块的放manifest文件的目录
module_iml_manifest_dir = src/main/
#实现模块.api接口文件的的目录(插件也会在syc阶段自动识别成src，让android sutido当作可编辑java文件，运行时会剔除这个目录)
module_iml_api_src = src/main/api/src/
#接口模块名(包名) = 实现模块名(包名) + api_modulename_suffix
api_modulename_suffix = _api
#接口模块的build.gradle compileSdkVersion
api_compile_sdkversion = 29
#接口模块的build.gradle buildToolsVersion
api_build_toolsversion = 29.0.2
#接口模块的build.gradle minSdkVersion
api_min_sdkversion = 19
#接口模块的build.gradle targetSdkVersion
api_min_targetversion = 29
#--------------------------------------

```

### step3: settings.gradle 里面引入auto_create_api_library.gradle 脚本

```
apply from:'auto_create_api_library.gradle'
include ':app'
api_include ':module_login'
```

### stpe4: settings.gradle里面使用 api_include 关键字来引入 模块， syc下会 自动读取配置目录下的.api文件 生成.api工程

* 主要是读取根目录gradle.properties的配置
* 生成api工程
* 简单校验目录下.api文件是否有修改，有修改就全量copy .api接口到 api工程的src下（todo diff更新）
* AndroidManifest.xml、Gradle、GitIgnore 文件生成
* 最后include api工程

### step5: 其他模块引入.api工程采用常规的api 或者 implement来引入

```
api project(":module_login_api")
```

## 模块通信、自动初始化、自动注册接口-实现
我们的目标 <br>
* 模块初始化继承特定接口，就自动完成初始化 <br>
* 模块通信采用接口， 接口实现只需要 @Annatation就把 接口-实现关系 自动注册到接口中心 <br>


### step1: 模块和app主工程的build.gradle 引入 rpc-modularization组件
```
implementation 'com.github.rpc.modularization:rpc-modularization:1.0.2'

```

### step2: 确认声明modularization插件

```
buildscript {
    repositories {
        maven{url 'https://dl.bintray.com/loganpluo/maven/'}//暂时这样引用，审核通过之后jceneter之后就不需要了
    }
    dependencies {
        classpath 'com.github.rpc.modularization:plugin-modularization:1.0.0'
    }
}

```

### stpe3: app主工程的application的onCreate 初始化模块管理中心

```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RPCModuleServiceManager.init(getApplicationContext());

    }
}
```

### step5: 

### step5: 接口实现类 继承.api的接口，并且注解为 @ModuleService

```
@ModuleService
public class LoginServiceImpl implements LoginService {
    @Override
    public String getUserName() {
        return "loganpluo";
    }
}
```

### step6: 模块初始化类继承RPCModule接口
```

public class LoginModule implements RPCModule {
    @Override
    public void onInit(Context context) {
        Log.d("LoginModule","onInit");
    }
}

```

### step7: 其他模块 依赖module_login_api， 调用接口LoginService
```
        String userName =
                RPCModuleServiceManager.findService(LoginService.class).getUserName();

        Toast.makeText(this,"userName:"+userName,Toast.LENGTH_LONG).show();

```

### step7: run

## step1: 模块接口服务中心，支持自动注册
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
