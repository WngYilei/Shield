package com.xl.shieldplugin


import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter


/**
 * @Author : wyl
 * @Date : 2023/4/20
 * Desc :
 */
class ShieldMethodVisitor(val methodVisitor: MethodVisitor,
                          access: Int,
                          name: String?,
                          descriptor: String?) :
    AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, descriptor) {

    var shieldAnnotationVisit: ShieldAnnotationVisit? = null


    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val annotationVisit = super.visitAnnotation(descriptor, visible)
        if (descriptor != null && descriptor == "Lcom/xl/shieldlib/annotation/ShieldAnnotation;") {
            shieldAnnotationVisit = ShieldAnnotationVisit()
            return shieldAnnotationVisit as ShieldAnnotationVisit
        }

        return annotationVisit
    }


    override fun onMethodEnter() {
        super.onMethodEnter()
        if (shieldAnnotationVisit == null) return

        printMethod(name,shieldAnnotationVisit?.key,shieldAnnotationVisit?.time)

        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitLineNumber(19, label0)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "com/xl/shieldlib/ShieldUtils",
            "getInstance",
            "()Lcom/xl/shieldlib/ShieldUtils;",
            false
        )
        methodVisitor.visitLdcInsn(shieldAnnotationVisit?.key)
        methodVisitor.visitIntInsn(SIPUSH, shieldAnnotationVisit?.time ?: 1000)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/xl/shieldlib/ShieldUtils",
            "isShield",
            "(Ljava/lang/String;I)Z",
            false
        )
        val label1 = Label()
        methodVisitor.visitJumpInsn(IFEQ, label1)
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitLineNumber(20, label2)
        methodVisitor.visitInsn(RETURN)
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(23, label1)
        methodVisitor.visitFrame(F_SAME, 0, null, 0, null)

    }


}

private val LINE = "═════════════════════════════════════════"

fun printMethod(name: String, key: String?, time: Int?) {
    val msgBuilder: StringBuilder = StringBuilder(16 * 10)
    msgBuilder.append(" ")
        .append("\n╔").append(LINE).append("╗")
        .append("\n║ [Method]:").append(name)
        .append("\n║ [KEY]:").append(key)
        .append("\n║ [TIME]:").append("$time ms")
        .append("\n╚").append(LINE).append("╝")
    println(msgBuilder)
}
