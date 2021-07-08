#!/bin/bash
### 基于分组规则测试脚本
for (( i = 0; i < 1000000; i++ )); do
curl http://10.211.55.5:8094/testAuthority?origin=appB
done
