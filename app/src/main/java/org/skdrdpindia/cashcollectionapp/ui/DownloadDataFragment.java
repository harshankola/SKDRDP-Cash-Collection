package org.skdrdpindia.cashcollectionapp.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContentProvider;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;
import org.skdrdpindia.cashcollectionapp.provider.MembersContract;


/**
 * A fragment to hold Download Options.
 */
public class DownloadDataFragment extends Fragment {

    public DownloadDataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View downloadFragment = inflater.inflate(R.layout.fragment_download_data, container, false);

        //TODO: debug code, delete in Production Version.

        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri groupsContentProvider = Uri.parse("content://" + GroupsContentProvider.PROVIDER_URI);
        groupsContentProvider = Uri.withAppendedPath(groupsContentProvider, GroupsContract.GroupsInfo.TABLE_NAME);


        Uri membersContentProvider = Uri.parse("content://" + GroupsContentProvider.PROVIDER_URI);
        membersContentProvider = Uri.withAppendedPath(membersContentProvider, MembersContract.MemberInfo.TABLE_NAME);
        Toast
                .makeText(getActivity(),
                        "GroupsCP:" + groupsContentProvider +
                                "\n MembersCP:" + membersContentProvider,
                        Toast.LENGTH_LONG)
                .show();
        for (int i = 0; i < 15; i++) {
            ContentValues values = new ContentValues();
            values.put(GroupsContract.GroupsInfo.GROUP_ID, i + 1);
            values.put(GroupsContract.GroupsInfo.GROUP_NAME, "grp" + i);
            values.put(GroupsContract.GroupsInfo.MOBILE_1, (i * 125) + "");
            values.put(GroupsContract.GroupsInfo.MOBILE_2, (i * 250) + "");
            values.put(GroupsContract.GroupsInfo.MOBILE_3, (i * 375) + "");
            values.put(GroupsContract.GroupsInfo.IS_SHOWN, 1);
            try {
                contentResolver.insert(groupsContentProvider, values);
            } catch (Exception e) {

                Log.e("SKDRDP UI", "insert operation failed group" + i);
                break;
            }
        }
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 10; j++) {
                ContentValues memvalues = new ContentValues();
                memvalues.put(MembersContract.MemberInfo.GROUP_ID, i + 1);
                memvalues.put(MembersContract.MemberInfo.MEMBER_ID, j + 1);
                memvalues.put(MembersContract.MemberInfo.MEMBER_NAME, "mem" + j);
                memvalues.put(MembersContract.MemberInfo.INSTALLMENT, 0);
                memvalues.put(MembersContract.MemberInfo.SAVINGS, 0);
                memvalues.put(MembersContract.MemberInfo.IS_PRESENT, 1);
                try {
                    contentResolver.insert(membersContentProvider, memvalues);
                } catch (Exception e) {
                    Log.e("SKDRDP UI", "insert op failed mem" + i);
                    break;
                }

            }
        }
        AppState.status.isGroupsDatabaseInflated = true;
        AppState.status.isCashDatabaseInflated = true;
        return downloadFragment;
    }
}