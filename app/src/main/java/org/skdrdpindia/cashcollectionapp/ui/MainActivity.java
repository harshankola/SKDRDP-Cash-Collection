package org.skdrdpindia.cashcollectionapp.ui;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.ui.AppState;
import org.skdrdpindia.cashcollectionapp.ui.DownloadDataFragment;
import org.skdrdpindia.cashcollectionapp.ui.GroupListFragment;
import org.skdrdpindia.cashcollectionapp.ui.MembersListFragment;


public class MainActivity extends ActionBarActivity
        implements GroupListFragment.OnFragmentInteractionListener,
        MembersListFragment.OnFragmentInteractionListener {

    DownloadDataFragment downloadDataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            downloadDataFragment = new DownloadDataFragment();

            AppState.status.fragmentChanger = getFragmentManager().beginTransaction();
            AppState.status.fragmentChanger.replace(R.id.fragment, downloadDataFragment);
            AppState.status.fragmentChanger.addToBackStack(null);
            AppState.status.fragmentChanger.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
