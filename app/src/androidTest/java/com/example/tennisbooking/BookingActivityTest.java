package com.example.tennisbooking;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Intent;

import com.example.tennisbooking.db.DatabaseHelper;
import com.example.tennisbooking.entity.Booking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30) // Specify the SDK version
public class BookingActivityTest {

    private BookingActivity bookingActivity;

    @Mock
    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bookingActivity = new BookingActivity();
        bookingActivity.databaseHelper = databaseHelper;

        // Mocking the Intent to provide court information
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.getStringExtra("courtNo")).thenReturn("1");
        Mockito.when(intent.getStringExtra("courtType")).thenReturn("Grass");
        Mockito.when(intent.getStringExtra("availableSeason")).thenReturn("Open in Summer");
        bookingActivity.setIntent(intent);
    }

    @Test
    public void testIsValidEmail() {
        assertTrue(invokePrivateMethod("isValidEmail", "test@example.com"));
        assertFalse(invokePrivateMethod("isValidEmail", "testexample.com"));
        assertFalse(invokePrivateMethod("isValidEmail", ""));
    }

    @Test
    public void testIsValidPhoneNumber() {
        assertTrue(invokePrivateMethod("isValidPhoneNumber", "1234567890"));
        assertFalse(invokePrivateMethod("isValidPhoneNumber", "123"));
        assertFalse(invokePrivateMethod("isValidPhoneNumber", ""));
    }

    @Test
    public void testIsWithin48Hours_Valid() {
        // Date in the future, within 48 hours
        String validDate = "2024-10-22 15:00";
        assertTrue(invokePrivateMethod("isWithin48Hours", validDate));
    }

    @Test
    public void testIsWithin48Hours_Invalid() {
        // Date in the future, more than 48 hours
        String invalidDate = "2024-10-25 15:00";
        assertFalse(invokePrivateMethod("isWithin48Hours", invalidDate));
    }

    @Test
    public void testAddBooking_Successful() {
        // Mock database methods
        Mockito.when(databaseHelper.getCurrentUserAccountNo()).thenReturn("1");
        Mockito.when(databaseHelper.userHasBooking(1)).thenReturn(false);
        Mockito.when(databaseHelper.addBooking(ArgumentMatchers.any(Booking.class))).thenReturn(1L);

        // Simulate user input
        setFieldValue("etEmail", "test@example.com");
        setFieldValue("etPhoneNumber", "1234567890");
        setFieldValue("etBookingDate", "2024-10-22 15:00");
        setFieldValue("etDuration", "2 hours");

        // Perform booking
        bookingActivity.btnConfirmBooking.performClick();

        // Verify booking added
        Mockito.verify(databaseHelper).addBooking(ArgumentMatchers.any(Booking.class));
        Mockito.verify(databaseHelper).updateUserBookingStatus(1, true);
    }

    @Test
    public void testAddBooking_Failed_AlreadyHasBooking() {
        // Mock database methods
        Mockito.when(databaseHelper.getCurrentUserAccountNo()).thenReturn("1");
        Mockito.when(databaseHelper.userHasBooking(1)).thenReturn(true);

        // Simulate user input
        setFieldValue("etEmail", "test@example.com");
        setFieldValue("etPhoneNumber", "1234567890");
        setFieldValue("etBookingDate", "2024-10-22 15:00");
        setFieldValue("etDuration", "2 hours");

        // Perform booking
        bookingActivity.btnConfirmBooking.performClick();

        // Verify booking was not added due to existing booking
        Mockito.verify(databaseHelper, Mockito.never()).addBooking(ArgumentMatchers.any(Booking.class));
        Mockito.verify(databaseHelper, Mockito.never()).updateUserBookingStatus(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
    }

    // Helper method to invoke private methods
    private boolean invokePrivateMethod(String methodName, String arg) {
        try {
            java.lang.reflect.Method method = BookingActivity.class.getDeclaredMethod(methodName, String.class);
            method.setAccessible(true);
            return (boolean) method.invoke(bookingActivity, arg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to set private field values
    private void setFieldValue(String fieldName, String value) {
        try {
            java.lang.reflect.Field field = BookingActivity.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            ((android.widget.EditText) field.get(bookingActivity)).setText(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}