## ADDED Requirements

### Requirement: 系统服务调用

系统 SHALL 通过调用Java系统服务实现用户信息查询，而非直接读取文件。

#### Scenario: 服务工具暴露

- **WHEN** 系统启动时
- **THEN** UserInfoService通过ToolCallback暴露为可调用工具

#### Scenario: 工具调用查询

- **WHEN** skill需要查询用户信息时
- **THEN** 通过调用UserInfoTool实现查询

### Requirement: 拼音实时转换

系统 SHALL 在查询时实时进行拼音转换，而非依赖预存储的拼音字段。

#### Scenario: 拼音匹配查询

- **WHEN** 用户输入拼音（如"zhangsan"）
- **THEN** 系统实时转换姓名拼音并匹配

#### Scenario: 首字母匹配查询

- **WHEN** 用户输入拼音首字母（如"zs"）
- **THEN** 系统实时提取首字母并匹配

### Requirement: 数据模型简化

系统 SHALL 使用简化的数据模型，不包含拼音字段。

#### Scenario: 数据文件格式

- **WHEN** customers.json被读取
- **THEN** 文件不包含namePinyin和namePinyinInitial字段

### Requirement: 技能功能保持

系统 SHALL 保持原有skill的完整功能。

#### Scenario: 精确匹配

- **WHEN** 用户输入完整中文姓名
- **THEN** 系统返回精确匹配结果

#### Scenario: 模糊匹配

- **WHEN** 用户输入谐音或模糊拼音
- **THEN** 系统通过系统服务实现模糊匹配

#### Scenario: 结果展示

- **WHEN** 查询返回结果
- **THEN** 以格式化方式展示用户信息
