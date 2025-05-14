```toml
name = 'add ministry'
method = 'POST'
url = '{{url}}/admin/ministry/create'
sortWeight = 1000000
id = '299183b0-1b08-45a3-bd66-5f5240393385'

[auth.bearer]
token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1AaGFsbGVsLmNvbSIsInJvbGVzIjpbIlVTRVIiLCJBRE1JTiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJleHAiOjE3NDk4MzU4NjUsImlhdCI6MTc0NzI0Mzg2NX0.hr1jKx21NwM87lQhV559XywAoDx_AS77FUA3eZ4rc2A'

[[body.formData]]
key = 'request'
value = '{   "title": "Ministério da dança",   "description": "✨ Ministério de Dança ✨\nO Ministério de Dança é um chamado para adorar a Deus com o corpo, com excelência, reverência e sensibilidade ao Espírito Santo.\nAtravés de movimentos, expressões e coreografias inspiradas, buscamos transmitir a mensagem do Evangelho e tocar corações de forma profunda e visual.\n\nNossa missão é usar a dança como instrumento profético e de edificação, glorificando a Deus em cada apresentação e despertando uma adoração verdadeira no ambiente.\nCom humildade e compromisso, servimos à igreja e ao Reino, dedicando nossos talentos àquele que nos chamou.\n\nMais do que uma performance, cada dança é uma oferta viva diante do Senhor, fruto de oração, consagração e paixão por Sua presença.",   "hasRepertoire": true,   "ministryType": "DANCE",   "coordinatorId": "fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190",   "viceCoordinatorId": "35e5d0dc-b95b-42c1-ab41-96954e488ea0" }'
contentType = 'application/json'

[[body.formData]]
type = 'FILE'
key = 'image'
value = 'C:\Users\Callidus\Documents\hallel imagem\ministerio dança image.jpeg'
contentType = 'image/jpeg'
```
