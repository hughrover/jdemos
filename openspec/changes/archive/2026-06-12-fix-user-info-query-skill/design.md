## Context

用户信息查询skill已经实现了UserInfoTool，支持中文和拼音查询。但是智能体在处理查询时，没有正确调用UserInfoTool，而是调用了已废弃的Python脚本query_user.py。该脚本现在是"空壳"，只返回提示信息，导致中文查询失败。

问题根源：
1. Python脚本query_user.py已废弃，但仍被智能体调用
2. SKILL.md没有明确指导智能体使用UserInfoTool
3. 智能体可能被旧的Python脚本示例误导

## Goals / Non-Goals

**Goals:**
- 确保智能体正确调用UserInfoTool进行查询
- 消除Python脚本的干扰
- 保持skill的完整功能（中文、拼音、首字母查询）

**Non-Goals:**
- 不修改UserInfoTool的核心逻辑
- 不修改UserInfoService的实现
- 不增加新的查询功能

## Decisions

### 1. 处理Python脚本

**选择**: 删除query_user.py脚本

**理由**:
- 脚本已经是"空壳"，不再执行真正查询
- 保留脚本会误导智能体
- 删除后，智能体只能使用UserInfoTool

**替代方案**:
- 重命名脚本：可能仍会被智能体发现
- 修改脚本内容：增加复杂性，不如直接删除

### 2. 更新SKILL.md

**选择**: 重写SKILL.md，明确指导智能体使用UserInfoTool

**理由**:
- SKILL.md是智能体的主要参考文档
- 需要明确告诉智能体使用UserInfoTool
- 提供清晰的查询示例

**实现方式**:
- 删除Python脚本相关的描述
- 强调使用UserInfoTool
- 提供明确的查询示例

### 3. 验证方式

**选择**: 通过实际查询验证

**理由**:
- 需要确保智能体真正调用UserInfoTool
- 验证中文和拼音查询都能正常工作

## Risks / Trade-offs

**[风险] 智能体可能仍不调用UserInfoTool** → 通过清晰的SKILL.md描述引导

**[风险] 删除脚本可能影响其他功能** → query_user.py已废弃，删除安全

**[权衡] 依赖SKILL.md的描述** → 这是skill的标准配置方式
