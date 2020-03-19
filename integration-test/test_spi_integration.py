# coding=UTF-8

import json
import unittest

import requests
from jsonpath import jsonpath


class TaskControllerTest(unittest.TestCase):

    def test_spi_integration(self):
        headers = {'Content-type': 'application/x-www-form-urlencoded'}
        payload = {'username': 'dev', 'password': 'dev', 'tenentCode': 'org'}
        response = requests.post('http://127.0.0.1:8002/auth/sign-in', headers=headers, data=payload)
        self.assertTrue(response.status_code == requests.codes.ok)
        content_json_data = json.loads(response.content)
        # 数据不存在时，会返回False，确保数据存在，不能等于False
        access_token = jsonpath(content_json_data, 'access-token')[0]
        request_headers = {'Content-type': 'application/json', 'access-token': access_token}
        extension_response = requests.get('http://127.0.0.1:8101/task-ext/extensions', headers=request_headers)
        self.assertTrue(extension_response.status_code == requests.codes.ok)
        content_json_data = json.loads(response.content)
        self.assertTrue(jsonpath(content_json_data, '$[*]'))

        # test FeignClientBuilder
        extension_response = requests.get('http://127.0.0.1:8102/delegate-extensions', headers=request_headers)
        self.assertTrue(extension_response.status_code == requests.codes.ok)
        content_json_data = json.loads(response.content)
        self.assertTrue(jsonpath(content_json_data, '$[*]'))

if __name__ == '__main__':
    unittest.main()
