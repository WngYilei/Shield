package com.xl.buildsrc

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes


/**
 * @Author : wyl
 * @Date : 2023/4/23
 * Desc :
 */
class ShieldAnnotationVisit(descriptor: String, visible: Boolean) : AnnotationVisitor(Opcodes.ASM6) {


    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        println( "   name：${name}   value:${value}")
    }




}