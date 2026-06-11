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

## Usage

### Query by Chinese Name

To find a user by their Chinese name:

```
查询用户：张三
```

### Query by Pinyin

To find a user by pinyin:

```
查询用户：zhangsan
```

### Query by Pinyin Initials

To find a user by pinyin initials:

```
查询用户：zs
```

### Fuzzy Search

For fuzzy matching (similar pronunciations):

```
查询用户：张山
```

This will match "张三" and other similar names.

### List All Users

To see all users (limited to first 20 for display):

```
显示所有用户
```

### Query by ID

To find a user by their ID:

```
查询用户ID：1
```

## Implementation

The skill uses the following components:

1. **CustomerDataCache**: Manages customer data in memory with indexing
2. **PinyinMatcher**: Handles pinyin conversion and matching
3. **UserInfoService**: Provides the query interface

### Data Storage

Customer data is stored in `data/customers.json` with 1000 simulated customer records.

### Query Logic

The smart search function follows this priority:

1. Exact name match (highest priority)
2. Pinyin full match
3. Pinyin initial match
4. Fuzzy pinyin match
5. Fuzzy name match (lowest priority)

## Examples

### Example 1: Exact Match
**User**: 查询用户张三
**Result**: Returns user "张三" with full details

### Example 2: Pinyin Match
**User**: 查询用户zhangsan
**Result**: Returns all users with pinyin "zhangsan"

### Example 3: Fuzzy Match
**User**: 查询用户张山
**Result**: Returns "张三" and other similar names

### Example 4: Initials Match
**User**: 查询用户zs
**Result**: Returns all users with initials "zs"

## Response Format

The skill returns user information in the following format:

```
姓名：张三
年龄：28
性别：男
电话：13800138000
邮箱：zhangsan@qq.com
地址：北京市朝阳区街道1号
公司：阿里巴巴
职位：软件工程师
```

For multiple results, the information is presented as a numbered list.
