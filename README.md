# RPCModularization
## 前言</br>
RPCModularization跨模块通信 采用 接口方式来进行，主要分为三个部分
* 接口定义采用微信.api方式实现物理隔离，增量感知.api文件自动copy生成api工程
* 模块继承RPCModule 自动实现初始化
* 注解模块接口实现为 @ModuleService，自动注册接口-实现 到模块服务中心， 其他模块依赖api工程通过 模块服务中心调用其他模块

![](https://github.com/loganpluo/RPCModularization/blob/master/pic/jiagoutu.png)<br>
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;模块组件架构图<br><br>
![](https://github.com/loganpluo/RPCModularization/blob/master/pic/module-service.png)<br>
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;模块（组件）结构图(来自美团组件化)，Export module就是api接口工程<br><br>
<br><br>


# 快速接入
## Part1 自动生成对外接口的api工程
* 我们的目标是，读取模块里面特定目录下.api文件， copy并重命名.java文件 到 自动生成.api工程

![](https://github.com/loganpluo/RPCModularization/blob/master/pic/auto-create-api-project.png)<br>

### step1: 声明插件
* 工程根目录下的build.gradle
```
buildscript {
    repositories {
        maven{ url'https://maven.aliyun.com/repository/jcenter'}
    }
    dependencies {
        classpath 'com.github.rpc.modularization:plugin-modularization:1.0.2'
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
module_iml_base_path = biz_module_iml/
#接口模块放的父目录
module_api_base_path = biz_module_api/
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
* 简单校验目录下.api文件是否有修改，有修改就全量copy .api接口到 api工程的src下（todo diff更新）<br>
  会在模块的根目录下面生成一个 api_lastmodified文件来记录.api文件最近修改时间，注意要保留commit提交<br>
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

implementation 'com.github.rpc.modularization:rpc-modularization:1.0.4'

```

### step2: 确认声明modularization插件

```
buildscript {
    dependencies {
        classpath 'com.github.rpc.modularization:plugin-modularization:1.0.2'
    }
}

```

### step3: app主工程的build.gradle引入modularization.gradle
* modularization.gradle为扫描模块通信、自动初始化和 修改字节码 配置
* 自己主要进行过滤扫描配置， exclude正则过滤配置,如果能确定接口所在包名可以用 include正则过滤

```
apply plugin: 'com.github.rpc.modularization'


classmodifier{
    configs = [
        [ //自动注册组件
          'type'             : 'InterfaceModuleInit',
          'scanInterface'             : 'com.github.rpc.modularization.RPCModule'
          , 'codeInsertToClass'   : 'com.github.rpc.modularization.RPCModuleServiceManager'
          , 'codeInsertToMethod'      : 'initModules'
          , 'codeInsertToMethodParams': 'android.content.Context'
          , 'codeInsertToMethodLocalVariables':[//asm 站桩方法的变量定义
                                        ['name':'context', 'type': 'android.content.Context']
          ]
          , 'callMethodName'      : 'initModule'
          , 'callMethodParams': 'android.content.Context;com.github.rpc.modularization.RPCModule'
//          , 'include'                 : [//包含类，支持正则表达式（包分隔符需要用/表示，不能用.）
//                                         'com.github.rpc'.replaceAll("\\.", "/") + ".*",
//           ]
          , 'exclude'                 : [//排除的类，支持正则表达式（包分隔符需要用/表示，不能用.）
                 'androidx.'.replaceAll("\\.", "/") + ".*",
                  'android.support'.replaceAll("\\.", "/") + ".*"
            ]
        ],
        [ //自动注册组件
          'type'             : 'AnnotationModuleAutoRegister',
          'scanAnnotation'   : 'com.github.rpc.modularization.ModuleService'
          , 'codeInsertToClass'   : 'com.github.rpc.modularization.RPCModuleServiceManager'
          , 'codeInsertToMethod'      : 'initModuleServices'
          , 'callMethodName'      : 'registerService'
          , 'callMethodParams': 'java.lang.Class'
//          , 'include'                 : [//包含类，支持正则表达式（包分隔符需要用/表示，不能用.）
//                                         'com.github.rpc'.replaceAll("\\.", "/") + ".*",
//            ]
          , 'exclude'                 : [//排除的类，支持正则表达式（包分隔符需要用/表示，不能用.）
                 'androidx.'.replaceAll("\\.", "/") + ".*",
                  'android.support'.replaceAll("\\.", "/") + ".*"
            ]
        ]
    ]
}


```
### step4: 根目录gradle.properties进行增量编译、debug日志配置
```
##插件true开启debug修改字节码插件日志
enableDebugPMLog=true

##使用增量编译缓存
enableIncrementalCache=true

```

### stpe5: app主工程的application的onCreate 初始化模块管理中心

```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RPCModuleServiceManager.init(getApplicationContext());

    }
}
```

### step6: 在实现模块的api目录下，添加.api文件，最好建个和实现模块包名一样

```
public interface LoginService{
    String getUserName();
}
```

### step7: module_login模块的build.gradle引入 自动生成的 module_login_api工程
```
    api project(":module_login_api")
```

### step8: module_login模块 接口实现类 继承.api的接口，并且注解为 @ModuleService

```
@ModuleService
public class LoginModuleServiceImpl implements LoginService {
    @Override
    public String getUserName() {
        return "loganpluo";
    }
}
```

### step9: 模块初始化类继承RPCModule接口
```

public class LoginModule implements RPCModule {
    @Override
    public void onInit(Context context) {
        Log.d("LoginModule","onInit");
    }
}

```

### step10: 其他模块 依赖module_login_api， 调用接口LoginService

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

### step11: run
你会惊喜的发现一下日志, 模块自动初始化完成， 接口和 实现自动注册到 模块管理中心了
```
2020-05-07 21:52:05.215 6525-6525/com.github.rpc.modularization D/LoginModule: LoginModule onInit
2020-05-07 21:52:05.216 6525-6525/com.github.rpc.modularization I/RPCModuleServiceManager: registerModuleService serviceImpl:com.github.rpc.module_login.LoginModuleServiceImpl
```

* 原理就是 利用Transform Api 阶段(class ->dex 之前)扫描 指定calss，asm 字节码修改自动注入模块初始化和接口服务绑定代码

效果反编译class如下：
![](https://github.com/loganpluo/RPCModularization/blob/master/pic/asm-java.png)<br>

插件介绍：
https://github.com/loganpluo/RPCModularization/blob/master/plugin-modularization/README.md <br>



<br>




# 参考
美团的组件化 https://tech.meituan.com/2018/12/20/modular-event.html  <br>
cc组件化  <br>

