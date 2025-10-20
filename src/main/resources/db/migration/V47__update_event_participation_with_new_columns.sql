UPDATE event_participation as ep
SET name         = u.name,
    email        = u.email,
    phone_number = u.phone_number
FROM "user" as u
where ep.user_id = u.id
  AND ep.user_id IS NOT NULL;