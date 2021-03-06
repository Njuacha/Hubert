package com.example.android.hubert.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.hubert.DatabaseClasses.Alist;
import com.example.android.hubert.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by hubert on 6/15/18.
 */

public class ContributionsAdapter extends RecyclerView.Adapter<ContributionsAdapter.A_list_ViewHolder> {

    private final Context mContext;
    private List<Alist> mOriginalList;
    private List<Alist> mListEntries;
    private final ItemClickListeners mItemClickListeners;

    public ContributionsAdapter(Context context, ItemClickListeners listener ){
        this.mContext = context;
        mItemClickListeners = listener;
    }

    @NonNull
    @Override
    public A_list_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list,parent,false);
        return new A_list_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final A_list_ViewHolder holder, int position) {
        Alist aList = mListEntries.get(position);
        holder.tvListName.setText(aList.getName());
    }

    @Override
    public int getItemCount() {
        if(mListEntries == null){
            return 0;
        }
        else
        return mListEntries.size();
    }

    public interface ItemClickListeners {
        void onContributionListClicked(Alist a_list);
        void onContributionOptionViewClicked(Alist alist, View view);
    }


    public class A_list_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView tvListName;
        final ImageView ivOptions;

        A_list_ViewHolder(View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tv_list_name);
            ivOptions = itemView.findViewById(R.id.tv_options);
            itemView.setOnClickListener(this);

            ivOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Alist aList = mListEntries.get(position);
                    mItemClickListeners.onContributionOptionViewClicked(aList, ivOptions);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Alist aList = mListEntries.get(position);
            mItemClickListeners.onContributionListClicked(aList);
        }



    }

    public void setListEntries(List<Alist> listEntries){
        this.mListEntries = listEntries;
        notifyDataSetChanged();
    }

    public void searchListStartingWith(String text){
        List<Alist> searchResults = new ArrayList<>();

        if ( mOriginalList == null) return;

        for(Alist alist:mOriginalList){
            if ( alist.getName().toLowerCase().startsWith(text.toLowerCase()))
                searchResults.add(alist);
        }

        setListEntries(searchResults);
    }

    public void saveOriginalList(){
        mOriginalList = mListEntries;
    }

    public void restoreOriginalList(){
        mListEntries = mOriginalList;
        mOriginalList = null;
    }


}
