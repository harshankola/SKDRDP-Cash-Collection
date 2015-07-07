package org.skdrdpindia.cashcollectionapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.skdrdpindia.cashcollectionapp.R;
import org.skdrdpindia.cashcollectionapp.provider.MembersContract;

/**
 * Adapter class to do the binding of Member Data to Corresponding Views.
 * Created by harsh on 6/22/2015.
 */
public class MemberListAdapter extends SimpleCursorAdapter
        implements SimpleCursorAdapter.ViewBinder {

    private Context context;

    /**
     * Standard constructor
     * <p/>
     * Constructor implements all the defaults for the views required.
     *
     * @param context The context where the ListView associated with this
     *                MemberListItemFactory is running.
     */
    public MemberListAdapter(Context context) {
        super(context,
                R.layout.member_list_item,
                null,
                new String[]{
                        MembersContract.MemberInfo.MEMBER_ID,
                        MembersContract.MemberInfo.MEMBER_NAME,
                        MembersContract.MemberInfo.INSTALLMENT,
                        MembersContract.MemberInfo.SAVINGS,
                        MembersContract.MemberInfo.IS_PRESENT},
                new int[]{
                        R.id.txtMemberId,
                        R.id.txtMemberName,
                        R.id.edtxtInstallment,
                        R.id.edtxtSavings,
                        R.id.chkIsPresent},
                1);
        this.context = context;
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
        View memberListItem = null;
        MemberListHolder memberListHolder;

        // Throw an exception if cursor doesn't have elements at given said position.
        if (!getCursor().moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        if (convertView == null) {
            memberListItem = super.newView(context, AppState.status.membersList, parent);
            memberListHolder = new MemberListHolder();
            memberListHolder.txtMemberID = (TextView) memberListItem.findViewById(R.id.txtMemberId);
            memberListHolder.txtMemberName = (TextView) memberListItem.findViewById(R.id.txtMemberName);
            memberListHolder.edtxtInstallment = (EditText) memberListItem.findViewById(R.id.edtxtInstallment);
            memberListHolder.edtxtSavings = (EditText) memberListItem.findViewById(R.id.edtxtSavings);
            memberListHolder.chkIsPresent = (CheckBox) memberListItem.findViewById(R.id.chkIsPresent);
            memberListItem.setTag(memberListHolder);
        } else {
            memberListItem = convertView;
        }

        // Initialize the member data by binding all the views with their respective data.
        bindView(memberListItem, context, AppState.status.membersList);

        return memberListItem;
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

        if (view instanceof EditText) {
            //Set the text
            setEditableText((EditText) view, ((EditText) view).getText().toString());
            //Attach a listener
            ((EditText) view).addTextChangedListener(MembersListFragment.getInstance());
            return true;
        } else if (view instanceof CheckBox) {
            // Set whether its checked or not.
            setChecked((CheckBox) view, ((CheckBox) view).isChecked());
            //Attach a listener
            return true;
        }
        return false;
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

    public boolean isPresent(View memberListItem) {
        MemberListHolder memberListHolder = (MemberListHolder) memberListItem.getTag();
        return memberListHolder.chkIsPresent.isChecked();
    }

    public int[] getMembersCollection(View memberListItem) {
        MemberListHolder memberListHolder = (MemberListHolder) memberListItem.getTag();
        int installment = Integer.parseInt(memberListHolder.edtxtInstallment.getText().toString());
        int savings = Integer.parseInt(memberListHolder.edtxtSavings.getText().toString());

        return new int[]{installment, savings};
    }

    private class MemberListHolder {
        TextView txtMemberID, txtMemberName;
        EditText edtxtInstallment, edtxtSavings;
        CheckBox chkIsPresent;
    }
}
