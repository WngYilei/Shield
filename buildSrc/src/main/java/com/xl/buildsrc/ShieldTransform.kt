package com.xl.buildsrc

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


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

                val dest: File? = outputProvider?.getContentLocation(dir.name,
                    dir.contentTypes,
                    dir.scopes,
                    Format.DIRECTORY)

                dest?.let {
                    transformDir(dir.file, it)
                }


            }
        }

    }


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

                    val ischeck  =checkClassFile(file.name)

                    println("$ischeck  :  ${file.name}")

                    if (ischeck || !file.path.contains("androidx")) {
                        FileUtils.touch(destFile)
                        asmHandleFile(file.absolutePath, destFile.absolutePath)
                    } else {
                        println("${file.isDirectory}")
                        FileUtils.copyFile(file, dest)
                    }

                }
            }
        } catch (e: Exception) {
            println("transformDir:" + e)
        }
    }


    private fun asmHandleFile(inputPath: String, destPath: String) {
        try {
            val fileInputStream = FileInputStream(inputPath)
            val cr = ClassReader(fileInputStream)
            val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            val visitor = ShieldClassVisitor(cw)
            cr.accept(visitor, 0)
            val fos = FileOutputStream(destPath)
            fos.write(cw.toByteArray())
            fos.close()
        } catch (e: Exception) {
            println(e)
        }
    }


    fun checkClassFile(name: String): Boolean {
        //只处理需要的class文件
        return name.endsWith(".class") && !name.startsWith("R\$") && "R.class" != name && "BuildConfig.class" != name && "android/support/v4/app/FragmentActivity.class" == name
    }

}
