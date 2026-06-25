---
name: branch-overview
description: Use this skill when the user wants to get a branch overview, including performance data, customer activities, customer profiles, and local news. This includes querying branch performance, searching for peer bank performance, customer profiles, bank dynamics, and local news. If the user mentions branch overview, bank performance, customer activities, or local news about a bank branch, use this skill.
---

# Branch Overview Guide

## Overview

This skill provides a comprehensive overview of bank branches, integrating:
1. **Branch Performance** - Policy count and premium amount (local data)
2. **Customer Activities** - Activity count and details (local data)
3. **Customer Profile** - Customer portrait and needs (web search)
4. **Local News** - Bank dynamics and local news (web search)

Supported banks: 浦发银行, 工商银行, 建设银行, 中国银行, 农业银行, 招商银行, etc.

## IMPORTANT: Two-Step Process

**You MUST follow the two-step process to get branch overview:**

### Step 1: Extract Branch Information

Extract bank name, city, and branch name from the user's question:

```
User: 浦发银行上海制造局路支行的网点概览
Extract: bank="浦发银行", city="上海", branch="制造局路支行"

User: 工商银行北京朝阳支行的周边同业业绩怎么样？
Extract: bank="工商银行", city="北京", branch="朝阳支行"
```

### Step 2: Call Branch Overview Script

Use the `execute_shell_command` tool to get branch overview:

```
execute_shell_command(command="python3 skills/branch-overview/scripts/branch_overview.py --bank '浦发银行' --city '上海' --branch '制造局路支行'")
```

## Usage Examples

### Get Full Branch Overview

To get a complete branch overview:

```
User: 浦发银行上海制造局路支行的网点概览
Action: execute_shell_command(command="python3 skills/branch-overview/scripts/branch_overview.py --bank '浦发银行' --city '上海' --branch '制造局路支行'")
```

### Query Peer Performance

To query peer bank performance:

```
User: 工商银行北京朝阳支行的周边同业业绩怎么样？
Action: execute_shell_command(command="python3 skills/branch-overview/scripts/branch_overview.py --bank '工商银行' --city '北京' --branch '朝阳支行' --section peer_performance")
```

### Query Customer Profile

To query customer profile:

```
User: 建设银行深圳福田支行的潜客画像是什么？
Action: execute_shell_command(command="python3 skills/branch-overview/scripts/branch_overview.py --bank '建设银行' --city '深圳' --branch '福田支行' --section customer_profile")
```

### Query Local News

To query local news:

```
User: 招商银行杭州西湖支行的周边新闻有哪些？
Action: execute_shell_command(command="python3 skills/branch-overview/scripts/branch_overview.py --bank '招商银行' --city '杭州' --branch '西湖支行' --section local_news")
```

## Query Flow

### Full Overview Query

```
用户输入: "浦发银行上海制造局路支行的网点概览"
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 1: 提取银行、城市、网点信息        │
│ bank="浦发银行", city="上海",           │
│ branch="制造局路支行"                   │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 2: 调用网点概览脚本                │
│ branch_overview.py --bank '浦发银行'    │
│   --city '上海' --branch '制造局路支行' │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ 脚本内部流程：                          │
│ 1. 查询本地业绩数据                     │
│ 2. 查询本地活动数据                     │
│ 3. 联网搜索同业业绩                     │
│ 4. 联网搜索潜客画像                     │
│ 5. 联网搜索银行动态                     │
│ 6. 联网搜索周边新闻                     │
│ 7. 聚合所有数据并格式化输出             │
└─────────────────────────────────────────┘
    │
    ▼
返回完整的网点概览报告
```

## Script Parameters

The script accepts the following parameters:

```
python3 branch_overview.py --bank <银行名称> --city <城市> --branch <网点名称> [options]
```

### Required Parameters

- `--bank` - Bank name (e.g., "浦发银行", "工商银行")
- `--city` - City name (e.g., "上海", "北京")
- `--branch` - Branch name (e.g., "制造局路支行", "朝阳支行")

### Optional Parameters

- `--section` - Specific section to query:
  - `all` - Full overview (default)
  - `performance` - Branch performance only
  - `activities` - Customer activities only
  - `peer_performance` - Peer bank performance (web search)
  - `customer_profile` - Customer profile (web search)
  - `bank_dynamics` - Bank dynamics (web search)
  - `local_news` - Local news (web search)

### Examples

```bash
# Full overview
python3 branch_overview.py --bank '浦发银行' --city '上海' --branch '制造局路支行'

# Performance only
python3 branch_overview.py --bank '浦发银行' --city '上海' --branch '制造局路支行' --section performance

# Peer performance only
python3 branch_overview.py --bank '工商银行' --city '北京' --branch '朝阳支行' --section peer_performance

# Customer profile only
python3 branch_overview.py --bank '建设银行' --city '深圳' --branch '福田支行' --section customer_profile

# Local news only
python3 branch_overview.py --bank '招商银行' --city '杭州' --branch '西湖支行' --section local_news
```

## Response Format

The script returns branch overview in the following format:

```
=== 网点概览：浦发银行制造局路支行 ===

【网点业绩】
本地数据：出单件数 156件，保费总和 2850.0万元
同业对比：周边银行平均出单142件，本支行排名前30%

【客经活动】
本月活动：8场
活动明细：
1. 理财讲座 - 2024-06-05 - 讲座 - 参与人数：45人
2. 客户答谢会 - 2024-06-12 - 答谢会 - 参与人数：120人
...

【客群特征】
潜客画像：周边居民以中产家庭为主，理财需求旺盛...

【周边时事】
银行动态：上海银行业推出新政策...
地区新闻：黄浦区制造业局路周边新建商业综合体...
```

## Technical Details

### Service Architecture

The skill uses a layered architecture:

```
智能体
    ↓ 调用
Branch Overview Script (branch_overview.py)
    ↓ HTTP调用
Branch Performance API (本地数据)
Branch Activity API (本地数据)
Tencent Web Search API (联网搜索)
    ↓
数据聚合与格式化
    ↓
返回网点概览报告
```

### Data Sources

1. **Local Data** - Branch performance and activity data
   - Policy count and premium amount
   - Activity count and details

2. **Web Search** - Real-time information via Tencent Web Search API
   - Peer bank performance comparison
   - Customer profile and needs
   - Bank channel dynamics
   - Local news

### Supported Banks

- 浦发银行 (SPDB)
- 工商银行 (ICBC)
- 建设银行 (CCB)
- 中国银行 (BOC)
- 农业银行 (ABC)
- 招商银行 (CMB)
- 交通银行 (BOCOM)
- 邮储银行 (PSBC)
- 兴业银行 (CIB)
- 中信银行 (CITIC)

### Environment Variables

- `TENCENT_API_SECRET_ID` - Tencent Cloud API SecretId
- `TENCENT_API_SECRET_KEY` - Tencent Cloud API SecretKey
