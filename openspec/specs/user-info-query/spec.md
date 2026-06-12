# User Info Query Specification

## Requirements

### Requirement: 技能配置

系统 SHALL 通过SKILL.md配置skill，明确指导智能体使用UserInfoTool进行查询。

#### Scenario: 智能体调用UserInfoTool

- **WHEN** 用户请求查询用户信息
- **THEN** 智能体调用UserInfoTool，而不是Python脚本

#### Scenario: 中文姓名查询

- **WHEN** 用户输入中文姓名（如"陶国军"）
- **THEN** 智能体调用UserInfoTool.searchUsers("陶国军")

#### Scenario: 拼音查询

- **WHEN** 用户输入拼音（如"taoguojun"）
- **THEN** 智能体调用UserInfoTool.searchUsers("taoguojun")

### Requirement: Python脚本处理

系统 SHALL 删除已废弃的Python脚本，避免误导智能体。

#### Scenario: 脚本删除

- **WHEN** skill被加载时
- **THEN** query_user.py脚本不存在，智能体无法调用

### Requirement: 文档清晰性

SKILL.md SHALL 明确说明使用UserInfoTool，不提及Python脚本。

#### Scenario: 文档内容

- **WHEN** 智能体读取SKILL.md
- **THEN** 文档明确说明使用UserInfoTool进行查询

### Requirement: 拼音模糊匹配

系统 SHALL 支持将中文输入转换成拼音后进行匹配，实现谐音字匹配。

#### Scenario: 谐音字匹配

- **WHEN** 用户输入"陈玮"（数据库中是"陈伟"）
- **THEN** 系统将"陈玮"转换成拼音"chenwei"，匹配到"陈伟"

#### Scenario: 精确匹配优先

- **WHEN** 用户输入"陈伟"（数据库中存在）
- **THEN** 系统直接返回精确匹配结果，不进行拼音转换

#### Scenario: 拼音输入匹配

- **WHEN** 用户输入"chenwei"
- **THEN** 系统直接用拼音匹配，找到"陈伟"

### Requirement: 首字母匹配

系统 SHALL 支持将中文输入转换成拼音首字母后进行匹配。

#### Scenario: 中文输入首字母匹配

- **WHEN** 用户输入"陈玮"
- **THEN** 系统将"陈玮"转换成首字母"cw"，匹配到"陈伟"

#### Scenario: 首字母输入匹配

- **WHEN** 用户输入"cw"
- **THEN** 系统直接用首字母匹配，找到"陈伟"
