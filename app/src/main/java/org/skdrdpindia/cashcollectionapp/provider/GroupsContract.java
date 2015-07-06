package org.skdrdpindia.cashcollectionapp.provider;

import android.provider.BaseColumns;

/**
 * Created by harsh on 6/1/2015.
 * A contract class indicating the names of columns in Groups Database
 */
public final class GroupsContract {

    // Empty constructor to prevent accidental instantiation of this class
    public GroupsContract() {
    }

    // Column headers for group list
    public static abstract class GroupsInfo implements BaseColumns {
        public static final String TABLE_NAME = "groupsinfo";
        public static final String GROUP_ID = "groupid";
        public static final String GROUP_NAME = "groupname";
        public static final String MOBILE_1 = "mobile1";
        public static final String MOBILE_2 = "mobile2";
        public static final String MOBILE_3 = "mobile3";
        public static final String IS_SHOWN = "isshown";
    }

}
