import requests
import unittest
import json
from jsonpath import jsonpath
import random


class AuthSignUpTest(unittest.TestCase):

    def post_sign_up(self, payload):
        headers = {'Content-type': 'application/x-www-form-urlencoded'}
        response = requests.post('http://127.0.0.1:8002/auth/sign-up', headers=headers, data=payload)
        self.assertTrue(response.status_code == requests.codes.ok)
        content_json_data = json.loads(response.content)
        self.assertTrue(jsonpath(content_json_data, 'access-token'))
        self.assertIsNotNone(jsonpath(content_json_data, 'access-token')[0])
        self.assertTrue(jsonpath(content_json_data, 'refresh-token'))
        self.assertIsNotNone(jsonpath(content_json_data, 'refresh-token')[0])
        self.assertTrue(jsonpath(content_json_data, 'login-token'))
        self.assertIsNotNone(jsonpath(content_json_data, 'login-token')[0])
        self.assertTrue(jsonpath(content_json_data, 'principal'))
        self.assertIsNotNone(jsonpath(content_json_data, 'principal')[0])
        self.assertFalse(jsonpath(content_json_data, 'principal.tenentCode'))

    def test_sign_up_without_tenant(self):
        # 生成随机账号
        username = random.sample('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789', 5)
        payload_test = {'username': username, 'password': 'asdf'}
        self.post_sign_up(payload_test)

        headers = {'Content-type': 'application/x-www-form-urlencoded'}
        payload = {'username': username, 'password': 'qwer'}
        response = requests.post('http://127.0.0.1:8002/auth/sign-up', headers=headers, data=payload)
        self.assertTrue(response.status_code == requests.codes.unauthorized)
        self.assertEqual(1111, jsonpath(json.loads(response.content), 'ec')[0])

if __name__ == '__main__':
    unittest.main()
