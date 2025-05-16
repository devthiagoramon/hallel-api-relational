```toml
name = 'edit function ministry'
method = 'PUT'
url = '{{url}}/coordinator/ministry/function-ministry/edit/{function-ministry-id}'
sortWeight = 3000000
id = 'd767c898-d0bb-4a6e-8da9-79e4f7884424'

[[pathVariables]]
key = 'function-ministry-id'
value = '96e9d51d-3b1f-4863-8784-b04ee2a7487d'

[auth.bearer]
token = '{{token}}'

[body]
type = 'JSON'
raw = '''
{
  "name": "Cantor editado",
  "description": "É o cantor do ministério",
  "icon": "🎤",
  "color": "green"
}'''
```
