package com.github.rpc.modularization.plugin

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.github.rpc.modularization.plugin.util.LogUtil
import org.gradle.api.Project
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import com.android.build.api.transform.Format
import org.apache.commons.io.FileUtils

/**
 * 定义一个Transform
 */
class TestInjectTransform extends Transform {

    private static final String TAG = "TestInjectTransform"

    private Project mProject

    // 构造函数，我们将Project保存下来备用
    TestInjectTransform(Project project) {
        this.mProject = project
    }

    // 设置我们自定义的Transform对应的Task名称
    // 类似：transformClassesWithPreDexForXXX
    // 这里应该是：transformClassesWithInjectTransformForxxx
    @Override
    String getName() {
        return 'TestInjectTransform'
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    //  这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transform的作用范围
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    // 当前Transform是否支持增量编译
    @Override
    boolean isIncremental() {
        return false
    }

    // 核心方法
    // inputs是传过来的输入流，有两种格式：jar和目录格式
    // outputProvider 获取输出目录，将修改的文件复制到输出目录，必须执行
    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        def startTs = System.currentTimeMillis()
        println "-------------------- ${getName()} transform 开始-------------------"

        println "inputs.size: ${inputs.size()}"
        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历

        def modifyClassFile

        inputs.each {
            TransformInput input ->
                // 遍历文件夹
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
                println "directoryInputs.size: ${input.directoryInputs.size()}"
                input.directoryInputs.each {
                    DirectoryInput directoryInput ->
//                        // 注入代码
//                        MyInjectByJavassit.injectToast(directoryInput.file.absolutePath, mProject)

                        // 获取输出目录
                        def dest = outputProvider.getContentLocation(directoryInput.name,
                                directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                        String root = directoryInput.file.absolutePath
                        if (!root.endsWith(File.separator)) root += File.separator

                        def targetClassFile =
                                TestASMClassModify.recordModifyClassByDirectory(directoryInput.file, root, dest)

                        if(targetClassFile != null){
                            modifyClassFile = targetClassFile
                            LogUtil.d(TAG,"targetClassFile: $targetClassFile")
                        }

                        println("directory inputname:${directoryInput.file.absolutePath}" +
//                                " inputType.contentTypes:${directoryInput.contentTypes}" +
//                                " inputType.scopes:${directoryInput.scopes}" +
                                " output dest: $dest.absolutePath")
                        // 将input的目录复制到output指定目录
                        FileUtils.copyDirectory(directoryInput.file, dest)
                }
                println "jarInputs.size: ${input.jarInputs.size()}"
                //对类型为jar文件的input进行遍历
                input.jarInputs.each {
                        //jar文件一般是第三方依赖库jar文件
                    JarInput jarInput ->
                        // 重命名输出文件（同目录copyFile会冲突）
                        def jarName = jarInput.name
//                        println("jar: $jarInput.file.absolutePath")
                        def md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
                        if (jarName.endsWith('.jar')) {
                            jarName = jarName.substring(0, jarName.length() - 4)
                        }
                        def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                        println("jar jarInput：${jarInput.file.absolutePath} " +
                                "output jarName:$jarName, md5Name:$md5Name, dest: $dest.absolutePath")

                        FileUtils.copyFile(jarInput.file, dest)
                }
        }

        if(modifyClassFile){
            TestASMClassModify.testModifyClass(modifyClassFile, "TestInjectTransform")
        }

        long cost = System.currentTimeMillis() - startTs
        println "--------------------- ${getName()} transform cost:$cost 结束-------------------"
    }
}
