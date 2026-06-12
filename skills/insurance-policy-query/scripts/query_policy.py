#!/usr/bin/env python3
"""
保单查询脚本
先通过用户查询技能获取用户ID，再查询保单
"""

import json
import sys
import urllib.request
import urllib.error
import urllib.parse

USER_API_URL = "http://localhost:8080/api/users"
INSURANCE_API_URL = "http://localhost:8080/api/insurance-policies"

def get_user_by_name(name):
    """根据用户姓名查询用户信息"""
    encoded_name = urllib.parse.quote(name)
    url = f"{USER_API_URL}/search?name={encoded_name}"
    return make_request(url)

def get_user_by_id(user_id):
    """根据用户ID查询用户信息"""
    url = f"{USER_API_URL}/{user_id}"
    return make_request(url)

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

def query_by_user_name(name):
    """根据用户姓名查询保单"""
    # 第一步：获取用户信息
    user_result = get_user_by_name(name)

    if isinstance(user_result, dict) and 'error' in user_result:
        return f"查询用户失败：{user_result['error']}"

    if not user_result:
        return f"未找到用户：{name}"

    # 获取用户ID
    if isinstance(user_result, list):
        if len(user_result) == 0:
            return f"未找到用户：{name}"
        user = user_result[0]
    else:
        user = user_result

    user_id = user.get('id')
    if not user_id:
        return f"无法获取用户ID"

    # 第二步：查询保单
    policies_result = get_policies_by_policyholder_id(user_id)

    if isinstance(policies_result, dict) and 'error' in policies_result:
        return f"查询保单失败：{policies_result['error']}"

    return format_policies(policies_result)

def query_by_user_id(user_id):
    """根据用户ID查询保单"""
    try:
        user_id_int = int(user_id)
    except ValueError:
        return f"无效的用户ID：{user_id}"

    policies_result = get_policies_by_policyholder_id(user_id_int)

    if isinstance(policies_result, dict) and 'error' in policies_result:
        return f"查询保单失败：{policies_result['error']}"

    return format_policies(policies_result)

def main():
    if len(sys.argv) < 3:
        print("用法: python query_policy.py <查询类型> <查询值>")
        print("查询类型:")
        print("  userName - 按用户姓名查询（先获取用户ID，再查询保单）")
        print("  userId - 按用户ID查询")
        print("\n示例:")
        print("  python query_policy.py userName 陶国军")
        print("  python query_policy.py userId 1")
        sys.exit(1)

    query_type = sys.argv[1]
    query_value = sys.argv[2]

    if query_type == "userName":
        result = query_by_user_name(query_value)
    elif query_type == "userId":
        result = query_by_user_id(query_value)
    else:
        print(f"不支持的查询类型: {query_type}")
        sys.exit(1)

    print(result)

if __name__ == "__main__":
    main()
