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
public class DemoService {

    double count = 0;
    double slowRatioThreshold = 0;
    double slowRatioThresholdCount = 0;
    long failStartTime = 0;

    @SentinelResource(value = "queryUserInfoSlowRequest", blockHandler = "exceptionHandler", fallback = "helloFallback")
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
    public String helloFallback(long s) {
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


    @SentinelResource(value = "loginTestParamFlow", entryType = EntryType.IN, blockHandler = "exceptionBaseHandler")
    public String loginTestParamFlow(String userName, String password) {
        sleep(ThreadLocalRandom.current().nextInt(40, 60));
        System.out.println(String.format("param:%s request pass", password));
        return password;
    }


    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public String exceptionBaseHandler(String userName, String password, BlockException ex) {
//        System.out.println(String.format(
//                "[%d] Parameter flow metrics for pass count for param <%s> is %d, block count: %d", TimeUtil.currentTimeMillis(), userName,
//                passCountMap.get(userName).get(), blockCountMap.get(userName).get()));
        System.out.println("request block error,param:" + password);
        return "Oops " + password;
    }


    @SentinelResource(value = "testABCD",entryType = EntryType.IN,blockHandler = "exceptionAuthority")
    public String testAuthority(String origin) {
        System.out.println(String.format("Passed for resource testABCD, origin is %s", origin));
        return "passed";
    }

    public String exceptionAuthority(String origin, BlockException ex){
        System.out.println("request block error,param:" +origin);
        return "block";
    }



    public String queryUserInfo(String userName) {
        log.info("request pass userName:{} done",userName);
        return userName;
    }
}
