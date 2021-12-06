package com.example.virtual_itl;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ActivityRuta extends AppCompatActivity {

    private Spinner spinner1,spinner2;
    private ImageView imagenRuta,imagen1,imagen2;
    private Bitmap bitmap,bitmap2,bitmap3;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        consultaLugar();

        imagenRuta = (ImageView) findViewById(R.id.imageView);
        imagen1 = (ImageView) findViewById(R.id.imageView2);
        imagen2 = (ImageView) findViewById(R.id.imageView3);
        textView = (TextView) findViewById(R.id.textView);
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

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        llenarSpinner(lista,spinner1);
        llenarSpinner(lista,spinner2);
    }

    private void llenarSpinner(ArrayList<String> lista, Spinner spn) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,lista);
        spn.setAdapter(adapter);
    }

    public void click_boton(View view){
        mostrar_info();
    }

    private void mostrar_info() {
        String lugar1 = spinner1.getSelectedItem().toString(),
                lugar2 = spinner2.getSelectedItem().toString(),
                descripcion = "";
        String sql_img = "SELECT I.Imagen FROM Ilustracion I INNER JOIN Ruta R ON R.ID_Ilustracion = I.ID_Ilustracion INNER JOIN Lugar L ON L.ID_Lugar = R.ID_Lugar1 OR L.ID_Lugar = R.ID_Lugar2 INNER JOIN Lugar L2 ON L2.ID_Lugar = R.ID_Lugar1 OR L2.ID_Lugar = R.ID_Lugar2 WHERE (L.nombre_I LIKE ? AND L2.nombre_I LIKE ?) OR (L2.nombre_I LIKE ? AND L.nombre_I LIKE ?)";
        String sql_destino = "SELECT Imagen,descripcion FROM Ilustracion inner join Tiene on Tiene.ID_Ilustracion = Ilustracion.ID_Ilustracion inner join Lugar ON Tiene.ID_Lugar = Lugar.ID_Lugar where Lugar.nombre_I like ?";

        try {
            PreparedStatement st = conexion().prepareStatement(sql_img);
            st.setString(1,lugar1);
            st.setString(2,lugar2);
            st.setString(3,lugar2);
            st.setString(4,lugar1);
            ResultSet rs = st.executeQuery();
            rs.next();
            byte[] img = rs.getBytes("Imagen");

            st = conexion().prepareStatement(sql_destino);
            st.setString(1,lugar2);
            rs = st.executeQuery();
            ArrayList<byte[]> imagenes = new ArrayList<byte[]>();

            while (rs.next()){
                descripcion = rs.getString("descripcion");
                imagenes.add(rs.getBytes("Imagen"));
            }
            imagenRuta.setImageBitmap(convertirByteImg(img));
            imagen1.setImageBitmap(convertirByteImg(imagenes.get(0)));
            imagen2.setImageBitmap(convertirByteImg(imagenes.get(1)));
            textView.setText(descripcion);

        } catch (SQLException throwables) {
            Toast.makeText(getApplicationContext(),throwables.getMessage(),Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getApplicationContext(), "carga contenido", Toast.LENGTH_SHORT).show();
    }

    private Bitmap convertirByteImg(byte[] arreglo) {
        return BitmapFactory.decodeByteArray(arreglo , 0, arreglo.length);
    }
}