/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sentinel.example.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreaker.State;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.EventObserverRegistry;
import com.alibaba.csp.sentinel.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * todo weizhao.dong
 * Run this demo, and the output will be like:
 *
 * <pre>
 * 1529399827825,total:0, pass:0, block:0
 * 1529399828825,total:4263, pass:100, block:4164
 * 1529399829825,total:19179, pass:4, block:19176 // circuit breaker opens
 * 1529399830824,total:19806, pass:0, block:19806
 * 1529399831825,total:19198, pass:0, block:19198
 * 1529399832824,total:19481, pass:0, block:19481
 * 1529399833826,total:19241, pass:0, block:19241
 * 1529399834826,total:17276, pass:0, block:17276
 * 1529399835826,total:18722, pass:0, block:18722
 * 1529399836826,total:19490, pass:0, block:19492
 * 1529399837828,total:19355, pass:0, block:19355
 * 1529399838827,total:11388, pass:0, block:11388
 * 1529399839829,total:14494, pass:104, block:14390 // After 10 seconds, the system restored
 * 1529399840854,total:18505, pass:0, block:18505
 * 1529399841854,total:19673, pass:0, block:19676
 * </pre>
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class SlowRatioCircuitBreakerDemo {

    private static final String KEY = "some_method";


    private static AtomicInteger total = new AtomicInteger();
    private static AtomicInteger pass = new AtomicInteger();
    private static AtomicInteger block = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        //???????????????
        initDegradeRule();
        //???????????????
        registerStateChangeObserver();
        //???????????????
        double count = 0;
        //??????????????????50ms???????????????
        double slowRatioThresholdCount = 0;
        long failStartTime = 0;
        while (true) {
            Entry entry = null;
            long start = System.currentTimeMillis();
            try {
                ++count;
                entry = SphU.entry(KEY);
                //????????????
                if (failStartTime != 0) {
                    System.out.println("request fusing recovery time: " + (System.currentTimeMillis() - failStartTime) + " ms");
                    failStartTime = 0;
                    TimeUnit.SECONDS.sleep(10);
                }
                pass.incrementAndGet();
                sleep(ThreadLocalRandom.current().nextInt(40, 60));
                long end = System.currentTimeMillis();
                long callTime = end - start;
                //????????????
                if (callTime > 50) {
                    slowRatioThresholdCount += 1;
                }
                double slowRatioThreshold = slowRatioThresholdCount / count;
                System.out.println("request success totalCount:" + count + " passcount:" + pass.get() + " time: " + (end - start) + " ms count:" + count + "  slowRatioThreshold:" + slowRatioThreshold);
            } catch (BlockException e) {
                //??????????????????????????????
                if (failStartTime == 0) {
                    failStartTime = System.currentTimeMillis();
                }
                System.out.println("request fusing time: " + LocalDateTime.now() + " ms");
                sleep(ThreadLocalRandom.current().nextInt(5, 10));
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        }

    }

    //?????????????????????????????????????????????????????????????????????????????????????????????
    private static void registerStateChangeObserver() {
        EventObserverRegistry.getInstance().addStateChangeObserver("logging",
                (prevState, newState, rule, snapshotValue) -> {
                    if (newState == State.OPEN) {
                        System.err.println(String.format("%s -> OPEN at %d, snapshotValue=%.2f", prevState.name(),
                                TimeUtil.currentTimeMillis(), snapshotValue));
                    } else {
                        System.err.println(String.format("%s -> %s at %d", prevState.name(), newState.name(),
                                TimeUtil.currentTimeMillis()));
                    }
                });
    }

    private static void initDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule(KEY)
                .setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
                //?????????????????????
                .setCount(50)
                //???????????????????????? s 10s
                .setTimeWindow(10)
                //?????????????????????????????????????????????????????????1.8.0 ?????????
                .setSlowRatioThreshold(0.2)
                //?????????????????????????????????????????????????????????????????????????????????????????????????????????1.7.0 ?????????
                .setMinRequestAmount(100)
                //???????????????????????? ms???????????????20s
                .setStatIntervalMs(20000);
        rules.add(rule);

        DegradeRuleManager.loadRules(rules);
        System.out.println("Degrade rule loaded: " + rules);
    }

    private static void sleep(int timeMs) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeMs);
        } catch (InterruptedException e) {
            // ignore
        }
    }

}
