package net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.preferiti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import net.ddns.andrewnetwork.ludothornsoundbox.R;
import net.ddns.andrewnetwork.ludothornsoundbox.data.model.LudoAudio;
import net.ddns.andrewnetwork.ludothornsoundbox.data.model.LudoVideo;
import net.ddns.andrewnetwork.ludothornsoundbox.data.model.Thumbnail;
import net.ddns.andrewnetwork.ludothornsoundbox.databinding.ItemPreferitiBinding;
import net.ddns.andrewnetwork.ludothornsoundbox.utils.CommonUtils;
import java.util.List;

import static net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.video.controller.VideoManager.buildVideoUrl;
import static net.ddns.andrewnetwork.ludothornsoundbox.utils.ViewUtils.compareDrawables;


class PreferitiListAdapter extends RecyclerView.Adapter {

    private final List<LudoAudio> audioList;
    private final IFragmentAdapterBinder mBinder;
    private Context mContext;
    private static SparseArray<Bitmap> drawables = new SparseArray<>();
    private static SparseBooleanArray isPlaying = new SparseBooleanArray();

    interface ThumbnailLoadedListener {

        void onThumbnailLoaded(Thumbnail thumbnail);
    }

    PreferitiListAdapter(IFragmentAdapterBinder binder, Context context, List<LudoAudio> audioList) {
        this.mBinder = binder;
        this.mContext = context;
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new AudioViewHolder(inflater.inflate(R.layout.item_preferiti, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        LudoAudio item = audioList.get(position);
        if (viewHolder instanceof AudioViewHolder) {
            ((AudioViewHolder) viewHolder).set(item, position);
        }
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {

        ItemPreferitiBinding mBinding;

        private AudioViewHolder(View v) {
            super(v);
            mBinding = DataBindingUtil.bind(v);
        }

        public void set(LudoAudio item, int position) {

            mBinding.thumbnailProgressBar.getIndeterminateDrawable().setColorFilter(
                    mContext.getResources().getColor(R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN);

            mBinding.titleLabel.setText(item.getTitle());

            LudoVideo video = item.getVideo();

            setPreferitoListener();

            if (isPlaying.get(item.getAudio())) {
                mBinding.playButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_stop_black_24dp));
            } else {
                mBinding.playButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_play_black_24dp));
            }

            setPlayPauseListener(item, position);

            if (video != null && video.getTitle() != null) {
                mBinding.videoTitleLabel.setText(video.getTitle());
                mBinding.videoButton.setOnClickListener(v ->
                        CommonUtils.showDialog(mContext, "Aprire il video corrispondente su youtube?",
                                (dialog, which) -> {
                                    CommonUtils.openLink(mContext, buildVideoUrl(video.getId()));
                                    dialog.dismiss();
                                },
                                true
                        )
                );

                mBinding.videoTitleLabel.setVisibility(View.VISIBLE);
                mBinding.videoButton.setVisibility(View.VISIBLE);

                loadDrawable(item);
            } else {
                mBinding.videoButton.setVisibility(View.GONE);

                mBinding.videoTitleLabel.setText(mContext.getString(R.string.video_non_disponibile_label));

                hideLoading();

                mBinding.thumbnailImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_error_outline_white_24dp));
            }


        }

        private void loadDrawable(LudoAudio item) {
            if (item.getVideo().getThumbnail() != null && item.getVideo().getThumbnail().getImage() != null) {
                drawables.put(item.getAudio(), item.getVideo().getThumbnail().getImage());
            }

            if (drawables.get(item.getAudio()) == null) {
                showLoading();
                mBinder.loadThumbnail(item, thumbnail -> {
                    if (thumbnail != null) {
                        drawables.put(item.getAudio(), thumbnail.getImage());


                        setDrawable(item);
                    } else {
                        hideLoading();

                        mBinding.thumbnailImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_error_outline_white_24dp));
                    }
                });
            } else {
                setDrawable(item);
            }
        }

        private void setDrawable(LudoAudio audio) {
            mBinding.thumbnailImage.setImageBitmap(drawables.get(audio.getAudio()));

            hideLoading();
        }

        private void showLoading() {
            mBinding.thumbnailProgressBar.setVisibility(View.VISIBLE);
        }

        private void hideLoading() {
            mBinding.thumbnailProgressBar.setVisibility(View.INVISIBLE);
        }


        private void setPlayPauseListener(LudoAudio audio, int position) {
            Drawable playImage = ContextCompat.getDrawable(mContext, R.drawable.ic_play_black_24dp);
            Drawable stopImage = ContextCompat.getDrawable(mContext, R.drawable.ic_stop_black_24dp);
            if (compareDrawables(mBinding.playButton.getDrawable(), playImage)) {
                mBinding.playButton.setOnClickListener(v -> {
                    mBinding.playButton.setImageDrawable(stopImage);

                    setPlaying(audio, position, true);

                    mBinder.playAudio(audio);

                    mBinder.setOnCompletionListener(mp -> setPlaying(audio, position, false));

                    setPlayPauseListener(audio, position);
                });
            } else {
                mBinding.playButton.setOnClickListener(v -> {
                    mBinding.playButton.setImageDrawable(playImage);

                    setPlaying(audio, position, false);

                    mBinder.stopAudio(audio);

                    setPlayPauseListener(audio, position);
                });
            }
        }

        private void setPlaying(LudoAudio audio, int position, boolean isPlayin) {
            if (isPlayin) {
                isPlaying.clear();

                notifyOtherItemsChanged(position);
            }

            isPlaying.put(audio.getAudio(), isPlayin);

            notifyItemChanged(position);
        }

        private void notifyOtherItemsChanged(int position) {
            for (int i = 0; i < audioList.size(); i++) {
                if (i != position) {
                    notifyItemChanged(i);
                }
            }
        }

        private void setPreferitoListener() {
            Drawable isNotPreferito = ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_yellow_24dp);
            Drawable isPreferito = ContextCompat.getDrawable(mContext, R.drawable.ic_star_yellow_24dp);
            if (compareDrawables(mBinding.preferitoButton.getDrawable(), isPreferito)) {
                mBinding.preferitoButton.setOnClickListener(v -> {
                    mBinding.preferitoButton.setImageDrawable(isNotPreferito);
                    //TODO cancella preferito
                    setPreferitoListener();
                });
            } else {
                mBinding.preferitoButton.setOnClickListener(v -> {
                    mBinding.preferitoButton.setImageDrawable(isPreferito);
                    //TODO annulla cancellazione preferito
                    setPreferitoListener();
                });
            }
        }
    }
}
