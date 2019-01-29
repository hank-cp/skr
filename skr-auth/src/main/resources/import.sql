INSERT INTO organization (ver, code, name, vip_level) VALUES (0, 'org1', 'org1', 1);
INSERT INTO organization (ver, code, name, vip_level) VALUES (0, 'org2', 'org2', 1);

INSERT INTO account (ver, username, password, status) VALUES (0, 'dev', 'dev', 0);
INSERT INTO account (ver, username, password, status) VALUES (0, 'test', 'test', 0);

INSERT INTO org_user (id, ver, organization_code, account_username, status) VALUES (1, 0, 'org1', 'dev', 0);
INSERT INTO org_user (id, ver, organization_code, account_username, status) VALUES (2, 0, 'org1', 'test', 2);