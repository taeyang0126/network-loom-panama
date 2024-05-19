# network-loom-panama
使用jdk新特性loom + panama 实现的网络框架

### jdk版本
-  openjdk-21.0.2

### 编译配置
由于loom、panama中有部分预览的api，所以编译和运行都需要配置才能使用
- 编译时配置 `--enable-preview`
- 运行时VM配置 `--enable-preview --enable-native-access=ALL-UNNAMED`

### MemorySegment
MemorySegment类是自JDK19版本开始往后，JDK中的一个顶级项目Project Panama中Java语言对于C语言中指针类型的抽象
