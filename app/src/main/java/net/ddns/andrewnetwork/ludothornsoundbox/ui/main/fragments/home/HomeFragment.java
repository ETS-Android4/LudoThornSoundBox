package net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.android.material.appbar.AppBarLayout;

import net.ddns.andrewnetwork.ludothornsoundbox.R;
import net.ddns.andrewnetwork.ludothornsoundbox.data.model.LudoAudio;
import net.ddns.andrewnetwork.ludothornsoundbox.databinding.FragmentHomeBinding;
import net.ddns.andrewnetwork.ludothornsoundbox.di.component.ActivityComponent;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.GifFragment;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.home.HomeViewPresenterBinder.IHomePresenter;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.home.HomeViewPresenterBinder.IHomeView;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.home.view.ButtonViewPagerAdapter;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.main.fragments.home.view.OnButtonSelectedListener;
import net.ddns.andrewnetwork.ludothornsoundbox.ui.main.utils.model.ChiaveValore;
import net.ddns.andrewnetwork.ludothornsoundbox.utils.AudioUtils;
import net.ddns.andrewnetwork.ludothornsoundbox.utils.CommonUtils;
import net.ddns.andrewnetwork.ludothornsoundbox.utils.SpinnerUtils;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

public class HomeFragment extends GifFragment implements OnButtonSelectedListener<LudoAudio>, IHomeView {

    private FragmentHomeBinding mBinding;
    private List<LudoAudio> audioList;
    public static final int AUDIO_NOME = 1;
    public static final int AUDIO_DATA = 2;
    @Inject
    IHomePresenter<IHomeView> mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        ActivityComponent activityComponent = getActivityComponent();

        if(activityComponent != null) {
            activityComponent.inject(this);
            mPresenter.onAttach(this);
        }

        mPresenter.getVideoInformationForAudios(audioList);
        return mBinding.getRoot();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.spinner.setAdapter(SpinnerUtils.createOrderAdapter(getContext()));

        mBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ButtonViewPagerAdapter<LudoAudio> adapter = (ButtonViewPagerAdapter<LudoAudio>) mBinding.buttonsAudioPager.getAdapter();

                AudioUtils.sortBy(Objects.requireNonNull(adapter).getItemsAll(),
                        ((ChiaveValore<String>) parent.getSelectedItem()).getChiave());
                AudioUtils.sortBy(adapter.getList(),
                        ((ChiaveValore<String>) parent.getSelectedItem()).getChiave());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.reverse.setOnClickListener(view2 -> {
                    ButtonViewPagerAdapter<LudoAudio> adapter = (ButtonViewPagerAdapter<LudoAudio>) mBinding.buttonsAudioPager.getAdapter();

                    if (adapter == null)
                        return;

                    AudioUtils.reverse(adapter.getItemsAll());
                    AudioUtils.reverse(adapter.getList());

                    adapter.notifyDataSetChanged();
                }
        );

        ButtonViewPagerAdapter<LudoAudio> adapter = new ButtonViewPagerAdapter<>(getContext(), audioList, LudoAudio::getTitle);

        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBinding.counter.setText(String.format("%d/%d", adapter.getMaxItemsPerPage() * position + 1, adapter.getList().size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mBinding.buttonsAudioPager.addOnPageChangeListener(onPageChangeListener);

        mBinding.buttonsAudioPager.post(() -> onPageChangeListener.onPageSelected(0));

        mBinding.buttonsAudioPager.setAdapter(adapter);

        mBinding.expandUp.setOnClickListener(v -> expandToolbar());

        adapter.setOnButtonSelectedListener(this);

        mBinding.buttonRight.setOnClickListener(v -> mBinding.buttonsAudioPager.arrowScroll(View.FOCUS_RIGHT));
        mBinding.buttonLeft.setOnClickListener(v -> mBinding.buttonsAudioPager.arrowScroll(View.FOCUS_LEFT));

        mBinding.audioHeader.searchstring.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        audioList = AudioUtils.createAudioList(Objects.requireNonNull(getContext()));
    }

    @Override
    public void onButtonSelected(LudoAudio audio, int position, Button button) {

        AudioUtils.playTrack(getContext(), audio, completionListener);
        play_pause.setImageResource(R.drawable.ic_pause_white);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onButtonLongSelected(LudoAudio audio, int position, Button button) {
        MenuBuilder menuBuilder = new MenuBuilder(mActivity);
        MenuInflater inflater = new MenuInflater(mActivity);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(mActivity, menuBuilder, button);
        optionsMenu.setForceShowIcon(true);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.preferiti:
                        mPresenter.salvaPreferito(audio);
                        break;
                    case R.id.video_collegato:
                        break;
                    case R.id.nascondi_audio:
                        break;
                }
                return true;
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {

            }
        });

        inflater.inflate(R.menu.popup_menu_audio, menuBuilder);

        optionsMenu.show();

        return true;
    }

    private void expandToolbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mBinding.appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(mBinding.coordinatorRoot, mBinding.appBarLayout, null, 0, 1, new int[2]);
        }
    }

    @Override
    public void onPreferitoEsistente(LudoAudio audio) {
        CommonUtils.showDialog(getContext(), getString(R.string.audio_esistente_label));
    }

    @Override
    public void onPreferitoSalvataggioSuccess() {
        CommonUtils.showDialog(getContext(), getString(R.string.salvato_correttamente_audio_label));

    }

    @Override
    public void onPreferitoSalvataggioFailed(String messaggio) {

        if(messaggio == null || messaggio.isEmpty()) {
            messaggio = getString(R.string.salvato_fallito_audio_label);
        }

        CommonUtils.showDialog(getContext(), messaggio);

    }

    @Override
    public void onMaxAudioReached() {
        CommonUtils.showDialog(mActivity, mActivity.getString(R.string.max_audio_reached_label));
    }

    @Override
    public void onVideoInformationNotLoaded() {

    }
}
