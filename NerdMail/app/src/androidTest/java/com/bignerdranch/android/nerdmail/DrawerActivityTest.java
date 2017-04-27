package com.bignerdranch.android.nerdmail;

import android.support.annotation.StringRes;
import android.support.design.internal.NavigationMenuItemView;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;

import com.bignerdranch.android.nerdmail.controller.DrawerActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class DrawerActivityTest {
    @Rule
    public ActivityTestRule<DrawerActivity> mActivityRule
            = new ActivityTestRule<>(DrawerActivity.class);

    @Test
    public void userSeesInboxFirst() {
        String inboxText = mActivityRule.getActivity()
                .getString(R.string.nav_drawer_inbox);
        onView(allOf(withText(inboxText),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void inboxItemSelectedFirstInNavigationDrawer() {
        final String inboxText = mActivityRule.getActivity()
                .getString(R.string.nav_drawer_inbox);
        // open nav drawer
        DrawerActions.openDrawer(R.id.activity_drawer_layout);
        // check that the inbox item is checked
        onView(allOf(withText(inboxText),
                withParent(isAssignableFrom(NavigationMenuItemView.class))))
                .check(matches(isChecked()));
    }

    @Test
    public void selectingImportantItemShowsImportantScreen() {
        testSelectingEntryShowsScreen(R.string.nav_drawer_important);
    }

    @Test
    public void selectingSpamItemShowsSpamScreen() {
        testSelectingEntryShowsScreen(R.string.nav_drawer_spam);
    }
    @Test
    public void selectingAllItemShowsAllScreen() {
        testSelectingEntryShowsScreen(R.string.nav_drawer_all);
    }

    private void testSelectingEntryShowsScreen(final @StringRes int menuLabel) {
        final String tText = mActivityRule.getActivity().getString(menuLabel);
        // open nav drawer
        DrawerActions.openDrawer(R.id.activity_drawer_layout);
        //click on the corresponding label in the Navigation view
        onView(allOf(withText(tText),
                withParent(isAssignableFrom(NavigationMenuItemView.class))))
                .perform(click());
        // verify that the corresponding label text is shown in the toolbar
        onView(allOf(withText(tText),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(isDisplayed()));
    }

}
