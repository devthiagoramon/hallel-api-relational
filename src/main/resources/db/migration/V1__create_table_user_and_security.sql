CREATE EXTENSION IF NOT EXISTS pgcrypto;
create table "user"
(
    id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name           varchar(255) NOT NULL,
    email          varchar(100) not null unique,
    password       text  not null,
    token          text null,
    date_birth     date null,
    age            int null,
    cpf            varchar(11) null,
    file_image_url text null,
    phone_number   varchar(12) null
);

create table "role"
(
    id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    description varchar(255) not null
);

create table "user_role"
(
    user_id uuid,
    role_id uuid,
    PRIMARY KEY (user_id, role_id),
    constraint FK_USER_ID foreign key (user_id) REFERENCES "user" (id) on update cascade,
    constraint FK_ROLE_ID foreign key (role_id) references role (id) on update cascade

);