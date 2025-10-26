insert into "user"
values ('570e1cfa-7180-47df-86bd-c24ece6cee7e'::uuid, 'Leonardo Lopes', 'leonardolopesam@gmail.com', '{pbkdf2}90d94cf1e0c128797df1886b025508d95c8296b87d9c4c88793209268d6462bcf9e453f9305f4455e09b4b9586b414b8');


insert into "user_role"
values ('570e1cfa-7180-47df-86bd-c24ece6cee7e'::uuid, '2da3b58d-ba4a-40fb-91f4-c2ec21d6ecbd'::uuid),
       ('570e1cfa-7180-47df-86bd-c24ece6cee7e'::uuid, 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'::uuid);