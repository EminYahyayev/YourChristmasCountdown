package uk.co.friendlycode.yourchristmascountdown.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import butterknife.Bind;
import butterknife.OnClick;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.utils.PrefUtils;

public final class SettingsFragment extends BaseFragment
        implements CompoundButton.OnCheckedChangeListener {

    public interface Listener {
        void onAboutClick();

        Listener DUMMY = new Listener() {
            @Override public void onAboutClick() { /** dummy */}
        };
    }

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.music_switch) Switch mMusicSwitch;
    @Bind(R.id.sfx_switch) Switch mSfxSwitch;

    private Listener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (Listener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar.setTitle(R.string.title_settings);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mMusicSwitch.setChecked(PrefUtils.isMusicEnabled(getActivity()));
        mMusicSwitch.setOnCheckedChangeListener(this);

        mSfxSwitch.setChecked(PrefUtils.isSfxEnabled(getActivity()));
        mSfxSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onDetach() {
        mListener = Listener.DUMMY;
        super.onDetach();
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        if (R.id.music_switch == button.getId()) {
            PrefUtils.setMusicEnabled(getActivity(), isChecked);
        } else if (R.id.sfx_switch == button.getId()) {
            PrefUtils.setSfxEnabled(getActivity(), isChecked);
        } else {
            throw new UnsupportedOperationException("No operation for this button.");
        }
    }

    @OnClick(R.id.music_switch_container) void onMusicClick() {
        mMusicSwitch.setChecked(!mMusicSwitch.isChecked());
    }

    @OnClick(R.id.sfx_switch_container) void onSfxClick() {
        mSfxSwitch.setChecked(!mSfxSwitch.isChecked());
    }

    @OnClick(R.id.button_about) void onAboutClick() {
        mListener.onAboutClick();
    }
}
