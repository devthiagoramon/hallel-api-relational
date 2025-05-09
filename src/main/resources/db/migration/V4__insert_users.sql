INSERT INTO "user"(id,name,email,password)
VALUES
('64afc53d-e9e6-4320-92f2-859ee1ca2e73', 'Admin Hallel', 'adm@hallel.com','bda8678c5935e08584e43881e8cf78fa7973ad5aadc48420e8ffec9bf3b717d631224e80021a8597'),
('fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190', 'Thiago Barros', 'barros@gmail.com','bc702cd8128f84a05bd04fe409f3bf44caf63c094b190269d5ef7bec3de37d3b27145dc72376903c'),
('a78319c9-abd5-48d0-988a-60f421e9dd98', 'Miguel Arcanjo', 'miguel@gmail.com', '1ddf217f26982e1d6d871234ba8facefd6c54627424e8018d54e062fb5fa1533e1aed1689b1d8714'),
('da5ff759-3b46-474c-bb2c-e5f274a6527c', 'Felipe Gabriel', 'felipe@gmail.com', '7cb3d889fe7de71cdc1434927bafed424d7c056a58409ccc1d7030799e85b740b49a1cc361dceb71'),
('35e5d0dc-b95b-42c1-ab41-96954e488ea0', 'Emmerson Santa Rita', 'emmerson@gmail.com', 'f0990bb02bda00a8091c7595038e228a944770b914b71191bf6bb762b82ad437350bb590d3379494'),
('2c4d3394-c096-489d-9f3d-acd6953dcce3', 'Manfred Veiga', 'manfred@gmail.com', 'f3fd9486976b76a30508e84e8ab87c46022021893b8c98533eb04275d4f68c3c2ea76bcfa08fc988');

INSERT INTO "user_role"(user_id, role_id) VALUES
('64afc53d-e9e6-4320-92f2-859ee1ca2e73', '2da3b58d-ba4a-40fb-91f4-c2ec21d6ecbd'),
('64afc53d-e9e6-4320-92f2-859ee1ca2e73', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('a78319c9-abd5-48d0-988a-60f421e9dd98', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('da5ff759-3b46-474c-bb2c-e5f274a6527c', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('35e5d0dc-b95b-42c1-ab41-96954e488ea0', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071'),
('2c4d3394-c096-489d-9f3d-acd6953dcce3', 'a99dc834-8f6b-4fd3-bbd1-5b2992b19071');






