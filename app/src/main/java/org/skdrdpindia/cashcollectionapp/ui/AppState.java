package org.skdrdpindia.cashcollectionapp.ui;

import android.app.FragmentTransaction;
import android.database.Cursor;
import android.widget.ListView;

/**
 * Created by harsh on 6/8/2015.
 *
 * Class contains various states of app's critical data sources.
 * States of Cash and Groups Databases are stored.
 * States of Upload and Download status is stored.
 */
public final class AppState {

    //Empty constructor
    public AppState() {
    }
    public abstract static class status
    {
        public static boolean isGroupsDatabaseInflated = false; // status of groups data inflating
        public static boolean isGroupsDatabaseCleared = false;  // status of groups data getting cleared during creation of JSON
        public static boolean isCashDatabaseInflated = false;   // status of cash data inflating
        public static boolean isCashDatabaseCleared = false;    // status of cash data getting cleared during creation of JSON
        public static boolean isDataDownloaded = false;         // status of successful download from computer
        public static boolean isDataUploaded = false;           // status of successful upload to computer
        public static Cursor membersList = null;
        public static Cursor groupsList = null;
        public static FragmentTransaction fragmentChanger;
    }

}
