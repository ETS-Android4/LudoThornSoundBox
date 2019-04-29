package net.ddns.andrewnetwork.ludothornsoundbox.ui.settings.activity.hiddenaudio;

import net.ddns.andrewnetwork.ludothornsoundbox.data.DataManager;
import net.ddns.andrewnetwork.ludothornsoundbox.data.model.LudoAudio;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.base.BasePresenter;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.settings.activity.hiddenaudio.SettingsHiddenAudioViewPresenterBinder.ISettingsHiddenAudioPresenter;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.settings.activity.hiddenaudio.SettingsHiddenAudioViewPresenterBinder.ISettingsHiddenAudioView;
import net.ddns.andrewnetwork.ludothornsoundbox.utils.rx.SchedulerProvider;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class SettingsHiddenAudioPresenter<V extends ISettingsHiddenAudioView> extends BasePresenter<V> implements ISettingsHiddenAudioPresenter<V> {

    @Inject
    public SettingsHiddenAudioPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public List<LudoAudio> getAudioList() {
        return getDataManager().getAudioSavedList();
    }

    @Override
    public void salvaListaAudio(List<LudoAudio> audios) {
        for(LudoAudio audio : audios) {
            getDataManager().saveAudio(audio);
        }
    }
}
