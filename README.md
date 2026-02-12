# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

Endpoint Sequence Diagrams:
[DQGsd0AJLSFN+VBV473LatYPyogwawMA2CvYQeQFK1iXnRJl0OU5LlucY-mYEAA))
](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMykoKp+h-Ds0KPMB4lUEiMAIEJ4oYjZwkEkSYCkm+hi7jS+4MkyU76XOnl3kuwpihKboynKZbvEqmAqsGGputqur6vpRgQGoMBoBAzBWmiN4BQ6Sa+s6XYZb2-ZuSB1T+mFaWqAAcllUZolGMZxoUWmFfAyCpjA6YAIwETmqh5vMJGFsW0D1D40yXtASAAF4oLsdFNsOBUWVZYVbhVHm8iO9SHnIKDPvEGJbfI-l7QVlTLgGa4Bper7Fe2OmloJjn-gggFmRhnWgS8hHlp8pHfBRVH1mNtHofCybddhMC4fhoyA1FxEg7Bl7g8hkNofRjHeH4-heCg6AxHEiTE6T72+FgomCv9jTSBG-ERu0EbdD0cmqApwxg4h6DQ9pP2lnzSGYK99PFfUDk0-ZQk005agudu7lUre-LeWAJ3npj-NoJdC4CkForik+j3aLK8qiwLsXqpKM16nNi0MWti4bS6pV9irlUSaWNVqA1YBNTkLUoLGCmC51olpk4A3I0NI00UWJZTQ78RO0tDZ467RvuyVJ1PZ2go5-UHAoNwx6XjrL7aAb+73sKMAZDMEA0A9NfyN7nWvfU1OnhkqgAeLws+zULyaVVMOVNHCN4VmK0MZ4BMBCi67+Ng4oavxaIwAA4kqGh0xZkm76zHP2EqvO62LHWVD3xrXwLEt59LaL7zm9lvwfiskl3av5fSGAjItZV2tvrPKV1Fw3UbiFM2HdgCWwfvBPWMVVTxTThnF26s3ZFU7N2MqXdj5+xnLVQOwcg7RjDm1SOsMShgBjnHbM-JE6Q2TpNBUGCoALUzgvCBhtR5WQLttZ6u1DavxyO-NQGI66BWgfUDIFhUBtxNAgPeSoPQ5wES6SRhdLLvjvsLcRYBJGqAHkPZ+RCXjLAvjmAsDRxg2JQAASWkAWPq4RgiBBBJseIuoUBuk5Hsb4yRQBqgCZBRY3xHF1SVJEi4nQJ6+0wnDehs8kYjGsQfOxDilQuLcR4rxywfF+PCcDMYwSEChNKfMSJIJomxPKfE3hS9mL+A4AAdjcE4FATgYgRmCHALiAA2eAE5DCSJgEUFJktfb1Ckh0c+l805YyzPUuYiTEwGNhP6MBqylQxPWcPbZ8JcF6JKoddEkiMQXJQJIn+ysdr-0gaOIBTJtZgJkddIU9RYHtyvBbCKYDUFxXthRTBfD65-Slp7cqIjLEbndKQxq1oQ6UPDvGDqtCer9UGsw-MrCJqlmmmCrhzss6rWwbnU5gjzad0eTISlB0xlXJ0bXCFsjvl3XXKyulcKtlfiZUeW5SozFfSOV+UeukxiOLyWNdxniNm-SxfDRGBEZWuLlQU5pTFCaWHLjZTYZMkAJDAHqvsEBDUACkIDijUXMGIISQBqimXQmZY9GhNGZDJHojir7IKQlmbAlS9VQDgBAGyUA1jqsVSch4xz6i7NGEG4AIaw0Rqjbk6QpljluqsgAKxtWgK5BbxR3JQISJWrkRFPLEa8kBZ4Pnsq+bdX5Qj5CIKBbbdBJLuFYIAVSjsZz8Fex2vC-29VkXNTRdQzF08UkMNxbmfF0E2FEs4b28lfbnlaPzrS4Af8GX9s1lcxx15NHGxgL8nlCD+jikcOq4FdsJkcFqhlLKMAco5CbTgwdVkewjr5YO-0OikUUKna1COs6up0JjuksYCdl3jRTrKEMMAUQVpyJu79A63L1GvQekuCpsBaEuUqDE17VLBsoGm6Aaw23AE+VAzlTiABmH6ZixksGsAwzAQAqIwvoGANgUBEBY6gdcIBw3QBgLa-DXa7WGBfelTK2UUVYHPS-GFhDTn+kDGaSwYYkKh3Re1SeySYO9QzIu4aiGxiroTVWc0MBaz1l4Rp6lHt8Ojv5XCeoJai0is+t9HNULZkwBGDG8zPVVWjG1cvImUAU1GpNV4JLiBgywGANgINhA8gFEmUfHTpYGhMxZmzDmxhI4+d0tmiVmmQDcDwPcgjjKYANYy9I7DDd5FHFbuM9RW5z1yKbr1tu9GusXubn1hFujBwfkMW1xrUBRXBbq-CiLNC50WZixtvGQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

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
