package com.faceop.faceop.db;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Agape on 26/12/2016.
 * FaceOP Sign in
 * TimePickerFragment is for selecting the meeting time for a match.
 */

public class MySQLConnector {

    private static final String LINK = "213.171.200.91";
    private static final String username = "overflow777";
    private static final String password = "raaWASm1e5";

    public void dbtest() {


        Connection conn = null;
        try {
            Log.d("SQL", "before-con");
            //DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn =
                    DriverManager.getConnection("jdbc:mysql://213.171.200.91:3036/FaceOP", "overflow777", "raaWASm1e5");

            // Do something with the Connection
            //conn.createBlob();
            // prep statement etc.
            Log.d("SQL-A", "after-con");


        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT faceop FROM test");

            // or alternatively, if you don't know ahead of time that
            // the query will be a SELECT...

          /*  if (stmt.execute("SELECT foo FROM bar")) {
                rs = stmt.getResultSet();
            }*/

            // Now do something with the ResultSet ....
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    Blob blob = rs.getBlob(0);
                    long l = blob.length();
                    Log.d("blob", "blob-l=" + l);
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }

        }
    }

    public String postSQL() throws IOException {
        URL url = new URL(LINK);
        String data = URLEncoder.encode("username", "UTF-8")
                + "=" + URLEncoder.encode(username, "UTF-8");
        data += "&" + URLEncoder.encode("password", "UTF-8")
                + "=" + URLEncoder.encode(password, "UTF-8");
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

        wr.write(data);
        wr.flush();

        BufferedReader reader = new BufferedReader(new
                InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line = null;

        // Read Server Response
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            break;
        }

        return sb.toString();
    }

    public String getSQL() throws URISyntaxException, IOException {
        URL url = new URL(LINK);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(new URI(LINK));

        HttpResponse response = client.execute(request);
        BufferedReader in = new BufferedReader(new
                InputStreamReader(response.getEntity().getContent()));

        StringBuffer sb = new StringBuffer("");
        String line = "";

        while ((line = in.readLine()) != null) {
            sb.append(line);
            break;
        }

        in.close();

        return sb.toString();
    }
}
