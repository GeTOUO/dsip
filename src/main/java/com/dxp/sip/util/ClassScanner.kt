package com.dxp.sip.util

import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.util.*

/**
 * 包扫描
 *
 * @author carzy
 * @date 2020/8/14
 */
object ClassScanner {
    /**
     * 在文件夹中扫描包和类
     */
    @Throws(ClassNotFoundException::class)
    private fun doScanPackageClassesByFile(classes: MutableSet<Class<*>>, packageName: String, packagePath: String) {
        // 转为文件
        val dir = File(packagePath)
        if (!dir.exists() || !dir.isDirectory) {
            return
        }

        // 列出文件，进行过滤
        val dirFiles = dir.listFiles() ?: return
        for (file in dirFiles) {
            if (file.isDirectory) {
                // 如果是目录，则递归
                doScanPackageClassesByFile(classes, packageName + "." + file.name, file.absolutePath)
            } else {
                // 用当前类加载器加载 去除 fileName 的 .class 6 位
                val className = file.name.substring(0, file.name.length - 6)
                val loadClass = Thread.currentThread().contextClassLoader.loadClass("$packageName.$className")
                classes.add(loadClass)
            }
        }
    }

    /**
     * Do scan all classes set.
     *
     * @return the set
     */
    @JvmStatic
    @Throws(IOException::class, ClassNotFoundException::class)
    fun doScanAllClasses(packageName: String): Set<Class<*>> {
        var packages = packageName
        val classes: MutableSet<Class<*>> = LinkedHashSet()

        // 如果最后一个字符是“.”，则去掉
        if (packages.endsWith(".")) {
            packages = packages.substring(0, packages.lastIndexOf('.'))
        }

        // 将包名中的“.”换成系统文件夹的“/”
        val basePackageFilePath = packages.replace('.', '/')
        val resources = Thread.currentThread().contextClassLoader.getResources(basePackageFilePath)
        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            val protocol = resource.protocol
            if ("file" == protocol) {
                val filePath = URLDecoder.decode(resource.file, "UTF-8")
                // 扫描文件夹中的包和类
                doScanPackageClassesByFile(classes, packages, filePath)
            }
        }
        return classes
    }
}