insert into "user"
values ('42662260-ef02-4177-94f5-643fb1349806'::uuid, 'Carla Munique', 'carlamunique05@gmail.com', '{pbkdf2}bd25fadc81b05ed8774154a4e00984b24f38a90c44b21d0dece244270f729ea5f144b696921cf90a3e67ccc00f4f0d81'),
       ('a577191d-4b5f-4f65-8ff4-f1c5592fd59b'::uuid, 'Adm Thiago Ramon', 'dev.thiagoramon@gmail.com', '{pbkdf2}796ad9545b402bb13a25389a374ab4105858defb8c28d0c04b4e7f6f8b550cd3b9fe8cce7ee0829e58cebd74ad61da72');


insert into "user_role"
values ('42662260-ef02-4177-94f5-643fb1349806'::uuid, '2da3b58d-ba4a-40fb-91f4-c2ec21d6ecbd'::uuid),
       ('42662260-ef02-4177-94f5-643fb1349806'::uuid, 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'::uuid),
       ('a577191d-4b5f-4f65-8ff4-f1c5592fd59b'::uuid, '2da3b58d-ba4a-40fb-91f4-c2ec21d6ecbd'::uuid),
       ('a577191d-4b5f-4f65-8ff4-f1c5592fd59b'::uuid, 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'::uuid);