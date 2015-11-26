package uk.co.friendlycode.yourchristmascountdown.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.OnClick;
import uk.co.friendlycode.yourchristmascountdown.R;

public final class AboutFragment extends BaseFragment {

    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar.setTitle(R.string.title_about);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    @OnClick(R.id.about_visit_website) void onWebsiteClick() {
        openUrl(getString(R.string.url_website));
    }

    @OnClick(R.id.about_facebook) void onFacebookClick() {
        openUrl(getString(R.string.url_facebook));
    }

    @OnClick(R.id.about_twitter) void onTwitterClick() {
        openUrl(getString(R.string.url_twitter));
    }

    @OnClick(R.id.about_play_market) void onPlayMarketClick() {
        openUrl(getString(R.string.url_play_market));
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }
}
