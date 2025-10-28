INSERT INTO role(id, description)
VALUES ('6c33c8c7-c7e4-42a4-a503-9302f83209ce', 'ADMIN_USER'),
       ('70db60bd-d0b5-4ebe-bb97-8194cf3e35a1', 'ADMIN_EVENT'),
       ('6001a717-d572-4237-8fb7-7cc812303940', 'ADMIN_ASSOCIADO'),
       ('99699e70-56e2-4905-9f98-b145a7684663', 'ADMIN_FINANCEIRO'),
       ('ac28e77e-487c-4821-a521-25f30fc26fc8', 'ADMIN_MINISTERIO'),
       ('d317abc1-bf6b-4e67-9102-d845a80cb60c', 'ASSOCIADO');

-- insert role for admin
INSERT INTO user_role(user_id, role_id)
VALUES ('64afc53d-e9e6-4320-92f2-859ee1ca2e73'::uuid, '6c33c8c7-c7e4-42a4-a503-9302f83209ce'::uuid),
       ('64afc53d-e9e6-4320-92f2-859ee1ca2e73'::uuid, '70db60bd-d0b5-4ebe-bb97-8194cf3e35a1'::uuid),
       ('64afc53d-e9e6-4320-92f2-859ee1ca2e73'::uuid, '6001a717-d572-4237-8fb7-7cc812303940'::uuid),
       ('64afc53d-e9e6-4320-92f2-859ee1ca2e73'::uuid, '99699e70-56e2-4905-9f98-b145a7684663'::uuid),
       ('64afc53d-e9e6-4320-92f2-859ee1ca2e73'::uuid, 'ac28e77e-487c-4821-a521-25f30fc26fc8'::uuid);