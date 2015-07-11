package org.skdrdpindia.cashcollectionapp.updaters;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContentProvider;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;

/**
 * An Intent Service for Parsing JSON and populating Database during Download Operations
 * also deflates database and preapres JSON for Uploading.
 * ----------------------------------------------------------------------------------------
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DatabasePopulationService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_INFLATE_DB = "org.skdrdpindia.cashcollectionapp.updaters.action.INFLATE";
    private static final String ACTION_DEFLATE_DB = "org.skdrdpindia.cashcollectionapp.updaters.action.DEFLATE";
    
    // TODO: Rename parameters
    private static final String EXTRA_GROUPS_URI = "org.skdrdpindia.cashcollectionapp.updaters.extra.GROUPS_URI";
    private static final String EXTRA_MEMBERS_URI = "org.skdrdpindia.cashcollectionapp.updaters.extra.MEMBERS_URI";
    
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionInflateDb(Context context, String groupsUri, String membersUri) {
        Intent intent = new Intent(context, DatabasePopulationService.class);
        intent.setAction(ACTION_INFLATE_DB);
        intent.putExtra(EXTRA_GROUPS_URI, groupsUri);
        intent.putExtra(EXTRA_MEMBERS_URI, membersUri);
        context.startService(intent);
    }
    
    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionDeflateDb(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DatabasePopulationService.class);
        intent.setAction(ACTION_DEFLATE_DB);
        intent.putExtra(EXTRA_GROUPS_URI, param1);
        intent.putExtra(EXTRA_MEMBERS_URI, param2);
        context.startService(intent);
    }
    
    public DatabasePopulationService() {
        super("DatabasePopulationService");
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INFLATE_DB.equals(action)) {
                final String groupsDataURL = intent.getStringExtra(EXTRA_GROUPS_URI);
                final String membersDataURL = intent.getStringExtra(EXTRA_MEMBERS_URI);
                handleActionInflateDb(groupsDataURL, membersDataURL);
            } else if (ACTION_DEFLATE_DB.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_GROUPS_URI);
                final String param2 = intent.getStringExtra(EXTRA_MEMBERS_URI);
                handleActionDeflateDb(param1, param2);
            }
        }
    }
    
    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionInflateDb(String groupsDataURL, String membersDataURL) {
        // Insert the data fetched from JSON into database.
        try {
            JSONArray downloadedData = new JSONArray(groupsDataURL);
            ContentResolver contentResolver;

            //Iterate over each group in downloaded JSON Array.
            for (int itemIndex = 0; itemIndex < downloadedData.length(); itemIndex++) {
                // For each group item insert into database.
                JSONObject item = downloadedData.getJSONObject(itemIndex);
                ContentValues groupRowItem = new ContentValues();
                groupRowItem.put(GroupsContract.GroupsInfo.GROUP_ID, item.getLong("G"));
                groupRowItem.put(GroupsContract.GroupsInfo.GROUP_NAME, item.getString("GN"));
                groupRowItem.put(GroupsContract.GroupsInfo.MOBILE_1, item.getLong("M1"));
                groupRowItem.put(GroupsContract.GroupsInfo.MOBILE_2, item.getLong("M2"));
                groupRowItem.put(GroupsContract.GroupsInfo.MOBILE_3, item.getLong("M3"));
                groupRowItem.put(GroupsContract.GroupsInfo.IS_SHOWN, 1);
                contentResolver = getContentResolver();
                contentResolver.insert(GroupsContentProvider.GROUPS_PROVIDER_URI, groupRowItem);
            }

            downloadedData = new JSONArray(membersDataURL);

            //Iterate over each member in downloaded JSON Array.
            for (int itemIndex = 0; itemIndex < downloadedData.length(); itemIndex++) {
                // For each group item insert into database.
                JSONObject item = downloadedData.getJSONObject(itemIndex);
                ContentValues memberRowItem = new ContentValues();
                memberRowItem.put(GroupsContract.MemberInfo.GROUP_ID, item.getLong("G"));
                memberRowItem.put(GroupsContract.MemberInfo.MEMBER_ID, item.getLong("M"));
                memberRowItem.put(GroupsContract.MemberInfo.MEMBER_NAME, item.getString("MN"));
                memberRowItem.put(GroupsContract.MemberInfo.INSTALLMENT, item.getLong("EW"));
                memberRowItem.put(GroupsContract.MemberInfo.SAVINGS, item.getLong("MM"));
                memberRowItem.put(GroupsContract.MemberInfo.IS_PRESENT, item.getBoolean("P") ? 1 : 0);
                contentResolver = getContentResolver();
                contentResolver.insert(GroupsContentProvider.MEMBERS_PROVIDER_URI, memberRowItem);
            }
        } catch (JSONException e) {
            Log.e("Inflating DB", "Error in parsing the JSON Array");
        } catch (Exception e) {
            Log.e("Inflating DB", "Error inserting data.");
        }
    }
    
    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDeflateDb(String param1, String param2) {
        // TODO: Prepare JSON file by deflating database.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
