package com.sentinel.example.service;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author weizhao.dong
 * @Date: 2021/6/30 2:55 下午
 */
@Service
@Slf4j
public class FlowService {
    double count = 0;
    double slowRatioThreshold = 0;
    double slowRatioThresholdCount = 0;
    long failStartTime = 0;

    /**
     * @description:熔断降级测试
     * @author: dongweizhao
     * @date: 2022/1/29 3:02 下午
     * @param:
     * @return: java.lang.String
     */
    @SentinelResource(value = "queryUserInfoSlowRequest", blockHandler = "exceptionHandler", fallback = "slowRequestFallback")
    public String queryUserInfoSlowRequest() {
        long start = System.currentTimeMillis();
        ++count;
        if (failStartTime != 0) {
            System.out.println("request fusing recovery time: " + (System.currentTimeMillis() - failStartTime) + " ms");
            failStartTime = 0;
            sleep(1000 * 2);
        }
        sleep(ThreadLocalRandom.current().nextInt(40, 60));
        long end = System.currentTimeMillis();
        long callTime = end - start;
        //调用时间
        if (callTime > 50) {
            slowRatioThresholdCount += 1;
        }
        slowRatioThreshold = slowRatioThresholdCount / count;
        System.out.println("request success time: " + (end - start) + " ms count:" + count + "  slowRatioThreshold:" + slowRatioThreshold);
        return "success";
    }


    // Fallback 函数，函数签名与原函数一致或加一个 Throwable 类型的参数.
    public String slowRequestFallback(long s) {
        return String.format("Halooooo %d", s);
    }

    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public String exceptionHandler(BlockException ex) {
        if (failStartTime == 0) {
            failStartTime = System.currentTimeMillis();
        }
        System.out.println("request fusing time: " + LocalDateTime.now() + " ms");
        sleep(ThreadLocalRandom.current().nextInt(5, 10));
        return "Oops ";
    }

    public static void sleep(int timeMs) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeMs);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * @description:流量控制测试
     * @author: dongweizhao
     * @date: 2022/1/29 3:03 下午
     * @param: origin
     * @return: java.lang.String
     */
    @SentinelResource(value = "testFlow", entryType = EntryType.IN, blockHandler = "exceptionFlowHandler")
    public String testFlow(String origin) {

        System.out.println(String.format("Passed for resource testFlow, requestDate:%s , origin is %s", LocalDateTime.now(), origin));
        return "passed";
    }


    public String exceptionFlowHandler(String origin, BlockException ex) {
        System.out.println(String.format("request block error,requestDate:%s , param:%s ", LocalDateTime.now(), origin));
        return "block";
    }

}
