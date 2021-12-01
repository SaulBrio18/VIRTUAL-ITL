package com.example.virtual_itl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class addImage extends AppCompatActivity {

    private Spinner spinner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        consultaLugar();
    }

    public Connection conexion(){
        Connection con = null;

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            //con = DriverManager.getConnection("jdbc:jtds:sqlserver://DESKTOP-LKJB0LG;databaseName=VIRTUAL-ITL;user=sa;password=Nalgamessi01;");
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.104;databaseName=VIRTUAL-ITL;user=sa;password=Nalgamessi01;");
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return con;
    }

    private void consultaLugar(){
        ArrayList<String> lista = new ArrayList<String>();
        try {
            String SQL = "SELECT * FROM Lugar";
            Statement stmt = conexion().createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                lista.add(rs.getString(2));
            }

            //rs.close();
            //stmt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        llenarSpinner(lista);
    }

    private void llenarSpinner(ArrayList<String> lista) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,lista);
        spinner1.setAdapter(adapter);
    }
}