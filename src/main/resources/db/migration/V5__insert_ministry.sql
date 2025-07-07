INSERT INTO ministry (id,title,description,image,has_repertoire,ministry_type,coordinator_id,vice_coordinator_id) VALUES
                                                                                                                             ('8675330f-9c78-4c6d-9230-b046b4097392','Ministério da música','🎵 Descrição do Ministério de Música
O Ministério de Música é responsável por conduzir a igreja em adoração por meio do louvor.
Mais do que cantar ou tocar instrumentos, nosso objetivo é criar um ambiente onde a presença de Deus seja sentida e onde corações sejam levados a uma verdadeira conexão com Ele.

Servimos com excelência, dedicação e reverência, entendendo que cada nota, voz e canção é uma oferta de adoração.
Nossa missão é proclamar o Evangelho com alegria, edificar a igreja com letras inspiradas na Palavra de Deus e preparar o coração dos fiéis para receber a mensagem.

Somos uma equipe unida por um propósito: **glorificar a Deus através da música**.
Buscamos crescer espiritualmente e tecnicamente, sendo instrumentos disponíveis nas mãos do Senhor.','https://storage.googleapis.com/download/storage/v1/b/hallel-bucket/o/8675330f-9c78-4c6d-9230-b046b4097392-Ministry-image?generation=1747246308409753&alt=media',true,'1','fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190','a78319c9-abd5-48d0-988a-60f421e9dd98'),
                                                                                                                             ('dc317933-167f-4f76-b7cb-a2a219dc8191','Ministério da dança','✨ Ministério de Dança ✨
O Ministério de Dança é um chamado para adorar a Deus com o corpo, com excelência, reverência e sensibilidade ao Espírito Santo.
Através de movimentos, expressões e coreografias inspiradas, buscamos transmitir a mensagem do Evangelho e tocar corações de forma profunda e visual.

Nossa missão é usar a dança como instrumento profético e de edificação, glorificando a Deus em cada apresentação e despertando uma adoração verdadeira no ambiente.
Com humildade e compromisso, servimos à igreja e ao Reino, dedicando nossos talentos àquele que nos chamou.

Mais do que uma performance, cada dança é uma oferta viva diante do Senhor, fruto de oração, consagração e paixão por Sua presença.','https://storage.googleapis.com/download/storage/v1/b/hallel-bucket/o/dc317933-167f-4f76-b7cb-a2a219dc8191-Ministry-image?generation=1747246377826157&alt=media',true,'0','fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190','35e5d0dc-b95b-42c1-ab41-96954e488ea0');

INSERT INTO member_ministry (id, user_id,ministry_id) VALUES
                                                             ("fbc03b19-c26e-4b27-90cd-08586c8d1470", 'fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190','8675330f-9c78-4c6d-9230-b046b4097392'),
                                                             ("a21575a9-8068-4820-a5b0-2de6e6d6577f", 'a78319c9-abd5-48d0-988a-60f421e9dd98','8675330f-9c78-4c6d-9230-b046b4097392'),
                                                             ("2f284993-2431-4f0f-99cc-7929e239eb4e", 'fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190','dc317933-167f-4f76-b7cb-a2a219dc8191'),
                                                             ("66704d4c-61b7-4c38-8ef9-175b32fc017b", '35e5d0dc-b95b-42c1-ab41-96954e488ea0','dc317933-167f-4f76-b7cb-a2a219dc8191');
