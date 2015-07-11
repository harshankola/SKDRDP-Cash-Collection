package org.skdrdpindia.cashcollectionapp.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContentProvider;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MembersListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MembersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MembersListFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String GROUP_SELECTED = "GROUP_SELECTED";
    public static final String ACTION_SAVE_COLLECTIONS = "SAVE_COLLECTION";
    private static MembersListFragment memberListFragment;


    private final CashCollectionWatcher collectionWatcher = new CashCollectionWatcher();
    private final SaveCollectionListener saveCollectionListener = new SaveCollectionListener();
    ListView membersListView;
    private OnFragmentInteractionListener mListener;
    private MemberListAdapter membersAdapter;
    private int totalSum = 0;
    private long groupSelected;

    public MembersListFragment() {
        // Required empty public constructor
        memberListFragment = MembersListFragment.this;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MembersListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MembersListFragment newInstance() {
        return new MembersListFragment();
    }

    public static MembersListFragment getInstance() {
        if (memberListFragment != null) {
            return memberListFragment;
        }
        return newInstance();
    }

    /**
     * gets instance of Listener hearing Cash Collections.
     *
     * @return instance of Cash Collection Watcher.
     */
    public CashCollectionWatcher getCollectionWatcher() {
        return collectionWatcher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View membersListFragment = inflater.inflate(R.layout.fragment_members_list, container, false);
        Bundle bundle = getArguments();
        groupSelected = bundle.getLong(GROUP_SELECTED);

        // initialize the Adapter and views.
        membersListView = (ListView) membersListFragment.findViewById(R.id.MembersList);
        membersAdapter = new MemberListAdapter(getActivity().getApplicationContext());
        membersAdapter.setViewBinder(membersAdapter);

        //set the adapter.
        membersListView.setAdapter(membersAdapter);

        //set listener for save collection button.
        Button btnSaveCollection = (Button) membersListFragment.findViewById(R.id.btnSaveCollection);
        btnSaveCollection.setOnClickListener(saveCollectionListener);

        //Prepare the loader.
        getLoaderManager().initLoader(0, bundle, MembersListFragment.this);

        return membersListFragment;
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
    }

    public void saveCashCollection() {
        // get instance of Async Task and execute it.
        Bundle dbParameters = new Bundle();
        dbParameters.putLong(GROUP_SELECTED, groupSelected);
        dbParameters.putString("ACTION", ACTION_SAVE_COLLECTIONS);
        MainActivity.DatabaseUpdateTasks asyncTask = ((MainActivity) this.getActivity()).newDatabaseUpdateTask();
        asyncTask.execute(dbParameters);

        //after updation is done, do the back button pressing event.
        ((MainActivity) this.getActivity()).swapFragment(new GroupListFragment());
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

        String SELECTION = GroupsContract.MemberInfo.GROUP_ID + "=" + groupSelected;
        Log.d("Member Frag", "Displaying Members from group " + groupSelected);
        return new CursorLoader(getActivity(), GroupsContentProvider.MEMBERS_PROVIDER_URI,
                new String[]{
                        GroupsContract.MemberInfo._ID,
                        GroupsContract.MemberInfo.MEMBER_ID,
                        GroupsContract.MemberInfo.MEMBER_NAME,
                        GroupsContract.MemberInfo.INSTALLMENT,
                        GroupsContract.MemberInfo.SAVINGS,
                        GroupsContract.MemberInfo.IS_PRESENT
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
        membersAdapter.swapCursor(data);
        Log.d("Mem Cursor", "Members in cursor=" + data.getCount());
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
        membersAdapter.swapCursor(null);
    }

    /**
     * This method updates the TextView as and when the collection entry
     * is made in text view. Store the value in arraylist.
     *
     * @param cashCollected
     */
    private void updateCashCollected(Editable cashCollected) {
        TextView txtTotalCollection = (TextView) getActivity().findViewById(R.id.txtTotalCollection);
        if (!cashCollected
                .toString()
                .isEmpty()) {
            totalSum += Integer.parseInt(cashCollected.toString());
        }

        txtTotalCollection.setText(totalSum + " Rupees");
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

    private class CashCollectionWatcher implements TextWatcher {
        /**
         * This method is called to notify you that, within <code>s</code>,
         * the <code>count</code> characters beginning at <code>start</code>
         * are about to be replaced by new text with length <code>after</code>.
         * It is an error to attempt to make changes to <code>s</code> from
         * this callback.
         *
         * @param s
         * @param start
         * @param count
         * @param after
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * This method is called to notify you that, within <code>s</code>,
         * the <code>count</code> characters beginning at <code>start</code>
         * have just replaced old text that had length <code>before</code>.
         * It is an error to attempt to make changes to <code>s</code> from
         * this callback.
         *
         * @param s
         * @param start
         * @param before
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * Update the cash balances in total field.
         * ------------------------------------------------------------
         * This method is called to notify you that, somewhere within
         * <code>cashCollected</code>, the text has been changed.
         * It is legitimate to make further changes to <code>cashCollected</code> from
         * this callback, but be careful not to get yourself into an infinite
         * loop, because any changes you make will cause this method to be
         * called again recursively.
         * (You are not told where the change took place because other
         * afterTextChanged() methods may already have made other changes
         * and invalidated the offsets.  But if you need to know here,
         * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
         * to mark your place and then look up from here where the span
         * ended up.
         *
         * @param cashCollected
         */
        @Override
        public void afterTextChanged(Editable cashCollected) {
            // The cash collected
            updateCashCollected(cashCollected);
        }
    }

    private class SaveCollectionListener implements View.OnClickListener {
        /**
         * If the save button was pressed then the saving method is called.
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSaveCollection:
                    saveCashCollection();
                    break;
            }

        }
    }
}
