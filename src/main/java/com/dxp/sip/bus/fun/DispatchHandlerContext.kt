package com.dxp.sip.bus.`fun`

import com.dxp.sip.codec.sip.SipMethod
import com.dxp.sip.util.ClassScanner.doScanAllClasses
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

/**
 * gb-sip 请求分发
 *
 * @author carzy
 * @date 2020/8/14
 */
object DispatchHandlerContext {

    private val CONTROLLER_MAP: MutableMap<SipMethod?, HandlerController> = ConcurrentHashMap(256)
    private var ALLOW_METHOD = ""

    fun method(method: SipMethod): HandlerController? {
        return CONTROLLER_MAP[method]
    }

    fun allowMethod(): String {
        return ALLOW_METHOD
    }

    @Throws(IOException::class, ClassNotFoundException::class, InstantiationException::class, IllegalAccessException::class)
    fun init() {
        val classes = doScanAllClasses("com.dxp.sip.bus.fun.controller")
        for (aClass in classes) {
            if (HandlerController::class.java.isAssignableFrom(aClass)) {
                addHandlerController(newClass(aClass) as HandlerController)
            }
        }
        ALLOW_METHOD = CONTROLLER_MAP.keys.stream().map { obj: SipMethod? -> obj!!.name() }.collect(Collectors.joining(","))
    }

    private fun addHandlerController(o: HandlerController) {
        require(!CONTROLLER_MAP.containsKey(o.method())) { "handlerController has be created." }
        CONTROLLER_MAP[o.method()] = o
    }

    @Throws(IllegalAccessException::class, InstantiationException::class)
    private fun <T> newClass(tClass: Class<T>): T {
        return tClass.newInstance()
    }

    init {
        try {
            init()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}