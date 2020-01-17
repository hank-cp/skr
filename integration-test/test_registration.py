# coding=UTF-8

import json
import unittest

import requests
from jsonpath import jsonpath


class TaskControllerTest(unittest.TestCase):

    def test_registration(self):
        headers = {'Content-type': 'application/x-www-form-urlencoded'}
        payload = {'username': 'dev', 'password': 'dev', 'tenentCode': 'org'}
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
        task_list_headers = {'Content-type': 'application/json', 'access-token': access_token}
        task_list_response = requests.get('http://127.0.0.1:8101/task/extensions', headers=task_list_headers)
        self.assertTrue(task_list_response.status_code == requests.codes.ok)


if __name__ == '__main__':
    unittest.main()
