-- Habilita Supabase Realtime para as tabelas do scale-chat (só roda em ambiente Supabase)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_publication WHERE pubname = 'supabase_realtime') THEN
        ALTER PUBLICATION supabase_realtime ADD TABLE scale_chat_message;
        ALTER PUBLICATION supabase_realtime ADD TABLE message_scale_status;
    END IF;
END
$$;
