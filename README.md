# network-loom-panama
使用jdk新特性loom + panama 实现的网络框架

### jdk版本
-  openjdk-21.0.2

### 编译配置
由于loom、panama中有部分预览的api，所以编译和运行都需要配置才能使用
- 编译时配置 `--enable-preview`
- 运行时VM配置 `--enable-preview --enable-native-access=ALL-UNNAMED`

### 动态库生成
- 执行 `make` 命令，会在 [lib](lib) 目录下生成动态库
- [lib](lib) 目录下已经存放了各个环境生成的动态库，建议自己在机器上重新执行 `make` 命令生成
- [c](c) 是用到文件
