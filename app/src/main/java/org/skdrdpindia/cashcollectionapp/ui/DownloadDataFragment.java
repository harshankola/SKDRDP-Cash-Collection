package org.skdrdpindia.cashcollectionapp.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContentProvider;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;


/**
 * A fragment to hold Download Options.
 */
public class DownloadDataFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Activity activity;

    public DownloadDataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View downloadFragment = inflater.inflate(R.layout.fragment_download_data, container, false);

        //TODO: debug code, delete in Production Version.

        ContentResolver contentResolver = getActivity().getContentResolver();

        if (!AppState.status.isGroupsDatabaseInflated) {
            for (int i = 0; i < 15; i++) {
                ContentValues values = new ContentValues();
                values.put(GroupsContract.GroupsInfo.GROUP_ID, i + 1);
                values.put(GroupsContract.GroupsInfo.GROUP_NAME, "grp" + i);
                values.put(GroupsContract.GroupsInfo.MOBILE_1, (i * 125) + "");
                values.put(GroupsContract.GroupsInfo.MOBILE_2, (i * 250) + "");
                values.put(GroupsContract.GroupsInfo.MOBILE_3, (i * 375) + "");
                values.put(GroupsContract.GroupsInfo.IS_SHOWN, 1);
//                Log.d("SKDRDP Group insert", "grp " + (i + 1));
                try {
                    /*Log.d("Group Insert", "Items:"
                            + "Group ID: "
                            + values.getAsString(GroupsContract.GroupsInfo.GROUP_ID));*/
                    contentResolver.insert(GroupsContentProvider.GROUPS_PROVIDER_URI, values);
                } catch (Exception e) {

                    Log.e("SKDRDP UI", "insert operation failed group" + i);
                    break;
                }
            }
            if (!AppState.status.isCashDatabaseInflated) {
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 10; j++) {
                        ContentValues memvalues = new ContentValues();
                        memvalues.put(GroupsContract.MemberInfo.GROUP_ID, i + 1);
                        memvalues.put(GroupsContract.MemberInfo.MEMBER_ID, (long) ((i + 1) * 10) + (j + 1));
                        memvalues.put(GroupsContract.MemberInfo.MEMBER_NAME, "mem" + j);
                        memvalues.put(GroupsContract.MemberInfo.INSTALLMENT, 0);
                        memvalues.put(GroupsContract.MemberInfo.SAVINGS, 0);
                        memvalues.put(GroupsContract.MemberInfo.IS_PRESENT, 0);
//                        Log.d("SKDRDP Member insert", "grp " + (i + 1) + " mem " + ((i + 1) * 10) + (j + 1));
                        try {
                            /*Log.d("Member Insert", "Items:"
                                    + "Group ID: "
                                    + memvalues.getAsString(GroupsContract.MemberInfo.GROUP_ID)
                                    + "Member ID: "
                                    + memvalues.getAsString(GroupsContract.MemberInfo.MEMBER_ID));*/
                            Uri itemID = contentResolver.insert(GroupsContentProvider.MEMBERS_PROVIDER_URI, memvalues);
//                            Log.d("Member insert", "Inserted URI:" + itemID);
                        } catch (Exception e) {
                            Log.e("SKDRDP UI", "insert op failed mem" + i);
                            break;
                        }

                    }
                }
            }
        }
        AppState.status.isGroupsDatabaseInflated = true;
        AppState.status.isCashDatabaseInflated = true;
        return downloadFragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.activity = activity;
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
