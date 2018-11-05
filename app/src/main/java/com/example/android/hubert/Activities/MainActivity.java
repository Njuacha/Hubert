package com.example.android.hubert.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.example.android.hubert.Adapters.ContributionsAdapter;
import com.example.android.hubert.Adapters.MembersAdapter;
import com.example.android.hubert.AppExecutors;
import com.example.android.hubert.DatabaseClasses.Alist;
import com.example.android.hubert.DatabaseClasses.AppDatabase;
import com.example.android.hubert.DatabaseClasses.Member;
import com.example.android.hubert.DialogFragments.NameDialog;
import com.example.android.hubert.R;
import com.example.android.hubert.ViewModels.ContributionsViewModel;
import com.example.android.hubert.ViewModels.MembersViewModel;

import java.util.Date;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements NameDialog.NameDialogListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static AppDatabase mDb;
    private static Alist mlist;
    private static Member mMember;

    public static final String LIST_EXTRA = "list";
    public static final String EXTRA_MEMBER = "member";
    int mFabState = 0;
    private FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mDb = AppDatabase.getDatabaseInstance(this);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mFabState = position;
                if(mFabState == 1){
                    mFab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_action_add_list));
                }else {
                    mFab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_action_add_person));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mFab = findViewById(R.id.fab);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 0 stands for first page which is members page
                if(mFabState == 0){
                    openNameDialog(getString(R.string.name_of_member));
                }else if(mFabState == 1){
                    openNameDialog(getString(R.string.nameOfList));
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                // Show settings
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void openNameDialog(String title) {

        NameDialog dialog = new NameDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "edit_list_name_dialog");
    }

    @Override
    public void onOkSelected(final String textEntered) {
        final AppDatabase db = AppDatabase.getDatabaseInstance(this);

        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                // 0 stands for first page which is members page
                if(mFabState == 0){
                    if(mMember == null){
                        db.member_dao().insertMember(new Member(textEntered));
                        mMember = null;
                    }else{
                        // Set the new name to member object
                        mMember.setName(textEntered);
                        db.member_dao().updateMember(mMember);
                    }
                }else if(mFabState == 1){
                    if (mlist == null){
                        db.a_list_dao().insert_a_list(new Alist(textEntered,new Date()));
                        mlist = null;
                    }else {
                        mlist.setName(textEntered);
                        db.a_list_dao().update_a_list(mlist);
                    }
                }


            }
        });
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements ContributionsAdapter.ItemClickListeners, MembersAdapter.ItemClickListeners {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final int MEMBERS_SECTION = 1;
        private static final int CONTRIBUTIONS_SECTION = 2;


        public static int sectionNumb;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final TextView textView = rootView.findViewById(R.id.tv_empty);
            // Declare and instantiate recycler view
            final RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
            // Set the layout of the recycler view to be a linear la
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            // Declare, instantiate and a divider that would separate items in recycler view
            DividerItemDecoration decoration = new DividerItemDecoration(getContext().getApplicationContext(), VERTICAL);
            recyclerView.addItemDecoration(decoration);
            // Get the section number
            sectionNumb = getArguments().getInt(ARG_SECTION_NUMBER);
            // Use sectionNumb in a switch
            switch (sectionNumb) {
                case MEMBERS_SECTION:
                    // Declare and instantiate Members Adapter
                    final MembersAdapter membersAdapter = new MembersAdapter(getContext(), this);
                    recyclerView.setAdapter(membersAdapter);
                    // Instantiates view model which provides list of members data
                    MembersViewModel membView_model = ViewModelProviders.of(this).get(MembersViewModel.class);
                    membView_model.getMembers().observe(this, new Observer<List<Member>>() {
                        @Override
                        public void onChanged(@Nullable List<Member> members) {
                            if (members.size() == 0) {
                                recyclerView.setVisibility(View.INVISIBLE);
                                textView.setVisibility(View.VISIBLE);
                                textView.setText(R.string.no_members_so_add);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.INVISIBLE);
                                textView.setText(R.string.no_members_so_add);
                                membersAdapter.setMembers(members);
                            }
                        }
                    });


                    break;
                case CONTRIBUTIONS_SECTION:

                    // Declare and instantiate Contributions Adapter
                    final ContributionsAdapter contributionsAdapter = new ContributionsAdapter(getContext(), this);
                    // Set the adapter to the recycler view
                    recyclerView.setAdapter(contributionsAdapter);
                    // Instantiates view model which provides list of contributions data
                    ContributionsViewModel contViewModel = ViewModelProviders.of(this).get(ContributionsViewModel.class);
                    contViewModel.getLists().observe(this, new Observer<List<Alist>>() {
                        @Override
                        public void onChanged(@Nullable List<Alist> lists) {
                            if (lists.size() == 0) {
                                recyclerView.setVisibility(View.INVISIBLE);
                                textView.setVisibility(View.VISIBLE);
                                textView.setText(R.string.no_contrib_so_add);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.INVISIBLE);
                                textView.setText(R.string.no_contrib_so_add);
                                contributionsAdapter.setListEntries(lists);
                            }
                        }
                    });




                    break;

            }
            return rootView;
        }


        @Override
        public void onContributionListClicked(Alist a_list) {
            Intent intent = new Intent(getActivity(), DisplayAList.class);
            intent.putExtra(LIST_EXTRA,a_list);
            startActivity(intent);
        }

        @Override
        public void onContributionListLongClicked(int itemId, String name) {
            //Todo: Put Appropriate action like select for group deleting, archiving or sharing
        }

        @Override
        public void onContributionOptionViewClicked(final Alist alist, View view) {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.inflate(R.menu.list);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_details:
                            Intent showSummaryIntent = new Intent(getActivity(), ContributionSummaryActivity.class);
                            showSummaryIntent.putExtra(LIST_EXTRA, alist);
                            startActivity(showSummaryIntent);
                            break;
                        case R.id.action_delete:
                            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mDb.a_list_dao().delete_a_list(alist.getListId());
                                }
                            });
                            break;
                        case R.id.action_edit:
                            mlist = alist;
                            NameDialog dialog = new NameDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString(getString(R.string.name),alist.getName());
                            bundle.putString(getString(R.string.dialog_title),getString(R.string.nameOfList));
                            dialog.setArguments(bundle);
                            dialog.show(getFragmentManager(), "name dialog");
                    }
                    return false;
                }
            });

            popupMenu.show();
        }

        @Override
        public void onMemberOptionViewClicked(final Member member, View view) {
            PopupMenu popupMenu = new PopupMenu(getContext(),view);
            popupMenu.inflate(R.menu.list);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.action_edit:
                            //startActivity(new Intent(getActivity(),AddMember.class).putExtra(EXTRA_MEMBER,member));
                            mMember = member;
                            NameDialog dialog = new NameDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString(getString(R.string.dialog_title),getString(R.string.name_of_member));
                            bundle.putString(getString(R.string.name),member.getName());
                            dialog.setArguments(bundle);
                            dialog.show(getFragmentManager(), "name dialog");
                            break;
                        case R.id.action_delete:
                            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mDb.member_dao().deleteMember(member);
                                }
                            });
                            break;
                        case R.id.action_details:
                            Intent intent = new Intent(getActivity(),MemberSummaryActivity.class);
                            startActivity(intent.putExtra(EXTRA_MEMBER,member));
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }

        @Override
        public void onMemberClicked(Member member) {
            startActivity(new Intent(getActivity(),MemberActivity.class).putExtra(EXTRA_MEMBER, member));
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }


}
