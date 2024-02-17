package hn.uth.pm1e17673;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hn.uth.pm1e17673.configuracion.SQLiteConexion;
import hn.uth.pm1e17673.transacciones.Transacciones;

public class MainActivity extends AppCompatActivity {

    SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);
    SQLiteDatabase db;
    EditText nombre, telefono, nota;
    Spinner spninner;
    Button btnver;
    Bitmap imagen;

    static final int peticion_acceso_camara = 101;
    static final int peticion_toma_fotografia = 102;


    ImageView imageView;
    ImageButton btntakefoto;
    String currentPhotoPath;
    int codigoPaisSeleccionado;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre= (EditText) findViewById(R.id.nombres);
        telefono = (EditText) findViewById(R.id.txttelefono);
        nota = (EditText) findViewById(R.id.notas);
        imageView = (ImageView) findViewById(R.id.imageView);
        btntakefoto = (ImageButton) findViewById(R.id.takepicture);

        Button btnsalvar= (Button) findViewById(R.id.btnsalvar);


        spninner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new String[]{"Honduras (504)", "Costa Rica (506)", "Guatemala (502)", "El Salvador (503)"});

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spninner.setAdapter(adapter);

        btntakefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        btnver = (Button) findViewById(R.id.btnvercontactos);
        btnver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(intent);
            }
        });
        btnsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });



    }


    private void validarDatos() {
        if (nombre.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un nombre" ,Toast.LENGTH_LONG).show();
        }else if (telefono.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un telefono" ,Toast.LENGTH_LONG).show();
        }else if (nota.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir una nota" ,Toast.LENGTH_LONG).show();
        }else {
            salvarContacto(imagen);
        }
    }
    private void salvarContacto(Bitmap bitmap) {
        try {
            // Abre la conexión a la base de datos
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();

            if (bitmap != null) {
                // Convierte la imagen a un array de bytes
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] ArrayImagen = stream.toByteArray();

                // Obtiene los valores de los campos
                String Pais = spninner.getSelectedItem().toString();
                String PaisText = Pais.substring(0, Pais.length() - 6);
                String PaisNumber = Pais.substring(Pais.length() - 4, Pais.length() - 1);

                // Crea un objeto ContentValues y agrega los valores
                ContentValues valores = new ContentValues();
                valores.put(Transacciones.pais, PaisText);
                valores.put(Transacciones.nombre, nombre.getText().toString());
                valores.put(Transacciones.telefono, PaisNumber + telefono.getText().toString());
                valores.put(Transacciones.nota, nota.getText().toString());
                valores.put(Transacciones.imagen, ArrayImagen);

                // Inserta los valores en la base de datos
                long resultado = db.insert(Transacciones.tablacontactos, null, valores);

                // Cierra la base de datos
                db.close();

                if (resultado != -1) {
                    // Éxito al insertar el registro
                    Toast.makeText(getApplicationContext(), "Registro ingreso con éxito, Código " + resultado, Toast.LENGTH_LONG).show();
                    ClearScreen();
                } else {
                    // Si el resultado es -1, hubo un error al insertar el registro
                    Toast.makeText(getApplicationContext(), "Error al guardar el contacto", Toast.LENGTH_LONG).show();
                }
            } else {
                // Si la imagen es nula, muestra un mensaje de error
                Toast.makeText(getApplicationContext(), "La imagen es nula. Asegúrate de tomar una foto antes de guardar el contacto.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            // Captura y muestra cualquier excepción que se produzca
            Toast.makeText(getApplicationContext(), "Error al guardar el contacto: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void ClearScreen() {
        spninner.setSelection(0);
        nombre.setText("");
        telefono.setText("");
        nota.setText("");;
    }

    private void permisos() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, peticion_acceso_camara );

        }
        else {
            //TomarFoto();
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == peticion_acceso_camara ){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //TomarFoto();
                dispatchTakePictureIntent();
            }
            else {
                Toast.makeText(getApplicationContext(), "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void TomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!= null) {
            startActivityForResult(intent, peticion_toma_fotografia);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(

                imageFileName,  /* prefix */

                ".jpg",         /* suffix */

                storageDir      /* directory */

        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "hn.uth.pm1e17673.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, peticion_toma_fotografia);
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == peticion_toma_fotografia && resultCode == RESULT_OK) {
            try {
                File foto = new File(currentPhotoPath);
                Bitmap image = BitmapFactory.decodeFile(foto.getAbsolutePath());
                imageView.setImageBitmap(image);
                imagen = image;  // Asegúrate de asignar la imagen a la variable 'imagen'
            } catch (Exception ex) {
                ex.toString();
            }
        }
    }
}