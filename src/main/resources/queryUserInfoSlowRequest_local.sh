#!/bin/bash
### 基于分组规则测试脚本
curl http://127.0.0.1:8094/restSlowValue;
for (( i = 0; i < 1000000; i++ )); do
 curl http://127.0.0.1:8094/queryUserInfoSlowRequest
done
