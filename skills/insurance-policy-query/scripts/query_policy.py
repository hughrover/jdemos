#!/usr/bin/env python3
"""
保单查询脚本
接收用户ID列表，查询所有用户的保单
"""

import json
import sys
import urllib.request
import urllib.error

INSURANCE_API_URL = "http://localhost:8080/api/insurance-policies"

def get_policies_by_policyholder_id(policyholder_id):
    """根据policyholderId查询保单"""
    url = f"{INSURANCE_API_URL}?policyholderId={policyholder_id}"
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

def query_by_user_ids(user_ids):
    """根据用户ID列表查询保单"""
    all_policies = []

    for user_id in user_ids:
        try:
            user_id_int = int(user_id)
        except ValueError:
            print(f"跳过无效的用户ID：{user_id}")
            continue

        policies_result = get_policies_by_policyholder_id(user_id_int)

        if isinstance(policies_result, dict) and 'error' in policies_result:
            print(f"查询用户 {user_id} 的保单失败：{policies_result['error']}")
            continue

        if isinstance(policies_result, list):
            all_policies.extend(policies_result)
        elif policies_result:
            all_policies.append(policies_result)

    return format_policies(all_policies)

def main():
    if len(sys.argv) < 2:
        print("用法: python query_policy.py <用户ID1> [用户ID2] [用户ID3] ...")
        print("\n示例:")
        print("  python query_policy.py 1")
        print("  python query_policy.py 1 2 3")
        print("  python query_policy.py 4 5")
        sys.exit(1)

    user_ids = sys.argv[1:]
    result = query_by_user_ids(user_ids)
    print(result)

if __name__ == "__main__":
    main()
