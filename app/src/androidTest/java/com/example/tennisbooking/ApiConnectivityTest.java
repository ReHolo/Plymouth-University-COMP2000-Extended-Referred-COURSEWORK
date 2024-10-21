package com.example.tennisbooking;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiConnectivityTest {

    @Test
    public void testGetRequest() {
        try {
            URL url = new URL("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            assertEquals("GET request failed", 200, responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            assertNotNull("Response content is null", content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("Exception occurred", true, false);
        }
    }

    @Test
    public void testPostRequest() {
        try {
            URL url = new URL("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"courtNo\": \"1\", \"courtType\": \"Grass\", \"availableSeason\": \"Open in Summer\"}";

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            assertEquals("POST request failed", 201, responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            assertNotNull("Response content is null", content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("Exception occurred", true, false);
        }
    }

    @Test
    public void testPutRequest() {
        try {
            URL url = new URL("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"courtNo\": \"1\", \"courtType\": \"Clay\", \"availableSeason\": \"Open in Winter\"}";

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            assertEquals("PUT request failed", 200, responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            assertNotNull("Response content is null", content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("Exception occurred", true, false);
        }
    }

    @Test
    public void testDeleteRequest() {
        try {
            URL url = new URL("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            assertEquals("DELETE request failed", 200, responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            assertNotNull("Response content is null", content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("Exception occurred", true, false);
        }
    }
}