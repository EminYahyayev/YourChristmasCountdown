package uk.co.friendlycode.yourchristmascountdown.ui.fragment;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.Bind;
import uk.co.friendlycode.yourchristmascountdown.R;

public abstract class BaseMainFragment extends BaseFragment {

    @Bind(R.id.ad_view) AdView mAdView;
    @Bind(R.id.ad_container) ViewGroup mAdContainer;

    private AdRequest mAdRequest;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(mAdRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdView.pause();
    }

    @Override
    public void onDestroy() {
        mAdRequest = null;
        mAdView.removeAllViews();
        mAdView.setAdListener(null);
        mAdView.destroy();
        mAdContainer.removeView(mAdView);

        mAdView = null;
        mAdContainer = null;
        super.onDestroy();
    }
}
