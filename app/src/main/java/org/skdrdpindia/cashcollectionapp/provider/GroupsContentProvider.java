package org.skdrdpindia.cashcollectionapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.skdrdpindia.cashcollectionapp.ui.AppState;

public class GroupsContentProvider extends ContentProvider {

    //Authority of URI, URL to provider
    public static final String PROVIDER_URI = "org.skdrdpindia.cashcollectonapp.provider";
    public static final Uri GROUPS_PROVIDER_URI = Uri.parse(
            "content://"
                    + GroupsContentProvider.PROVIDER_URI
                    + "/"
                    + GroupsContract.GroupsInfo.TABLE_NAME);
    public static final Uri MEMBERS_PROVIDER_URI = Uri.parse(
            "content://"
                    + GroupsContentProvider.PROVIDER_URI
                    + "/"
                    + GroupsContract.MemberInfo.TABLE_NAME);

    //Reference to database helper.
    private GroupsDbHelper groupsDbHelper;
    private MembersDbHelper membersDbHelper;

    //URI Matcher and constants to match
    private static final UriMatcher dbUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int GROUPS_TABLE = 1;
    private static final int GROUPS_ROW = 2;
    private static final int MEMBERS_TABLE = 3;
    private static final int MEMBERS_ROW = 4;

    static {
        dbUriMatcher.addURI(PROVIDER_URI, GroupsContract.GroupsInfo.TABLE_NAME, GROUPS_TABLE);
        dbUriMatcher.addURI(PROVIDER_URI, GroupsContract.GroupsInfo.TABLE_NAME + "/#", GROUPS_ROW);
        dbUriMatcher.addURI(PROVIDER_URI, GroupsContract.MemberInfo.TABLE_NAME, MEMBERS_TABLE);
        dbUriMatcher.addURI(PROVIDER_URI, GroupsContract.MemberInfo.TABLE_NAME + "/#", MEMBERS_ROW);
    }

    public GroupsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int rowsDeleted = 0;
        SQLiteDatabase db;

        switch (dbUriMatcher.match(uri)) {
            case GROUPS_TABLE:
                db = groupsDbHelper.getWritableDatabase();
                rowsDeleted = db.delete(GroupsContract.GroupsInfo.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case GROUPS_ROW:
                db = groupsDbHelper.getWritableDatabase();
                String id = uri.getLastPathSegment();
                //if selection criteria is not given.
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(GroupsContract.GroupsInfo.TABLE_NAME,
                            GroupsContract.GroupsInfo.GROUP_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(GroupsContract.GroupsInfo.TABLE_NAME,
                            GroupsContract.GroupsInfo.GROUP_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                }
                break;
            case MEMBERS_TABLE:
                db = membersDbHelper.getWritableDatabase();
                rowsDeleted = db.delete(GroupsContract.MemberInfo.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case MEMBERS_ROW:
                db = membersDbHelper.getWritableDatabase();
                id = uri.getLastPathSegment();
                //if selection criteria is not given.
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(GroupsContract.MemberInfo.TABLE_NAME,
                            GroupsContract.MemberInfo.MEMBER_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(GroupsContract.MemberInfo.TABLE_NAME,
                            GroupsContract.MemberInfo.MEMBER_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                }
                break;
            default: throw new IllegalArgumentException("Invalid URI"+uri);
        }

        //Notify Listeners
        getContext().getContentResolver().notifyChange(uri,null);

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // return android type of input string.
        String tableType = "vnd.android.cursor.dir/vnd.org.skdrdpindia.cashcollectionapp.provider.";
        String itemType = "vnd.android.cursor.item/vnd.org.skdrdpindia.cashcollectionapp.provider.";

        switch (dbUriMatcher.match(uri)) {
            case GROUPS_TABLE:
                return  tableType + GroupsContract.GroupsInfo.TABLE_NAME;
            case MEMBERS_TABLE:
                return tableType + GroupsContract.MemberInfo.TABLE_NAME;
            case GROUPS_ROW:
                return itemType + GroupsContract.GroupsInfo.TABLE_NAME;
            case MEMBERS_ROW:
                return itemType + GroupsContract.MemberInfo.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Invalid URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //declare row_id which will be returned as URI's row ID.
        long id = 0;
        SQLiteDatabase db;
        int uriType = dbUriMatcher.match(uri);
        switch (uriType){
            case GROUPS_TABLE:
                if (!AppState.status.isGroupsDatabaseInflated) {
                    Log.d("Groups Provider", "Inserting into groups table if not infalted." + AppState.status.isGroupsDatabaseInflated);
                    db = groupsDbHelper.getWritableDatabase();
                    id = db.insert(GroupsContract.GroupsInfo.TABLE_NAME, null, values);
                }
                break;
            case MEMBERS_TABLE:
                if (!AppState.status.isCashDatabaseInflated) {
                    Log.d("Groups Provider", "Inserting into members table if not infalted." + AppState.status.isCashDatabaseInflated);
                    db = membersDbHelper.getWritableDatabase();
                    id = db.insert(GroupsContract.MemberInfo.TABLE_NAME, null, values);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid URI" + uri);
        }
        //Notify the listeners
        getContext().getContentResolver().notifyChange(uri,null);
        return Uri.parse(uri + "/" + id);
    }

    @Override
    public boolean onCreate() {
        // setup the database helper object.
        groupsDbHelper = new GroupsDbHelper(getContext());
        Log.d("Groups Provider", "Created Groups Helper" + groupsDbHelper.toString());
        membersDbHelper = new MembersDbHelper(getContext());
        Log.d("Groups Provider", "Created Members Helper" + membersDbHelper.toString());
        groupsDbHelper.onUpgrade(groupsDbHelper.getWritableDatabase(), 1, 1);
        membersDbHelper.onUpgrade(groupsDbHelper.getWritableDatabase(), 1, 1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //Create query builder instance
        SQLiteDatabase db;
        SQLiteQueryBuilder dbQueryBuilder = new SQLiteQueryBuilder();
        Log.d("Groups Provider", "Query method:selection:" + selection + " Uri:" + uri.toString());

        // find query type
        switch (dbUriMatcher.match(uri)) {
            case GROUPS_TABLE:
                db = groupsDbHelper.getWritableDatabase();
                //set table name according to query.
                dbQueryBuilder.setTables(GroupsContract.GroupsInfo.TABLE_NAME);
                Log.d("Groups Provider", "Query Group Table");
                break;
            case GROUPS_ROW:
                db = groupsDbHelper.getWritableDatabase();
                //set table name according to query.
                dbQueryBuilder.setTables(GroupsContract.GroupsInfo.TABLE_NAME);
                dbQueryBuilder.appendWhere(GroupsContract.GroupsInfo.GROUP_ID + "="
                        + uri.getLastPathSegment());//get the group based on its ID.
                Log.d("Groups Provider", "Query Group Row:" + GroupsContract.GroupsInfo.GROUP_ID + "="
                        + uri.getLastPathSegment());
                break;
            case MEMBERS_TABLE:
                db = membersDbHelper.getWritableDatabase();
                //set table name according to query.
                dbQueryBuilder.setTables(GroupsContract.MemberInfo.TABLE_NAME);
                Log.d("Groups Provider", "Query Member Table");
                break;
            case MEMBERS_ROW:
                db = membersDbHelper.getWritableDatabase();
                //set table name according to query.
                dbQueryBuilder.setTables(GroupsContract.MemberInfo.TABLE_NAME);
                dbQueryBuilder.appendWhere(GroupsContract.MemberInfo.MEMBER_ID + "="
                        + uri.getLastPathSegment());
                Log.d("Groups Provider", "Query Member Row:" + GroupsContract.MemberInfo.MEMBER_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Invalid URI" + uri);
        }

        //get instance of the database.
        Log.d("Groups Provider", "Querying:" + dbQueryBuilder.toString());
         Cursor resultData = dbQueryBuilder.query(db,projection,selection,
                selectionArgs,null,null,sortOrder);
        //Notify all the listeners.
        resultData.setNotificationUri(getContext().getContentResolver(),uri);
        Log.d("Groups Provider", "Results in cursor is:" + resultData.getCount());

        return resultData;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int rowsUpdated = 0;
        SQLiteDatabase db;

        switch (dbUriMatcher.match(uri)) {
            case GROUPS_TABLE:
                db = groupsDbHelper.getWritableDatabase();
                rowsUpdated = db.update(GroupsContract.GroupsInfo.TABLE_NAME, values,
                        selection, selectionArgs);

                break;
            case GROUPS_ROW:
                db = groupsDbHelper.getWritableDatabase();
                String id = uri.getLastPathSegment();
                //if selection criteria is not given.
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(GroupsContract.GroupsInfo.TABLE_NAME, values,
                            GroupsContract.GroupsInfo.GROUP_ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(GroupsContract.GroupsInfo.TABLE_NAME, values,
                            GroupsContract.GroupsInfo.GROUP_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                }
                break;
            case MEMBERS_TABLE:
                db = membersDbHelper.getWritableDatabase();
                rowsUpdated = db.update(GroupsContract.MemberInfo.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case MEMBERS_ROW:
                db = membersDbHelper.getWritableDatabase();
                id = uri.getLastPathSegment();
                //if selection criteria is not given.
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(GroupsContract.MemberInfo.TABLE_NAME, values,
                            GroupsContract.MemberInfo.MEMBER_ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(GroupsContract.MemberInfo.TABLE_NAME, values,
                            GroupsContract.MemberInfo.MEMBER_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                }
                break;
            default: throw new IllegalArgumentException("Invalid URI"+uri);
        }

        //Notify Listeners
        getContext().getContentResolver().notifyChange(uri,null);

        return rowsUpdated;
    }
}
