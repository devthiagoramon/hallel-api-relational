```toml
name = 'list scales of user in ministry by range date with status'
method = 'GET'
url = '{{url}}/user/ministry/member-event-scale/scales-range-date-with-status/{idMinistry}/{userId}?start=2025-06-01T00:00:00&end=2025-06-30T23:59:59'
sortWeight = 6000000
id = 'f67ddfb6-15d7-4dfc-a69a-c95f632c0494'

[[queryParams]]
key = 'start'
value = '2025-06-01T00:00:00'

[[queryParams]]
key = 'end'
value = '2025-06-30T23:59:59'

[[pathVariables]]
key = 'idMinistry'
value = 'dc317933-167f-4f76-b7cb-a2a219dc8191'

[[pathVariables]]
key = 'userId'
value = 'fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190'

[auth.bearer]
token = '{{token}}'
```
