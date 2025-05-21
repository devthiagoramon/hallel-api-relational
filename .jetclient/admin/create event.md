```toml
name = 'create event'
method = 'POST'
url = '{{url}}/admin/event/create'
sortWeight = 1000000
id = '1682a7c0-8d8e-46f4-b6bb-eba2199ad854'

[auth.bearer]
token = '{{token}}'

[[body.formData]]
key = 'request'
value = '{    "title":"Tarde com Maria",    "description":"Momento de espiritualidade mariana com oração do terço, meditação e missa.",    "date":"2025-08-10T15:00:00.000Z",    "local_event_name":"Capela São João Paulo II",    "local_event_longitude":-60.0350,    "local_event_latitude":-3.1052,    "isImportant":false }'
contentType = 'application/json'

[[body.formData]]
type = 'FILE'
key = 'image_url'
value = 'C:\Users\Callidus\Documents\hallel imagem\Imagem Evento Hallel.png'
contentType = 'image/png'

[[body.formData]]
type = 'FILE'
key = 'banner_url'
value = 'C:\Users\Callidus\Documents\hallel imagem\Banner evento hallel.png'
contentType = 'image/png'
```
