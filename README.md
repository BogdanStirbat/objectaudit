# Diff tool

This tool detects object updates. The object to be inspected for updates is a POJO.

All fields of the object are checked for updates. Fields can be nested, or be lists. 
The tool checks for updates in nested objects and lists. 

## Project structure

The project is a typical Maven project. It was written in Java 17.

## Running the projects

There are two ways the project can be run:
- by running `mvn clean install` in the command line (will run the unit tests)
- by running the individual test classes in the IDE

It took me 10 hours to complete this project.