package net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import net.ddns.andrewnetwork.ludothornsoundbox.R;
import net.ddns.andrewnetwork.ludothornsoundbox.data.model.LudoVideo;
import net.ddns.andrewnetwork.ludothornsoundbox.data.model.Thumbnail;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.ParentFragment;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.video.controller.VideoInformationManager;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.video.controller.VideoManager;
import net.ddns.andrewnetwork.ludothornsoundbox.utils.AppUtils;
import net.ddns.andrewnetwork.ludothornsoundbox.utils.VideoUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoRecyclerAdapter extends RecyclerView.Adapter implements Filterable {

    private ParentFragment fragment;
    private List<LudoVideo> videoList;
    private List<LudoVideo> itemsAll;

    private static final int PROGRESS = 1;
    private static final int VIDEO = 0;

    public VideoRecyclerAdapter(ParentFragment fragment) {
        this.fragment = fragment;
        this.videoList = new ArrayList<>();

        this.itemsAll = new ArrayList<>(videoList);

    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        /*if(position==getItemCount()-1)
            return PROGRESS;
        else*/ return VIDEO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(fragment.getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if(i == VIDEO) {
            v = inflater.inflate(R.layout.object_video, parent, false);
            return new VideoViewHolder(v);
        } else {
            v = inflater.inflate(R.layout.progress_dialog, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder.getItemViewType() == 0) {
            LudoVideo p = videoList.get(position);
            if (p != null) {
                TextView tt1 = viewHolder.itemView.findViewById(R.id.videotitle);
                TextView tt2 = viewHolder.itemView.findViewById(R.id.videodesc);
                TextView channel = viewHolder.itemView.findViewById(R.id.videochannel);
                TextView likes = viewHolder.itemView.findViewById(R.id.likes);
                TextView dislikes = viewHolder.itemView.findViewById(R.id.dislikes);
                TextView views = viewHolder.itemView.findViewById(R.id.views);
                TextView updated = viewHolder.itemView.findViewById(R.id.Updated);


                ImageView tt3 = viewHolder.itemView.findViewById(R.id.icon);

                if (tt1 != null) {
                    tt1.setText(p.getTitle());
                }

                if (tt2 != null) {
                    tt2.setText(p.getDescription());
                }

                if (channel != null) {
                    channel.setText(p.getChannel().getChannelName());
                }
                VideoInformationManager videoInformationManager = new VideoInformationManager(fragment, p);
                if (likes != null) {
                    likes.setText(videoInformationManager.getCompactedLikes());
                }

                if (dislikes != null) {
                    dislikes.setText(videoInformationManager.getCompactedDislikes());
                }

                if (views != null) {
                    views.setText(videoInformationManager.getCompactedViews());

                }

                if (tt3 != null) {
                    Thumbnail thumbnail = p.getThumbnail();
                    Bitmap image = null;
                    if (thumbnail != null) image = thumbnail.getImage();
                    if (image != null) tt3.setImageBitmap(image);

                }
                if (updated != null)
                    if (AppUtils.isTablet(Objects.requireNonNull(fragment.getActivity()))) updated.setText(p.getDateTime().toString());
                    else updated.setText(p.getDateTime().toString());
            }
            viewHolder.itemView.setOnClickListener(v -> {
                Uri uri = null;
                if (p != null) {
                    uri = Uri.parse(VideoManager.buildVideoUrl(p.getId()));
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                viewHolder.itemView.getRootView().getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private VideoViewHolder(View v) {
            super(v);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressViewHolder(View v) {
            super(v);
        }
    }

    public void setVideoList(List<LudoVideo> videoList) {
        this.videoList = videoList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<LudoVideo> FilteredArrayNames = new ArrayList<>();
                if(constraint == null) {
                    FilteredArrayNames = itemsAll;
                } else {
                    // perform your search here using the searchConstraint String.

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < itemsAll.size(); i++) {
                        LudoVideo dataNames = itemsAll.get(i);
                        if (dataNames.getChannel() != null && dataNames.getChannel().getId().toLowerCase().equals(constraint.toString().toLowerCase())) {
                            FilteredArrayNames.add(dataNames);
                        }
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results.values != null)
                    videoList = (List<LudoVideo>) results.values;


                notifyDataSetChanged();
            }
        };
    }

    void addItems(List<LudoVideo> ludoVideoList) {

        //REMOVE THE PROGRESS BAR ITEM

        //ASSIGN TO ADAPTER VARIABLES
        videoList.addAll(ludoVideoList);
        itemsAll.addAll(ludoVideoList);

        //FILTER BOTH LISTS
        this.videoList = VideoUtils.filterList(videoList);
        this.itemsAll = VideoUtils.filterList(itemsAll);

        notifyDataSetChanged();
    }
}