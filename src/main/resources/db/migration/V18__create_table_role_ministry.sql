CREATE TABLE role_ministry
(
    id          uuid primary key default gen_random_uuid(),
    description varchar(50) not null
);