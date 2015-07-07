package org.skdrdpindia.cashcollectionapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class GroupsContentProvider extends ContentProvider {

    //Authority of URI, URL to provider
    public static final String PROVIDER_URI = "org.skdrdpindia.cashcollectonapp.provider";

    //Reference to database helper.
    private GroupsDbHelper groupsDb;
    private MembersDbHelper membersDb;

    //URI Matcher and constants to match
    private static final UriMatcher dbUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int GROUPS_TABLE = 1;
    private static final int GROUPS_ROW = 2;
    private static final int MEMBERS_TABLE = 3;
    private static final int MEMBERS_ROW = 4;

    static {
        dbUriMatcher.addURI(PROVIDER_URI, GroupsContract.GroupsInfo.TABLE_NAME, GROUPS_TABLE);
        dbUriMatcher.addURI(PROVIDER_URI, GroupsContract.GroupsInfo.TABLE_NAME + "/#", GROUPS_ROW);
        dbUriMatcher.addURI(PROVIDER_URI, MembersContract.MemberInfo.TABLE_NAME, MEMBERS_TABLE);
        dbUriMatcher.addURI(PROVIDER_URI, MembersContract.MemberInfo.TABLE_NAME + "/#", MEMBERS_ROW);
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
                db = groupsDb.getWritableDatabase();
                rowsDeleted = db.delete(GroupsContract.GroupsInfo.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case GROUPS_ROW:
                db = groupsDb.getWritableDatabase();
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
                db = membersDb.getWritableDatabase();
                rowsDeleted = db.delete(MembersContract.MemberInfo.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case MEMBERS_ROW:
                db = membersDb.getWritableDatabase();
                id = uri.getLastPathSegment();
                //if selection criteria is not given.
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(MembersContract.MemberInfo.TABLE_NAME,
                            MembersContract.MemberInfo.MEMBER_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(MembersContract.MemberInfo.TABLE_NAME,
                            MembersContract.MemberInfo.MEMBER_ID + "=" + id
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
                return tableType + MembersContract.MemberInfo.TABLE_NAME;
            case GROUPS_ROW:
                return itemType + GroupsContract.GroupsInfo.TABLE_NAME;
            case MEMBERS_ROW:
                return itemType + MembersContract.MemberInfo.TABLE_NAME;
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
                db = groupsDb.getWritableDatabase();
                id = db.insert(GroupsContract.GroupsInfo.TABLE_NAME,null,values);
                break;
            case MEMBERS_TABLE:
                db = membersDb.getWritableDatabase();
                id = db.insert(MembersContract.MemberInfo.TABLE_NAME,null,values);
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
        groupsDb = new GroupsDbHelper(getContext());
        membersDb = new MembersDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //Create query builder instance
        SQLiteDatabase db;
        SQLiteQueryBuilder dbQueryBuilder = new SQLiteQueryBuilder();

        // find query type
        switch (dbUriMatcher.match(uri)) {
            case GROUPS_TABLE:
                db = groupsDb.getWritableDatabase();
                //set table name according to query.
                dbQueryBuilder.setTables(GroupsContract.GroupsInfo.TABLE_NAME);
                break;
            case GROUPS_ROW:
                db = groupsDb.getWritableDatabase();
                //set table name according to query.
                dbQueryBuilder.setTables(GroupsContract.GroupsInfo.TABLE_NAME);
                dbQueryBuilder.appendWhere(GroupsContract.GroupsInfo.GROUP_ID + "="
                        + uri.getLastPathSegment());//get the group based on its ID.
                break;
            case MEMBERS_TABLE:
                db = membersDb.getWritableDatabase();
                //set table name according to query.
                dbQueryBuilder.setTables(MembersContract.MemberInfo.TABLE_NAME);
                break;
            case MEMBERS_ROW:
                db = membersDb.getWritableDatabase();
                //set table name according to query.
                dbQueryBuilder.setTables(MembersContract.MemberInfo.TABLE_NAME);
                dbQueryBuilder.appendWhere(MembersContract.MemberInfo.MEMBER_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Invalid URI" + uri);
        }

        //get instance of the database.
         Cursor resultData = dbQueryBuilder.query(db,projection,selection,
                selectionArgs,null,null,sortOrder);
        //Notify all the listeners.
        resultData.setNotificationUri(getContext().getContentResolver(),uri);

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
                db = groupsDb.getWritableDatabase();
                rowsUpdated = db.update(GroupsContract.GroupsInfo.TABLE_NAME, values,
                        selection, selectionArgs);

                break;
            case GROUPS_ROW:
                db = groupsDb.getWritableDatabase();
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
                db = membersDb.getWritableDatabase();
                rowsUpdated = db.update(MembersContract.MemberInfo.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case MEMBERS_ROW:
                db = membersDb.getWritableDatabase();
                id = uri.getLastPathSegment();
                //if selection criteria is not given.
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(MembersContract.MemberInfo.TABLE_NAME, values,
                            MembersContract.MemberInfo.MEMBER_ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(MembersContract.MemberInfo.TABLE_NAME, values,
                            MembersContract.MemberInfo.MEMBER_ID + "=" + id
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
