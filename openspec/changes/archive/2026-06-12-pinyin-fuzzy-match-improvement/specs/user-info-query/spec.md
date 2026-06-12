## MODIFIED Requirements

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
