INSERT INTO "user"(id,name,email,password)
VALUES
('64afc53d-e9e6-4320-92f2-859ee1ca2e73', 'Admin Hallel', 'adm@hallel.com','3e0d868f31882d8a628e829d6c422213a966e52f04a4a9f3601f55b910e74c3077c1b68bbe2693c3ab31cea08ca8aac1'),
('fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190', 'Thiago Barros', 'barros@gmail.com','{pbkdf2}95c40fac33779e20c71d863d9e67208db32d7e31156f98666ac516af4921cb51b3ec81de0920b3faaca1486b18bbd407'),
('a78319c9-abd5-48d0-988a-60f421e9dd98', 'Miguel Arcanjo', 'miguel@gmail.com', 'b9c342dc65322446465eba5ad9e3d8f587b5e5d21ed027e54bd5c7ec3d6aee5d9d88b649bab5c14acaf1c9121215fd74'),
('da5ff759-3b46-474c-bb2c-e5f274a6527c', 'Felipe Gabriel', 'felipe@gmail.com', 'ac0c30ba96893bbad9ad504d7f0d5ac0021943e134b017ec637820aefd68b3a7f276e641759482dba2309e80916785be'),
('35e5d0dc-b95b-42c1-ab41-96954e488ea0', 'Emmerson Santa Rita', 'emmerson@gmail.com', '393cd6616da5c00e6e31dcd2c615476a2a39d1b71d49e24cfd3020db70418e34fb5571a0d92ffc9bef6aa03e0f623d91'),
('2c4d3394-c096-489d-9f3d-acd6953dcce3', 'Manfred Veiga', 'manfred@gmail.com', '3607ce3d1654d3d41c22f53699ce20a6dfb8db55c44e503daa1b3b59d4ffb0dfaac4f7b3e12f6c2338de657af0ea475b');

INSERT INTO "user_role"(user_id, role_id) VALUES
('64afc53d-e9e6-4320-92f2-859ee1ca2e73', '2da3b58d-ba4a-40fb-91f4-c2ec21d6ecbd'),
('64afc53d-e9e6-4320-92f2-859ee1ca2e73', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('a78319c9-abd5-48d0-988a-60f421e9dd98', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('da5ff759-3b46-474c-bb2c-e5f274a6527c', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('35e5d0dc-b95b-42c1-ab41-96954e488ea0', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('2c4d3394-c096-489d-9f3d-acd6953dcce3', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071');






