package org.skdrdpindia.cashcollectionapp.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContentProvider;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;


public class MainActivity extends ActionBarActivity
        implements GroupListFragment.OnFragmentInteractionListener,
        MembersListFragment.OnFragmentInteractionListener,
        CashOptionsFragment.OnFragmentInteractionListener,
        DownloadDataFragment.OnFragmentInteractionListener,
        UploadDataFragment.OnFragmentInteractionListener {


    public DatabaseUpdateTasks newDatabaseUpdateTask() {
        return new DatabaseUpdateTasks();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppState.status.isGroupsDatabaseInflated = getContentResolver()
                .query(
                        GroupsContentProvider.GROUPS_PROVIDER_URI,
                        new String[]{GroupsContract.GroupsInfo.GROUP_ID},
                        null, null, null)
                .getCount() > 0;
        Log.d("Main Activity", "Group Status set to:" + AppState.status.isGroupsDatabaseInflated);
        AppState.status.isCashDatabaseInflated = getContentResolver()
                .query(
                        GroupsContentProvider.MEMBERS_PROVIDER_URI,
                        new String[]{GroupsContract.MemberInfo.MEMBER_ID},
                        null, null, null)
                .getCount() > 0;
        Log.d("Main Activity", "Mem Status set to:" + AppState.status.isCashDatabaseInflated);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finish();
        } else if (id == R.id.action_download) {
            Fragment downloadDataFragment = new DownloadDataFragment();
            swapFragment(downloadDataFragment);
        }

        return super.onOptionsItemSelected(item);
    }

    public void inflateDb(View view) {
        // Save Json to Database

        //load the group list fragment
        Fragment groupListFragment = new GroupListFragment();
        swapFragment(groupListFragment);
    }

    /**
     * called by attached fragments when their tasks are over and need to be replaced.
     *
     * @param fragment the instance of fragment to be changed with.
     */
    public void swapFragment(Fragment fragment) {
        FragmentTransaction fragmentChanger = getFragmentManager().beginTransaction();
        fragmentChanger.replace(R.id.fragment, fragment);
        fragmentChanger.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * An asynchronus task to save the cash collections, mark the group as absent.
     * Created by harsh on 7/11/2015.
     */
    public class DatabaseUpdateTasks extends AsyncTask<Bundle, Object, Object> {
        public DatabaseUpdateTasks() {
        }

        /**
         * Display a message to user that update has been completed.
         * ---------------------------------------------------------
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param o The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(Object o) {
            Toast
                    .makeText(MainActivity.this.getBaseContext(), "Database has been updated.", Toast.LENGTH_LONG)
                    .show();
            super.onPostExecute(o);
        }

        public void markGroupAbsent(long groupSelected) {
            // Get access to database.
            ContentResolver contentResolver = MainActivity.this.getContentResolver();
            String SELECTION = GroupsContract.MemberInfo.GROUP_ID + "=" + groupSelected;
            Cursor memberList = contentResolver.query(GroupsContentProvider.MEMBERS_PROVIDER_URI,
                    new String[]{GroupsContract.MemberInfo.MEMBER_ID},
                    SELECTION, null, null);


            //update each member separately.
            int totalMembers = memberList.getCount();
            memberList.moveToFirst();
            for (int memberAtPosition = 0;
                 memberAtPosition < totalMembers;
                 memberAtPosition++, memberList.moveToNext()) {

                Log.d("Update Task", "Absenting Member at position: " + memberAtPosition + " of " + totalMembers);

                //get the data from querying the adapter.
                boolean presence = false;
                int[] membersCollection = new int[]{0, 0};
                long memberID = memberList.getLong(memberList.getColumnIndex(GroupsContract.MemberInfo.MEMBER_ID));

                // Update the row.
                Log.d("Update Task", "Absenting: Member ID: " + memberID
                        + "Presence: " + presence
                        + "Collections" + membersCollection);
                updateMemberRow(groupSelected, contentResolver, presence, membersCollection, memberID);
            }

            //update the groups to indicate the list has been updated.
            markGroupAsCompleted(groupSelected, contentResolver);
        }

        /**
         * Private method which saves the the cash collected.
         *
         * @param groupSelected
         */
        private void saveCashCollection(long groupSelected) {

            // Get access to database.
            ContentResolver contentResolver = MainActivity.this.getContentResolver();
            ListView membersListView = (ListView) findViewById(R.id.MembersList);
            MemberListAdapter memberListAdapter = (MemberListAdapter) membersListView.getAdapter();


            //update each member separately.
            int totalMembers = memberListAdapter.getCount();
            for (int memberAtPosition = 0;
                 memberAtPosition < totalMembers;
                 memberAtPosition++) {

                Log.d("Update Task", "About to update Member at position: " + memberAtPosition + " of " + totalMembers);

                //get the data from querying the adapter.
                boolean presence = memberListAdapter.isPresent(memberAtPosition);
                int[] membersCollection = memberListAdapter.getMembersCollection(memberAtPosition);
                long memberID = memberListAdapter.getMembersID(memberAtPosition);

                // Update the row.
                Log.d("Update Task", "Updating: Member ID: " + memberID
                        + "Presence: " + presence
                        + "Collections" + membersCollection);
                updateMemberRow(groupSelected, contentResolver, presence, membersCollection, memberID);
            }

            //update the groups to indicate the list has been updated.
            markGroupAsCompleted(groupSelected, contentResolver);
        }

        private void markGroupAsCompleted(long groupSelected, ContentResolver contentResolver) {
            ContentValues groupData = new ContentValues();
            groupData.put(GroupsContract.GroupsInfo.IS_SHOWN, 0);
            Uri groupsProviderUri = GroupsContentProvider.GROUPS_PROVIDER_URI;
            groupsProviderUri = Uri.withAppendedPath(groupsProviderUri, Long.toString(groupSelected));
            contentResolver.update(groupsProviderUri, groupData, null, null);
        }

        private void updateMemberRow(long groupSelected, ContentResolver contentResolver, boolean presence, int[] membersCollection, long memberID) {
            ContentValues memberData = new ContentValues();
            memberData.put(GroupsContract.MemberInfo.INSTALLMENT, membersCollection[0]);
            memberData.put(GroupsContract.MemberInfo.SAVINGS, membersCollection[1]);
            // If the member was present then 1 is inserted into Db as booleans are not used.
            memberData.put(GroupsContract.MemberInfo.IS_PRESENT, presence ? 1 : 0);
            Uri membersProviderUri = Uri.withAppendedPath(
                    GroupsContentProvider.MEMBERS_PROVIDER_URI,
                    Long.toString(memberID)
            );
            String where = GroupsContract.GroupsInfo.GROUP_ID
                    + "="
                    + groupSelected;
            contentResolver.update(membersProviderUri, memberData, where, null);
        }

        /**
         * Does the handling of appropriate action based action parameter sent in  bundle.
         * The 2 supported actions are Saving of Collection and  Marking group as Absent.
         * The group on which these actions should be performed has to be sent expicitly
         * by caller in the bundle itself.
         * ----------------------------------------------------------------------------
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Object doInBackground(Bundle... params) {
            String action = params[0].getString("ACTION");
            if (action.equals(MembersListFragment.ACTION_SAVE_COLLECTIONS)) {
                saveCashCollection(params[0].getLong(GroupListFragment.GROUP_SELECTED));
            } else if (action.equals(GroupListFragment.ACTION_GROUP_ABSENT)) {
                markGroupAbsent(params[0].getLong(GroupListFragment.GROUP_SELECTED));
            }
            return null;
        }
    }
}
