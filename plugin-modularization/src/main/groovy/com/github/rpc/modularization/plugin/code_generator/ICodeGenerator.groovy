package com.github.rpc.modularization.plugin.code_generator


import com.github.rpc.modularization.plugin.class_modifier.ClassModifier
import org.objectweb.asm.MethodVisitor

interface ICodeGenerator{
    public void modifyClass(ClassModifier extension, int opcode, MethodVisitor mv)
}