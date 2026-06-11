## Context

当前用户查询skill通过Python脚本直接读取customers.json文件中的拼音字段来实现匹配。系统中已存在UserInfoService，它已经实现了完整的拼音转换和匹配功能（通过PinyinMatcher）。需要将skill改造为调用系统服务，而不是直接读取文件。

## Goals / Non-Goals

**Goals:**
- 将skill改造为通过调用Java系统服务实现查询
- 删除customers.json中的拼音字段，使数据模型更通用
- 保持skill的完整功能，包括拼音匹配
- 通过Spring AI的ToolCallback机制暴露系统服务

**Non-Goals:**
- 不改变UserInfoService的核心逻辑
- 不改变skill的基本使用方式
- 不增加新的查询功能

## Decisions

### 1. 服务调用方式

**选择**: 通过Spring AI的ToolCallback机制暴露系统服务

**理由**:
- 符合现有项目架构（ChatbotAgent中已使用ToolCallback）
- 智能体可以直接调用工具，无需额外的HTTP调用
- 类型安全，易于维护

**实现方式**:
- 创建UserInfoTool类，封装UserInfoService
- 注册为Spring Bean，返回ToolCallback
- 在ChatbotAgent中注入并使用

### 2. Python脚本改造

**选择**: 修改Python脚本，通过子进程调用Java工具

**理由**:
- 保持skill的独立性
- Python脚本作为skill的入口点
- 通过标准输出返回结果

**替代方案**:
- 直接在SKILL.md中描述工具调用：需要修改skill架构
- 使用HTTP调用：增加网络依赖

### 3. 数据文件改造

**选择**: 删除customers.json中的namePinyin和namePinyinInitial字段

**理由**:
- 拼音转换由系统服务实时处理
- 数据模型更通用，符合真实信息系统设计
- 减少数据冗余

## Risks / Trade-offs

**[风险] 性能影响** → 系统服务已实现内存缓存，性能可接受

**[风险] Python脚本调用Java的复杂性** → 通过子进程调用，输出解析简单可靠

**[权衡] 依赖Java环境** → 项目本身就是Java项目，这是可接受的

**[权衡] 增加系统耦合** → 通过接口抽象，保持松耦合
