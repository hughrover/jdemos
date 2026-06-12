---
name: user-info-query
description: Use this skill when the user wants to query user information, search for customers by name, or find user details. This includes searching by Chinese name, pinyin, pinyin initials, or fuzzy matching. If the user mentions looking up someone's information, finding a customer, or querying user details, use this skill.
---

# User Information Query Guide

## Overview

This skill provides functionality to query user information from a database of 1000 simulated customers. It supports:

- Exact name matching (Chinese characters)
- Pinyin full match (e.g., "zhangsan")
- Pinyin initial match (e.g., "zs")
- Fuzzy matching for similar pronunciations
- Smart search that combines all matching methods

## IMPORTANT: Use UserInfoTool

**You MUST use the UserInfoTool to query user information. Do NOT use any Python scripts.**

The UserInfoTool is a Java tool that provides all query functionality. It is registered as a Spring AI ToolCallback and can be called directly.

### How to Call UserInfoTool

To query user information, call the `search_users` tool with the query parameter:

```
search_users(query="陶国军")
search_users(query="taoguojun")
search_users(query="zs")
```

## Usage Examples

### Query by Chinese Name

To find a user by their Chinese name:

```
User: 查询用户陶国军
Action: search_users(query="陶国军")
```

### Query by Pinyin

To find a user by pinyin:

```
User: 查询用户taoguojun
Action: search_users(query="taoguojun")
```

### Query by Pinyin Initials

To find a user by pinyin initials:

```
User: 查询用户tgj
Action: search_users(query="tgj")
```

### Fuzzy Search

For fuzzy matching (similar pronunciations):

```
User: 查询用户张山
Action: search_users(query="张山")
```

This will match "张三" and other similar names.

### List All Users

To see all users:

```
User: 显示所有用户
Action: search_users(query="")
```

## Query Logic

The UserInfoTool's smart search function follows this priority:

1. Exact name match (highest priority)
2. Pinyin full match
3. Pinyin initial match
4. Fuzzy pinyin match
5. Fuzzy name match (lowest priority)

## Response Format

The tool returns user information in the following format:

```
找到 1 个匹配的用户：

姓名：陶国军
年龄：23
性别：男
电话：13355893226
邮箱：taoguojun1@qq.com
地址：长春市西城区街道57号
公司：美团
职位：人力资源经理
```

For multiple results, the information is presented as a numbered list.

## Technical Details

### Pinyin Conversion

Pinyin conversion is performed in real-time using the `PinyinMatcher` utility class. This approach:

- Eliminates the need to store pinyin fields in the data model
- Makes the data model more generic and suitable for real-world systems
- Leverages the existing `pinyin4j` library for accurate conversion

### Service Architecture

The skill uses a layered architecture:

```
UserInfoTool (ToolCallback)
    ↓
UserInfoService (Service Layer)
    ↓
CustomerDataCache (Data Cache)
    ↓
data/customers.json (Data File)
```

### Performance

- Data is cached in memory for fast access
- Pinyin conversion is performed on-demand
- Results are limited to 20 items for display purposes
