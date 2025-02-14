#!/bin/bash

mvn clean compile package

java -jar target/greedy-meshing.jar
