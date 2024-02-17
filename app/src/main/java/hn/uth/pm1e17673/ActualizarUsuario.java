package hn.uth.pm1e17673;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hn.uth.pm1e17673.configuracion.SQLiteConexion;
import hn.uth.pm1e17673.transacciones.Transacciones;

public class ActualizarUsuario extends AppCompatActivity {
    SQLiteConexion conexion;
    //Hola
    EditText nombre, telefono, notas;
    private String id;
    Button regresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_usuario);

        conexion= new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);

        Intent i = getIntent();
        id  = getIntent().getExtras().getString("ID");
        String name = getIntent().getExtras().getString("Nombre");
        String fono  = getIntent().getExtras().getString("Telefono");
        String notes  = getIntent().getExtras().getString("Nota");
        Button btnUpdate = (Button) findViewById(R.id.btn_Actualizar);

        nombre = (EditText) findViewById(R.id.txtNombreActualizar);
        telefono = (EditText) findViewById(R.id.txtTelefonoActualizar);
        notas = (EditText) findViewById(R.id.txtNotas);

        nombre.setText(name);
        telefono.setText(fono);
        notas.setText(notes);
        regresar = (Button) findViewById(R.id.btn_AtrasA);
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(intent);
            }


        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar();
            }
        });
    }

    private void actualizar() {
        SQLiteDatabase db = conexion.getWritableDatabase();
        String []  params = {id};

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.nombre, nombre.getText().toString());
        valores.put(Transacciones.telefono, telefono.getText().toString());
        valores.put(Transacciones.nota, notas.getText().toString());

        db.update(Transacciones.tablacontactos, valores, Transacciones.id +"=?", params);
        Toast.makeText(getApplicationContext(),"Dato actualizados", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, ActivityListView.class);
        startActivity(i);
    }
}