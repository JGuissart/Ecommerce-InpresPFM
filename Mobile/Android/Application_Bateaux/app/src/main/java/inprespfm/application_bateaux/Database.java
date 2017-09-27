package inprespfm.application_bateaux;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adrie on 30-10-15.
 */
public class Database extends SQLiteOpenHelper {

    public static final String MOUVEMENT_CREATE = "CREATE TABLE MOUVEMENTS(IdContainer TEXT, Destination TEXT);";
    public static final String MOUVEMENT_DROP = "DROP TABLE IF EXISTS MOUVEMENT";

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(MOUVEMENT_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(MOUVEMENT_DROP);
        onCreate(db);
    }
}
