package hn.uth.pm1e17673;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;

import hn.uth.pm1e17673.configuracion.SQLiteConexion;
import hn.uth.pm1e17673.transacciones.Transacciones;

public class MostrarFoto extends AppCompatActivity {
    SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);

    ImageView picture;

    Button regresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_foto);
        picture = (ImageView) findViewById(R.id.imagen);
        regresar = (Button) findViewById(R.id.btn_atrass);
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(intent);
            }


        });
        Bitmap retornarFoto = buscarPicture(getIntent().getStringExtra("codigo"));
        picture.setImageBitmap(retornarFoto);

    }

    //Metodo Retornar
    public Bitmap buscarPicture(String id) {
        SQLiteDatabase db = conexion.getWritableDatabase();

        String sql = "SELECT imagen FROM contactos WHERE id ="+ id;
        Cursor cursor = db.rawQuery(sql, new String[] {});
        Bitmap bitmap = null;
        if(cursor.moveToFirst()){
            byte[] blob = cursor.getBlob(0);
            ByteArrayInputStream bais = new ByteArrayInputStream(blob);
            bitmap = BitmapFactory.decodeStream(bais);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return bitmap;
    }
}