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

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContentProvider;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;


public class MainActivity extends ActionBarActivity
        implements GroupListFragment.OnFragmentInteractionListener,
        MembersListFragment.OnFragmentInteractionListener {

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
    private class DatabaseUpdateTasks extends AsyncTask<Bundle, Object, Object> {
        /**
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
            super.onPostExecute(o);
        }

        public void markGroupAbsent() {
            //TODO: mark the group as absent.
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
            Cursor memberList = memberListAdapter.getCursor();
            //update each member separately.
            for (int memberAtPosition = 0;
                 memberAtPosition < memberList.getCount();
                 memberAtPosition += 1, memberList.moveToNext()) {

                //get the data from members list view.
                View memberListItem = memberListAdapter.getView(memberAtPosition,
                        membersListView.getChildAt(memberAtPosition),
                        membersListView);
                boolean presence = memberListAdapter.isPresent(memberListItem);
                int[] membersCollection = memberListAdapter.getMembersCollection(memberListItem);

                //get the ID from member cursor
                long memberID = memberList.getLong(
                        memberList.getColumnIndex(GroupsContract.MemberInfo.MEMBER_ID));

                ContentValues memberData = new ContentValues();
                memberData.put(GroupsContract.MemberInfo.INSTALLMENT, membersCollection[0]);
                memberData.put(GroupsContract.MemberInfo.SAVINGS, membersCollection[1]);
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

            //update the groups to indicate the list has been updated.
            ContentValues groupData = new ContentValues();
            groupData.put(GroupsContract.GroupsInfo.IS_SHOWN, 0);
            Uri groupsProviderUri = GroupsContentProvider.GROUPS_PROVIDER_URI;
            groupsProviderUri = Uri.withAppendedPath(groupsProviderUri, Long.toString(groupSelected));
            contentResolver.update(groupsProviderUri, groupData, null, null);
        }

        /**
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
                saveCashCollection(params[0].getLong(MembersListFragment.GROUP_SELECTED));
            }
            return null;
        }
    }
}
