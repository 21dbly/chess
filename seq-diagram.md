[![Sequence Diagram](10k-architecture.png)](
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAA5M9qBACu2AMQALADMbgBMAJwgMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNMY7vcebT+fUpVAeZhHXz1PbKoiVC6Bu6-YZSgjqs8YApTShgOb2ur0ABRS0qbAEApk6iUH3wZDmeqBJzBYbjObqYCMhbLRNQbx1A1TJXG6Ox+XyNXoKFmTiaHQ5z101T1NA+BAID3aJ3e0O+8n+mAjseZqgUh2Tr1DkCq9Gu9mB7Tc9eDwXGWoKDgcTXS7TL1dUo-Orcx9EKHx6jHAN-xHmHmkbk-Cuel6vnqQZziG9wwk89bYmieJqKOWBQXCOY3BGryGkqSz1PsIKfnq7QQB2aDYcslzLtmM65iUYD1GEThOCWEyYZ8MA4cCyz4fEhHEaR+xXOgHCmF4vgBNA7CMjEIpwIm0hwAoMAADIQFkhTFOYgpofWzRtF0vQGOo+RoCWipzGsvz-BwVxaYKyHoaZKDmfofw7NCjwoWGIjzspqkYj54oEkSYCksGgoDs6MCMiybIOb+vLHvcp5ihKroynKDbvEqmAqjGGqutqur6g5RgQGoC4QMwVpor2-YPvSC6juO4XTp5K7zou46hVRWn1AAkmgIDQCi4CupyyapumhQ2VR6kYAWTgAIxMWWqgVvMOE1nW9TFRwpUCmgFUwFVORdoJNVUc1Q5Xm6N6Xah4EBte8jZaqGrioNtYoOAMAWTst4qPdWb1sy0xftASAAF4oBw40oGmRkUfC9yzbRMCFktoxjCta1VmMm3QNtoN6uDUO7AJPZdfef6DrUT5yCgIHxB+X4-hO1P8gBZ4XpGLM3l1kHuRG-loBkqiIZgdlI7OQMvMxjasex3xcTxnZsWRXbTcjeZzTA9GMZjDl8RxYzK0RquK+TQmeN4fj+F4KDoDEcSJPbjs+b4WAo5p4badIiaKYm7SJt0PQGaoRnDKbxGI7Zgv1lH6AS3H3teSg9Tu2+fkqR7gVqMF-0hlT8URSiMwQDQjM8szBFm2gcVTgKiXChkZc0DzoHaLK8oJ4UOXqjA-VUCaSDAV+KuFHaF11VdABm4Js8XLXS21acwHPpwF4DNQukT8Qk9DMczdrqPo8t-I4xttYEwqu-72T3ZCZPa7s-VYsbgvDdb0iMBv4Om9UZLeov9+RJ1hJRVq6FrI+xzCjOiDESxXAfsJG2AQUSXn8NgcUGpFJohgAAcSVBoL2EDtJ4MDiHewSpI5j1rkg62ol-AcAAOwRCcCgJwMREzBDgDJAAbPAbchgCFzBgEUY+Kdt6NFaB0ChVDd7jxLJQuYAA5JUltkEMMsCgMcEBNhOyQAkMAWidF6IAFIQHFPgps-hkigDVGImiEiIzNGZHpHoSiUDUJrsREs2AEDAC0VAOAEAEDQDWB43q0h1H0Ntl4AJ+jDFxPlIgGMsBgDYD8YQPIBRRHEOXs4v2Acg4h2MEgoAA
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
Server -> Handler: {username, password}
Handler -> Service: Login(LoginRequest)
Service -> DataAccess: getUser(username)
DataAccess -> db:Find UserData by username
break User with username does not exist
db --> DataAccess: null
DataAccess --> Service: null
Service --> Server: IncorrectUsernameException
Server --> Client: 401\n{"message": "Error: username does not exist"}
end
db --> DataAccess: UserData
DataAccess --> Service: UserData
break incorrect password
Service --> Server: UnauthorizedException
Server --> Client: 401\n{"message": "Error: unauthorized"}
end
Service -> DataAccess:createAuth(authData)
DataAccess -> db:Add AuthData
Service --> Handler: LoginResult
Handler --> Server: {"username" : " ", "authToken" : " "}
Server --> Client: 200\n{"username" : " ", "authToken" : " "}
end

group#green #lightgreen Logout
Client -> Server: [DELETE] /session\nauthToken
Server -> Handler: authToken
Handler -> Service: Logout(LogoutRequest)
Service -> DataAccess: removeAuthData(authToken)
DataAccess -> db:Remove AuthData by authToken
break Invalid AuthToken
db --> DataAccess: fail
DataAccess --> Service: fail
Service --> Server: Unauthorized
Server --> Client: 401\n{"message": "Error: unauthorized"}
end
db --> DataAccess: success
DataAccess --> Service: success
Service --> Handler: success
Handler --> Server: {}
Server --> Client: 200\n{}

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