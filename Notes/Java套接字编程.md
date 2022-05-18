# JAVA 套接字编程

## 类

* `InetAddress` 网络地址，如果不是 IP 地址，而是域名
  * 没有设置 `jdk.net.hosts.file` 的情况下，`nameService` 将使用 `PlatformNameService`, 并且此时我这里`Inet6AddressImpl` 可用
  * 方法 `getByName` -> `getAllByName0` -> `NameServiceAddresses.get` -> `getAddressesFromNameService` -> `public native InetAddress[] lookupAllHostAddr(String hostname)`

* `ServerSocket` 没有 python `listen(1)` 的方法，只能连接完之后 `close`，但是这也会导致下一次连接报错
  * `backlog` 表示的等待连接的队列，最小只能是 1，意思是处理一个，等待一个连接

* JAVA 程序运行先 `javac` 然后再 `java`
  * `javac` 可以指定 `-d` 指定输出目录，也可以不指定，默认是当前目录
  * `java` 可以指定 `-cp` 指定类路径，也可以不指定，默认是当前目录

* 使用 `PrintWriter` 注意设置 `autoFlush` 注意只有 `println` 才会自动刷新，`print` 不会

## 其它

1. JAVA 和 Python 程序配置基本一致
2. 有些网络代理不支持 `UDP`， 所以使用 online 的 vscode 时，有可能连接不上
3. TCP 一旦连接上如果没有 try catch，客户端报错会使用 `Socket` 流断开报错

## 代码

* [TCPClient](java/TCPClient.java)
* [TCPServer](java/TCPServer.java)
* [UDPClient](java/UDPClient.java)
* [UDPServer](java/UDPServer.java)
