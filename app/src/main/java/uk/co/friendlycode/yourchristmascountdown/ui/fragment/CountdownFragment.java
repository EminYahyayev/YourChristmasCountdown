package uk.co.friendlycode.yourchristmascountdown.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.widget.PanningView;
import uk.co.friendlycode.yourchristmascountdown.utils.PrefUtils;
import uk.co.friendlycode.yourchristmascountdown.utils.TimeModel;

public final class CountdownFragment extends BaseFragment {

    public interface Listener {
        void onSettingsClick();

        void onShareClick();

        Listener DUMMY = new Listener() {
            @Override public void onSettingsClick() { /** dummy */}

            @Override public void onShareClick() { /** dummy */}
        };
    }

    @Bind(R.id.view_pager) ViewPager mViewPager;
    @Bind(R.id.countdown_title) TextView mTitleView;
    @Bind(R.id.background_view) PanningView mBackgroundView;

    private Listener mListener = Listener.DUMMY;
    private PagerAdapter mPagerAdapter;
    private ArrayList<TimerFragment> mRegisteredFragments;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (Listener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countdown, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRegisteredFragments = new ArrayList<>();
        mPagerAdapter = new PagerAdapter(getChildFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(TimerFragment.layoutsCount());

        if (savedInstanceState == null)
            mViewPager.setCurrentItem(1, false);

        updateName();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBackgroundView.startPanning();
    }

    @Override
    public void onPause() {
        mBackgroundView.stopPanning();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRegisteredFragments != null)
            mRegisteredFragments.clear();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mListener = Listener.DUMMY;
        super.onDetach();
    }

    @OnClick(R.id.personalise_button) void onPersonaliseClick() {
        Timber.v("onPersonalise");

        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.title_dialog_personalise);
        dialog.setContentView(R.layout.dialog_personalise);

        final EditText nameEditText = (EditText) dialog.findViewById(R.id.personalise_name);
        nameEditText.setText(PrefUtils.getPersonaliseName(getActivity()));

        dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_clear).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                nameEditText.setText("");
            }
        });
        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final String name = nameEditText.getText().toString();
                PrefUtils.setPersonaliseName(getActivity(), name);
                updateName();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.button_settings) void onSettingsClick() {
        mListener.onSettingsClick();
    }

    @OnClick(R.id.button_share) void onShareClick() {
        mListener.onShareClick();
    }

    public void updateTimeContent(TimeModel model) {
        if (mRegisteredFragments == null) {
            Timber.w("updateTimeContent: mRegisteredFragments == null");
            return;
        }

        for (TimerFragment fragment : mRegisteredFragments)
            fragment.updateTimeContent(model);
    }

    private void updateName() {
        final String name = PrefUtils.getPersonaliseName(getActivity());
        mTitleView.setText(TextUtils.isEmpty(name)
                ? getString(R.string.countdown_title)
                : getString(R.string.personalise_name, name));
    }

    final class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TimerFragment fragment = (TimerFragment) super.instantiateItem(container, position);
            mRegisteredFragments.add(fragment);
            Timber.d("instantiateItem: mRegisteredFragments.size()==%d", mRegisteredFragments.size());
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            TimerFragment fragment = (TimerFragment) object;
            mRegisteredFragments.remove(fragment);
            Timber.d("destroyItem: mRegisteredFragments.size()==%d", mRegisteredFragments.size());
            super.destroyItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            return TimerFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TimerFragment.layoutsCount();
        }
    }
}
