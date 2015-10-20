#! /bin/bash

mvn clean && mvn install
for d in /Users/swalter/Downloads/xmlNW/* ; do
mvn exec:java -Dexec.mainClass="de.citec.io.ImportNW"  -Dexec.args="$d"

done