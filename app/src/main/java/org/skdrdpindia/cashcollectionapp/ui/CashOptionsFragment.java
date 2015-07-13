package org.skdrdpindia.cashcollectionapp.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.skdrdpindia.cashcollectionapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CashOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CashOptionsFragment extends Fragment {

    
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CashOptionsFragment.
     */
    public static CashOptionsFragment newInstance() {
        CashOptionsFragment fragment = new CashOptionsFragment();
        return fragment;
    }
    
    public CashOptionsFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cash_options, container, false);
        Button btnCashCollection = (Button) view.findViewById(R.id.btnCashCollection);
        btnCashCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMemberList();
            }
        });
        return view;
    }

    /**
     * get the arguments and resend them to member List Fragment.
     */
    private void loadMemberList() {
        Bundle bundle = getArguments();
        MembersListFragment membersListFragment = MembersListFragment.newInstance();
        membersListFragment.setArguments(bundle);

        //Start member list fragment.
        ((MainActivity) this.getActivity()).swapFragment(membersListFragment);
    }
    
    
}
