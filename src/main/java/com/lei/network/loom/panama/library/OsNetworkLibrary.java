package com.lei.network.loom.panama.library;

import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;
import com.lei.network.loom.panama.util.NativeUtil;

/**
 * <p>
 * OsNetworkLibrary <br/>
 * sealed 表明是一个密封接口，只允许存在指定的这几个实现类，密封接口的主要目的是为Java编译器提供一些帮助，
 * 比如在针对密封接口的switch语句中，编译器就可以提前知道所有可能出现的实现类的类型，
 * 这样我们可以忽略掉默认的default分支，比较建议在明确知道实现类会有哪些的情况下，将接口进行封闭
 * </p>
 *
 * @author 伍磊
 */
public sealed interface OsNetworkLibrary permits WindowsNetworkLibrary, LinuxNetworkLibrary, MacOSNetworkLibrary {

    // 定义当前使用哪个实现
    OsNetworkLibrary CURRENT = switch (NativeUtil.ostype()) {
        case Windows -> new WindowsNetworkLibrary();
        case Linux -> new LinuxNetworkLibrary();
        case MacOS -> new MacOSNetworkLibrary();
        default -> throw new FrameworkException(ExceptionType.NETWORK, "Unsupported operating system");
    };


}
