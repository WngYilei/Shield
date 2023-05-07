package com.xl.buildsrc

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes


/**
 * @Author : wyl
 * @Date : 2023/4/23
 * Desc :
 */
class ShieldAnnotationVisit : AnnotationVisitor(Opcodes.ASM6) {

    var  key = ""
    var  time = 0

    override fun visit(name: String?, value: Any?) {
        if ("key" == name) {
           key = value as String
        } else if ("time" == name) {
           time = value as Int
        }

        super.visit(name, value)
    }

}