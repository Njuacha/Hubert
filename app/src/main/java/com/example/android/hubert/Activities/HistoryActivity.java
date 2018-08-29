package com.example.android.hubert.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.android.hubert.Adapters.HistoryAdapter;
import com.example.android.hubert.AppExecutors;
import com.example.android.hubert.DatabaseClasses.Alist;
import com.example.android.hubert.DatabaseClasses.AppDatabase;
import com.example.android.hubert.DatabaseClasses.History;
import com.example.android.hubert.DatabaseClasses.Member;
import com.example.android.hubert.R;
import com.example.android.hubert.View_model_classes.HistViewModel;
import com.example.android.hubert.View_model_classes.HistViewModelFactory;

import java.util.List;

import static com.example.android.hubert.Activities.MainActivity.EXTRA_MEMBER;
import static com.example.android.hubert.Activities.MainActivity.LIST_EXTRA;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.ItemClickListeners {
    private Alist mAlist;
    private Member mMember;
    private HistoryAdapter mAdapter;
    private RecyclerView mRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRv = findViewById(R.id.rv_history);
        mAdapter = new HistoryAdapter(this, this);
        mRv.setAdapter(mAdapter);
        mRv.setLayoutManager(new LinearLayoutManager(this));

        mRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


        // Get the list and the memberId from the displayListActivity
        Intent intent = getIntent();
        if(intent.hasExtra(LIST_EXTRA)&& intent.hasExtra(EXTRA_MEMBER)){
           mAlist = intent.getParcelableExtra(LIST_EXTRA);
           mMember = intent.getParcelableExtra(EXTRA_MEMBER);
        }
        setTitle();
        setUpViewModel();
    }

    private void setTitle() {
        String memberName = mMember.getName();
        String listName = mAlist.getName();
        setTitle(memberName + "/" + listName);
    }

    private void setUpViewModel() {

        HistViewModelFactory factory = new HistViewModelFactory(AppDatabase.getDatabaseInstance(this)
                ,mAlist.getId(),mMember.getMemberId());
        final HistViewModel viewModel = ViewModelProviders.of(this,factory).get(HistViewModel.class);
        final List<History> historyList = viewModel.getmHistoryList();

        AppExecutors.getsInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                mAdapter.setHistoryList(historyList);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this,Display_a_list.class)
                        .putExtra(LIST_EXTRA,mAlist));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onOptionViewClicked(History history, View view) {
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.inflate(R.menu.member_option_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.action_edit:
                        break;
                    case R.id.action_delete:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
