package com.xl.buildsrc


import org.objectweb.asm.*

/**
 * @Author : wyl
 * @Date : 2023/4/20
 * Desc :
 */
class ShieldClassVisitor(cw: ClassVisitor) : ClassVisitor(Opcodes.ASM5, cw) {

    override fun visitMethod(access: Int,
                             name: String?,
                             descriptor: String?,
                             signature: String?,
                             exceptions: Array<String?>?): MethodVisitor? {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name.equals("<init>")) return methodVisitor
        if (methodVisitor != null) return ShieldMethodVisitor(
            methodVisitor, access, name, descriptor
        )

        return null
    }


}


