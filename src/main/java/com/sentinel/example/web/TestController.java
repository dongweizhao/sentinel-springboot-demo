package com.sentinel.example.web;

import com.sentinel.example.paramflow.ParamFlowQpsRunner;
import com.sentinel.example.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author weizhao.dong
 * @Date: 2021/6/30 3:05 下午
 */
@RestController
public class TestController {
    @Autowired
    private DemoService demoService;

    /**
     * @description:慢调用测试
     * @author: dongweizhao
     * @date: 2021/6/30 3:07 下午
     * @param:
     * @return: void
     */
    @RequestMapping(value = "/queryUserInfoSlowRequest")
    public String queryUserInfoSlowRequest() {
        return demoService.queryUserInfoSlowRequest();
    }


    /**
     * @description:慢调用测试
     * @author: dongweizhao
     * @date: 2021/6/30 3:07 下午
     * @param:
     * @return: void
     */
    @RequestMapping(value = "/loginTestParamFlow")
    public String loginTestParamFlow() {
        String[] PARAMS = new String[]{"test1", "test2", "test3", "test4"};
        String[] PARAMS2 = new String[]{"a1", "a2", "a3", "a4"};
        int i = ThreadLocalRandom.current().nextInt(0, PARAMS.length);
        return demoService.loginTestParamFlow(PARAMS[i], PARAMS2[i]);
    }


    private static final int PARAM_A = 1;
    private static final int PARAM_B = 2;
    private static final int PARAM_C = 3;
    private static final int PARAM_D = 4;

    /**
     * Here we prepare different parameters to validate flow control by parameters.
     */
    private static final Integer[] PARAMS = new Integer[]{PARAM_A, PARAM_B, PARAM_C, PARAM_D};

    private static final String RESOURCE_KEY = "resA";

    /**
     * @description:慢调用测试
     * @author: dongweizhao
     * @date: 2021/6/30 3:07 下午
     * @param:
     * @return: void
     */
    @RequestMapping(value = "/loginTestParamFlo2")
    public void loginTestParamFlow2() throws InterruptedException {
        final int threadCount = 20;
        ParamFlowQpsRunner<Integer> runner = new ParamFlowQpsRunner<>(PARAMS, RESOURCE_KEY, threadCount, 120);
        runner.tick();
        Thread.sleep(1000);
        runner.simulateTraffic();
    }

    /**
     * @description:来源访问测试
     * @author: dongweizhao
     * @date: 2021/6/30 3:07 下午
     * @param:
     * @return: void
     */
    @GetMapping(value = "/testAuthority")
    public String testAuthority(@RequestParam("origin") String origin) {
      return   demoService.testAuthority(origin);
    }

    /**
     * @description:查询用户信息
     * @author: dongweizhao
     * @date: 2021/6/30 3:07 下午
     * @param:
     * @return: void
     */
    @GetMapping(value = "/queryUserInfo")
    public String queryUserInfo(@RequestParam("userName") String userName) {
        return   demoService.queryUserInfo(userName);
    }

    /**
     * @description:查询用户信息
     * @author: dongweizhao
     * @date: 2021/6/30 3:07 下午
     * @param:
     * @return: void
     */
    @GetMapping(value = "/queryUserInfo2")
    public String queryUserInfo2(@RequestParam("userName") String userName) {
        return   demoService.queryUserInfo(userName);
    }





}
