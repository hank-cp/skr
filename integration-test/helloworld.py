# https://github.com/h2non/jsonpath-ng
# https://docs.python.org/3/library/unittest.html

import http.client, urllib.parse
import json
from jsonpath_ng import jsonpath, parse

params = urllib.parse.urlencode({
    'username': 'test',
    'password': 'test',
})
headers = {
    "Content-type": "application/x-www-form-urlencoded"
}

conn = http.client.HTTPConnection("127.0.0.1", 8002)
conn.request("POST", "/auth/sign-in", params, headers)
response = conn.getresponse()
print(response.status, response.reason)
data = response.read()
jsonData = json.loads(data)
print(jsonData['access-token'])

jsonpath_expression = parse('access-token')
for match in jsonpath_expression.find(jsonData):
    print(f'token: {match.value}')

conn.close()