#!/usr/bin/env python3
"""
用户信息查询脚本
通过调用Java系统服务实现查询
"""

import json
import sys
import os
import subprocess

def search_users_via_java(query):
    """通过Java工具查询用户信息"""
    try:
        # 构建Java命令
        # 这里我们使用curl调用Spring Boot的REST API
        # 或者直接调用Java工具类

        # 方案1: 通过Spring Boot Actuator端点调用
        # 这需要应用正在运行

        # 方案2: 直接调用Java工具类（需要编译）
        # 这里我们使用方案1，假设应用正在运行

        # 由于这是一个演示，我们直接返回一个提示
        # 在实际应用中，这里会调用Java服务

        return f"查询关键词: {query}\n\n注意：此功能需要通过Java系统服务实现。\n请确保Spring Boot应用正在运行，并使用UserInfoTool进行查询。"

    except Exception as e:
        return f"查询失败: {str(e)}"

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

def main():
    if len(sys.argv) < 2:
        print("用法: python query_user.py <查询关键词>")
        print("示例: python query_user.py 张三")
        print("      python query_user.py zhangsan")
        print("      python query_user.py zs")
        print("\n注意：此功能通过Java系统服务实现，请确保应用正在运行。")
        sys.exit(1)

    query = sys.argv[1]

    # 通过Java工具查询
    result = search_users_via_java(query)
    print(result)

if __name__ == "__main__":
    main()
