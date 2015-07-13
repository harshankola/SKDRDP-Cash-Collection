package org.skdrdpindia.cashcollectionapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;

import java.util.ArrayList;

/**
 * Adapter class to do the binding of Member Data to Corresponding Views.
 * Created by harsh on 6/22/2015.
 */
public class MemberListAdapter extends SimpleCursorAdapter
        implements SimpleCursorAdapter.ViewBinder {

    private Context context;
    private MembersListFragment parentFragment;
    private ArrayList<MemberListHolder> memberListHolders;
    private ArrayList<View> memberListItems;

    /**
     * Standard constructor
     * <p/>
     * Constructor implements all the defaults for the views required.
     *
     * @param context        The context where the ListView associated with this
     *                       MemberListItemFactory is running.
     * @param parentFragment The parent fragment reference so that its callback is accesible.
     */
    public MemberListAdapter(Context context, MembersListFragment parentFragment) {
        super(context,
                R.layout.member_list_item,
                null,
                new String[]{
                        GroupsContract.MemberInfo.MEMBER_ID,
                        GroupsContract.MemberInfo.MEMBER_NAME,
                        GroupsContract.MemberInfo.INSTALLMENT,
                        GroupsContract.MemberInfo.SAVINGS,
                        GroupsContract.MemberInfo.IS_PRESENT},
                new int[]{
                        R.id.txtMemberId,
                        R.id.txtMemberName,
                        R.id.edtxtInstallment,
                        R.id.edtxtSavings,
                        R.id.chkIsPresent},
                1);
        this.context = context;
        memberListHolders = new ArrayList<>();
        memberListItems = new ArrayList<>();
        this.parentFragment = parentFragment;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View memberListItem;
        MemberListHolder memberListHolder;
        Cursor cursor = getCursor();

        // Throw an exception if cursor doesn't have elements at given said position.
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        // Inflate new member list item and return it.
        memberListItem = super.newView(context, cursor, parent);
        /*if (convertView == null) {
            memberListItem = super.newView(context, cursor, parent);

            Log.d("Mem Adapter", "New view: Member ID is ="
                    + cursor
                    .getLong(
                            cursor
                                    .getColumnIndex(GroupsContract.MemberInfo.MEMBER_ID)));
            Log.d("Mem Adapter", "_ID id="
                    + cursor
                    .getLong(
                            cursor
                                    .getColumnIndex(GroupsContract.MemberInfo._ID)));
        } else {
            memberListItem = convertView;

            Log.d("Mem Adapter", "Old view: Member ID is ="
                    + cursor
                    .getLong(
                            cursor
                                    .getColumnIndex(GroupsContract.MemberInfo.MEMBER_ID)));
        }*/

        // Initialize the member data by binding all the views with their respective data.
        bindView(memberListItem, context, cursor);
        memberListItems.add(position, memberListItem);
        attachHolder(position, memberListItem, cursor);




        Log.d("Mem Adapter", "Member binding view for:"
                + "pos=" + position
                + " _ID id=" + cursor.getLong(cursor.getColumnIndex(GroupsContract.MemberInfo._ID))
                + " Member ID=" + cursor.getLong(cursor.getColumnIndex(GroupsContract.MemberInfo.MEMBER_ID)));

        return memberListItem;
    }

    private void attachHolder(int position, View memberListItem, Cursor cursor) {
        MemberListHolder memberListHolder = new MemberListHolder();
        memberListHolder.txtMemberID = (TextView) memberListItem.findViewById(R.id.txtMemberId);
        memberListHolder.txtMemberName = (TextView) memberListItem.findViewById(R.id.txtMemberName);
        memberListHolder.edtxtInstallment = (EditText) memberListItem.findViewById(R.id.edtxtInstallment);
        memberListHolder.edtxtSavings = (EditText) memberListItem.findViewById(R.id.edtxtSavings);
        memberListHolder.chkIsPresent = (CheckBox) memberListItem.findViewById(R.id.chkIsPresent);

        memberListHolder.isPresent = cursor.getInt(cursor.getColumnIndex(GroupsContract.MemberInfo.IS_PRESENT)) == 1;
        memberListHolder.memberID = cursor.getLong(cursor.getColumnIndex(GroupsContract.MemberInfo.MEMBER_ID));
        memberListHolder.savings = cursor.getInt(cursor.getColumnIndex(GroupsContract.MemberInfo.SAVINGS));
        memberListHolder.installment = cursor.getInt(cursor.getColumnIndex(GroupsContract.MemberInfo.INSTALLMENT));
        memberListHolder.memberName = cursor.getString(cursor.getColumnIndex(GroupsContract.MemberInfo.MEMBER_NAME));

        memberListHolder.edtxtInstallment.addTextChangedListener(new CashCollectionWatcher(memberListHolder.edtxtInstallment, position));
        memberListHolder.edtxtSavings.addTextChangedListener(new CashCollectionWatcher(memberListHolder.edtxtSavings, position));


        memberListItem.setTag(memberListHolder);
        memberListHolders.add(position, memberListHolder);
    }

    /**
     * Binds the Cursor column defined by the specified index to the specified view.
     * <p/>
     * When binding is handled by this ViewBinder, this method must return true.
     * If this method returns false, SimpleCursorAdapter will attempts to handle
     * the binding on its own.
     *
     * @param view        the view to bind the data to
     * @param cursor      the cursor to get the data from
     * @param columnIndex the column at which the data can be found in the cursor
     * @return true if the data was bound to the view, false otherwise
     */
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

        // Bind the data from cursor to view.
        switch (view.getId()) {
            case R.id.edtxtInstallment:
                setEditableText(
                        (EditText) view,
                        Integer.toString(
                                cursor.getInt(cursor.getColumnIndex(GroupsContract.MemberInfo.INSTALLMENT))
                        )
                );
                break;
            case R.id.edtxtSavings:
                setEditableText(
                        (EditText) view,
                        Integer.toString(
                                cursor.getInt(cursor.getColumnIndex(GroupsContract.MemberInfo.SAVINGS))
                        )
                );
                break;
            case R.id.chkIsPresent:
                setChecked(
                        (CheckBox) view,
                        (cursor.getInt(cursor.getColumnIndex(GroupsContract.MemberInfo.IS_PRESENT)) == 1));
                break;
            case R.id.txtMemberName:
                ((TextView) view).setText(
                        cursor.getString(cursor.getColumnIndex(GroupsContract.MemberInfo.MEMBER_NAME))
                );
                break;
            case R.id.txtMemberId:
                ((TextView) view).setText(
                        Long.toString(cursor.getLong(cursor.getColumnIndex(GroupsContract.MemberInfo.MEMBER_ID)))
                );
        }
        return true;
    }

    /**
     * sets the check box in list view item to its default state or the one sent.
     * Used during initialization from cursor and event getting fired.
     *
     * @param checkBox instance of checkbox view to be set.
     * @param choice   boolean value to be set.
     */
    public void setChecked(CheckBox checkBox, boolean choice) {
        checkBox.setChecked(choice);
    }

    /**
     * sets the edit texts of Installment and Savings to its default value of 0
     * This is used during initialization and text entry event
     *
     * @param editText
     * @param s
     */
    public void setEditableText(EditText editText, String s) {
        editText.setText(s);
    }

    /**
     * overloaded method which returns whether the given member at given view was present or not.
     *
     * @param memberListHolder holder object from which data is returned.
     * @return isPresent
     */
    private boolean isPresent(MemberListHolder memberListHolder) {
        return memberListHolder.isPresent;
    }

    /**
     * returns whether member at given position was present or not.
     *
     * @param position
     * @return
     */
    public boolean isPresent(int position) {
        return isPresent(memberListHolders.get(position));
    }

    /**
     * overloaded method which returns the cash collected from members.
     *
     * @param memberListHolder
     * @return collections[] array containing collections.
     * [0] is Installment, [1] is Savings.
     */
    private int[] getMembersCollection(MemberListHolder memberListHolder) {
        return new int[]{memberListHolder.installment, memberListHolder.savings};
    }

    /**
     * returns the array containing updated collection data of given member.
     *
     * @param position
     * @return
     */
    public int[] getMembersCollection(int position) {
        return getMembersCollection(memberListHolders.get(position));
    }

    /**
     * returns the array containing updated collection data of given member.
     *
     * @param position
     * @return
     */
    public long getMembersID(int position) {
        return getMembersID(memberListHolders.get(position));
    }

    /**
     * Overloaded method which returns the Member ID of the member.
     *
     * @param memberListHolder
     * @return
     */
    private long getMembersID(MemberListHolder memberListHolder) {
        return memberListHolder.memberID;
    }

    /**
     * private method to update installment amount of given member at given position.
     *
     * @param position
     * @param installment
     */
    private void setInstallment(int position, int installment) {
        setInstallment(memberListHolders.get(position), installment);
    }

    private void setInstallment(MemberListHolder holder, int installment) {
        holder.installment = installment;
    }

    /**
     * private method to update savings amount of given member at given position.
     *
     * @param position
     * @param savings
     */
    private void setSavings(int position, int savings) {
        setSavings(memberListHolders.get(position), savings);
    }

    private void setSavings(MemberListHolder holder, int savings) {
        holder.savings = savings;
    }
    private class MemberListHolder {
        TextView txtMemberID, txtMemberName;
        EditText edtxtInstallment, edtxtSavings;
        CheckBox chkIsPresent;
        String memberName;
        boolean isPresent;
        long memberID;
        int installment, savings;
    }

    /**
     * Text Change Listener to update label when text is changed and also updates the row's in memory data.
     * Created by harsh on 7/13/2015.
     */
    class CashCollectionWatcher implements TextWatcher {
        private EditText callingView;
        private int position;

        public CashCollectionWatcher(EditText caller, int position) {
            callingView = caller;
            this.position = position;
        }

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
            // Update Label indicating total collection.
            parentFragment.updateCashCollected(cashCollected);
            // Set the text value in holder.
            if (callingView.getId() == R.id.edtxtInstallment) {
                MemberListAdapter.this.setInstallment(position, Integer.parseInt(cashCollected.toString()));
            } else {
                MemberListAdapter.this.setSavings(position, Integer.parseInt(cashCollected.toString()));
            }
        }
    }
}
