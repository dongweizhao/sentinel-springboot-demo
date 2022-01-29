package com.sentinel.example.web;

import com.sentinel.example.service.FlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weizhao.dong
 * @Date: 2021/6/30 3:05 下午
 */
@RestController
public class TestController {

    @Autowired
    private FlowService demoService;

    /**
     * @description:熔断降级测试
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
     * @description:限流测试
     * @author: dongweizhao
     * @date: 2021/6/30 3:07 下午
     * @param:
     * @return: void
     */
    @GetMapping(value = "/testFlow")
    public String testFlow(@RequestParam("origin") String origin) {
        return demoService.testFlow(origin);
    }



}
