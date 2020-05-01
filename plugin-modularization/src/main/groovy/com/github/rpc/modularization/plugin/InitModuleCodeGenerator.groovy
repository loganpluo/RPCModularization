package com.github.rpc.modularization.plugin


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
                        , ClassInfoUtil.getDescMethodParamsConfig(classModifier.classModifierConfig.callMethodParams)
                        , false)
            }

            Label label2 = new Label()
            mv.visitLabel(label2)
            mv.visitInsn(Opcodes.RETURN)

            Label endLabel = new Label()
            mv.visitLabel(endLabel)
            mv.visitLocalVariable("context","Landroid/content/Context;", null, startLabel, endLabel, 0)

            LogUtil.i(TAG,"InitModule modifyClass success")
        }

    }

}