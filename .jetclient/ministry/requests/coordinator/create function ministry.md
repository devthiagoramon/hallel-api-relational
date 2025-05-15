```toml
name = 'create function ministry'
method = 'POST'
url = '{{url}}/coordinator/ministry/function-ministry/add/{ministry-id}'
sortWeight = 2000000
id = '8eb14ec0-3fbf-41b3-a431-ae577ff7ac19'

[[pathVariables]]
key = 'ministry-id'
value = '8675330f-9c78-4c6d-9230-b046b4097392'

[auth.bearer]
token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYXJyb3NAZ21haWwuY29tIiwicm9sZXMiOlsiVVNFUiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJleHAiOjE3NDk5MzI3NzEsImlhdCI6MTc0NzM0MDc3MX0.iUUnAiLgBn7kSoQRodT0YC0t7KGAMj53vTzt4H4PWHI'

[body]
type = 'JSON'
raw = '''
{
  "name": "Guitarrista",
  "description": "É o guitarrista do ministério",
  "icon": "\uD83C\uDFB8",
  "color": "yellow"
}'''
```
