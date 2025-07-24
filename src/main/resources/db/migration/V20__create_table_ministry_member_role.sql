CREATE TABLE ministry_member_role
(
    "member_ministry_id" UUID,
    "role_ministry_id"   UUID,
    primary key (member_ministry_id, role_ministry_id),
    CONSTRAINT fk_member_ministry FOREIGN KEY (member_ministry_id) REFERENCES member_ministry (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_role_ministry FOREIGN KEY (role_ministry_id) REFERENCES role_ministry (id)
);