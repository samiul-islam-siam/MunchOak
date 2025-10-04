#!/bin/sh

sh ../mvnw clean package -f ../pom.xml -DskipTests $*
