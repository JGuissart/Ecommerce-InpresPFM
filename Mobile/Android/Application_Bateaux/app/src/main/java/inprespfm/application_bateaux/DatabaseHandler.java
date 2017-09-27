package inprespfm.application_bateaux;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Julien on 19-11-15.
 */
public class DatabaseHandler extends SQLiteOpenHelper
{
    public static final String CREATE_TABLE_MOUVEMENT = "CREATE TABLE MOUVEMENTS (IdContainer TEXT, TypeMouvement TEXT, DateMouvement TEXT, TempsMouvement INTERGER, Docker TEXT, Destination TEXT);";
    //public static final String BATEAU_TABLE_CREATE = "CREATE TABLE BATEAU (IdBateau TEXT, Destination TEXT, Docker TEXT);";
    public  static final String DROP_TABLE_MOUVEMENT = "DROP TABLE IF EXISTS MOUVEMENTS;";
    //public  static final String BATEAU_TABLE_DROP = "DROP TABLE IF EXISTS BATEAU;";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MOUVEMENT);
        //db.execSQL(BATEAU_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_MOUVEMENT);
        //db.execSQL(BATEAU_TABLE_DROP);
        onCreate(db);
    }
}