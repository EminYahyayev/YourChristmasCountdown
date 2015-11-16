package uk.co.friendlycode.yourchristmascountdown.ui.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.PrefUtils;
import uk.co.friendlycode.yourchristmascountdown.ui.fragment.CountdownFragment;
import uk.co.friendlycode.yourchristmascountdown.ui.widget.PanningView;
import uk.co.friendlycode.yourchristmascountdown.utils.TimeUtils;

public final class CountdownActivity extends BaseMainActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Bind(R.id.view_pager) ViewPager mViewPager;
    @Bind(R.id.countdown_title) TextView mTitleView;
    @Bind(R.id.background_view) PanningView mBackgroundView;

    private Timer mTimer;
    private PagerAdapter mPagerAdapter;
    private ArrayList<CountdownFragment> mRegisteredFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        mRegisteredFragments = new ArrayList<>();
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(CountdownFragment.layoutsCount());

        if (savedInstanceState == null)
            mViewPager.setCurrentItem(1, false);

        updateName();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override public void run() {
                final TimeUtils.TimeModel model = TimeUtils.modelFromNow();
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        for (CountdownFragment fragment : mRegisteredFragments)
                            fragment.updateTimeContent(model);
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBackgroundView.startPanning();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        mBackgroundView.stopPanning();
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        mTimer.cancel();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mRegisteredFragments.clear();
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {}

    private void nextPage() {
        Timber.d("nextPage");
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }

    @OnClick(R.id.personalise_button) void onPersonaliseClick() {
        Timber.v("onPersonalise");

        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.title_dialog_personalise);
        dialog.setContentView(R.layout.dialog_personalise);

        final EditText nameEditText = (EditText) dialog.findViewById(R.id.personalise_name);
        nameEditText.setText(PrefUtils.getPersonaliseName(this));

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
                PrefUtils.setPersonaliseName(CountdownActivity.this, name);
                updateName();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateName() {
        final String name = PrefUtils.getPersonaliseName(this);
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
            CountdownFragment fragment = (CountdownFragment) super.instantiateItem(container, position);
            mRegisteredFragments.add(fragment);
            Timber.d("instantiateItem: mRegisteredFragments.size()==%d", mRegisteredFragments.size());
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            CountdownFragment fragment = (CountdownFragment) object;
            mRegisteredFragments.remove(fragment);
            Timber.d("destroyItem: mRegisteredFragments.size()==%d", mRegisteredFragments.size());
            super.destroyItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            return CountdownFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return CountdownFragment.layoutsCount();
        }
    }
}
