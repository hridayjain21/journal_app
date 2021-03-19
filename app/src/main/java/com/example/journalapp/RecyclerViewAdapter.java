package com.example.journalapp;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journalapp.model.Journal;
import com.example.journalapp.util.Journal_Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;

    public RecyclerViewAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_view,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        String imageurl;
        Journal journal = journalList.get(position);
        holder.Title.setText(journal.getTitle());
        holder.Thoughts.setText(journal.getThought());
        holder.date.setText(journal.getThought());
        holder.Title.setText(journal.getTitle());
        imageurl = journal.getImageurl();
        Picasso.get()
                .load(imageurl)
                .placeholder(R.drawable.background_secnic2)
                .fit()
                .into(holder.imageView);
        String timeago = (String) DateUtils.getRelativeTimeSpanString(journal
        .getTimeAdded().getSeconds() * 1000);
        holder.date.setText(timeago);
        holder.username.setText(journal.getUsername());


    }

    @Override
    public int getItemCount() {
       return journalList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imageView;
        private TextView Title;
        private TextView Thoughts,date,timeadded,username;
        private ImageButton share_button;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;
            imageView = itemView.findViewById(R.id.journal_image_list);
            Title = itemView.findViewById(R.id.journal_title_list);
            Thoughts = itemView.findViewById(R.id.journal_thought_list);
            date = itemView.findViewById(R.id.journal_timestamp_list);
            username = itemView.findViewById(R.id.journal_row_username);
            share_button = itemView.findViewById(R.id.journal_row_share_button);
            share_button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            Journal journal = journalList.get(getAdapterPosition());
            String titleToShare = " ";
            String thoughtToShare = " ";
            String imgUrlToShare = " ";
            for (int i = 0; i < journalList.size(); i++){
                if (journalList.get(i).getTitle() == Title.getText()){

                    titleToShare = journalList.get(i).getTitle();
                    thoughtToShare = journalList.get(i).getThought();
                    imgUrlToShare = journalList.get(i).getImageurl();
                    i = journalList.size()+1;
                }

            }

            String text = "Title : " + titleToShare + " \n " +
                    " Thought : " + thoughtToShare + "\n"
                    + " Image : " + imgUrlToShare;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT,"My Journal : ");
            intent.putExtra(Intent.EXTRA_TEXT,text);
            context.startActivity(intent);
        }
    }
}
