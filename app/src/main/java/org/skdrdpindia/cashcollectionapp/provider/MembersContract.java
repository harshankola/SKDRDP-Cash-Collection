package org.skdrdpindia.cashcollectionapp.provider;

import android.provider.BaseColumns;

/**
 * Created by harsh on 6/1/2015.
 * A contract class indicating the names of columns in Groups Database
 */
public final class MembersContract {

    // Empty constructor to prevent accidental instantiation of this class
    public MembersContract() {
    }

    // Column headers for member list
    public static abstract class MemberInfo implements BaseColumns {
        public static final String TABLE_NAME = "membersinfo";
        public static final String MEMBER_ID = "memberid";
        public static final String GROUP_ID = "groupid";
        public static final String MEMBER_NAME = "membername";
        public static final String SAVINGS = "savings";
        public static final String INSTALLMENT = "installment";
        public static final String IS_PRESENT = "present";
    }
}
