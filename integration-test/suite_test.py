import unittest

from test_auth_sign_in import AuthSignInTest
from test_auth_sign_up import AuthSignUpTest
from test_permission_check import PermissionCheckTest
from test_task_controller import TaskControllerTest

if __name__ == '__main__':
    suite = unittest.TestSuite()
    tests = [
        AuthSignInTest('test_sign_in_without_tenant'),
        AuthSignUpTest('test_sign_up_without_tenant'),
        PermissionCheckTest('test_permission_granted_demo_a'),
        PermissionCheckTest('test_permission_denied_demo_a'),
        PermissionCheckTest('test_permission_granted_demo_b'),
        PermissionCheckTest('test_permission_denied_demo_b'),
        TaskControllerTest('test_permission_granted')
    ]
    suite.addTests(tests)

    runner = unittest.TextTestRunner()
    runner.run(suite)
