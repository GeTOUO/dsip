package com.dxp.sip.bus.`fun`

import com.dxp.sip.codec.sip.FullSipRequest
import com.dxp.sip.util.SendErrorResponseUtil.err400
import com.dxp.sip.util.SendErrorResponseUtil.err405
import com.dxp.sip.util.SendErrorResponseUtil.err500
import io.netty.channel.Channel
import io.netty.channel.DefaultEventLoopGroup
import io.netty.channel.EventLoopGroup
import io.netty.util.concurrent.DefaultThreadFactory
import org.dom4j.DocumentException

/**
 * @author carzy
 * @date 2020/8/14
 */
class DispatchHandler private constructor() {

    /**
     * 异步执行处理函数， 不阻塞work线程。
     */
    private val loopGroup: EventLoopGroup = DefaultEventLoopGroup(DefaultThreadFactory("dis-han"))

    fun handler(request: FullSipRequest, channel: Channel) {
        request.retain()
        loopGroup.submit { handler0(request, channel) }
    }

    private fun handler0(request: FullSipRequest, channel: Channel) {
        try {
            val method = request.method()
            val controller = DispatchHandlerContext.method(method)
            if (controller == null) {
                err405(request, channel)
            } else {
                controller.handler(request, channel)
            }
        } catch (e: DocumentException) {
            err400(request, channel, "xml err")
        } catch (e: Exception) {
            err500(request, channel, e.message!!)
        } finally {
            request.release()
        }
    }

    companion object {
        val INSTANCE = DispatchHandler()
    }
}