```toml
name = 'list all members of ministry'
method = 'GET'
url = '{{url}}/coordinator/ministry/member-ministry/list/{ministry-id}'
sortWeight = 1000000
id = '906bce01-e188-443f-9fe4-b137d3731fbf'

[[pathVariables]]
key = 'ministry-id'
value = 'dc317933-167f-4f76-b7cb-a2a219dc8191'

[[headers]]
key = 'coordenador-token'
value = '{{coordenador-token}}'

[auth.bearer]
token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYXJyb3NAZ21haWwuY29tIiwicm9sZXMiOlsiVVNFUiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJleHAiOjE3NDk5MzI3NzEsImlhdCI6MTc0NzM0MDc3MX0.iUUnAiLgBn7kSoQRodT0YC0t7KGAMj53vTzt4H4PWHI'
```
