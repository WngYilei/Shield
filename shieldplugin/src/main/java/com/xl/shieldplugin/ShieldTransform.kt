package com.xl.shieldplugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.xl.buildsrc.ShieldClassVisitor
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

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =  TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = false


    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        val inputs = transformInvocation?.inputs

        val outputProvider = transformInvocation?.outputProvider

        inputs?.forEach {
            it.directoryInputs.forEach { dir ->

                val dest: File? = outputProvider?.getContentLocation(
                    dir.name, dir.contentTypes, dir.scopes, Format.DIRECTORY
                )
                dest?.let {
                    transformDir(dir.file, it)
                }
            }

            it.jarInputs.forEach { jar ->

                val dest: File? = outputProvider?.getContentLocation(
                    jar.name, jar.contentTypes, jar.scopes, Format.JAR
                )
                dest?.let {
                    FileUtils.copyFile(jar.file, it)
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
                    transformDir(file, destFile)
                } else if (file.isFile) {

                    val ischeck = checkClassFile(file)

                    FileUtils.touch(destFile)
                    if (ischeck) {
                        try {
                            asmHandleFile(file.absolutePath, destFile.absolutePath)
                        } catch (e: Exception) {
                            System.err.println("asmHandleFile:class文件：${file.name}    异常信息：$e")
                            e.printStackTrace()
                            FileUtils.copyFile(file,destFile)
                        }
                    } else {

                        FileUtils.copyFile(file,destFile)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            System.err.println(e)
        }
    }


    private fun asmHandleFile(inputPath: String, destPath: String) {

        val fileInputStream = FileInputStream(inputPath)
        val cr = ClassReader(fileInputStream)
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        val visitor = ShieldClassVisitor(cw)
        cr.accept(visitor, ClassReader.EXPAND_FRAMES)
        val fos = FileOutputStream(destPath)
        fos.write(cw.toByteArray())
        fos.close()
    }

    private fun checkClassFile(file: File): Boolean {
        return file.name.endsWith(".class") && !file.name.startsWith("R$") && "R.class" != file.name && "BuildConfig.class" != file.name && !file.path.contains("android")
    }

}
