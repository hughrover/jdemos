---
name: insurance-policy-query
description: Use this skill when the user wants to query insurance policy information. This includes searching by user name or user ID. If the user mentions looking up insurance policy, checking policy details, or querying policy information, use this skill.
---

# Insurance Policy Query Guide

## Overview

This skill provides functionality to query insurance policy information. The query process is:

1. First, use the user information query skill to get user IDs (supports pinyin fuzzy search)
2. Then, query insurance policies by user IDs

## IMPORTANT: Two-Step Query Process

**You MUST follow the two-step process to query insurance policies:**

### Step 1: Get User IDs

Use the `search_users` tool to find users by name or pinyin:

```
search_users(query="陈伟")
search_users(query="chenwei")
search_users(query="cw")
```

This will return a list of matching users with their IDs.

### Step 2: Query Policies

Use the `execute_shell_command` tool to query policies by user IDs:

```
execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py <user_id1> <user_id2> ...")
```

## Usage Examples

### Query by User Name

To find all policies for a user by their name:

```
User: 查看陈伟的保单
Action 1: search_users(query="陈伟")
Action 2: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py 4 5")
```

### Query by Pinyin

To find all policies for a user by pinyin:

```
User: 查看chenwei的保单
Action 1: search_users(query="chenwei")
Action 2: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py 4 5")
```

### Query by Pinyin Initials

To find all policies for a user by pinyin initials:

```
User: 查看cw的保单
Action 1: search_users(query="cw")
Action 2: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py 4 5")
```

### Query by User ID

To find all policies for a user by their ID:

```
User: 查询用户1的保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py 1")
```

### Query Multiple Users

To find policies for multiple users:

```
User: 查询用户1和2的保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py 1 2")
```

## Query Flow

### By User Name (with Fuzzy Search)

```
用户输入: "查看陈伟的保单"
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 1: 调用用户信息查询技能            │
│ search_users(query="陈伟")              │
│ 返回匹配的用户列表                      │
│ [{"id": 4, "name": "陈伟"}, ...]        │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 2: 调用保单查询脚本                │
│ query_policy.py 4 5                     │
│ 查询所有用户的保单                      │
└─────────────────────────────────────────┘
    │
    ▼
返回所有匹配用户的保单信息
```

### By Pinyin (Fuzzy Search)

```
用户输入: "查看chenwei的保单"
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 1: 调用用户信息查询技能            │
│ search_users(query="chenwei")           │
│ 通过拼音模糊搜索找到用户                │
│ [{"id": 4, "name": "陈伟"}, ...]        │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 2: 调用保单查询脚本                │
│ query_policy.py 4 5                     │
│ 查询所有用户的保单                      │
└─────────────────────────────────────────┘
    │
    ▼
返回所有匹配用户的保单信息
```

## Script Parameters

The script accepts the following parameters:

```
python3 query_policy.py <user_id1> [user_id2] [user_id3] ...
```

Examples:
- `python3 query_policy.py 1` - Query policies for user 1
- `python3 query_policy.py 1 2 3` - Query policies for users 1, 2, and 3
- `python3 query_policy.py 4 5` - Query policies for users 4 and 5

## Response Format

The script returns policy information in the following format:

```
保单号：P000001
投保人：陶国军
被保险人：陶国军的家属
险种：教育保险
保费：49884.00元
保额：1047564.00元
保险期间：2024-06-11 至 2026-06-11
状态：有效
```

For multiple results, the information is presented as a numbered list.

## Technical Details

### Service Architecture

The skill uses a layered architecture:

```
智能体
    ↓ 调用
User Info Skill (search_users)
    ↓ 返回用户ID列表
Python Script (query_policy.py)
    ↓ HTTP调用
Insurance Policy API (GET /api/insurance-policies?policyholderId=X)
    ↓
InsurancePolicyService (Service Layer)
    ↓
data/insurance_policies.json (Data File)
```

### API Endpoints

- `GET /api/insurance-policies?policyholderId={id}` - Get policies by policyholder ID
- `GET /api/insurance-policies/count` - Get total policy count

### User Info Skill

The user information query skill supports:
- Exact name matching (Chinese characters)
- Pinyin full match (e.g., "chenwei")
- Pinyin initial match (e.g., "cw")
- Fuzzy matching for similar pronunciations
