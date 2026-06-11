## Why

当前用户查询skill通过Python脚本直接读取customers.json文件中的拼音字段（namePinyin、namePinyinInitial）来实现匹配。这种方式存在两个问题：
1. 大多数真实信息系统中不会存储拼音字段，数据模型不通用
2. 系统中已有的UserInfoService已经实现了拼音转换和匹配功能，重复实现造成资源浪费

需要将skill改造为调用系统服务，实现更通用、更高效的数据查询方式。

## What Changes

- 改造Python查询脚本，通过调用Java系统服务实现查询，而非直接读取文件
- 删除customers.json中所有用户的namePinyin和namePinyinInitial字段
- 在UserInfoService中暴露工具接口，供skill调用
- 保持skill的可用性，确保拼音匹配功能正常工作

## Capabilities

### New Capabilities

- `user-info-query-v2`: 改进版用户信息查询技能，通过调用系统服务实现查询

### Modified Capabilities

- `user-info-query`: 原有技能将被v2版本替代

## Impact

- 修改文件：`skills/user-info-query/scripts/query_user.py`
- 修改文件：`data/customers.json`（删除拼音字段）
- 修改文件：`src/main/java/agent/demo/userinfo/service/UserInfoServiceImpl.java`（暴露工具接口）
- 新增：系统服务工具暴露机制（通过Spring AI的ToolCallback）
