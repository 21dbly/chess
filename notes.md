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