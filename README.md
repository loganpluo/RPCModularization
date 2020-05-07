# RPCModularization
* 模块结构图(来自美团组件化)
<br>
![](https://raw.githubusercontent.com/loganpluo/RPCModularization/master/pic/module-service.png)

<br>
![你妹](https://github.com/loganpluo/RPCModularization/raw/master/pic/module-service.png)
<br>
和美团的组件化结构类似 https://tech.meituan.com/2018/12/20/modular-event.html  <br>
RPCModule(模块初始化) + RPCModuleService（模块暴露的接口服务）


# 快速接入
## Part1 自动生成对外接口的api工程
* 我们的目标是，读取模块里面特定目录下.api文件， copy并重命名.java文件 到 自动生成.api工程

### step1: 声明插件
* 工程根目录下的build.gradle
```
buildscript {
    repositories {
        maven{url 'https://dl.bintray.com/loganpluo/maven/'}//暂时这样引用，审核通过之后jceneter之后就不需要了
    }
    dependencies {
        classpath 'com.github.rpc.modularization:plugin-modularization:1.0.3'
    }
}

```

### stpe2: 模块的build.gradle引入 modularization插件
* 该插件在syc阶段识别下面  module_iml_api_src = src/main/api/src/目录，能把.api文件当作java编辑,运行时会剔除这个目录
* 还有个作用就是会扫描需要注册的接口 和 模块 完成自动注册，会在下面解说

```
apply plugin: 'com.github.rpc.modularization'

```

### step3: 在根目录gradle.properties 里面配置 自动生成.api工程的信息

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

### step4: settings.gradle 里面引入auto_create_api_library.gradle 脚本，api_include 关键字来引入 模块

```
apply from:'auto_create_api_library.gradle'
include ':app'
api_include ':module_login'
```

### stpe5: syc下配置会 自动读取配置的module_login目录下的.api文件 生成.api工程
过程如下
* 读取根目录gradle.properties的配置
* 生成api工程
* 简单校验目录下.api文件是否有修改，有修改就全量copy .api接口到 api工程的src下（todo diff更新）
* AndroidManifest.xml、Gradle、GitIgnore 文件生成
* 最后include api工程

### step6: 其他模块引入.api工程采用常规的api 或者 implement来引入

```

api project(":module_login_api")

```

## Part2 模块通信、自动初始化、自动注册接口-实现
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
        classpath 'com.github.rpc.modularization:plugin-modularization:1.0.3'
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

### step4: 在实现模块的api目录下，添加.api文件，最好建个和实现模块包名一样

```
public interface LoginService{
    String getUserName();
}
```

### step5: module_login模块的build.gradle引入 自动生成的 module_login_api工程
```
    api project(":module_login_api")
```

### step6: module_login模块 接口实现类 继承.api的接口，并且注解为 @ModuleService

```
@ModuleService
public class LoginServiceImpl implements LoginService {
    @Override
    public String getUserName() {
        return "loganpluo";
    }
}
```

### step7: 模块初始化类继承RPCModule接口
```

public class LoginModule implements RPCModule {
    @Override
    public void onInit(Context context) {
        Log.d("LoginModule","onInit");
    }
}

```

### step8: 其他模块 依赖module_login_api， 调用接口LoginService

```
        String userName =
                RPCModuleServiceManager.findService(LoginService.class).getUserName();

        Toast.makeText(this,"userName:"+userName,Toast.LENGTH_LONG).show();

```

* findService(Class<ModuleService> service,ModuleServiceType moduleServiceType) <br>
   参数 ModuleServiceType.SingleInstance 全局app单利接口服务<br>
   参数 ModuleServiceType.New 每次new 接口实现服务<br>
* findService(Class<ModuleService> service) <br>
  默认采用ModuleServiceType.New方式构建服务，可以在初始化指定默认findService(LoginService.class)的构建服务默认方式, 如下
    
```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RPCModuleServiceManager.init(getApplicationContext(), ModuleServiceType.SingleInstance);

    }
}

```

### step9: run
你会惊喜的发现一下日志, 模块自动初始化完成， 接口和 实现自动注册到 模块管理中心了
```
2020-05-07 21:52:05.215 6525-6525/com.github.rpc.modularization D/LoginModule: LoginModule onInit
2020-05-07 21:52:05.216 6525-6525/com.github.rpc.modularization I/RPCModuleServiceManager: registerModuleService serviceImpl:com.github.rpc.module_login.LoginModuleServiceImpl
```

* 原理就是 利用Transform Api 阶段扫描 指定calss，asm 字节码修改自动注入模块初始化和接口服务绑定代码

插件介绍：
https://github.com/loganpluo/RPCModularization/blob/master/plugin-modularization/README.md





## 模块支持单独debug run (暂停 不可用， 共用一个配置 两个mainfest merge问题)
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




# 参考
* 模块结构图(来自美团组件化)
<br>
![](https://github.com/loganpluo/RPCModularization/blob/master/pic/module-service.png)
<br>
和美团的组件化结构类似 https://tech.meituan.com/2018/12/20/modular-event.html  <br>
RPCModule(模块初始化) + RPCModuleService（模块暴露的接口服务）

