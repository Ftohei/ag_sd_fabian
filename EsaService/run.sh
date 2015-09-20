mvn clean && mvn install
mvn exec:java -Dexec.mainClass="de.citec.util.ImportNW"  -Dexec.args="file.xml"
