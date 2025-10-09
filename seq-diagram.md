[![Sequence Diagram](10k-architecture.png)](
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAA5M9qBACu2AMQALADMbgBMAJwgMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNMY7vcebT+fUpVAeZhHXz1PbKoiVC6Bu6-YZSgjqs8YApTShgOb2ur0ABRS0qbAEApk6iUH3wZDmeqBJzBYbjObqYCMhbLRNQbx1A1TJXG6Ox+XyNXoKFmTiaHQ5z101T1NA+BAID3aJ3e0O+8n+mAjseZqgUh2Tr1DkCq9Gu9mB7Tc9eDwXGWoKDgcTXS7TL1dUo-Orcx9EKHx6jHAN-xHmHmkbk-Cuel6vnqQZziG9wwk89bYmieJqKOWBQXCOY3BGryGkqSz1PsIKfnq7QQB2aDYcslzLtmM65iUYD1GEThOCWEyYZ8MA4cCyz4fEhHEaR+xXOgHCmF4vgBNA7CMjEIpwIm0hwAoMAADIQFkhTFOYgpofWzRtF0vQGOo+RoCWipzGsvz-BwVxaYKyHoaZKDmfofw7NCjwoWGIjzspqkYj54oEkSYCksGgoDs6MCMiybIOb+vLHvcp5ihKroynKDbvEqmAqjGGqutqur6g5RgQGoC4QMwVpor2-YPvSC6juO4XTp5K7zou46hVRWn1AAkmgIDQCi4CupyyapumhQ2VR6kYAWTgAIxMWWqgVvMOE1nW9TFRwpUCmgFUwFVORdoJNVUc1Q5Xm6N6Xah4EBte8jZaqGrioNtYoOAMAWTst4qPdWb1sy0xftASAAF4oBw40oGmRkUfC9yzbRMCFktoxjCta1VmMm3QNtoN6uDUO7AJPZdfef6DrUT5yCgIHxB+X4-hO1P8gBZ4XpGLM3l1kHuRG-loBkqiIZgdlI7OQMvMxjasex3xcTxnZsWRXbTcjeZzTA9GMZjDl8RxYzK0RquK+TQmeN4fj+F4KDoDEcSJPbjs+b4WAo5p4badIiaKYm7SJt0PQGaoRnDKbxGI7Zgv1lH6AS3H3teSg9Tu2+GKMyraAheBYV1VdKIzBANCMzyzMEWbuds-FHOJcKGQlzQPOgdosrygnhQ5eqMD9VQJpIMBX45+da7s-VABm4K11OArdQ9MDT6c-0QdLNQukT8Qk9DMczdrqPo8t-I4xttYEwqW872T3ZCXaF2F-UYsbrP-4L6nT8+C--OVJLn8v5LQGG8YAjGsj7HMKM6IMRLFcW+wkbYBBRJefw2BxQakUmiGAABxJUGgvatQjA0LBgcQ72CVJHEe1c96-zjvULuSdYSUXXkiJSaIcFlizpQ4iedU4FwnldLiO9K7cWrnFOenNkrDzbvIDuxouGJx7hqfug8pEiOjvfcedd6odVfglVqLDl6dXzu-YBINBFQEhrvTWlRIFo0WsfcslYz5bUvuYyxN8zoaKploq65dbqFyASw5+g5V58J8QyJk7C1AYjEW-Sop4YCSMjGObBuCx7eLnvUIhSoZTIByJ0XR9d9HzmyXMXJaICk-weIw+sGCchRNUKLcWgCTHoVKSgcp+SwFAwgQfKB+sRjtM6WATolt4GiX8BwAA7BEJwKAnAxETMEOAMkABs8BtyGCiTAIoB8U7AJ0h0Uh5Ct45xLGQuYAA5JUYzrYTMsCgMcEBNhOyQAkMADynkvIAFIQHFKkuYMRkigDVLsmi+zCFNGZHpHoFyUAUKrsREs2AEDAAeVAOAEAEDQDWHC3q0hbkiVtl4NFrz3kkvlIgGMsBgDYBRYQPIBQdn4PXoQv2Acg4h2MHAoAA
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
Handler -> Service: Logout(AuthToken)
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
Server -> Handler: authToken
Handler -> Service: ListGames(AuthToken)
Service -> DataAccess: authorize(authToken)
DataAccess -> db:Find AuthData by authToken
break Invalid AuthToken
db --> DataAccess: null
DataAccess --> Service: fail
Service --> Server: Unauthorized
Server --> Client: 401\n{"message": "Error: unauthorized"}
end
db --> DataAccess: AuthData
DataAccess --> Service: success
Service -> DataAccess: getGames()
DataAccess -> db: Find All Games
db --> DataAccess: [GameData list]
DataAccess --> Service: [GameData list]
Service --> Handler: ListGamesResult
Handler --> Server: {[GameData list]}
Server --> Client: 200\n{[GameData list]}
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