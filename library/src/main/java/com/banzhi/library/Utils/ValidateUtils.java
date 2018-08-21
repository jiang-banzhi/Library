package com.banzhi.library.Utils;

import java.util.Collection;

/**
 * <pre>
 * author : No.1
 * time : 2017/3/28.
 * desc : 数据有效性判断
 * </pre>
 */

public class ValidateUtils {
    public static boolean isValidate(Collection<?> collection) {
        return null != collection && !collection.isEmpty();
    }
}
