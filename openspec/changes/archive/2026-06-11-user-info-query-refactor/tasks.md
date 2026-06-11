## 1. 数据文件改造

- [x] 1.1 重新生成customers.json，删除namePinyin和namePinyinInitial字段
- [x] 1.2 更新UserInfoDataGenerator，不再生成拼音字段
- [x] 1.3 更新UserInfo模型类，移除拼音字段

## 2. 系统服务工具暴露

- [x] 2.1 创建UserInfoTool类，封装UserInfoService
- [x] 2.2 实现查询方法（smartSearch、getCustomersByName等）
- [x] 2.3 注册UserInfoTool为Spring Bean
- [x] 2.4 在ChatbotAgent中注入UserInfoTool

## 3. Python脚本改造

- [x] 3.1 修改query_user.py，通过子进程调用Java工具
- [x] 3.2 实现结果解析和格式化输出
- [x] 3.3 保持原有查询接口兼容

## 4. 测试与验证

- [x] 4.1 测试系统服务工具调用
- [x] 4.2 测试Python脚本改造后的功能
- [x] 4.3 验证拼音匹配功能正常
- [x] 4.4 验证精确匹配功能正常

## 5. 文档更新

- [x] 5.1 更新SKILL.md，说明新的调用方式
- [x] 5.2 更新query_user.py的使用说明
