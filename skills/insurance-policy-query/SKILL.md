---
name: insurance-policy-query
description: Use this skill when the user wants to query insurance policy information. This includes searching by policy ID, policyholder name, or user ID. If the user mentions looking up insurance policy, checking policy details, or querying policy information, use this skill.
---

# Insurance Policy Query Guide

## Overview

This skill provides functionality to query insurance policy information from a database of 2023 policy records. It supports:

- Query by policy ID (e.g., "P000001")
- Query by policyholder name (e.g., "陶国军")
- Query by user ID

## IMPORTANT: Use Python Script

**You MUST use the Python script to query insurance policy information. Do NOT use any Java tools.**

The script is located at: `skills/insurance-policy-query/scripts/query_policy.py`

### How to Call the Script

To query insurance policy information, use the `execute_shell_command` tool:

```
execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py <query_type> <query_value>")
```

## Usage Examples

### Query by Policy ID

To find a policy by its ID:

```
User: 查询保单P000001
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py policyId P000001")
```

### Query by Policyholder Name

To find all policies for a policyholder:

```
User: 查询陶国军的保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py policyholderName 陶国军")
```

```
User: 查看陈伟的保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py policyholderName 陈伟")
```

```
User: 查询用户名下所有保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py policyholderName 用户姓名")
```

### Query by User ID

To find all policies for a user by their ID:

```
User: 查询用户1的保单
Action: execute_shell_command(command="python3 skills/insurance-policy-query/scripts/query_policy.py userId 1")
```

## Script Parameters

The script accepts the following parameters:

```
python3 query_policy.py <query_type> <query_value>
```

Query types:
- `policyId` - Query by policy ID (e.g., "P000001")
- `policyholderName` - Query by policyholder name (e.g., "陶国军", "陈伟")
- `userId` - Query by user ID (e.g., "1")

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
InsurancePolicyController (REST API)
    ↓
InsurancePolicyService (Service Layer)
    ↓
data/insurance_policies.json (Data File)
```

### Data Storage

Policy data is stored in `data/insurance_policies.json` with 2023 policy records. Each policy is associated with a user from `data/customers.json` via `policyholderId`.

### REST API Endpoints

- `GET /api/insurance-policies/{policyId}` - Get policy by ID
- `GET /api/insurance-policies?policyholderName={name}` - Get policies by policyholder name
- `GET /api/insurance-policies?userId={userId}` - Get policies by user ID
- `GET /api/insurance-policies/count` - Get total policy count
