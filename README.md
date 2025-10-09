# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

Phase 2 Sequence Diagram: [sequencediagram.org](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAA5M9qBACu2AMQALADMbgBMAJwgMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNMY7vcebT+fUpVAeZhHXz1PbKoiVC6Bu6-YZSgjqs8YApTShgOb2ur0ABRS0qbAEApk6iUH3wZDmeqBJzBYbjObqYCMhbLRNQbx1A1TJXG6Ox+XyNXoKFmTiaHQ5z101T1NA+BAID3aJ3e0O+8n+mAjseZqgUh2Tr1DkCq9Gu9mB7Tc9eDwXGWoKDgcTXS7TL1dUo-Orcx9EKHx6jHAN-xHmHmkbk-Cuel6vnqQZziG9wwk89bYmieJqKOWBQXCOY3BGryGkqSz1PsIKfnq7QQB2aDYcslzLtmM65iUYD1GEThOCWEyYZ8MA4cCyz4fEhHEaR+xXOgHCmF4vgBNA7CMjEIpwIm0hwAoMAADIQFkhTFOYgpofWzRtF0vQGOo+RoCWipzGsvz-BwVxaYKyHoaZKDmfofw7NCjwoWGIjzspqkYj54oEkSYCksGgoDs6MCMiybIOb+vLHvcp5ihKroynKDbvEqmAqjGGqutqur6g5RgQGoC4QMwVpor2-YPvSC6juO4XTp5K7zou46hVRWn1AAkmgIDQCi4CupyyapumhQ2VR6kYAWTgAIxMWWqgVvMOE1nW9TFRwpUCmgFUwFVORdoJNVUc1Q5Xm6N6Xah4EBte8jZaqGrioNtYoOAMAWTst4qPdWb1sy0xftASAAF4oBw40oGmRkUfC9yzbRMCFktoxjCta1VmMm3QNtoN6uDUO7AJPZdfef6DrUT5yCgIHxB+X4-hO1P8gBZ4XpGLM3l1kHuRG-loBkqiIZgdlI7OQMvMxjasex3xcTxnZsWRXbTcjeZzTA9GMZjDl8RxYzK0RquK+TQmeN4fj+F4KDoDEcSJPbjs+b4WAo5p4badIiaKYm7SJt0PQGaoRnDKbxGI7Zgv1lH6AS3H3teSg9Tu2+GKMyraAheBYV1VdKIzBANCMzyzMEWbuds-FHOJcKGQlzQPOgdosrygnhQ5eqMD9VQJpIMBX45+da7s-VABm4K11OArdQ9MDT6c-0QdLNQukT8Qk9DsPwxmmuVCj80Y6W-I4xttYEwqW872T3ZCXaF2F-UYsbrP-4L6nr8+O--OVJLH+79JaAw3jAEY1kfY5mPrrBiJYrgP2EjbAIKJLz+GwOKDUik0QwAAOJKg0F7VqEYGi4MDiHewSpI4j2rjHAWsIIxdyTgw+E68kRKTRPgssWcaHETzqnAuE8rpcR3pXbi1c4pz05slYebd5Ad2NLwxOPcNT90HrI8R0cn7jzrvVDqH8EqtXYcvTq+cv5gJBiIqAkNd4pjhpNOhWsaIn2WufSsl8to3ysTY++Z1tFU10Vdcut1C6gPYW-Qcq9BGBIZEyLhagMSSM-pUU8MAZGRjHHgghY8Alz3qKQpUMpkA5E6AY+uRj5wFLmEUtEpT-4PBYenThBDRbixAeY9CVSUA1JKZAoG0Dtaoz1iWLpPSwCdEtkg0S-gOAAHYIhOBQE4GIiZghwBkgANngNuQw8SYBFEGSnMBOkOgUKoVvHOJZKFzAAHJKj6bceh0F6hMPaWw+ccAdnxIxJ858KB4mBTUMFKJOi8mKOJtYqGYic5JISik4U6TgnyPSkwlRfc0AD2QBo0e-iZAvwakuO65jjEz3qT1SUt9IW2ImgjQ+1F8xo0Wq48s7jqxX3rD4SlPjTo9lxZdeoSLgBlJau8tOMAIn8hBbkjc9Q6bom+dclAdy5iwvrvC+ovz6ZZLmAVPUkUlTKrEHy-F8T3REoqWKxVvVpAgqeXCDVXylStIQEhZOHT6wjCtdIeoC0wjBECA8yiTiGXDNGF6n1fqA2IOttMywKAxwQE2E7JACQwBxoTUmgAUhAcU2rDD+GSKANUByaJHJIU0ZkekeiKuoVXYiJZsAIGAHGqAcAIAIGgGsL1gbWENOeeCzR6AG1NpbW2jtUAu1KmtW5FhRz2HZvFN8hdaAAUoEJEC-hbUQzSppgO0RXdVXTnVYi3myLO5KO7q9dFmKh6t0HYUY1QjhyNWFfPC19QTG2vXhGSxYMqUwzsfvKaUCZqDJcZjbGrK8bssJt40mPLH59mfk+u9ZrQnEvnBK9QUq8Uoaigqqd0hD3z3VWk8Ul49npS9S9XKMBxQ3svNRx9MSCVNXQ++1jX6qgyzzdIPaNyKrJjRHvBxdKYGFmLBBtx602WeMVSVMqB1KrWhOpM5jYLTUhKEWE+cmnnposVT0Qa47mxmjbAmYDAj3X1HifGYiInaUgeDTrCTPQpMspk9BzxJozMwHbJ2SZlNcMsb8FoeVSoMRerWLFV9nNJTYDC7spsVHCPDB1HqhyXHAHit-pE9pxCPU9oGc42B+sIHRpErbLwzbk2puq-KRAMZYDAGwI2wgeQCj7KId+32-tA7B16MYOhAC471GYdBOd84QDcDwIknD-KYDTaa3Nol6qm6l0MCgGolg9TinsDk4LYKsMaHNaKoBkT6nZeO+NjyBWXhFdAyV0N5WzpAA)

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
