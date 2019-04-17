package heartbeat.barcode.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBClass extends SQLiteOpenHelper {
    private Context context;
    public DBClass(Context context) {

        super(context, "BarCode.db", null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        UserDetails.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    @Override
    public SQLiteDatabase getReadableDatabase() {

        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {

        return super.getWritableDatabase();
    }
}
