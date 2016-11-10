package org.apache.atlas.performance.tools.hive;


import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.apache.jmeter.protocol.http.util.Base64Encoder;
import com.jayway.jsonpath.JsonPath;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.Date;

public class HiveUtil {


    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void createTables(File file, Integer tableCount) throws ClassNotFoundException, SQLException, IOException, InterruptedException {

        //String connectionString=String.format("jdbc:hive2://%s:10000", PropertiesFileReader.getDomain());
        Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000", "", "");
        Statement stmt = con.createStatement();
        String hql = "";
        String ddlCommands = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));

        while ((ddlCommands = reader.readLine()) != null) {
            hql = hql + ddlCommands + "\n";
            stmt.execute(ddlCommands);
        }
        Thread.sleep(500);

        Class.forName(driverName);
        ResultSet rs = stmt.executeQuery("show tables");
       /* while(rs.next()){
            System.out.println(rs.getString(1));
        }*/

        Long curtime = new Date().getTime();

        Long etime = curtime + 60000;

        String encoding = Base64Encoder.encode("admin:admin");
/*        String gremlinQuery = String.format("http://%s:21000/api/atlas/discovery/search/gremlin?query=g.V%28%22__typeName%22%2C++%22hive_table%22%29.count%28%29", PropertiesFileReader.getDomain());
        URL url = new URL(gremlinQuery);
        HttpURLConnection connection = null;
        while (curtime <= etime) {
            curtime = new Date().getTime();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            InputStream content = connection.getInputStream();
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(content));
            String json = in.readLine();
            System.out.println(json);
            Integer count = Integer.parseInt((String) JsonPath.read(json, "$.results[0].result"));
            if (count == tableCount) {
                break;
            } else {
                System.out.println("Sleeping for 5 seconds");
                Thread.sleep(5000);
            }
        }*/


    }

}


