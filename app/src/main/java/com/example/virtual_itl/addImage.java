package com.example.virtual_itl;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;

public class addImage extends AppCompatActivity {

    private Spinner spinner1, spinner2;
    ImageView imagen;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        consultaLugar();
        imagen = (ImageView) findViewById(R.id.imageView);
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
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.104;databaseName=VIRTUAL-ITL;user=sa;password=Nalgamessi01;");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return con;
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
        llenarSpinner(lista, spinner2);
    }

    private void llenarSpinner(ArrayList<String> lista, Spinner spn) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, lista);
        spn.setAdapter(adapter);
    }

    public void escuchaSpinner(View view) {
        enviarImagen();
    }

    private void enviarImagen() {
//        String imagen = convertirImgToString(bitmap);
        String
                lugar1 = spinner1.getSelectedItem().toString(),
                lugar2 = spinner2.getSelectedItem().toString();

        if (lugar1.equals(lugar2)) {
            Toast.makeText(getApplicationContext(), "El lugar de origen y el lugar de destino no puede ser el mismo", Toast.LENGTH_SHORT).show();
        } else {
            try {
                String SQL = "SELECT TOP 1 Ruta.ID_Ilustracion FROM Ruta INNER JOIN Lugar AS Lugar ON Ruta.ID_Lugar1 = Lugar.ID_Lugar OR Ruta.ID_Lugar2 = Lugar.ID_Lugar INNER JOIN Lugar AS Lugar2 ON Ruta.ID_Lugar2 = Lugar2.ID_Lugar OR Ruta.ID_Lugar1 = Lugar2.ID_Lugar WHERE (Lugar.nombre_I like ? AND Lugar2.nombre_I like ?) OR (Lugar2.nombre_I like ? AND Lugar.nombre_I like ?)";

                PreparedStatement stmt = null;
                stmt = conexion().prepareStatement(SQL);
                stmt.setString(1, lugar1);
                stmt.setString(2, lugar2);
                stmt.setString(3, lugar2);
                stmt.setString(4, lugar1);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) { //Si ya hay un registro entonces se actualiza
                    String insert = "UPDATE Ilustracion SET nombre_I = ? WHERE ID_Ilustracion = ?";
                    stmt = conexion().prepareStatement(insert);
                    stmt.setString(1, lugar1+"-"+lugar2);
                    stmt.setString(2, rs.getString("ID_Ilustracion"));
                    stmt.executeUpdate();
                    Toast.makeText(getApplicationContext(), "Proceso terminado", Toast.LENGTH_SHORT).show();
                } else { //Si no hay un registro entonces insertamos uno nuevo
                    //insertamos la imagen en su tabla
                    String insert = "INSERT INTO Ilustracion(nombre_I) VALUES(?)";
                    stmt = conexion().prepareStatement(insert);
                    stmt.setString(1, lugar1+"-"+lugar2);
                    stmt.execute();

                    //Consutamos el id de la imagen
                    String sql_img = "SELECT MAX(ID_Ilustracion) AS ID_Ilustracion FROM Ilustracion WHERE nombre_I like ? ";
                    stmt = conexion().prepareStatement(sql_img);
                    stmt.setString(1, lugar1+"-"+lugar2);
                    rs = stmt.executeQuery();

                    rs.next();
                    int idImg = rs.getInt("ID_Ilustracion");

                    //Consutamos los ids de los lugares seleccionados
                    String sql_lugares = "SELECT ID_Lugar FROM Lugar WHERE nombre_I like ? OR nombre_I like ?";
                    stmt = conexion().prepareStatement(sql_lugares);
                    stmt.setString(1, lugar1);
                    stmt.setString(2, lugar2);
                    rs = stmt.executeQuery();
                    int [] ids = new int[3];
                    int aux = 0;
                    String a = "";
                    while (rs.next()) {
                       ids[++aux] = rs.getInt("ID_Lugar");
                    }
                    Toast.makeText(getApplicationContext(), "Proceso terminado", Toast.LENGTH_SHORT).show();
                    //Ahora insertamos la relacion de los dos lugares en su tabla
                    insert = "INSERT INTO Ruta(ID_Lugar1,ID_Lugar2,ID_Ilustracion) VALUES(?,?,?)";
                    stmt = conexion().prepareStatement(insert);
                    stmt.setInt(1, ids[1]);
                    stmt.setInt(2, ids[2]);
                    stmt.setInt(3, idImg);
                    stmt.execute();

                    /*

                    Toast.makeText(getApplicationContext(), "insert ruta", Toast.LENGTH_SHORT).show();
                    */
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String convertirImgToString(Bitmap bitmap) {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imgByte = array.toByteArray();
        //String imagen = Base64.encodeToString(imgByte,Base64.DEFAULT);
        String retIMG = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            retIMG = Base64.getEncoder().encodeToString(imgByte);
        }
        return retIMG;
    }

    public void onclick(View view) {
        cargarImagen();
    }

    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la Aplicacion"), 10);
    }
}