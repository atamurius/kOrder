#!/usr/bin/sh
JAR="target/kOrder-1.0-SNAPSHOT.jar"
INSTR="/home/atamurius/.m2/repository/org/springframework/spring-instrument/4.3.4.RELEASE/spring-instrument-4.3.4.RELEASE.jar"
java -javaagent:${INSTR} -jar ${JAR}
