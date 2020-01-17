# coding=UTF-8

import json
import unittest

import requests
from jsonpath import jsonpath


class PermissionCheckTest(unittest.TestCase):

    def permission_sign_in(self, payload):
        headers = {'Content-type': 'application/x-www-form-urlencoded'}
        response = requests.post('http://127.0.0.1:8002/auth/sign-in', headers=headers, data=payload)
        self.assertTrue(response.status_code == requests.codes.ok)
        content_json_data = json.loads(response.content)
        # 数据不存在时，会返回False，确保数据存在，不能等于False
        self.assertTrue(jsonpath(content_json_data, 'access-token'))
        access_token = jsonpath(content_json_data, 'access-token')[0]
        # token不能为空
        self.assertIsNotNone(access_token)
        # token不能为空字符串
        self.assertIsNot(access_token, "")
        return access_token

    def permission_task_list(self, access_token, status_code):
        task_list_headers = {'Content-type': 'application/json', 'access-token': access_token}
        task_list_response = requests.get('http://127.0.0.1:8101/task/list', headers=task_list_headers)
        self.assertTrue(task_list_response.status_code == status_code)

    def permission_task_record_list(self, access_token, status_code):
        task_list_headers = {'Content-type': 'application/json', 'access-token': access_token}
        task_list_response = requests.get('http://127.0.0.1:8102/task_record/list', headers=task_list_headers)
        self.assertTrue(task_list_response.status_code == status_code)

    def test_permission_granted_demo_a(self):
        payload = {'username': 'dev', 'password': 'dev', 'tenentCode': 'org'}
        access_token = self.permission_sign_in(payload)
        self.permission_task_list(access_token, requests.codes.ok)

    def test_permission_denied_demo_a(self):
        payload = {'username': 'test', 'password': 'test', 'tenentCode': 'org'}
        access_token = self.permission_sign_in(payload)
        self.permission_task_list(access_token, requests.codes.forbidden)

    def test_permission_granted_demo_b(self):
        payload = {'username': 'dev', 'password': 'dev', 'tenentCode': 'org'}
        access_token = self.permission_sign_in(payload)
        self.permission_task_record_list(access_token, requests.codes.ok)

    def test_permission_denied_demo_b(self):
        payload = {'username': 'test', 'password': 'test', 'tenentCode': 'org'}
        access_token = self.permission_sign_in(payload)
        self.permission_task_record_list(access_token, requests.codes.forbidden)

if __name__ == '__main__':
    unittest.main()
