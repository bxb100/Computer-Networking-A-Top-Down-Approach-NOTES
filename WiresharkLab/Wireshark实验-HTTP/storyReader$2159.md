# HTTP 身份认证方案

> 原文地址：<http://frontier.userland.com/stories/storyReader$2159>
> 文章有些内容陈旧，仅用来了解 HTTP Authentication Schemes

## 介绍

这篇文章描述两种 HTTP 认证方案并且假定你熟悉基本的 HTTP 请求、返回、状态码和消息头的结构。

## HTTP 访问认证框架

HTTP 协议（[RFC 2616](ftp://ftp.isi.edu/in-notes/rfc2616.txt)）定义了一种简单访问认证方案。假设有一组特定页面 -- 通常被认为一个被保护的域或者仅仅只是一个域 -- 只能被那些服务需要证明时能提供证书的用户访问。

如果一个 HTTP 客户端，比如：WEB 浏览器，试图访问一个被保护的页面，服务器会返回 `401 Unauthorized` 状态码，并且会包含一个 `WWW-Authenticate` 消息头。这个消息头必须包含一个适用于所请求页面的认证方案。

然后，客户端会发起另外一个请求，这时包含一个 `Authorization` 消息头，该消息头包含了用户的认证信息以用于服务端验证口令。

如果服务端接受凭据，返回请求的页面。否则，服务端会返回 `401 Unauthorized` 响应来指明客户端认证失败。

具体的 `WWW-Authenticate` 内容依赖于使用的认证方案类型。在写本文是，两种认证方案被广泛使用。

## 基本访问认证

基本认证方案假定你（客户端）凭据包含一个用户名和一个只有你和服务器知道的密码。

服务器的 `401` 响应包含一个身份验证质询，该质询一个 `Basic` 字段和一个指定受保护领域名称的键值对组成。例子：

```http
WWW-Authenticate: Basic realm="Control Panel"
```

当收到上述的 401 响应，你的浏览器会提示你输入对应领域的用户名和密码。浏览器后续请求的 `Authorization` 消息头包含令牌 『Basic』和 base64 编码的用户名、冒号和密码。

```http
Authorization: Basic QWRtaW46Zm9vYmFy
```

服务器使用 base64 解码用户名和密码，然后验证用户名和密码是否匹配。如果匹配，服务器会返回请求的页面。

这种方案的主要缺点是窃听者可以相对简单地窥视你的用户名和密码，因为它是公开传输的。

## 密码学来助

另一种称为摘要身份验证的身份验证方案通过使用加密哈希来弥补这一弱点，通常是 [RFC 1321](ftp://ftp.isi.edu/in-notes/rfc1321.txt) 中定义的 MD5 消息摘要算法。

MD5 能够把任意长度的数据转换为一个 128 位的摘要。例如：有 $2^128 = 340,282,366,920,938,463,463,374,607,431,768,211,456$ 个可能的摘要。由于 MD5 是单向函数，因此实际上不可能逆向计算并从输出值中获得输入值。

MD5 算法的实现是 Frontier 6.1 的一部分，请参阅 [string.hashMD5](http://docserver.userland.com/string/hashMD5)。

现在，如果你只是拿着你的用户名和密码然后 MD5 一下，就像使用 base64 进行基本身份验证那样将结果发送到服务器，那么潜在的窃听者显然可以记录你的散列后的用户名和密码。当服务器提示需要他需要验证，那么他只需要简单的发送你的散列值就能成功验证。这叫做重放攻击。

## 摘要访问认证

为了安全地防止重放攻击，显然需要一个更复杂的过程：摘要访问身份验证方案。

首先，服务器的初始 401 响应的 `WWW-Authenticate` 标头包含更多超出领域字符串的键值对，包括一个称为 `nonce` 的值。服务器有责任确保每个 401 响应都带有一个唯一的、以前未使用的 nonce 值。

你的浏览器后续请求的 `Authentication` 标头包含您的明文用户名、它刚刚收到的 `nonce` 值以及所谓的摘要请求，它可能按如下方式计算（如果它是用 `UserTalk` 编写的）：

```UserTalk
A1 = string.hashMD5 (username + ":" + realm + ":" + password)

A2 = string.hashMD5 (paramTable.method + ":" + paramTable.uri)


requestdigest = string.hashMD5 (A1 + ":" + nonce + ":" + A2)
```

所有的输入值都被服务器所知或者是请求头的一部分，服务器能够和你做一样的计算，如果如果计算出相同的请求摘要，那么服务器就能够确保你拥有正确的密码。

此外，由于 MD5 算法不可逆，潜在的窃听者无法从请求摘要中获取你的密码。此外，服务器可以通过不接受多个身份验证请求的 `nonce` 值来非常有效地防止重放攻击。对于下一个请求，服务器分发一个不同的 `nonce` 值，因此客户端必须重新计算请求摘要。

实际上，这里描述的只是“摘要”访问认证的一个稍微简化的版本。 [RFC 2617](ftp://ftp.isi.edu/in-notes/rfc2617.txt) 描述了额外的功能，包括一种防止第三方在传输过程中操纵消息正文的方法。

## 安全注意事项

你应当意识到，即使适应摘要验证，除了密码，其它所有数据都是明文传输，潜在的窃听者完全可以访问。

客户端无法确定它实际上正在与它打算与之交谈的服务器交谈。没有适当的机制允许服务器向客户端验证自己。

有关摘要访问身份验证的安全属性的详细审查，请参阅 [RFC 2617](ftp://ftp.isi.edu/in-notes/rfc2617.txt) 的第 4 节。

不幸的是，一些浏览器缺乏对摘要身份验证的支持，请参阅此[讨论组消息](http://discuss.userland.com/msgReader$12483)和回复以获取详细信息。

## 指南

RFC 2616 -- Hypertext Transfer Protocol -- HTTP/1.1
RFC 2617 -- HTTP Authentication: Basic and Digest Access Authentication
RFC 1321 -- The MD5 Message-Digest Algorithm
mainResponder.security.httpAuthentication -- Protect a website via basic or digest authentication.
string.hashMD5 -- Compute a hash value using the MD5 algorithm.
