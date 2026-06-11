## Why

当前智能体系统已具备基础对话和两个技能能力，但缺少用户信息查询功能。为了提升智能体的实用性，需要添加一个能够查询用户个人信息的技能。该技能需要支持拼音模糊匹配，以应对用户输入不准确或存在谐音的情况，提高查询的容错性和用户体验。

## What Changes

- 新增用户信息查询技能（user-info-query skill）
- 实现拼音模糊匹配算法，支持中文姓名的谐音匹配
- 创建1000个模拟客户信息数据，存储在临时文件中
- 封装接口读取文件中的客户信息
- 提供对话框输出客户信息的功能
- 集成到现有的spring-ai-alibaba智能体框架中

## Capabilities

### New Capabilities

- `user-info-query`: 用户信息查询技能，支持通过中文姓名（包括拼音模糊匹配）查询用户个人信息

### Modified Capabilities

（无需修改现有capability）

## Impact

- 新增skill目录：`skills/user-info-query/`
- 新增Java类：用户信息服务、拼音匹配工具类
- 新增数据文件：1000个模拟客户信息的临时数据文件
- 依赖项：需要添加拼音转换库（如pinyin4j或pinyin-plus）
- 集成点：通过SkillRegistry注册新技能
