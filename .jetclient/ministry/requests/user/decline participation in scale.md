```toml
name = 'decline participation in scale'
method = 'PATCH'
url = '{{url}}/user/ministry/member-event-scale/decline-participation?eventScaleId=9b35a429-d2b5-43f8-84d7-ee60cc488c2b&userId=35e5d0dc-b95b-42c1-ab41-96954e488ea0'
sortWeight = 4000000
id = '7041c96e-1ef7-4e5b-9551-aef29b100e19'

[[queryParams]]
key = 'eventScaleId'
value = '9b35a429-d2b5-43f8-84d7-ee60cc488c2b'

[[queryParams]]
key = 'userId'
value = '35e5d0dc-b95b-42c1-ab41-96954e488ea0'

[auth.bearer]
token = '{{token'

[body]
type = 'JSON'
raw = '''
{
  "reason": "Não vou poder infelizmente!"
}'''
```
