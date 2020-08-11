# D-SIP

借鉴 netty 的 HTTP 编解码, 实现 `sip2.0` 协议的解析.

## 编解码

`SipObjectDecoder` 对外的核心编解码类,实现了 SIP 消息的编解码.
`SipObjectAggregator` 对外的核心编解码类,实现了 SIP 消息的封装, 组装成 FullSipMessage

## 测试

test包下是相关的测试类
