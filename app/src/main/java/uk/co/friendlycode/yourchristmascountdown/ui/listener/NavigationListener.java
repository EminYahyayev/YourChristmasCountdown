package uk.co.friendlycode.yourchristmascountdown.ui.listener;


public interface NavigationListener {
    void onSettingsClick();

    void onShareClick(String message);

    NavigationListener DUMMY = new NavigationListener() {
        @Override public void onSettingsClick() { /** dummy */}

        @Override public void onShareClick(String message) { /** dummy */}
    };
}
