## Why

用户信息查询skill存在问题：智能体在处理中文姓名查询时，没有正确调用UserInfoTool，而是调用了已废弃的Python脚本query_user.py。该脚本现在是"空壳"，只返回提示信息，导致中文查询失败。

根本原因：
1. Python脚本query_user.py已废弃，但仍被智能体调用
2. SKILL.md没有明确指导智能体使用UserInfoTool
3. 智能体可能被旧的Python脚本示例误导

## What Changes

- 更新SKILL.md，明确指导智能体使用UserInfoTool，禁止调用Python脚本
- 删除或重命名query_user.py，避免智能体误调用
- 验证智能体能够正确使用UserInfoTool进行中文和拼音查询

## Capabilities

### New Capabilities

（无新增capability）

### Modified Capabilities

- `user-info-query`: 修改skill配置，确保智能体正确调用UserInfoTool

## Impact

- 修改文件：`skills/user-info-query/SKILL.md`
- 删除文件：`skills/user-info-query/scripts/query_user.py`
- 验证点：智能体能够正确处理中文和拼音查询
