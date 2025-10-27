insert into "user"
values ('3da8cb24-9df4-4619-bc32-32925a126d7c'::uuid, 'Tanny Cavalcante', 'tannycavalcante.mk@gmail.com', '{pbkdf2}e042d232cd9f3948f3441cdc2f1c8d6be4ca67dca167482d6955fdec37624394ecd398d3893543191821b10ea8f883dc');


insert into "user_role"
values ('3da8cb24-9df4-4619-bc32-32925a126d7c'::uuid, '2da3b58d-ba4a-40fb-91f4-c2ec21d6ecbd'::uuid),
       ('3da8cb24-9df4-4619-bc32-32925a126d7c'::uuid, 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'::uuid);