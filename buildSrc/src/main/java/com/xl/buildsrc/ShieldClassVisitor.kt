package com.xl.buildsrc


import org.objectweb.asm.*

/**
 * @Author : wyl
 * @Date : 2023/4/20
 * Desc :
 */
class ShieldClassVisitor(cw: ClassVisitor) : ClassVisitor(Opcodes.ASM6, cw), Opcodes {
    var shieldAnnotationVisit: ShieldAnnotationVisit? = null


    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
//        val clazz = ClassLoader.getSystemClassLoader().loadClass("com.xl.shield.ShieldAnnotation")
//        clazz.annotations.forEach {
//            println("注解：${it}")
//        }


        val annotationVisit = super.visitAnnotation(descriptor, visible)
        if (descriptor != null) {
            shieldAnnotationVisit = ShieldAnnotationVisit(descriptor, visible)
            return shieldAnnotationVisit as ShieldAnnotationVisit
        }
        return annotationVisit


    }


    override fun visitMethod(access: Int,
                             name: String?,
                             descriptor: String?,
                             signature: String?,
                             exceptions: Array<String?>?): MethodVisitor? {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (shieldAnnotationVisit == null) {
            methodVisitor
        } else {
            ShieldMethodVisitor(methodVisitor, access, name, descriptor)
        }
    }


}


