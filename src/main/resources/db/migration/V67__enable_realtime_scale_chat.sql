-- Habilita Supabase Realtime para as tabelas do scale-chat
ALTER PUBLICATION supabase_realtime ADD TABLE scale_chat_message;
ALTER PUBLICATION supabase_realtime ADD TABLE message_scale_status;
