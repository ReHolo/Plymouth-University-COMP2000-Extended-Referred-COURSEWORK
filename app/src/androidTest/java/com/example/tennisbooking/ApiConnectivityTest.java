package com.example.tennisbooking;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

public class ApiConnectivityTest {
    @Test
    public void testApiConnectivity() {
        try {
            URL url = new URL("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            assertEquals("API is not reachable", 200, responseCode);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("Exception occurred", true, false);
        }
    }
}