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
value = '{    "title":"Missa de Pentecostes",    "description":"Celebração especial de Pentecostes com louvor, adoração e pregação.",    "date":"2025-06-08T19:00:00.000Z",    "local_event_name":"Igreja Matriz São Sebastião",    "local_event_longitude":-60.0231,    "local_event_latitude":-3.1316,    "isImportant":false,    "ministryIds":[       "8675330f-9c78-4c6d-9230-b046b4097392",       "dc317933-167f-4f76-b7cb-a2a219dc8191"    ] }'
contentType = 'application/json'

[[body.formData]]
type = 'FILE'
key = 'image_url'
value = 'C:\Users\thiag\OneDrive\Imagens\DummyImages\Imagem Evento Hallel.png'
contentType = 'image/png'

[[body.formData]]
type = 'FILE'
key = 'banner_url'
value = 'C:\Users\thiag\OneDrive\Imagens\DummyImages\Banner Evento Hallel.png'
contentType = 'image/png'
```
