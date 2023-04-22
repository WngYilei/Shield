package com.xl.buildsrc


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes;

/**
 * @Author : wyl
 * @Date : 2023/4/20
 * Desc :
 */
class ShieldClassVisitor(cw: ClassVisitor) : ClassVisitor(Opcodes.ASM6, cw), Opcodes {

    override fun visitMethod(access: Int,
                             name: String?,
                             descriptor: String?,
                             signature: String?,
                             exceptions: Array<String?>?): MethodVisitor? {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)

        return if (name.equals("<init>")) {
            methodVisitor
        } else {
            ShieldMethodVisitor(methodVisitor, access, name, descriptor)
        }

    }


}


