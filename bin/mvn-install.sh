#!/bin/sh

sh ../mvnw clean install -f ../pom.xml -DskipTests $*
