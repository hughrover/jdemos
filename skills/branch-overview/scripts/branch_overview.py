#!/usr/bin/env python3
"""
网点概览脚本
查询银行网点的业绩、活动、客群特征和周边时事
"""

import json
import sys
import argparse
import urllib.request
import urllib.error
import hashlib
import hmac
import os
import time
from datetime import datetime, timezone

# 腾讯搜索API配置
TENCENT_SEARCH_API_URL = "https://wsa.tencentcloudapi.com"
TENCENT_API_SECRET_ID = os.environ.get("TENCENT_API_SECRET_ID", "")
TENCENT_API_SECRET_KEY = os.environ.get("TENCENT_API_SECRET_KEY", "")

# 模拟数据存储
MOCK_PERFORMANCE_DATA = {
    "浦发银行_上海_制造局路支行": {"policyCount": 156, "premiumAmount": 2850.0, "period": "2024年6月"},
    "工商银行_北京_朝阳支行": {"policyCount": 203, "premiumAmount": 3420.0, "period": "2024年6月"},
    "建设银行_深圳_福田支行": {"policyCount": 178, "premiumAmount": 2980.0, "period": "2024年6月"},
    "中国银行_广州_天河支行": {"policyCount": 165, "premiumAmount": 2750.0, "period": "2024年6月"},
    "农业银行_成都_武侯支行": {"policyCount": 142, "premiumAmount": 2350.0, "period": "2024年6月"},
    "招商银行_杭州_西湖支行": {"policyCount": 189, "premiumAmount": 3150.0, "period": "2024年6月"},
}

MOCK_ACTIVITY_DATA = {
    "浦发银行_上海_制造局路支行": [
        {"name": "理财讲座", "date": "2024-06-05", "type": "讲座", "participants": 45},
        {"name": "客户答谢会", "date": "2024-06-12", "type": "答谢会", "participants": 120},
        {"name": "社区金融知识普及", "date": "2024-06-15", "type": "社区活动", "participants": 80},
        {"name": "新产品推介会", "date": "2024-06-18", "type": "推介会", "participants": 65},
        {"name": "VIP客户专享活动", "date": "2024-06-22", "type": "专享活动", "participants": 30},
        {"name": "信用卡推广活动", "date": "2024-06-25", "type": "推广活动", "participants": 150},
        {"name": "老年人防诈骗讲座", "date": "2024-06-28", "type": "讲座", "participants": 90},
        {"name": "企业客户座谈会", "date": "2024-06-30", "type": "座谈会", "participants": 25},
    ],
    "工商银行_北京_朝阳支行": [
        {"name": "理财规划讲座", "date": "2024-06-03", "type": "讲座", "participants": 55},
        {"name": "高端客户品酒会", "date": "2024-06-10", "type": "答谢会", "participants": 40},
        {"name": "社区义诊活动", "date": "2024-06-14", "type": "社区活动", "participants": 200},
        {"name": "手机银行推广", "date": "2024-06-17", "type": "推广活动", "participants": 180},
        {"name": "亲子财商教育", "date": "2024-06-21", "type": "讲座", "participants": 60},
        {"name": "企业年金宣讲会", "date": "2024-06-24", "type": "推介会", "participants": 35},
        {"name": "信用卡优惠活动", "date": "2024-06-27", "type": "推广活动", "participants": 250},
        {"name": "退休规划讲座", "date": "2024-06-30", "type": "讲座", "participants": 75},
        {"name": "小微企业融资对接会", "date": "2024-06-08", "type": "座谈会", "participants": 20},
        {"name": "金融知识竞赛", "date": "2024-06-16", "type": "社区活动", "participants": 120},
        {"name": "外汇投资讲座", "date": "2024-06-23", "type": "讲座", "participants": 45},
        {"name": "贵宾客户生日会", "date": "2024-06-29", "type": "答谢会", "participants": 30},
    ],
}

def sha256hex(s):
    """SHA256哈希并转为十六进制"""
    return hashlib.sha256(s.encode('utf-8')).hexdigest()

def hmac_sha256(key, msg):
    """HMAC-SHA256计算"""
    return hmac.new(key, msg.encode('utf-8'), hashlib.sha256).digest()

def get_authorization(action, payload, timestamp):
    """生成腾讯云API的Authorization头"""
    date = datetime.fromtimestamp(timestamp, timezone.utc).strftime('%Y-%m-%d')

    # 1. 拼接规范请求串
    http_request_method = "POST"
    canonical_uri = "/"
    canonical_querystring = ""
    canonical_headers = f"content-type:application/json\nhost:wsa.tencentcloudapi.com\n"
    signed_headers = "content-type;host"
    payload_hash = sha256hex(payload)
    canonical_request = f"{http_request_method}\n{canonical_uri}\n{canonical_querystring}\n{canonical_headers}\n{signed_headers}\n{payload_hash}"

    # 2. 拼接待签名字符串
    algorithm = "TC3-HMAC-SHA256"
    credential_scope = f"{date}/wsa/tc3_request"
    hashed_canonical_request = sha256hex(canonical_request)
    string_to_sign = f"{algorithm}\n{timestamp}\n{credential_scope}\n{hashed_canonical_request}"

    # 3. 计算签名
    secret_date = hmac_sha256(("TC3" + TENCENT_API_SECRET_KEY).encode('utf-8'), date)
    secret_service = hmac_sha256(secret_date, "wsa")
    secret_signing = hmac_sha256(secret_service, "tc3_request")
    signature = hmac.new(secret_signing, string_to_sign.encode('utf-8'), hashlib.sha256).hexdigest()

    # 4. 拼接Authorization
    authorization = f"{algorithm} Credential={TENCENT_API_SECRET_ID}/{credential_scope}, SignedHeaders={signed_headers}, Signature={signature}"
    return authorization

def call_tencent_search_api(query):
    """调用腾讯搜索API"""
    print(f"\n[DEBUG] ====== 开始调用腾讯搜索API ======")
    print(f"[DEBUG] 查询内容: {query}")
    print(f"[DEBUG] TENCENT_API_SECRET_ID: {'已设置' if TENCENT_API_SECRET_ID else '未设置'}")
    print(f"[DEBUG] TENCENT_API_SECRET_KEY: {'已设置' if TENCENT_API_SECRET_KEY else '未设置'}")

    if not TENCENT_API_SECRET_ID or not TENCENT_API_SECRET_KEY:
        error_msg = "错误：TENCENT_API_SECRET_ID 或 TENCENT_API_SECRET_KEY 环境变量未设置"
        print(f"[DEBUG] {error_msg}")
        return error_msg

    try:
        timestamp = int(time.time())
        payload = json.dumps({
            "Query": query
        })

        print(f"[DEBUG] 请求URL: {TENCENT_SEARCH_API_URL}")
        print(f"[DEBUG] 请求时间戳: {timestamp}")
        print(f"[DEBUG] 请求体: {payload}")

        authorization = get_authorization("SearchPro", payload, timestamp)
        print(f"[DEBUG] Authorization: {authorization[:50]}...")

        headers = {
            'Content-Type': 'application/json',
            'Host': 'wsa.tencentcloudapi.com',
            'X-TC-Action': 'SearchPro',
            'X-TC-Version': '2025-05-08',
            'X-TC-Timestamp': str(timestamp),
            'Authorization': authorization
        }

        print(f"[DEBUG] 请求头: {json.dumps(headers, indent=2, ensure_ascii=False)}")

        req = urllib.request.Request(TENCENT_SEARCH_API_URL, data=payload.encode('utf-8'), headers=headers)

        print(f"[DEBUG] 发送请求...")
        with urllib.request.urlopen(req, timeout=10) as response:
            response_body = response.read().decode('utf-8')
            print(f"[DEBUG] 响应状态码: {response.status}")
            print(f"[DEBUG] 响应内容: {response_body[:500]}...")

            data = json.loads(response_body)
            result = parse_search_response(data, query)
            print(f"[DEBUG] 解析结果: {result[:200]}...")
            print(f"[DEBUG] ====== API调用完成 ======\n")
            return result

    except urllib.error.HTTPError as e:
        error_body = e.read().decode('utf-8') if e.fp else ""
        error_msg = f"API错误：HTTP {e.code} - {error_body}"
        print(f"[DEBUG] {error_msg}")
        print(f"[DEBUG] ====== API调用失败 ======\n")
        return error_msg
    except urllib.error.URLError as e:
        error_msg = f"网络错误：{e.reason}"
        print(f"[DEBUG] {error_msg}")
        print(f"[DEBUG] ====== API调用失败 ======\n")
        return error_msg
    except Exception as e:
        error_msg = f"错误：{str(e)}"
        print(f"[DEBUG] {error_msg}")
        print(f"[DEBUG] ====== API调用失败 ======\n")
        return error_msg

def parse_search_response(data, query):
    """解析搜索响应"""
    response = data.get('Response', {})

    # 检查是否有错误
    error = response.get('Error')
    if error:
        error_code = error.get('Code', 'Unknown')
        error_message = error.get('Message', '未知错误')
        request_id = response.get('RequestId', '')
        return f"API错误：{error_code} - {error_message}（请求ID: {request_id}）"

    pages = response.get('Pages', [])
    request_id = response.get('RequestId', '')

    if not pages:
        return f"未找到与\"{query}\"相关的结果（请求ID: {request_id}）"

    # 提取前3条结果的摘要
    summaries = []
    for page_json in pages[:3]:
        try:
            page = json.loads(page_json)
            title = page.get('title', '')
            passage = page.get('passage', '')
            url = page.get('url', '')
            if title and passage:
                summaries.append(f"{title}: {passage[:100]}...")
        except:
            pass

    if summaries:
        return "；".join(summaries)
    else:
        return f"找到{len(pages)}条结果（请求ID: {request_id}）"

def generate_mock_performance(bank, city, branch):
    """生成模拟业绩数据"""
    key = f"{bank}_{city}_{branch}"
    if key in MOCK_PERFORMANCE_DATA:
        return MOCK_PERFORMANCE_DATA[key]

    # 使用名称的hashCode生成一致的随机数据
    hash_val = int(hashlib.md5(f"{bank}{branch}".encode()).hexdigest()[:8], 16)
    policy_count = 100 + (hash_val % 150)
    premium_amount = 1500.0 + (hash_val % 2000)

    return {"policyCount": policy_count, "premiumAmount": premium_amount, "period": "2024年6月"}

def generate_mock_activities(bank, city, branch):
    """生成模拟活动数据"""
    key = f"{bank}_{city}_{branch}"
    if key in MOCK_ACTIVITY_DATA:
        return MOCK_ACTIVITY_DATA[key]

    # 使用名称的hashCode生成一致的随机数据
    hash_val = int(hashlib.md5(f"{bank}{branch}".encode()).hexdigest()[:8], 16)
    activity_count = 5 + (hash_val % 8)

    activity_names = ["理财讲座", "客户答谢会", "社区金融知识普及", "新产品推介会", "VIP客户专享活动"]
    activity_types = ["讲座", "答谢会", "社区活动", "推介会", "专享活动"]

    activities = []
    for i in range(activity_count):
        name_idx = (hash_val + i) % len(activity_names)
        activities.append({
            "name": activity_names[name_idx],
            "date": f"2024-06-{1 + ((hash_val + i) % 28):02d}",
            "type": activity_types[name_idx],
            "participants": 20 + ((hash_val + i) % 100)
        })

    return activities

def search_web(query):
    """调用腾讯搜索API"""
    return call_tencent_search_api(query)

def get_performance(bank, city, branch):
    """获取网点业绩数据"""
    data = generate_mock_performance(bank, city, branch)
    return f"本地数据：出单件数 {data['policyCount']}件，保费总和 {data['premiumAmount']:.1f}万元"

def get_activities(bank, city, branch):
    """获取活动数据"""
    activities = generate_mock_activities(bank, city, branch)
    result = f"本月活动：{len(activities)}场\n活动明细：\n"
    for i, act in enumerate(activities, 1):
        result += f"{i}. {act['name']} - {act['date']} - {act['type']} - 参与人数：{act['participants']}人\n"
    return result

def get_peer_performance(bank, city, branch):
    """获取同业业绩对比（联网搜索）"""
    query = f"{bank} {city} {branch} 周边 银行 业绩"
    return search_web(query)

def get_customer_profile(bank, city, branch):
    """获取潜客画像（联网搜索）"""
    query = f"{city} {branch} 居民 理财需求 客户画像"
    return search_web(query)

def get_bank_dynamics(bank, city, branch):
    """获取银行动态（联网搜索）"""
    query = f"{city} 银行 网点 渠道 动态"
    return search_web(query)

def get_local_news(bank, city, branch):
    """获取周边新闻（联网搜索）"""
    query = f"{city} {branch} 新闻"
    return search_web(query)

def format_overview(bank, city, branch, sections=None):
    """格式化网点概览"""
    if sections is None:
        sections = ["all"]

    result = f"=== 网点概览：{bank}{branch} ===\n\n"

    if "all" in sections or "performance" in sections:
        result += "【网点业绩】\n"
        result += get_performance(bank, city, branch) + "\n"
        if "all" in sections:
            result += f"同业对比：{get_peer_performance(bank, city, branch)}\n"
        result += "\n"

    if "peer_performance" in sections:
        result += "【同业业绩对比】\n"
        result += get_peer_performance(bank, city, branch) + "\n\n"

    if "all" in sections or "activities" in sections:
        result += "【客经活动】\n"
        result += get_activities(bank, city, branch) + "\n"

    if "all" in sections or "customer_profile" in sections:
        result += "【客群特征】\n"
        result += f"潜客画像：{get_customer_profile(bank, city, branch)}\n\n"

    if "all" in sections or "bank_dynamics" in sections or "local_news" in sections:
        result += "【周边时事】\n"
        if "all" in sections or "bank_dynamics" in sections:
            result += f"银行动态：{get_bank_dynamics(bank, city, branch)}\n"
        if "all" in sections or "local_news" in sections:
            result += f"地区新闻：{get_local_news(bank, city, branch)}\n"

    return result

def main():
    parser = argparse.ArgumentParser(description='网点概览查询工具')
    parser.add_argument('--bank', required=True, help='银行名称')
    parser.add_argument('--city', required=True, help='城市名称')
    parser.add_argument('--branch', required=True, help='网点名称')
    parser.add_argument('--section', default='all', choices=[
        'all', 'performance', 'activities', 'peer_performance',
        'customer_profile', 'bank_dynamics', 'local_news'
    ], help='查询部分')

    args = parser.parse_args()

    sections = [args.section] if args.section != 'all' else ['all']
    result = format_overview(args.bank, args.city, args.branch, sections)
    print(result)

if __name__ == "__main__":
    main()
