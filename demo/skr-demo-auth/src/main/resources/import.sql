INSERT INTO tenent (ver, code, name, vip_level) VALUES (0, 'org1', 'org1', 1);
INSERT INTO tenent (ver, code, name, vip_level) VALUES (0, 'org2', 'org2', 1);

INSERT INTO account (ver, username, password, status) VALUES (0, 'dev', 'dev', 0);
INSERT INTO account (ver, username, password, status) VALUES (0, 'test', 'test', 0);

INSERT INTO t_user (id, ver, tenent_code, account_username, permission_bit1, permission_bit2, permission_bit3, status) VALUES (1, 0, 'org1', 'dev', 9223372036854775807, 9223372036854775807, 9223372036854775807, 0);
INSERT INTO t_user (id, ver, tenent_code, account_username, permission_bit1, permission_bit2, permission_bit3, status) VALUES (2, 0, 'org1', 'test', 0, 0, 0, 2);