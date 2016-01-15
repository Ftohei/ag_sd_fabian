
package de.citec.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author swalter
 */
public class ImportResourceClassMapping {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException, IOException{
        Config config = new Config();
        DB_Connector connector = new DB_Connector(config);
        connector.connect();
        
        FileInputStream inputStream = new FileInputStream("resources/mapping_resources_classes.tsv");
        String everything = "";
        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }

        String[] lines = everything.split("\n");
        Map<String,List<String>> mapping = new HashMap<>();
        for(String line : lines){
            line = line.replace("\n","");
            String[] tmp = line.split("\t");
            List<String> tmp_list = new ArrayList<>();
            String resource = tmp[0];
            resource = resource.replaceFirst("res:", "");
            int counter = 0;
            for(String x: tmp){
                counter+=1;
                if(counter>1){
                    tmp_list.add(x);
                }
            }
            mapping.put(resource, tmp_list);
        }
        int counter =0;
        int error_counter = 0;
        Statement stmt;
        try (Connection connect = connector.connect()) {
            stmt = connect.createStatement(); 
            for(String key : mapping.keySet()){
                try{
                    stmt.executeUpdate("INSERT INTO resources SET "+
                        " res = '" + key.toLowerCase() + "'" +
                        ", resId = '" + Integer.toString(counter) + "';"
                    );
                    for(String value : mapping.get(key)){
                        stmt.executeUpdate("INSERT INTO resource_class_mapping SET "+
                            " id = '" + Integer.toString(counter) + "'" +
                            ", Classes = '" + value + "';"
                        );
                    }
                    counter+=1;
                }
                
                catch(Exception e){
                    error_counter+=1;
                }

            }
            stmt.close();         
        }
        System.out.println(counter);
        System.out.println(error_counter);
        
    }
}
