INSERT INTO organization (id, ver, code, name, vip_level) VALUES (1, 0, 'org1', 'org1', 1);
INSERT INTO organization (id, ver, code, name, vip_level) VALUES (2, 0, 'org2', 'org2', 1);

INSERT INTO account (id, ver, username, password, status) VALUES (1, 0, 'dev', 'dev', 0);
INSERT INTO account (id, ver, username, password, status) VALUES (2, 0, 'test', 'test', 0);

INSERT INTO org_user (id, ver, organization_id, account_id, status) VALUES (1, 0, 1, 1, 0);
INSERT INTO org_user (id, ver, organization_id, account_id, status) VALUES (2, 0, 1, 2, 2);