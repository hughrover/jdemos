---
name: regional-market-briefing
description: Use this skill when the user wants to get a regional market briefing for a bank branch. This includes analyzing the branch's surrounding market environment, economic data, financial news, banking competition, and market opportunities. If the user mentions regional market briefing, market analysis, branch market environment, or wants to understand the market around a bank branch, use this skill.
---

# Regional Market Briefing Guide

## Overview

This skill generates a comprehensive regional market briefing for a bank branch, integrating:
1. **Branch Information** - Bank name, branch name, city, district
2. **Regional Economic Data** - GDP, population, industry structure
3. **Financial News** - Recent financial policies and market dynamics
4. **Banking Competition** - Peer bank activities and market landscape
5. **Market Opportunities** - Actionable insights and recommendations

Supported banks: 浦发银行, 工商银行, 建设银行, 中国银行, 农业银行, 招商银行, etc.

## IMPORTANT: Use Tencent Web Search Tool

**You MUST use the `tencent_web_search` tool to gather information. Do NOT create separate tools or scripts.**

The skill works by making multiple targeted web searches and synthesizing the results into a structured briefing.

## Step-by-Step Process

### Step 1: Extract Branch Information

Parse the user's input to extract:
- **Bank name** (银行名称): e.g., 浦发银行, 工商银行
- **City** (城市): e.g., 上海, 北京
- **Branch name** (网点名称): e.g., 制造局路支行, 朝阳支行
- **District** (区域): e.g., 黄浦区, 朝阳区 (if mentioned)

```
User: 浦发银行上海制造局路支行的区域市场简报
Extract: bank="浦发银行", city="上海", branch="制造局路支行"

User: 中国工商银行北京市朝阳区支行的市场环境分析
Extract: bank="中国工商银行", city="北京", district="朝阳区", branch="朝阳支行"
```

### Step 2: Gather Data via Web Search

Make **3 parallel searches** using `tencent_web_search`:

#### Search 1: Regional Economic Data
```
tencent_web_search(query="{city} GDP 经济数据 人口 产业结构 宏观经济")
```
Example: `tencent_web_search(query="上海 GDP 经济数据 人口 产业结构 宏观经济")`

#### Search 2: Financial News
```
tencent_web_search(query="{city} 财经资讯 金融政策 市场动态")
```
Example: `tencent_web_search(query="上海 财经资讯 金融政策 市场动态")`

#### Search 3: Banking Competition
```
tencent_web_search(query="{city} 银行业动态 同业竞争 网点变化 {bank}")
```
Example: `tencent_web_search(query="上海 银行业动态 同业竞争 网点变化 浦发银行")`

### Step 3: Synthesize the Briefing

Analyze the search results and generate a structured Markdown briefing with the following sections:

```markdown
# 区域市场简报：{bank}{branch}

## 📍 网点基础信息
- **银行**: {bank}
- **网点**: {branch}
- **城市**: {city}
- **区域**: {district or "详见搜索结果"}

## 📊 区域经济概况
{Summarize GDP, population, industry structure from Search 1}

## 📰 区域财经资讯
{List 3-5 key financial news items from Search 2}
1. {News item 1}
2. {News item 2}
3. {News item 3}

## 🏦 银行业竞争态势
{Summarize banking competition landscape from Search 3}

## 💡 市场机会与建议
{Based on all data, provide 3-5 actionable recommendations}
1. {Recommendation 1}
2. {Recommendation 2}
3. {Recommendation 3}

---
*数据来源：公开网络搜索，仅供参考*
*生成时间：{current date}*
```

## Usage Examples

### Full Market Briefing

```
User: 浦发银行上海制造局路支行的区域市场简报
Action 1: tencent_web_search(query="上海 GDP 经济数据 人口 产业结构 宏观经济")
Action 2: tencent_web_search(query="上海 财经资讯 金融政策 市场动态")
Action 3: tencent_web_search(query="上海 银行业动态 同业竞争 网点变化 浦发银行")
→ Synthesize results into briefing
```

### Economic Analysis Only

```
User: 北京的经济数据怎么样？
Action: tencent_web_search(query="北京 GDP 经济数据 人口 产业结构 宏观经济")
→ Summarize economic data
```

### Banking Competition Analysis

```
User: 深圳银行业的竞争态势如何？
Action: tencent_web_search(query="深圳 银行业动态 同业竞争 网点变化")
→ Analyze banking competition
```

## Query Flow

```
用户输入: "浦发银行上海制造局路支行的区域市场简报"
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 1: 提取网点信息                    │
│ bank="浦发银行", city="上海",           │
│ branch="制造局路支行"                   │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 2: 并行执行3个搜索                 │
│ 1. 上海 GDP 经济数据 人口 产业结构      │
│ 2. 上海 财经资讯 金融政策 市场动态      │
│ 3. 上海 银行业动态 同业竞争 浦发银行    │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 3: 整合分析，生成简报              │
│ - 网点基础信息                          │
│ - 区域经济概况                          │
│ - 区域财经资讯                          │
│ - 银行业竞争态势                        │
│ - 市场机会与建议                        │
└─────────────────────────────────────────┘
    │
    ▼
返回Markdown格式的区域市场简报
```

## Response Format

The briefing should be formatted as clean Markdown with:
- Clear section headers (##)
- Bullet points for lists
- Numbered items for recommendations
- Data source attribution
- Generation timestamp

## Tips for Quality Briefings

1. **Be specific**: Use actual data from search results, not generic statements
2. **Be actionable**: Recommendations should be concrete and implementable
3. **Be balanced**: Present both opportunities and risks
4. **Be concise**: Focus on the most relevant information
5. **Attribute sources**: Mention where key data points came from
6. **No intermediate output**: Do NOT show raw search results to the user. Only present the final integrated briefing.

## Data Limitations

- Search results are real-time and may vary
- Some regions may have less available data
- Economic data may not be the most recent
- Always note that data is for reference only
