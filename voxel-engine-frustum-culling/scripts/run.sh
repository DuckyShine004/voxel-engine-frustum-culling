#!/bin/bash

mvn clean install

mvn exec:java -Dexec.mainClass="com.duckyshine.app.Main"
