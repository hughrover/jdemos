#!/usr/bin/env python3
"""
用户信息查询脚本
用于在技能中查询用户信息
"""

import json
import sys
import os

def load_customers(file_path):
    """加载客户数据"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    except Exception as e:
        print(f"加载数据失败: {e}")
        return []

def format_user(user):
    """格式化用户信息"""
    return f"""姓名：{user.get('name', 'N/A')}
年龄：{user.get('age', 'N/A')}
性别：{user.get('gender', 'N/A')}
电话：{user.get('phone', 'N/A')}
邮箱：{user.get('email', 'N/A')}
地址：{user.get('address', 'N/A')}
公司：{user.get('company', 'N/A')}
职位：{user.get('position', 'N/A')}"""

def search_users(customers, query):
    """搜索用户"""
    if not query:
        return customers[:20]  # 返回前20个

    results = []

    # 精确匹配
    for user in customers:
        if user.get('name') == query:
            results.append(user)

    # 拼音匹配
    if not results:
        for user in customers:
            if user.get('namePinyin', '').lower() == query.lower():
                results.append(user)

    # 首字母匹配
    if not results:
        for user in customers:
            if user.get('namePinyinInitial', '').lower() == query.lower():
                results.append(user)

    # 模糊匹配
    if not results:
        for user in customers:
            if query in user.get('name', '') or \
               query.lower() in user.get('namePinyin', '').lower() or \
               query.lower() in user.get('namePinyinInitial', '').lower():
                results.append(user)

    return results[:20]  # 限制返回数量

def main():
    if len(sys.argv) < 2:
        print("用法: python query_user.py <查询关键词>")
        print("示例: python query_user.py 张三")
        print("      python query_user.py zhangsan")
        print("      python query_user.py zs")
        sys.exit(1)

    query = sys.argv[1]

    # 查找数据文件
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.join(script_dir, '..', '..', '..')
    data_file = os.path.join(project_root, 'data', 'customers.json')

    # 尝试多个可能的路径
    possible_paths = [
        data_file,
        os.path.join(os.getcwd(), 'data', 'customers.json'),
        os.path.join(script_dir, 'data', 'customers.json'),
        'data/customers.json'
    ]

    customers = None
    for path in possible_paths:
        if os.path.exists(path):
            customers = load_customers(path)
            break

    if customers is None:
        print("错误：找不到客户数据文件")
        sys.exit(1)

    # 搜索用户
    results = search_users(customers, query)

    if not results:
        print(f"未找到匹配的用户：{query}")
    elif len(results) == 1:
        print("找到 1 个匹配的用户：\n")
        print(format_user(results[0]))
    else:
        print(f"找到 {len(results)} 个匹配的用户：\n")
        for i, user in enumerate(results, 1):
            print(f"{i}. {user.get('name', 'N/A')} - {user.get('company', 'N/A')} - {user.get('position', 'N/A')}")

if __name__ == "__main__":
    main()
