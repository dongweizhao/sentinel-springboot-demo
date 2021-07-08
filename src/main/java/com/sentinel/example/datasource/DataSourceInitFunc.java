package com.sentinel.example.datasource;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

//@Configuration
public class DataSourceInitFunc implements InitFunc {
    //    @Value("${zk.url:127.0.0.1:2181}")
    String zkUrl = "10.211.55.2:2181";
//    /zk
    @Override
    public void init() throws Exception {
        String path="/sentinel_rule_config/sentinel-springboot-demo";
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new ZookeeperDataSource<>(zkUrl, path, source ->
                JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

     //集群限流配置
        ReadableDataSource<String, List<ParamFlowRule>> paramRuleSource = new ZookeeperDataSource<>(zkUrl, path, source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {}));
        ParamFlowRuleManager.register2Property(paramRuleSource.getProperty());

    }

    // nacos server ip
    private static final String remoteAddress = "localhost";
    // nacos group
    private static final String groupId = "Sentinel:Demo";
    // nacos dataId
    private static final String dataId = "com.alibaba.csp.sentinel.demo.flow.rule";

//    //nacos
//    @Override
//    public void init() throws Exception {
//
//        loadRules();
//        loadParamRules();
//
//
//        // 注册服务访问相关属性并指定了token为客户端
////        ClusterClientConfigManager.registerServerAssignProperty(assignConfigNacosDataSource.getProperty());
//        ClusterClientConfigManager.applyNewConfig(new ClusterClientConfig().setRequestTimeout(1000));
//        ClusterStateManager.applyState(ClusterStateManager.CLUSTER_CLIENT);
//
//    }
//
//
//    private static void loadRules() {
//        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId,
//                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
//                }));
//        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
//    }
//
//
//    private static void loadParamRules() {
//        ReadableDataSource<String, List<ParamFlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId,
//                source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {
//                }));
//        ParamFlowRuleManager.register2Property(flowRuleDataSource.getProperty());
//    }

}