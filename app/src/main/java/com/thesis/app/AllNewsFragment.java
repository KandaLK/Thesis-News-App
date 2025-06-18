package com.thesis.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.thesis.app.models.NewsItem;
import java.util.ArrayList;
import java.util.List;

public class AllNewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> allNewsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initializeViews(view);
        loadAllNews();
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.rv_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
    }

    private void loadAllNews() {
        allNewsList = new ArrayList<>();
        allNewsList.addAll(NewsDataProvider.getSportsData());
        allNewsList.addAll(NewsDataProvider.getEventsData());
        allNewsList.addAll(NewsDataProvider.getFacultyNewsData());

        newsAdapter = new NewsAdapter(getContext(), allNewsList);
        recyclerView.setAdapter(newsAdapter);
    }
}
