package com.example.tennisbooking;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class BookingActivityUITest {

    @Rule
    public ActivityTestRule<BookingActivity> activityRule =
            new ActivityTestRule<>(BookingActivity.class);

    @Test
    public void testBookingProcess() {
        // Type email
        onView(withId(R.id.etEmail)).perform(typeText("test@example.com"));

        // Type phone number
        onView(withId(R.id.etPhoneNumber)).perform(typeText("1234567890"));

        // Type booking date
        onView(withId(R.id.etBookingDate)).perform(typeText("2024-10-22 15:00"));

        // Type duration
        onView(withId(R.id.etDuration)).perform(typeText("2 hours"));

        // Click confirm booking button
        onView(withId(R.id.btnConfirmBooking)).perform(click());



    }
}