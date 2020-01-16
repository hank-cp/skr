import requests
import unittest
import json
from jsonpath import jsonpath


class AuthSignInTest(unittest.TestCase):

    def post_sign_in(self, payload):
        headers = {'Content-type': 'application/x-www-form-urlencoded'}
        response = requests.post('http://127.0.0.1:8002/auth/sign-in', headers=headers, data=payload)
        self.assertTrue(response.status_code == requests.codes.ok)
        content_json_data = json.loads(response.content)
        # 数据不存在时，会返回False，确保数据存在，不能等于False
        self.assertTrue(jsonpath(content_json_data, 'access-token'))
        self.assertIsNotNone(jsonpath(content_json_data, 'access-token')[0])
        self.assertTrue(jsonpath(content_json_data, 'refresh-token'))
        self.assertIsNotNone(jsonpath(content_json_data, 'refresh-token')[0])
        self.assertTrue(jsonpath(content_json_data, 'login-token'))
        self.assertIsNotNone(jsonpath(content_json_data, 'login-token')[0])
        self.assertTrue(jsonpath(content_json_data, 'principal'))
        self.assertIsNotNone(jsonpath(content_json_data, 'principal')[0])
        self.assertFalse(jsonpath(content_json_data, 'principal.tenentCode'))

    def test_sign_in_without_tenant(self):
        payload_test = {'username': 'test', 'password': 'test'}
        self.post_sign_in(payload_test)

        payload_guest = {'username': 'guest', 'password': 'guest'}
        self.post_sign_in(payload_guest)


if __name__ == '__main__':
    unittest.main()
