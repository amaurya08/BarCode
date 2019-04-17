package heartbeat.barcode.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDetails {
    public final static String TABLE_NAME = "userdata";
    public final static String UID = "uid";
    public final static String NAME = "name";
    public final static String HOUSE="house";
    public final static String STREET="street";
    public final static String LM="lm";
    public final static String LOC="loc";
    public final static String VTC="vtc";
    public final static String PO="po";
    public final static String SUBDIST="subdist";
    public final static String DIST="dist";
    public final static String STATE_NAME="statename";
    public final static String PINCODE="pincode";
    public final static String YOB = "yob";
    public final static String DOB = "dob";
    public final static String FATHER_NAME = "father";
    public final static String FILE_NAME = "filename";
    public final static String GENDEDR = "gender";

    private final static String my[] = {FILE_NAME};
    private final static String createTable = "CREATE TABLE `userdata` (\n" +
            "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
            "\t`uid`\tTEXT,\n" +
            "\t`name`\tTEXT,\n" +
            "\t`house`\tTEXT,\n" +
            "\t`street`\tTEXT,\n" +
            "\t`lm`\tTEXT,\n" +
            "\t`loc`\tTEXT,\n" +
            "\t`vtc`\tTEXT,\n" +
            "\t`po`\tTEXT,\n" +
            "\t`dist`\tTEXT,\n" +
            "\t`subdist`\tTEXT,\n" +
            "\t`statename`\tTEXT,\n" +
            "\t`pincode`\tTEXT,\n" +
            "\t`yob`\tTEXT,\n" +
            "\t`dob`\tTEXT,\n" +
            "\t`father`\tTEXT,\n" +
            "\t`filename`\tTEXT,\n" +
            "\t`gender`\tTEXT\n" +
            ");";
    public static void createTable(SQLiteDatabase db) {
        db.execSQL(createTable);
        Log.d("DATABASE", "Table Created");
    }

    public static long insert(SQLiteDatabase db, ContentValues cv) {
        return db.insert(UserDetails.TABLE_NAME, null, cv);
    }



    public static Cursor distinctSelection(SQLiteDatabase db, String col){
        String query="SELECT DISTINCT "+col+" FROM "+TABLE_NAME;
        return db.rawQuery(query,null);
    }


}
