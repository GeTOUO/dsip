# D-SIP

借鉴 netty 的 HTTP 编解码, 实现 `sip2.0` 协议的解析.

## 编解码

- `SipObjectTcpDecoder` 对外的核心编解码类,实现了 `TCP SIP` 消息的编解码.  
- `SipObjectUdpDecoder` 对外的核心编解码类,实现了 `UDP SIP` 消息的编解码.  
- `SipObjectAggregator` 对外的核心编解码类,实现了 SIP 消息的封装, 组装成 FullSipMessage  

## 业务处理

- `GbLoggingHandler` 日志输出处理器
- `SipRequestHandler` 请求消息分发中心
- `SipResponseHandler` 响应消息分发中心

### com.dxp.sip.bus.fun.controller

> 具体业务消息处理.

- InviteController 收到终端对讲请求消息处理
- MessageController 收到Message心跳消息处理
- RegisterController 收到注册消息处理

## 测试

test包下是相关的测试类