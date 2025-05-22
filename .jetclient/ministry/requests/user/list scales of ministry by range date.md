```toml
name = 'list scales of ministry by range date'
method = 'GET'
url = '{{url}}/user/ministry/member-event-scale/list-all/scales-range-date/{idMinistry}?start=2025-06-01T00:00:00&end=2025-06-30T23:59:59'
sortWeight = 5000000
id = 'b2dfad54-2dcd-45fe-8403-bf8a2b590a65'

[[queryParams]]
key = 'start'
value = '2025-06-01T00:00:00'

[[queryParams]]
key = 'end'
value = '2025-06-30T23:59:59'

[[pathVariables]]
key = 'idMinistry'
value = '8675330f-9c78-4c6d-9230-b046b4097392'

[auth.bearer]
token = '{{token}}'
```
