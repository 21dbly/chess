# My Notes

## OOP (in java)
- everything extends from Object
- each file must have exactly one highest level class
- any file/class can have up to one entry point ("main" method)
- overloading constructors:
  - default, parameterized, copy
  - `this(copy.value)`
- enums are classes too
- encapsulation: hiding the details. Only allow access if it's needed
- extend means inherit
- equals operator (==) always compares memory location. So basically only use it with primitive types
- .equals method for comparing classes

### Records
- for immutable objects like data
- automatically overrides equals, hashcode, toString, and getters?
- can still add more functions

## immutability
- safer

## abstraction

### interfaces
- makes it easy to change the implementation

### inheritance

### abstract classes

## JDK Collections
- jdk contains the standard util library that you can look through

## Exceptions
```
try {
    // Code that may throw an exception
} catch (Exception ex) {
    // Code to execute if there's an exception
} finally {
    // Code that always gets called
}
```
Try with finally built in:
```
// automatically closes closable thing if there's an error in the try
try (FileInputStream input = new FileInputStream("test.txt")) {
    
}
```
## Domain Driven Design
- Object Oriented Design
  - properties/fields: things they are/have
  - methods: things they do

- Focus on the domain the objects are in to know what to represent
- Sequence Diagram

- Relationships:
  - is-a: usually inheritance, extending
  - has-a: usually encapsulation, field
  - uses-a: transient association, method parameter

- Decomposition: break into pieces
  - we can't keep big things in our head. Just the small things
  - decompose based on domain

- Simplicity
  - should be as simple as possible, but not simpler - Einstein

- YAGNI: You're not going to need it
  - always implement things when you actually need them, never when you just foresee that you need them.
  - don't pre-optimize

- DRY: don't repeat yourself

- High cohesion low coupling
  - cohesion: object has a clear single purpose for the domain
  - coupling: objects depend on other objects

### Solid

S ingle responsibility - a module should be responsible to one, and only one, actor\
O pen closed - open to extension, closed to modification\
L iskov substitution - keep the expectations of an interface\
I nterface segregation - don't force clients to depend on methods they don't need\
D ependency inversion - High-level modules should not depend on low-level modules. Both should depend on abstractions

### Pola: Principle of least astonishment
- if your code would make someone astonished, it violates Pola

## Inner classes

- Static inner classes
- non-static inner classes
- local inner classes
  - closure: close around surrounding creation space
  - factories: like constructors but more flexible
- anonymous classes
```java
var instance = new Interface() {
    public String method() {
        return "stuff";
    }
};
```
- lambda functions: concise anonymous closures
  - interface has only one method defined: functional interface
```
interface Speaker {
    String sayHello();
}

speak(() -> "hello");
```

## IO
`System.in.read()`
`System.out.println()`

## Generics
`ArrayList<Integer>`

## HTTP
doing verbs on resources
- GET: Get an existing resource (no body)
- POST: Create a new resource
- PUT: Update an existing resource
- DELETE: Delete a resource
- OPTIONS: Get information about a resource

### Status codes:
2xx: 200 Success, 204 No Content
3xx: 301/302 redirect, 304 not modified
4xx: 400 bad request
     404 not found, 403 forbidden
     429 Too many requests
5xx: 500 server error, 503 not available

### Javalin
- java web framework

## Relational Databases
columns: fields
rows: objects

relate multiple tables with minimum coupling and maximum cohesion using keys
- primary keys (this table)
- secondary keys / foreign keys
- keys: unique, stable, simple

RDBMS: Relational Database Management System
SQL: Structured Query language
MySQL: RDBMS implementation
JDBC: Java Database JDK library

### SQL
DDL: Data Definition Language
- CREATE
DML: Data Manipulation Language
DQL: Data Query Language
- Select

example:
```
CREATE TABLE pet (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO pet (name, type) VALUES ('Puddles', 'cat');
```

shorttext, mediumtext, longtext, blob

## JDBC
Java Database Connector

```
try (var preparedStatement = conn.preparedStatement("SELECT * from table WHERE id = ?")) {
    try (var resultSet = preparedStatement.execute()) {
    }
}
```

## CLI
(Command Line Interface)
- Repl: Read, Evaluate, Print, Loop

- Colors:
  `echo -e "\u001b[31;44;1m red on blue"`