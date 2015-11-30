/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
CREATE TABLE IF NOT EXISTS wikipedia (
  id int(10) unsigned NOT NULL PRIMARY KEY,
  person int(10) unsigned NOT NULL,
  title varchar(200) NOT NULL,
  body text NOT NULL,
  FULLTEXT (body)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/
/**
 *
 * @author swalter
 */
public class ImportWikipediaToDB {
    
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        
        Config config = new Config();
        DB_Connector connector = new DB_Connector(config);
        connector.connect();
        
        File fileDir = new File("/Users/swalter/Documents/EsaDeutsch/new_copus_german.txt");

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
        String str;
        String query = "INSERT INTO wikipedia (id,person,title,body) VALUES (?,?,?,?);";
        PreparedStatement ps = connector.getConn().prepareStatement(query);
        final int batchSize = 1000;
        int count = 0;
        while ((str = in.readLine()) != null) {
            if(++count % batchSize == 0) {
                ps.executeBatch();
                ps.clearBatch();
            }
            updateDatabase(str,connector,ps);
        }

        in.close();
        // execute the preparedstatement
        ps.executeBatch();
        ps.close();

        connector.closeConnection();
    }

    private static void updateDatabase(String line, DB_Connector connector,PreparedStatement ps) throws SQLException {
        /*
        0=person
        1=id
        2=Titel
        3=body
        */
        if(line.contains("######")){
            String[] tmp = line.split("######");
            int person = Integer.valueOf(tmp[0]);
            int id = Integer.valueOf(tmp[1]);
            String title = tmp[2];
            if(title.contains(" ")){
                 String[] title_tmp = title.split(" ");
                 title = "";
                 for(String s:title_tmp){
                     if(s.contains("_")) title+=" "+s.split("_")[0];
                     else title+=" "+s;
                 }
            }
            title = title.replace("-RRB-_TRUNC","");
            title = title.replace("-LRB-_TRUNC","");
            title = title.replace("-RRB-", "");
            title = title.replace("-LRB-", "");
            title = title.replace("._$.","");
            title = title.replace(" ._$.", "");
            title = title.replace("_$.", "");
            title = title.replace("/_$[", "");
            title = title.replace("-_$[", "");
            title = title.replace("_$[", "");
            title = title.replace(" 's", "s");
            title = title.replace("' ", "");
            title = title.replace("'", " ");
            title = title.replace("  ", " ");
            title = title.trim();
            String body = tmp[3];
            while (body.contains("  ")) body = body.replace("  ", " ");
            body = body.trim();

            ps.setInt(1, id);
            ps.setInt(2, person);
            ps.setString(3, title);
            ps.setNString(4, body);
            ps.addBatch();

    }
    }
}
