package com.xl.buildsrc

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import groovyjarjarasm.asm.tree.ClassNode

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


/**
 * @Author : wyl
 * @Date : 2023/4/20
 * Desc :
 */
class ShieldTransform : Transform() {
    override fun getName() = "ShieldTransform"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = false


    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        val inputs = transformInvocation?.inputs

        val outputProvider = transformInvocation?.outputProvider

        inputs?.forEach {
            it.directoryInputs.forEach {
                //获取transform的输出目录，等我们插桩后就将修改过的class文件替换掉transform输出目录中的文件，就达到修改的效果了。
                val dest = outputProvider?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.DIRECTORY
                )

            }
        }

    }


    /**
     * 遍历文件夹，对文件进行插桩
     *
     * @param input 源文件
     * @param dest  源文件修改后的输出地址
     */

    private fun transformDir(input: File, dest: File) {
        if (dest.exists()) {
            FileUtils.forceDelete(dest)
        }
        FileUtils.forceMkdir(dest)
        val srcDirPath: String = input.getAbsolutePath()
        val destDirPath: String = dest.getAbsolutePath()
        val fileList: Array<File> = input.listFiles() ?: return
        for (file in fileList) {
            val destFilePath: String = file.getAbsolutePath().replace(srcDirPath, destDirPath)
            val destFile = File(destFilePath)
            if (file.isDirectory()) {
                //如果是文件夹，继续遍历
                transformDir(file, destFile)
            } else if (file.isFile()) {
                //创造了大小为0的新文件，或者，如果该文件已存在，则将打开并删除该文件关闭而不修改，但更新文件日期和时间
                FileUtils.touch(destFile)
                asmHandleFile(file.getAbsolutePath(), destFile.getAbsolutePath())
            }
        }
    }


    /**
     * 通过ASM进行插桩
     *
     * @param inputPath 源文件路径
     * @param destPath  输出路径
     */
    private fun asmHandleFile(inputPath: String, destPath: String) {
        try {
            //获取源文件的输入流
            val `is` = FileInputStream(inputPath)
            //将原文件的输入流交给ASM的ClassReader
            val cr = ClassReader(`is`)
            val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            //构建一个ClassVisitor，ClassVisitor可以理解为一组回调接口，类似于ClickListener
            val visitor = ShieldClassVisitor(cw)
            //这里是重点，asm通过ClassReader的accept方法去解析class文件，去读取每一个节点。
            // 每读到一个节点，就会通过传入的visitor相应的方法回调，这样我们就能在每一个节点的回调中去做操作。
            cr.accept(visitor, 0)
            //将文件保存到输出目录下
            val fos = FileOutputStream(destPath)
            fos.write(cw.toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}
