package com.example.virtual_itl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class add_image2 extends AppCompatActivity {

    private Spinner spinner1;
    private ImageView imagen;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image2);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        imagen = (ImageView) findViewById(R.id.imageView);
        consultaLugar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri path = data.getData();
            imagen.setImageURI(path);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), path);
                imagen.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Connection conexion() {
        Connection con = null;

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            //con = DriverManager.getConnection("jdbc:jtds:sqlserver://DESKTOP-LKJB0LG;databaseName=VIRTUAL-ITL;user=sa;password=Nalgamessi01;");
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.65;databaseName=VIRTUAL-ITL;user=sa;password=Nalgamessi01;");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return con;
    }

    public void onclick(View view) {
        cargarImagen();
    }

    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la Aplicacion"), 10);
    }

    public void escuchaSpinner(View view) {
        enviarDatos();
    }

    private void enviarDatos() {
        String lugar1 = spinner1.getSelectedItem().toString();
        byte[] bimagen = convertirImgToString(bitmap);
        PreparedStatement stmt;
        ResultSet rs;

        try {
            //insertamos la imagen en su tabla
            String insert = "INSERT INTO Ilustracion(nombre_I, Imagen) VALUES(?,?)";
            stmt = conexion().prepareStatement(insert);
            stmt.setString(1, lugar1);
            stmt.setBytes(2, bimagen);
            stmt.execute();

            //Consutamos el id de la imagen
            String sql_img = "SELECT MAX(ID_Ilustracion) AS ID_Ilustracion FROM Ilustracion WHERE nombre_I like ? ";
            stmt = conexion().prepareStatement(sql_img);
            stmt.setString(1, lugar1);
            rs = stmt.executeQuery();

            rs.next();
            int idImg = rs.getInt("ID_Ilustracion");

            //Consutamos los ids de los lugares seleccionados
            String sql_lugares = "SELECT Top 1 ID_Lugar FROM Lugar WHERE nombre_I like ?";
            stmt = conexion().prepareStatement(sql_lugares);
            stmt.setString(1, lugar1);
            rs = stmt.executeQuery();
            int[] ids = new int[2];
            int aux = 0;
            String a = "";
            while (rs.next()) {
                ids[++aux] = rs.getInt("ID_Lugar");
            }
            //Ahora insertamos la relacion de los dos lugares en su tabla
            insert = "INSERT INTO Tiene(ID_Lugar,ID_Ilustracion) VALUES(?,?)";
            stmt = conexion().prepareStatement(insert);
            stmt.setInt(1, ids[1]);
            stmt.setInt(2, idImg);
            stmt.execute();

            Toast.makeText(getApplicationContext(), "Proceso terminado", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] convertirImgToString(Bitmap bitmap) {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imgByte = array.toByteArray();
        return imgByte;
    }

    private void consultaLugar() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        llenarSpinner(lista, spinner1);
    }

    private void llenarSpinner(ArrayList<String> lista, Spinner spn) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, lista);
        spn.setAdapter(adapter);
    }

}