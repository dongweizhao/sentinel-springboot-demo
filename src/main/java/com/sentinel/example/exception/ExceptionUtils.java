package com.sentinel.example.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author weizhao.dong
 * @Date: 2021/7/2 4:55 下午
 */
public class ExceptionUtils {
    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public static String exceptionCommonHandler(String param, BlockException ex) {
        System.out.println("request block error,param:" +param);
        return "Oops "+param;
    }

    // Fallback 函数，函数签名与原函数一致或加一个 Throwable 类型的参数.
    public static String fallback(String s) {
        return String.format("Halooooo %s", s);
    }
}
