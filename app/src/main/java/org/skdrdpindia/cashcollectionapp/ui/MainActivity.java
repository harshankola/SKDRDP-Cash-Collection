package org.skdrdpindia.cashcollectionapp.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContentProvider;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;
import org.skdrdpindia.cashcollectionapp.provider.MembersContract;


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
                        new String[]{MembersContract.MemberInfo.MEMBER_ID},
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
        }
        else if (id == R.id.action_download) {
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
}
