INSERT INTO event_invite (id, name, description, value, event_id)
SELECT gen_random_uuid(), 'Ingresso Gratuito', 'Acesso gratuito ao evento', 0, e.id
FROM events e
WHERE e.its_free = true
  AND NOT EXISTS (SELECT 1 FROM event_invite ei WHERE ei.event_id = e.id);
