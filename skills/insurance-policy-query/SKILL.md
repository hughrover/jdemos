---
name: insurance-policy-query
description: Use this skill when the user wants to query insurance policy information. This includes searching by user name or user ID. If the user mentions looking up insurance policy, checking policy details, or querying policy information, use this skill.
---

# Insurance Policy Query Guide

## Overview

This skill provides functionality to query insurance policy information. The query process is:

1. First, get the user ID by user name (using user query API)
2. Then, query insurance policies by user ID

## IMPORTANT: Use Python Script

**You MUST use the Python script to query insurance policy information. Do NOT use any Java tools.**

The script is located at: `skills/insurance-policy-query/scripts/query_policy.py`

### How to Call the Script

To query insurance policy information, use the `execute_shell_command` tool:

```
execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py <query_type> <query_value>")
```

## Usage Examples

### Query by User Name

To find all policies for a user by their name:

```
User: 查看陶国军的保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py userName 陶国军")
```

```
User: 查询陈伟的保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py userName 陈伟")
```

```
User: 查看用户名下所有保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py userName 用户姓名")
```

### Query by User ID

To find all policies for a user by their ID:

```
User: 查询用户1的保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py userId 1")
```

## Query Flow

### By User Name

```
用户输入: "查看陶国军的保单"
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 1: 查询用户信息                     │
│ GET /api/users/search?name=陶国军       │
│ 获取用户ID                              │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 2: 查询保单                         │
│ GET /api/insurance-policies?            │
│     policyholderId=1                    │
└─────────────────────────────────────────┘
    │
    ▼
返回陶国军的保单信息
```

### By User ID

```
用户输入: "查询用户1的保单"
    │
    ▼
┌─────────────────────────────────────────┐
│ 查询保单                                 │
│ GET /api/insurance-policies?            │
│     policyholderId=1                    │
└─────────────────────────────────────────┘
    │
    ▼
返回用户1的保单信息
```

## Script Parameters

The script accepts the following parameters:

```
python3 query_policy.py <query_type> <query_value>
```

Query types:
- `userName` - Query by user name (first get user ID, then query policies)
- `userId` - Query by user ID (direct query)

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
Python Script (query_policy.py)
    ↓ HTTP调用
User API (GET /api/users/search?name=X)
    ↓ 获取用户ID
Insurance Policy API (GET /api/insurance-policies?policyholderId=X)
    ↓
InsurancePolicyService (Service Layer)
    ↓
data/insurance_policies.json (Data File)
```

### API Endpoints

- `GET /api/users/search?name={name}` - Search users by name
- `GET /api/users/{id}` - Get user by ID
- `GET /api/insurance-policies?policyholderId={id}` - Get policies by policyholder ID
- `GET /api/insurance-policies/count` - Get total policy count
