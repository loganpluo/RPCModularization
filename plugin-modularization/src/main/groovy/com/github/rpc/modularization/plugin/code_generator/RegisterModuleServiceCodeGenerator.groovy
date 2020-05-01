package com.github.rpc.modularization.plugin.code_generator

import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import com.github.rpc.modularization.plugin.util.LogUtil
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class RegisterModuleServiceCodeGenerator implements ICodeGenerator {

    private static final String TAG = "RegisterModuleServiceCodeGenerator"

    public void modifyClass(ClassModifier classModifier, int opcode, MethodVisitor mv){

        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
            classModifier.classList.each { name ->
                Label label1 = new Label()
                mv.visitLabel(label1)
                LogUtil.i(TAG,"MyMethodVisitor new name:$name")

                LogUtil.i(TAG,"MyMethodVisitor call staic class:${classModifier.classModifierConfig.codeInsertToClass}" +
                        ".${classModifier.classModifierConfig.callMethodName}." +
                        "${classModifier.classModifierConfig.callMethodParams}")

                mv.visitLdcInsn(Type.getObjectType(name))

                mv.visitMethodInsn(Opcodes.INVOKESTATIC
                        , classModifier.classModifierConfig.codeInsertToClass
                        , classModifier.classModifierConfig.callMethodName
                        , classModifier.classModifierConfig.callMethodParams
                        , false)
            }

            Label label2 = new Label()
            mv.visitLabel(label2)
            mv.visitInsn(Opcodes.RETURN)

            LogUtil.i(TAG,"RegisterModuleService modifyClass  success")
        }

    }
}