## Why

当前用户信息查询skill的拼音匹配逻辑存在问题：当用户输入中文姓名（如"陈玮"）时，系统直接用中文去匹配客户的拼音字段，而不是先将中文转换成拼音再进行匹配。这导致谐音字无法正确匹配。

例如：
- 用户输入"陈玮"，系统找不到匹配
- 但"陈玮"的拼音是"chenwei"，"陈伟"的拼音也是"chenwei"
- 系统应该能够通过拼音匹配找到"陈伟"

## What Changes

- 修改UserInfoServiceImpl.smartSearch()方法，在拼音匹配前先将用户输入转换成拼音
- 当精确匹配失败时，自动将用户输入转换成拼音，然后与客户的拼音进行匹配
- 保持其他匹配逻辑不变（首字母匹配、模糊匹配等）

## Capabilities

### New Capabilities

（无新增capability）

### Modified Capabilities

- `user-info-query`: 改进拼音匹配逻辑，支持中文输入的拼音模糊匹配

## Impact

- 修改文件：`src/main/java/agent/demo/userinfo/service/UserInfoServiceImpl.java`
- 依赖：PinyinMatcher工具类（已存在）
- 验证点：输入"陈玮"能够匹配到"陈伟"
