[![Sequence Diagram](10k-architecture.png)](
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAA5M9qBACu2AMQALADMbgBMAJwgMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNMY7vcebT+fUpVAeZhHXz1PbKoiVC6Bu6-YZSgjqs8YApTShgOb2ur0ABRS0qbAEApk6iUH3wZDmeqBJzBYbjObqYCMhbLRNQbx1A1TJXG6Ox+XyNXoKFmTiaHQ5z101T1NA+BAID3aJ3e0O+8n+mAjseZqgUh2Tr1DkCq9Gu9mB7Tc9eDwXGWoKDgcTXS7TL1dUo-Orcx9EKHx6jHAN-xHmHmkbk-Cuel6vnqQZziG9wwk89bYmieJqKOWBQXCOY3BGryGkqSz1PsIKfnq7QQB2aDYcslzLtmM65iUYD1GEThOCWEyYZ8MA4cCyz4fEhHEaR+xXOgHCmF4vgBNA7CMjEIpwIm0hwAoMAADIQFkhTFOYgpofWzRtF0vQGOo+RoCWipzGsvz-BwAk9p43h+P4XgoOgMRxIkjnOcp9i+Fg6llLOWbadIiaKYm7SJt0PQGaoRnDFxPHoN2Qm2aJ-gope-jYOKGqKWiMAAOJKhovnMGGAX1A0eWhRF9hKrFX7xYUgnCXZAQcAA7BETgoE4MSJsEcAyQAbPA26GAVcwwEUeZ+VUZWNK0HTVbV0z1UR6AljVcwAHJKtZSUifZlgoGOECbC5SAJGAR0nWdABSEDivlTb+MkoBqlNNGaeG2lNMyek9JtKB1QRa3GaM2AIMAR1QHAEAINAayAwAktIe3NSlXhQ+dl2Y-KiAxrAwDYBDhB5AUk3FV9c0NEFIVhRFxiJZgQA
)
```
actor Client
participant Server
participant Handler
participant Service
participant DataAccess
database db

entryspacing 0.8
group#43829c #lightblue Registration
Client -> Server: [POST] /user\n{"username":" ", "password":" ", "email":" "}
Server -> Handler: {"username":" ", "password":" ", "email":" "}
Handler -> Service: register(RegisterRequest)
Service -> DataAccess: getUser(username)
DataAccess -> db:Find UserData by username
break User with username already exists
db --> DataAccess: UserData
DataAccess --> Service: UserData
Service --> Server: AlreadyTakenException
Server --> Client: 403\n{"message": "Error: username already taken"}
end
db --> DataAccess: null
DataAccess --> Service: null
Service -> DataAccess:createUser(userData)
DataAccess -> db:Add UserData
Service -> DataAccess:createAuth(authData)
DataAccess -> db:Add AuthData
Service --> Handler: RegisterResult
Handler --> Server: {"username" : " ", "authToken" : " "}
Server --> Client: 200\n{"username" : " ", "authToken" : " "}
end

group#orange #FCEDCA Login
Client -> Server: [POST] /session\n{username, password}
end

group#green #lightgreen Logout
Client -> Server: [DELETE] /session\nauthToken
end

group#red #pink List Games
Client -> Server: [GET] /game\nauthToken
end

group#d790e0 #E3CCE6 Create Game 
Client -> Server: [POST] /game\nauthToken\n{gameName}
end

group#yellow #lightyellow Join Game #black
Client -> Server: [PUT] /game\nauthToken\n{playerColor, gameID}
end

group#gray #lightgray Clear application 
Client -> Server: [DELETE] /db
end
```