# **Tennis Court Booking System**

This repository contains the **Tennis Court Booking System**, developed as part of the **COMP2000 Extended Referral Coursework** for Plymouth University. The Android application enables users to book tennis courts, manage existing bookings, and access features such as updating or canceling bookings.

## **Table of Contents**
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Screenshots](#screenshots)
- [Future Enhancements](#future-enhancements)
- [Contributors](#contributors)

---

## **Features**
- **User Authentication**: Log in and manage tennis court bookings.
- **Court Booking**: Select and book tennis courts based on type and availability.
- **Manage Bookings**: Users can update or cancel their existing bookings.
- **Seasonal Court Availability**: Grass courts are only bookable during the summer months.
- **Local Database**: User and booking details are stored using SQLite.
- **API Integration**: Retrieve user and booking data from a remote API.

## **Installation**

To clone and run this project locally:

1. Clone the repository using the following command:
   ```bash
   git clone https://github.com/ReHolo/Plymouth-University-COMP2000-Extended-Referred-COURSEWORK.git
   ```

2. Open the project in **Android Studio**.

3. Build the project and run it on an Android device or emulator.

## **Usage**

1. Register or log in to the app with a user account.
2. Select the tennis court you want to book.
3. Enter the booking details such as date and duration.
4. Confirm the booking.
5. Navigate to the "Manage Bookings" section to update or cancel the booking.
6. View real-time weather information to make informed booking decisions.

## **Project Structure**

```
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
```

## **Technologies Used**
- **Android Studio**: The primary IDE for developing the app.
- **SQLite**: Used to store user and booking data locally.
- **Retrofit**: Used for network requests and API integration.
- **Java**: The primary language used for app development.

## **Screenshots**

*(Add some screenshots of your app here to showcase its features)*

## **Future Enhancements**

- **Push Notifications**: Implement push notifications for booking reminders or updates.
- **Payment Integration**: Allow users to pay for bookings directly through the app.
- **Improved UI/UX**: Further enhancements to the user interface for better user experience.
- **Booking History**: Add a feature for users to view their past bookings.

## **Contributors**

- **ReHolo** - Lead Developer and Creator
