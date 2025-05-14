```toml
name = 'edit ministry'
method = 'PUT'
url = '{{url}}/admin/ministry/edit/8675330f-9c78-4c6d-9230-b046b4097392'
sortWeight = 3000000
id = '496067ef-8f16-44ce-87a4-4c0208fbad63'

[auth.bearer]
token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1AaGFsbGVsLmNvbSIsInJvbGVzIjpbIlVTRVIiLCJBRE1JTiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJleHAiOjE3NDk4MzU4NjUsImlhdCI6MTc0NzI0Mzg2NX0.hr1jKx21NwM87lQhV559XywAoDx_AS77FUA3eZ4rc2A'

[[body.formData]]
key = 'request'
value = '{   "title": "Ministério da música editado",   "description": "🎵 Descrição do Ministério de Música\nO Ministério de Música é responsável por conduzir a igreja em adoração por meio do louvor.\nMais do que cantar ou tocar instrumentos, nosso objetivo é criar um ambiente onde a presença de Deus seja sentida e onde corações sejam levados a uma verdadeira conexão com Ele.\n\nServimos com excelência, dedicação e reverência, entendendo que cada nota, voz e canção é uma oferta de adoração.\nNossa missão é proclamar o Evangelho com alegria, edificar a igreja com letras inspiradas na Palavra de Deus e preparar o coração dos fiéis para receber a mensagem.\n\nSomos uma equipe unida por um propósito: **glorificar a Deus através da música**.\nBuscamos crescer espiritualmente e tecnicamente, sendo instrumentos disponíveis nas mãos do Senhor.",   "hasRepertoire": true,   "ministryType": "VIDEO",   "coordinatorId": "fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190",   "viceCoordinatorId": "a78319c9-abd5-48d0-988a-60f421e9dd98" }'
contentType = 'application/json'

[[body.formData]]
key = 'image'
disabled = true
```
