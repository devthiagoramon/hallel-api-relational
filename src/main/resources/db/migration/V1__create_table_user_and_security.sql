create table "user"
(
    id             uuid PRIMARY KEY,
    name           varchar(255) NOT NULL,
    email          varchar(100) not null unique,
    password       varchar(60)  not null,
    token          text,
    date_birth     date,
    age            date,
    cpf            varchar(11),
    file_image_url text,
    phone_number   varchar(12)
);

create table "role"
(
    id          uuid PRIMARY KEY,
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