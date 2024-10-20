Tennis Court Booking System
This repository contains the Tennis Court Booking System created as part of the COMP2000 Extended Referral Coursework for Plymouth University. The project is an Android application that allows users to book tennis courts and manage their bookings, with features for canceling and updating bookings.

Table of Contents
Features
Installation
Usage
Project Structure
Technologies Used
Screenshots
Future Enhancements
Contributors
Features
User Authentication: Login and manage court bookings.
Court Booking: Book available tennis courts based on seasonality and court type.
Booking Management: Update or cancel an existing booking.
Seasonal Restrictions: Grass courts are only available during certain months.
SQLite Database Integration: Store user and booking details locally.
API Integration: Retrieve user and booking data from a remote server.
Installation
To clone and run this application locally:

Clone the repository:

bash

git clone https://github.com/ReHolo/Plymouth-University-COMP2000-Extended-Referred-COURSEWORK.git
Open the project in Android Studio.

Build and run the project on an Android device or emulator.

Usage
Register a new account or log in with an existing user.
Select a tennis court based on the available list.
Book the court for a specified date and duration.
Manage bookings: update email/phone number or cancel the booking if needed.
View weather updates to help choose the best time for outdoor court bookings.
Project Structure
bash

TennisCourtBooking/
│
├── app/src/main/
│   ├── java/com/example/tennisbooking/
│   │   ├── MainActivity.java
│   │   ├── BookingActivity.java
│   │   ├── ManageBookingActivity.java
│   │   ├── db/
│   │   │   └── DatabaseHelper.java
│   │   ├── entity/
│   │   │   └── Booking.java
│   │   ├── fragment/
│   │   │   └── HomeFragment.java
│   │   │   └── CourtsFragment.java
│   │   │   └── WeatherFragment.java
│   │   │   └── MineFragment.java
│   └── res/layout/
│       ├── activity_booking.xml
│       ├── activity_manage_booking.xml
│       ├── fragment_home.xml
│       ├── fragment_courts.xml
│       ├── fragment_weather.xml
│       └── fragment_mine.xml
└── README.md
Technologies Used
Android Studio for development.
SQLite for local data storage.
Retrofit for API integration.
Java for core application logic.
Screenshots
(Add screenshots of the application to demonstrate the key features)

Future Enhancements
Push Notifications: Notify users when their booking is confirmed or when their session is approaching.
Payment Gateway: Add integration for court booking payments.
Booking History: Provide a history view for all past bookings.
Improved UI: Enhance user interface for better user experience.
Contributors
ReHolo - Developer
