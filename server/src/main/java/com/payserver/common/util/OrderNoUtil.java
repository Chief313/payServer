package com.payserver.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单号生成工具类
 */
public final class OrderNoUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OrderNoUtil() {
    }

    /**
     * 生成唯一订单号
     * 格式：ORD + 时间戳 + 4位随机数
     *
     * @return 订单号
     */
    public static String generate() {
        String time = LocalDateTime.now().format(FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD" + time + random;
    }
}
