package org.skdrdpindia.cashcollectionapp.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContentProvider;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter groupsAdapter;
    String[] GROUPS_PROJECTION = new String[]
            {GroupsContract.GroupsInfo.GROUP_ID, GroupsContract.GroupsInfo.GROUP_NAME};
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupListFragment newInstance() {
        GroupListFragment fragment = new GroupListFragment();
        return fragment;
    }

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupListFragment = inflater.inflate(R.layout.fragment_group_list, container, false);
        ListView groupsListView;

        if (AppState.status.isGroupsDatabaseInflated) {
            // initialize the Adapter and views.
            groupsListView = (ListView) groupListFragment.findViewById(R.id.GroupListView);
            groupsAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                    R.layout.group_list_item, AppState.status.groupsList,
                    GROUPS_PROJECTION, new int[]{R.id.txtGroupsId, R.id.txtGroupsName}, 1);

            //set the listener.
            groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    loadMemberList(parent, position);

                }
            });

            //set the adapter.
            groupsListView.setAdapter(groupsAdapter);

            //Prepare the loader.
            getLoaderManager().initLoader(0, null, this);

        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Download the data from computer.", Toast.LENGTH_LONG).show();
        }

        return groupListFragment;

    }

    private void loadMemberList(AdapterView<?> parent, int position) {
        // get GROUP_ID of selected group.
        Cursor groupList = (Cursor) parent.getItemAtPosition(position);
        MembersListFragment membersListFragment = new MembersListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("GROUP_SELECTED",groupList.getLong(
                groupList.getColumnIndex(
                        GroupsContract.GroupsInfo.GROUP_ID
                )
        ));
        membersListFragment.setArguments(bundle);

        //Start member list fragment.
        AppState.status.fragmentChanger = getFragmentManager().beginTransaction();
        AppState.status.fragmentChanger.replace(R.id.fragment, membersListFragment);
        AppState.status.fragmentChanger.addToBackStack(null);
        AppState.status.fragmentChanger.commit();
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
        getLoaderManager().destroyLoader(0);
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link Activity#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();

    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri groupsContentProvider = Uri.parse("content://" + GroupsContentProvider.PROVIDER_URI);
        groupsContentProvider = Uri.withAppendedPath(groupsContentProvider, GroupsContract.GroupsInfo.TABLE_NAME);

        String SELECTION = GroupsContract.GroupsInfo.IS_SHOWN + "=1";
        return new CursorLoader(getActivity(), groupsContentProvider,
                new String[]{
                        GroupsContract.GroupsInfo._ID,
                        GroupsContract.GroupsInfo.GROUP_ID,
                        GroupsContract.GroupsInfo.GROUP_NAME
                },
                SELECTION, null, null);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p/>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p/>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context,
     * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        groupsAdapter.swapCursor(data);
        AppState.status.groupsList = data;
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        groupsAdapter.swapCursor(null);
        AppState.status.groupsList = null;
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
        public void onFragmentInteraction(Uri uri);
    }

}
