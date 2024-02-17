package hn.uth.pm1e17673.configuracion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import hn.uth.pm1e17673.transacciones.Transacciones;

public class SQLiteConexion extends SQLiteOpenHelper {

    public SQLiteConexion(Context context, String dbname, SQLiteDatabase.CursorFactory factory, int version){
        super(context,dbname,factory,version);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Lista de las tablas a crear
        sqLiteDatabase.execSQL(Transacciones.CreateTBContactos);
    }


    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Transacciones.DropTableContactos);
    }

}

