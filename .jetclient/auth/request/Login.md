```toml
name = 'Login'
method = 'POST'
url = '{{url}}/auth/login'
sortWeight = 1000000
id = 'bb9b1990-c37f-471b-8036-960e7b66d9ec'

[body]
type = 'JSON'
raw = '''
{
  "email": "barros@gmail.com",
  "password": "barros123"
}'''
```
