package com.xl.buildsrc

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager

import org.apache.commons.io.FileUtils

import jdk.internal.org.objectweb.asm.tree.*;
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
            it.directoryInputs.forEach { dir ->
                //获取transform的输出目录，等我们插桩后就将修改过的class文件替换掉transform输出目录中的文件，就达到修改的效果了。
                val dest: File? = outputProvider?.getContentLocation(
                    dir.name, dir.contentTypes, dir.scopes, Format.DIRECTORY
                )
                println(dest)
                dest?.let {
                    transformDir(dir.file, it)
                }

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
        try {

            if (dest.exists()) {
                FileUtils.forceDelete(dest);
            }
            FileUtils.forceMkdir(dest);

            val srcDirPath: String = input.absolutePath
            val destDirPath: String = dest.absolutePath
            val fileList: Array<File> = input.listFiles() ?: return
            for (file in fileList) {

                val destFilePath: String = file.absolutePath.replace(srcDirPath, destDirPath)

                val destFile = File(destFilePath)

                if (file.isDirectory) {
                    //如果是文件夹，继续遍历
                    transformDir(file, destFile)
                } else if (file.isFile) {
                    FileUtils.touch(destFile);
                    asmHandleFile(file.absolutePath, destFile.absolutePath)
                }
            }
        } catch (e: Exception) {
            println("transformDir:" + e)
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
            println(inputPath)
            val fileInputStream = FileInputStream(inputPath)


            val classReader = jdk.internal.org.objectweb.asm.ClassReader(fileInputStream)

            val cn = ClassNode()
            classReader.accept(cn, 0)
            val visibleAnnotations = cn.visibleAnnotations




            //将原文件的输入流交给ASM的ClassReader
            val cr = ClassReader(fileInputStream)
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
