package com.example.white_butterfly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {
    private List<Integer> adList;
    private int selectedPosition = 0; // 초기 선택된 위치

    public AdAdapter(List<Integer> adList) {
        this.adList = adList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        holder.adImageView.setImageResource(adList.get(position));
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        // 광고 이미지
        ImageView adImageView;

        // 점 3개
        ImageView ad_count_1;
        ImageView ad_count_2;
        ImageView ad_count_3;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            adImageView = itemView.findViewById(R.id.adImageView);
            ad_count_1 = itemView.findViewById(R.id.ad_count_1);
            ad_count_2 = itemView.findViewById(R.id.ad_count_2);
            ad_count_3 = itemView.findViewById(R.id.ad_count_3);
        }
    }
}
