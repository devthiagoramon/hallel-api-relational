CREATE OR REPLACE VIEW v_scale_chat_message_details AS
SELECT
    scm.id,
    scm.event_scale_id,
    sender.id AS participant_sender_id,
    u.id AS user_sender_id,
    scm.content,
    scm.content_type,
    scm.sent_at,
    scm.updated_at,
    scm.visibility,
    (
        SELECT
            CASE
                WHEN COUNT(CASE WHEN mss.status = 'READ' THEN 1 END) = COUNT(mss.id) THEN 'READ'
                WHEN COUNT(CASE WHEN mss.status IN ('RECEIVED', 'READ') THEN 1 END) = COUNT(mss.id) THEN 'RECEIVED'
                ELSE 'SENT'
                END
        FROM message_scale_status mss
                 -- O JOIN aqui é com a tabela de participantes
                 JOIN scale_chat_participant scp ON mss.recipíent_chat_id = scp.id
        WHERE mss.message_id = scm.id AND scp.id != sender.id
    ) AS aggregated_status
FROM
    scale_chat_message scm
        JOIN scale_chat_participant sender ON scm.member_chat_sender_id = sender.id
        JOIN member_event_scale mes ON sender.member_scale_chat_id = mes.id
        JOIN member_ministry mm ON mes.member_ministry_id = mm.id
        JOIN "user" u ON mm.user_id = u.id;