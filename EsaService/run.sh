mvn clean && mvn install
mvn exec:java -Dexec.mainClass="de.citec.io.ImportNW"  -Dexec.args="file.xml"
