package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An {@link NewsAdapter} knows how to create a list item layout for each article
 * in the data source (a list of {@link News} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user
 */

public class NewsAdapter extends ArrayAdapter<News>{

    /**
     * The part of the section name string from the Guardian service that we use to determine
     * whether or not we should abbreviate the "Guardian" word in the section name
     */
    private static final String GUARDIAN_KEYWORD = "Guardian ";

    /**
     * Constructs a new {@link NewsAdapter}
     * @param context of the app
     * @param news is the list of articles, which is data source of the adapter
     */
    public NewsAdapter(Context context, List<News> news){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for four TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, news);
    }

    /**
     * Get a View that displays the data at the specified position in the data set
     * @param position The position of the item within the adapter's data set whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse.
        // Otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        // Initialize a new ViewHolder instance to reference the child views for later actions.
        // This way if a view already exists we can retrieve the holder, instead of calling
        // findViewById
        ViewHolder holder;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder)listItemView.getTag();
        }

        News currentNews = getItem(position);

        if (currentNews != null){

            // If there is a thumbnail for the news, set it on the ImageView, otherwise
            // replace it with a placeholder image
            Picasso
                    .with(getContext())
                    .load(currentNews.getThumbnail())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);

            // Get the news' title and set it on the TextView with the ID title_text
            if (!TextUtils.isEmpty(currentNews.getTitle())){
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(currentNews.getTitle());
            } else {holder.title.setVisibility(View.INVISIBLE);}

            // If there is, get the news' author and set it on the TextView with the ID author
            if (!TextUtils.isEmpty(currentNews.getAuthor())){
                holder.author.setVisibility(View.VISIBLE);
                holder.author.setText(currentNews.getAuthor());
            } else {
                holder.author.setVisibility(View.INVISIBLE);
            }

            if (!TextUtils.isEmpty(currentNews.getDate())){
                holder.datePublished.setVisibility(View.VISIBLE);
                holder.datePublished.setText(currentNews.getDate());
            } else {
                holder.datePublished.setVisibility(View.INVISIBLE);
            }

            if (!TextUtils.isEmpty(currentNews.getSectionName())){
                holder.sectionName.setVisibility(View.VISIBLE);
                // If the section name contains the "Guardian" keyword,
                // abbreviate it simply to "G."
                if (currentNews.getSectionName().contains(GUARDIAN_KEYWORD)){
                    String newSectionName = "G. " + currentNews.getSectionName().split(GUARDIAN_KEYWORD)[1];
                    holder.sectionName.setText(newSectionName);
                } else {
                    holder.sectionName.setText(currentNews.getSectionName());
                }
            } else {
                holder.sectionName.setVisibility(View.INVISIBLE);
            }
            return listItemView;
        } else return null;
    }

    /**
     * A {@link ViewHolder} class to cache child views at runtime
     */
    static class ViewHolder{
        @BindView(R.id.thumbnail) ImageView thumbnail;
        @BindView(R.id.title_text) TextView title;
        @BindView(R.id.author) TextView author;
        @BindView(R.id.datePublished) TextView datePublished;
        @BindView(R.id.sectionName) TextView sectionName;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
