---
name: branch-icebreaker
description: Use this skill when the user wants to prepare icebreaker topics and scripts for visiting a bank branch. This includes generating conversation topics, talking points, and structured scripts for customer managers or insurance agents to break the ice with branch staff. If the user mentions icebreaker, conversation topics, talking points, branch visit preparation, or wants to know what to talk about when visiting a branch, use this skill.
---

# Branch Icebreaker Script Guide

## Overview

This skill generates icebreaker topics and conversation scripts for bank branch visits, integrating:
1. **Financial News** - Fund, wealth management, insurance, stock, and real estate market dynamics
2. **Banking Industry Updates** - Policy changes, interest rates, new products
3. **Local News** - Community, commercial district, and transportation updates
4. **Topic Extraction** - Identify 3-5 conversation-worthy topics from search results
5. **Script Generation** - Create three-part scripts: opening line, deep dive, response guide

Supported banks: 浦发银行, 工商银行, 建设银行, 中国银行, 农业银行, 招商银行, etc.

## IMPORTANT: Use Tencent Web Search Tool

**You MUST use the `tencent_web_search` tool to gather information. Do NOT create separate tools or scripts.**

The skill works by making multiple targeted web searches, extracting hot topics, and generating conversation scripts.

## Step-by-Step Process

### Step 1: Extract Branch Information

Parse the user's input to extract:
- **Bank name** (银行名称): e.g., 浦发银行, 工商银行
- **City** (城市): e.g., 上海, 北京
- **Branch name** (网点名称): e.g., 制造局路支行, 朝阳支行

```
User: 帮我准备浦发银行上海制造局路支行的破冰话术
Extract: bank="浦发银行", city="上海", branch="制造局路支行"

User: 我要去拜访北京工商银行朝阳支行，准备点聊天话题
Extract: bank="工商银行", city="北京", branch="朝阳支行"
```

### Step 2: Gather Data via Web Search

Make **5 parallel searches** using `tencent_web_search`:

#### Search 1: Banking Industry Updates
```
tencent_web_search(query="{city} {bank} 银行 政策 利率 新产品 业务动态")
```
Example: `tencent_web_search(query="上海 浦发银行 银行 政策 利率 新产品 业务动态")`

#### Search 2: Local News
```
tencent_web_search(query="{city} 社区 商圈 交通 新闻 周边")
```
Example: `tencent_web_search(query="上海 社区 商圈 交通 新闻 周边")`

#### Search 3: Fund & Wealth Management
```
tencent_web_search(query="{city} 基金 理财 市场 动态 收益")
```
Example: `tencent_web_search(query="上海 基金 理财 市场 动态 收益")`

#### Search 4: Insurance Industry
```
tencent_web_search(query="{city} 保险 行业 资讯 产品 理赔")
```
Example: `tencent_web_search(query="上海 保险 行业 资讯 产品 理赔")`

#### Search 5: Stock & Real Estate
```
tencent_web_search(query="{city} 股票 房产 市场 热点 投资")
```
Example: `tencent_web_search(query="上海 股票 房产 市场 热点 投资")`

### Step 3: Extract Hot Topics

Analyze the search results and identify **3-5 conversation-worthy topics**:

1. **Scan for high-frequency keywords** - Words that appear across multiple search results
2. **Identify trending events** - Recent news or policy changes
3. **Look for local relevance** - Topics specific to the city or branch location
4. **Consider practical value** - Topics that can lead to meaningful conversations

For each topic, create:
- **Topic name** (话题名称): 10 characters or less
- **Topic description** (话题简介): 50 characters or less

### Step 4: Generate Conversation Scripts

For each topic, create a **three-part script**:

#### Opening Line (开场白)
- 1-2 sentences
- Naturally introduce the topic
- Show you're informed and professional

#### Deep Dive (深入探讨)
- 2-3 sentences
- Demonstrate expertise
- Share relevant insights or data points
- Spark the other person's interest

#### Response Guide (回应引导)
- 1-2 sentences
- Ask an open-ended question
- Encourage the other person to share their views

**Length Control**: Each part should NOT exceed 100 characters.

### Step 5: Generate the Report

Compile everything into a structured Markdown report:

```markdown
# 破冰话术：{bank}{branch}

## 📍 网点信息
- **银行**: {bank}
- **网点**: {branch}
- **城市**: {city}

## 💡 话题方向
1. **{Topic 1 Name}** - {Topic 1 Description}
2. **{Topic 2 Name}** - {Topic 2 Description}
3. **{Topic 3 Name}** - {Topic 3 Description}

## 🗣️ 话术脚本

### 话题 1：{Topic 1 Name}

**开场白**
> {Opening line for Topic 1}

**深入探讨**
> {Deep dive for Topic 1}

**回应引导**
> {Response guide for Topic 1}

### 话题 2：{Topic 2 Name}

**开场白**
> {Opening line for Topic 2}

**深入探讨**
> {Deep dive for Topic 2}

**回应引导**
> {Response guide for Topic 2}

### 话题 3：{Topic 3 Name}

**开场白**
> {Opening line for Topic 3}

**深入探讨**
> {Deep dive for Topic 3}

**回应引导**
> {Response guide for Topic 3}

## 📝 使用建议
1. **自然引入**：从开场白开始，自然地引入话题
2. **展示专业**：在深入探讨环节展示您的专业度
3. **引导互动**：用回应引导鼓励对方参与讨论
4. **灵活调整**：根据对方反应灵活调整话术
5. **真诚交流**：保持真诚的态度，不要过于机械

---
*数据来源：实时网络搜索，仅供参考*
*生成时间：{current date}*
```

## Usage Examples

### Full Icebreaker Script

```
User: 帮我准备浦发银行上海制造局路支行的破冰话术
Action 1: tencent_web_search(query="上海 浦发银行 银行 政策 利率 新产品 业务动态")
Action 2: tencent_web_search(query="上海 社区 商圈 交通 新闻 周边")
Action 3: tencent_web_search(query="上海 基金 理财 市场 动态 收益")
Action 4: tencent_web_search(query="上海 保险 行业 资讯 产品 理赔")
Action 5: tencent_web_search(query="上海 股票 房产 市场 热点 投资")
→ Extract topics → Generate scripts → Compile report
```

### Topic Ideas Only

```
User: 最近有什么可以聊的金融话题？
Action: tencent_web_search(query="上海 金融 市场 热点 话题")
→ Extract and list 3-5 conversation topics
```

### Specific Topic Script

```
User: 帮我准备关于基金定投的话术
Action: tencent_web_search(query="基金定投 收益 策略 市场 动态")
→ Generate detailed script for fund investment topic
```

## Query Flow

```
用户输入: "帮我准备浦发银行上海制造局路支行的破冰话术"
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
│ Step 2: 并行执行5个搜索                 │
│ 1. 银行业务动态                         │
│ 2. 地区周边新闻                         │
│ 3. 基金理财市场                         │
│ 4. 保险行业资讯                         │
│ 5. 股票房产市场                         │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 3: 提炼3-5个话题方向               │
│ - 高频关键词提取                        │
│ - 热点事件识别                          │
│ - 本地相关性筛选                        │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 4: 为每个话题生成三段式话术        │
│ - 开场白（1-2句）                       │
│ - 深入探讨（2-3句）                     │
│ - 回应引导（1-2句）                     │
└─────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────┐
│ Step 5: 生成Markdown格式报告            │
│ - 网点信息                              │
│ - 话题方向列表                          │
│ - 话术脚本详情                          │
│ - 使用建议                              │
└─────────────────────────────────────────┘
    │
    ▼
返回Markdown格式的破冰话术报告
```

## Topic Categories

When extracting topics, consider these categories:

### 1. 银行动态 (Banking Updates)
- Interest rate changes
- New product launches
- Policy adjustments
- Service innovations

### 2. 金融市场 (Financial Markets)
- Fund performance
- Wealth management trends
- Investment opportunities
- Market outlook

### 3. 保险资讯 (Insurance News)
- New insurance products
- Industry policy changes
- Claims trends
- Customer needs

### 4. 房产市场 (Real Estate Market)
- Price trends
- Policy impacts
- New developments
- Mortgage rates

### 5. 经济形势 (Economic Situation)
- GDP growth
- Industry trends
- Employment data
- Consumer confidence

## Script Templates

### Opening Line Templates
- "最近{topic}挺火的，您这边有什么看法？"
- "听说{topic}有新变化，您了解吗？"
- "我看新闻说{topic}，想跟您交流一下。"

### Deep Dive Templates
- "我了解到{insight}，这对咱们业务影响挺大的。"
- "从数据来看{data}，您觉得客户会怎么反应？"
- "最近{event}，我觉得这可能是个机会。"

### Response Guide Templates
- "您觉得这个趋势会持续吗？"
- "对于这种情况，您有什么建议？"
- "您这边客户有什么反馈吗？"

## Tips for Quality Scripts

1. **Be specific**: Use actual data from search results, not generic statements
2. **Be relevant**: Focus on topics that matter to branch staff
3. **Be natural**: Scripts should sound conversational, not scripted
4. **Be concise**: Each part should be brief and to the point
5. **Be actionable**: Include questions that encourage dialogue
6. **No intermediate output**: Do NOT show raw search results to the user. Only present the final integrated report.

## Data Limitations

- Search results are real-time and may vary
- Some topics may not be relevant to all branches
- Market data may not be the most recent
- Always note that data is for reference only
