```toml
name = 'edit event'
method = 'PATCH'
url = '{{url}}/admin/event/edit/{id}'
sortWeight = 2000000
id = '2f839d3f-355e-47a1-9471-7aa55f6e0997'

[[pathVariables]]
key = 'id'
value = '54451953-43d4-41af-a9dc-684db7c023c3'

[auth.bearer]
token = '{{token}}'

[[body.formData]]
key = 'request'
value = '{    "title":"Missa de Pentecostes",    "description":"Celebração especial de Pentecostes com louvor, adoração e pregação.",    "date":"2025-06-13T19:00:00.000Z",    "local_event_name":"Igreja Matriz São Sebastião",    "local_event_longitude":-60.0231,    "local_event_latitude":-3.1316,    "isImportant":true }'
contentType = 'application/json'
```
