package org.skdrdpindia.cashcollectionapp.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by harsh on 6/1/2015.
 */
public class GroupsDbHelper extends SQLiteOpenHelper {
    static final String GROUPS_DB_NAME = "Groups.db";
    private static final int DB_VERSION = 1;


    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    public GroupsDbHelper(Context context) {
        super(context, GROUPS_DB_NAME, null, DB_VERSION);
    }

    // Query Strings
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMBER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String GROUP_DB_CREATE_QUERY =
            "CREATE TABLE " + GroupsContract.GroupsInfo.TABLE_NAME + " ("
                    + GroupsContract.GroupsInfo._ID + " INTEGER PRIMARY KEY" + COMMA_SEP
                    + GroupsContract.GroupsInfo.GROUP_ID + NUMBER_TYPE + COMMA_SEP
                    + GroupsContract.GroupsInfo.GROUP_NAME + TEXT_TYPE + COMMA_SEP
                    + GroupsContract.GroupsInfo.MOBILE_1 + TEXT_TYPE + COMMA_SEP
                    + GroupsContract.GroupsInfo.MOBILE_2 + TEXT_TYPE + COMMA_SEP
                    + GroupsContract.GroupsInfo.MOBILE_3 + TEXT_TYPE + COMMA_SEP
                    + GroupsContract.GroupsInfo.IS_SHOWN + NUMBER_TYPE + " )";
    private static final String GROUP_DB_DELETE_QUERY =
            "DROP TABLE IF EXISTS " + GroupsContract.GroupsInfo.TABLE_NAME;

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(GROUP_DB_DELETE_QUERY);
            db.execSQL(GROUP_DB_CREATE_QUERY);
        } catch (SQLException e) {
            Log.e("SKDRDP DB", "Error opening group DB");
        }

    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(GROUP_DB_DELETE_QUERY);
        onCreate(db);
    }
}
