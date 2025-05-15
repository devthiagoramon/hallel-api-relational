```toml
name = 'associate function to a member'
method = 'PATCH'
url = '{{url}}/coordinator/ministry/member-ministry/add/function'
sortWeight = 5000000
id = '4575a7df-1987-47e8-ad26-f41a2b947037'

[auth.bearer]
token = '{{token}}'

[body]
type = 'JSON'
raw = '''
{
  "functionMinistryId": "de4eb9ac-11b6-4f5b-8652-af1546ed334d",
  "userId": "fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190"
}'''
```
