package com.thesis.app;



import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.thesis.app.R;
import com.thesis.app.models.NewsItem;


import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<NewsItem> newsList;

    public NewsAdapter(Context context, List<NewsItem> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_card, parent, false);
        return new NewsViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);

        // Set data to views
        holder.titleText.setText(newsItem.getTitle());
        holder.descriptionText.setText(newsItem.getDescription());
        holder.dateText.setText(newsItem.getFormattedDate());


        int imageResId = getImageResourceId(newsItem.getImageResource());
        if (imageResId != 0) {
            holder.newsImage.setImageResource(imageResId);
        } else {
            holder.newsImage.setImageResource(R.drawable.ic_news_placeholder);
        }


        holder.cardView.setOnClickListener(v -> {

            Toast.makeText(context, "Reading: " + newsItem.getTitle(), Toast.LENGTH_SHORT).show();
        });

        holder.cardView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.98f).scaleY(0.98f).setDuration(100).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    private int getImageResourceId(String resourceName) {
        try {
            return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        } catch (Exception e) {
            return 0;
        }
    }

    public void updateData(List<NewsItem> newNewsList) {
        this.newsList = newNewsList;
        notifyDataSetChanged();
    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView newsImage;
        TextView titleText, descriptionText, dateText;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_news);
            newsImage = itemView.findViewById(R.id.iv_news_image);
            titleText = itemView.findViewById(R.id.tv_news_title);
            descriptionText = itemView.findViewById(R.id.tv_news_description);
            dateText = itemView.findViewById(R.id.tv_news_date);
        }
    }
}
