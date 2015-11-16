package uk.co.friendlycode.yourchristmascountdown.ui.fragment;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;
import uk.co.friendlycode.yourchristmascountdown.ChristmasApplication;

/**
 * Base class for all fragments.
 * Binds views and watches memory leaks
 *
 * @see ButterKnife
 * @see RefWatcher
 */
abstract class BaseFragment extends Fragment {

    @CallSuper
    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @CallSuper
    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @CallSuper
    @Override public void onDestroy() {
        super.onDestroy();
        ChristmasApplication.get(getActivity()).getRefWatcher().watch(this);
    }
}
