CREATE OR REPLACE FUNCTION create_message_statuses_for_participants()
    RETURNS TRIGGER AS
$$
BEGIN

    INSERT INTO message_scale_status(message_id, recipíent_chat_id, status)
    SELECT NEW.id,
           scp.id,
           CASE
               WHEN scp.id = NEW.member_chat_sender_id THEN 'READ'::message_delivery_status
               ELSE 'SENT'::message_delivery_status
               END
    FROM scale_chat_participant as scp
    WHERE scp.event_scale_id = NEW.event_scale_id;
    RETURN NEW;
end;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_create_message_statuses
    AFTER INSERT
    ON scale_chat_message
    FOR EACH ROW
EXECUTE FUNCTION create_message_statuses_for_participants();