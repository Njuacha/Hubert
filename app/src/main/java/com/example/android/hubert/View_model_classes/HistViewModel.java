package com.example.android.hubert.View_model_classes;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.hubert.AppExecutors;
import com.example.android.hubert.DatabaseClasses.AppDatabase;
import com.example.android.hubert.DatabaseClasses.History;

import java.util.List;

/**
 * Created by hubert on 8/22/18.
 */

public class HistViewModel extends ViewModel {
    private LiveData<List<History>> mHistoryList;

    public HistViewModel(final AppDatabase db, final int listId, final int memberId){
        mHistoryList = db.historyDoa().getHistory(listId,memberId);
    }

    public LiveData<List<History>> getmHistoryList(){

        return mHistoryList;
    }
}
