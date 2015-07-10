package org.skdrdpindia.cashcollectionapp.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Members Table housed in its own database.
 * Created by harsh on 6/1/2015.
 */
public class MembersDbHelper extends SQLiteOpenHelper {
    static final String MEMBERS_DB_NAME = "membersDb";
    private static final int DB_VERSION = 1;


    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    public MembersDbHelper(Context context) {
        super(context, MEMBERS_DB_NAME, null, DB_VERSION);
        Log.d("Member DB Helper", "Constructor:" + MEMBERS_DB_NAME);
    }

    // Query Strings
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMBER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String MEMBERS_DB_DELETE_QUERY =
            "DROP TABLE IF EXISTS " + GroupsContract.MemberInfo.TABLE_NAME;
    private static final String MEMBERS_DB_CREATE_QUERY =
            "CREATE TABLE IF NOT EXISTS " + GroupsContract.MemberInfo.TABLE_NAME + " ("
                    + GroupsContract.MemberInfo._ID + " INTEGER PRIMARY KEY" + COMMA_SEP
                    + GroupsContract.MemberInfo.GROUP_ID + NUMBER_TYPE + COMMA_SEP
                    + GroupsContract.MemberInfo.MEMBER_ID + NUMBER_TYPE + COMMA_SEP
                    + GroupsContract.MemberInfo.MEMBER_NAME + TEXT_TYPE + COMMA_SEP
                    + GroupsContract.MemberInfo.INSTALLMENT + NUMBER_TYPE + COMMA_SEP
                    + GroupsContract.MemberInfo.SAVINGS + NUMBER_TYPE + COMMA_SEP
                    + GroupsContract.MemberInfo.IS_PRESENT + NUMBER_TYPE + " )";


    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(MEMBERS_DB_DELETE_QUERY);
            Log.d("Members DB Helper", "Deleted: " + MEMBERS_DB_DELETE_QUERY);
            db.execSQL(MEMBERS_DB_CREATE_QUERY);
            Log.d("Members DB Helper", "Created: " + MEMBERS_DB_CREATE_QUERY);
        } catch (SQLException e) {
            Log.e("SKDRDP DB", "Error opening member DB " + MEMBERS_DB_CREATE_QUERY);
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
        try {
            db.execSQL(MEMBERS_DB_DELETE_QUERY);
            Log.d("Members DB Helper", "Members DB cleared. Query: " + MEMBERS_DB_DELETE_QUERY);
        } catch (SQLException e) {
            Log.e("SKDRDP DB", "Error dropping the table. " + MEMBERS_DB_DELETE_QUERY);
        }
        onCreate(db);
    }
}
