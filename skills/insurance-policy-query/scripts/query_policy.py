#!/usr/bin/env python3
"""
保单查询脚本
通过调用HTTP接口查询保单信息
"""

import json
import sys
import urllib.request
import urllib.error
import urllib.parse

BASE_URL = "http://localhost:8080/api/insurance-policies"

def query_by_policy_id(policy_id):
    """根据保单号查询"""
    url = f"{BASE_URL}/{policy_id}"
    return make_request(url)

def query_by_policyholder_name(name):
    """根据投保人姓名查询"""
    encoded_name = urllib.parse.quote(name)
    url = f"{BASE_URL}?policyholderName={encoded_name}"
    return make_request(url)

def query_by_user_id(user_id):
    """根据用户ID查询"""
    url = f"{BASE_URL}?userId={user_id}"
    return make_request(url)

def make_request(url):
    """发送HTTP请求"""
    try:
        req = urllib.request.Request(url)
        req.add_header('Content-Type', 'application/json')

        with urllib.request.urlopen(req, timeout=10) as response:
            data = json.loads(response.read().decode('utf-8'))
            return data
    except urllib.error.HTTPError as e:
        return {"error": f"HTTP Error: {e.code}"}
    except urllib.error.URLError as e:
        return {"error": f"URL Error: {e.reason}"}
    except Exception as e:
        return {"error": str(e)}

def format_policy(policy):
    """格式化单个保单信息"""
    return f"""保单号：{policy.get('policyId', 'N/A')}
投保人：{policy.get('policyholderName', 'N/A')}
被保险人：{policy.get('insuredName', 'N/A')}
险种：{policy.get('insuranceType', 'N/A')}
保费：{policy.get('premium', 0):.2f}元
保额：{policy.get('sumInsured', 0):.2f}元
保险期间：{policy.get('startDate', 'N/A')} 至 {policy.get('endDate', 'N/A')}
状态：{policy.get('status', 'N/A')}"""

def format_policies(policies):
    """格式化多个保单信息"""
    if not policies:
        return "未找到保单信息"

    if isinstance(policies, dict) and 'error' in policies:
        return f"查询失败：{policies['error']}"

    if isinstance(policies, list):
        if len(policies) == 0:
            return "未找到保单信息"

        if len(policies) == 1:
            return f"找到 1 个保单：\n\n{format_policy(policies[0])}"

        sb = f"找到 {len(policies)} 个保单：\n\n"
        for i, policy in enumerate(policies, 1):
            sb += f"{i}. {policy.get('policyId', 'N/A')} - {policy.get('insuranceType', 'N/A')} - {policy.get('status', 'N/A')}\n"
        return sb

    # 单个保单对象
    return f"找到 1 个保单：\n\n{format_policy(policies)}"

def main():
    if len(sys.argv) < 3:
        print("用法: python query_policy.py <查询类型> <查询值>")
        print("查询类型:")
        print("  policyId - 按保单号查询")
        print("  policyholderName - 按投保人姓名查询")
        print("  userId - 按用户ID查询")
        print("\n示例:")
        print("  python query_policy.py policyId P000001")
        print("  python query_policy.py policyholderName 陶国军")
        print("  python query_policy.py userId 1")
        sys.exit(1)

    query_type = sys.argv[1]
    query_value = sys.argv[2]

    if query_type == "policyId":
        result = query_by_policy_id(query_value)
    elif query_type == "policyholderName":
        result = query_by_policyholder_name(query_value)
    elif query_type == "userId":
        result = query_by_user_id(query_value)
    else:
        print(f"不支持的查询类型: {query_type}")
        sys.exit(1)

    print(format_policies(result))

if __name__ == "__main__":
    main()
