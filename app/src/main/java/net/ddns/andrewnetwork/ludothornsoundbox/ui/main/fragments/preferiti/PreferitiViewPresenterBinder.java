package net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.preferiti;

import net.ddns.andrewnetwork.ludothornsoundbox.data.model.LudoAudio;
import net.ddns.andrewnetwork.ludothornsoundbox.data.model.LudoVideo;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.base.MvpPresenter;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.base.MvpView;

import java.util.List;

public interface PreferitiViewPresenterBinder {

    interface IPreferitiView extends MvpView {
        void onPreferitoNonEsistente(LudoAudio audio);

        void onPreferitoRimossoSuccess();

        void onPreferitoRimossoFailed(String message);

        void onPreferitiListLoaded(List<LudoAudio> audioList);

        void onPreferitiListError(List<LudoAudio> audioList);

        void onPreferitiListEmpty();
    }

    interface IPreferitiPresenter<V extends IPreferitiView> extends MvpPresenter<V> {

        void getPreferitiList();

        void rimuoviPreferito(LudoAudio audio);

        void loadThumbnail(LudoVideo video, PreferitiListAdapter.ThumbnailLoadedListener thumbnailLoadedListener);
    }
}
