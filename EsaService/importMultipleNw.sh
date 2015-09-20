#!/bin/sh

START_MONTH = "$1"
END_MONTH = "$2START_MONTH = "$1"
END_MONTH = "$2""

mvn clean && mvn install

TAR_FILE=""
XML_FILE=""
FOLDER=""

for i in {6..8}
do
        for j in {1..31}
        do
                if [ $j -lt 10 ]
                then
                        c=0${j}0$i
			TAR_FILE="8586833-Kogni-${c}2015.tar.gz"
			XML_FILE="8586833-Kogni-${c}2015.xml"
			FOLDER="8586833-Kogni-${c}2015"
                else
                        c="${j}0$i"
			TAR_FILE="8586833-Kogni-${c}2015.tar.gz"
			XML_FILE="8586833-Kogni-${c}2015.xml"
			FOLDER="8586833-Kogni-${c}2015"
                fi
		
		echo "Ausgabe $XML_FILE wird geladen"
	
		wget sc-kognihome.techfak.uni-bielefeld.de/nw_resources/old/$TAR_FILE
		
		echo "Ausgabe $XML_FILE wird entpackt"
		tar -xzf $TAR_FILE
		
		mv "./${FOLDER}/$XML_FILE" "./$XML_FILE"
		rm -rf $FOLDER

		echo "Ausgabe $XML_FILE wird importiert"
		mvn exec:java -Dexec.mainClass="de.citec.util.ImportNW" -Dexec.args="$XML_FILE"

		rm $XML_FILE		
		rm $TAR_FILE

        done
done

echo "Erfolgreich alle Ausgaben importiert!"

mvn clean
