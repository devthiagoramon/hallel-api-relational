INSERT INTO "user"(id,name,email,password)
VALUES
('64afc53d-e9e6-4320-92f2-859ee1ca2e73', 'Admin Hallel', 'adm@hallel.com','0c3b5a467cfdf844d7abb40ae621c5d3b74227e150176c76fa3097575fac21c2d9c6d0966d414c115c1d6ef464493b49'),
('fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190', 'Thiago Barros', 'barros@gmail.com','be71cdfa836f3063c897cad4ae8a17bf656651113684a39f4f9b515ed08ab48ce78aa4583ad6125ba4648b426c9040e2'),
('a78319c9-abd5-48d0-988a-60f421e9dd98', 'Miguel Arcanjo', 'miguel@gmail.com', '6ccf3dd5d38d61027dd4b8bc6fe85a2713cc36ae81596a23c3586122b5686e9b51de775c6ce1ceb7a85c10433aead23f'),
('da5ff759-3b46-474c-bb2c-e5f274a6527c', 'Felipe Gabriel', 'felipe@gmail.com', 'c5dc5d15282a5c0e2c18ba109279841ddfcd6bd5befd6c7b8ded7f881311f5ab148b62892329f6f68b3468f3c2df7a29'),
('35e5d0dc-b95b-42c1-ab41-96954e488ea0', 'Emmerson Santa Rita', 'emmerson@gmail.com', '294f75a46a968510ae17c3aac99a88944044d4dfc39b44264305f5d5a06fc0b3de175e66b9889ab0ee5e40a4af5e8684'),
('2c4d3394-c096-489d-9f3d-acd6953dcce3', 'Manfred Veiga', 'manfred@gmail.com', '3ce1bc9e9fe8a459c9fb182d0c2741da8fa512ebb28f0fccd6dd00acdbc01f9c18f94d47bb120f8d613340ee5059493f');

INSERT INTO "user_role"(user_id, role_id) VALUES
('64afc53d-e9e6-4320-92f2-859ee1ca2e73', '2da3b58d-ba4a-40fb-91f4-c2ec21d6ecbd'),
('64afc53d-e9e6-4320-92f2-859ee1ca2e73', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('a78319c9-abd5-48d0-988a-60f421e9dd98', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('da5ff759-3b46-474c-bb2c-e5f274a6527c', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('35e5d0dc-b95b-42c1-ab41-96954e488ea0', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('2c4d3394-c096-489d-9f3d-acd6953dcce3', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071');






