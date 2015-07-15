package org.skdrdpindia.cashcollectionapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import org.skdrdpindia.cashcollectionapp.provider.GroupsContentProvider;
import org.skdrdpindia.cashcollectionapp.provider.GroupsContract;

/**
 * Created by harsh on 7/15/2015.
 */
public class GroupsContentProviderTest extends ProviderTestCase2<GroupsContentProvider> {
    
    private MockContentResolver mockResolver;
    private GroupsContentProvider provider;
    private SQLiteDatabase mockGroupsDb, mockMembersDb;

    /**
     * Constructor.
     *
     * @param providerClass     The class name of the provider under test
     * @param providerAuthority The provider's authority string
     */
    public GroupsContentProviderTest(Class providerClass, String providerAuthority) {
        super(providerClass, providerAuthority);
    }

    public void setUp() throws Exception {
        super.setUp();
        mockResolver = getMockContentResolver();
        provider = new GroupsContentProvider();
        mockResolver.addProvider(".provider.GroupsContentProvider", provider);
        mockGroupsDb = provider.getGroupsDbHelper().getWritableDatabase();
        mockMembersDb = provider.getMembersDbHelper().getWritableDatabase();
        // Add the test data into databases.
        ContentValues mockValues = new ContentValues();
        for (int i = 0; i < 15; i++) {
            mockValues.put(GroupsContract.GroupsInfo.GROUP_ID, i);
            mockValues.put(GroupsContract.GroupsInfo.GROUP_NAME, "grp" + i);
            mockValues.put(GroupsContract.GroupsInfo.MOBILE_1, i);
            mockValues.put(GroupsContract.GroupsInfo.MOBILE_2, i);
            mockValues.put(GroupsContract.GroupsInfo.MOBILE_3, i);
            mockGroupsDb.insert(GroupsContract.GroupsInfo.TABLE_NAME, null, mockValues);
        }
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 20; j++) {
                mockValues.put(GroupsContract.MemberInfo.MEMBER_ID, ((j * 10) + i));
                mockValues.put(GroupsContract.MemberInfo.GROUP_ID, i);
                mockValues.put(GroupsContract.MemberInfo.MEMBER_NAME, "mem" + j);
                mockValues.put(GroupsContract.MemberInfo.INSTALLMENT, 470);
                mockValues.put(GroupsContract.MemberInfo.SAVINGS, 50);
                mockValues.put(GroupsContract.MemberInfo.IS_PRESENT, 1);
                mockMembersDb.insert(GroupsContract.MemberInfo.TABLE_NAME, null, mockValues);
            }
        }

    }

    public void tearDown() throws Exception {
        //Delete all Inserted Data.

        // Add null to all other values.
        mockMembersDb = null;
        mockGroupsDb = null;
        mockResolver = null;
        provider = null;
        super.tearDown();
    }

    public void testDelete() throws Exception {

    }

    public void testGetType() throws Exception {

    }

    public void testInsert() throws Exception {

    }

    public void testOnCreate() throws Exception {

    }

    public void testQuery() throws Exception {

    }

    public void testUpdate() throws Exception {

    }
}