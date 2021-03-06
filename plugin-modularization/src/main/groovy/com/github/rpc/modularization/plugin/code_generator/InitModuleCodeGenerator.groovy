package com.github.rpc.modularization.plugin.code_generator

import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import com.github.rpc.modularization.plugin.config.LocalVariable
import com.github.rpc.modularization.plugin.util.LogUtil
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class InitModuleCodeGenerator implements ICodeGenerator {

    private static final String TAG = "InitModuleCodeGenerator"

    @Override
    public void modifyClass(ClassModifier classModifier, int opcode, MethodVisitor mv){

        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
            Label startLabel = null
            classModifier.classList.each { name ->
                Label label1 = new Label()
                if(startLabel == null){
                    startLabel = label1
                }
                mv.visitLabel(label1)
                LogUtil.i(TAG,"MyMethodVisitor new name:$name")
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitTypeInsn(Opcodes.NEW, name)
                mv.visitInsn(Opcodes.DUP)
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, name, "<init>", "()V", false)

                LogUtil.i(TAG,"MyMethodVisitor call staic class:${classModifier.classModifierConfig.codeInsertToClass}" +
                        ".${classModifier.classModifierConfig.callMethodName}." +
                        "${classModifier.classModifierConfig.callMethodParams}")



                mv.visitMethodInsn(Opcodes.INVOKESTATIC
                        , classModifier.classModifierConfig.codeInsertToClass
                        , classModifier.classModifierConfig.callMethodName
                        , classModifier.classModifierConfig.callMethodParams
                        , false)
            }

            Label label2 = new Label()
            mv.visitLabel(label2)
            mv.visitInsn(Opcodes.RETURN)

            Label endLabel = new Label()
            mv.visitLabel(endLabel)
            //循环读取LocalVariables来设置方法变量
            LogUtil.d(TAG,"modifyClass classModifier.localVariables:${classModifier.classModifierConfig.localVariables}")
            if(classModifier.classModifierConfig.localVariables){
                classModifier.classModifierConfig.localVariables.eachWithIndex{ LocalVariable entry, int i ->
                    mv.visitLocalVariable(entry.name,entry.desc, null, startLabel, endLabel, i)
                }
            }
            LogUtil.i(TAG,"InitModule modifyClass success")
        }

    }

}