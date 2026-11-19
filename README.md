# GuideMe – Android

GuideMe is a travel and tourism app for Android. Travellers use it to find and book **hotels**, **services** and **things to do**. Local businesses use the same app as **partners** to list what they offer and manage reservations.

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/language-Java%2011-orange?logo=openjdk)
![Min SDK](https://img.shields.io/badge/minSdk-27-blue)
![Target SDK](https://img.shields.io/badge/targetSdk-36-blue)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore%20%7C%20Storage%20%7C%20FCM-FFCA28?logo=firebase&logoColor=black)

> The app needs the companion REST API **[GuideMe-Backend](https://github.com/DinukaDilshan415/GuideMe-Backend)** (Jersey + Hibernate + MySQL).

---

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Endpoints Used](#api-endpoints-used)
- [Permissions](#permissions)
- [Author](#author)

---

## Features

### For travellers
- **Sign-up and login** with email/password or **Google Sign-In** (Firebase Authentication)
- **Browse listings** in three categories: Hotels, Services and Things To Do, with image sliders, facilities and reviews
- **Check availability and book**, including room-type selection for hotels
- **In-app payments** through the **PayHere** Android SDK
- **My Bookings** to see past and upcoming reservations
- **Wishlist** to save listings for later
- **Reviews and ratings** for listings
- **Maps and directions**: see a listing on Google Maps and get a route drawn to it (Directions API)
- **Shake to open the compass**: shake the phone to bring up a compass dialog
- **Call the business** straight from a listing's details page
- **Push notifications** through Firebase Cloud Messaging
- **Profile management**: name, photo, mobile number (with country-code picker), country and address

### For partners (business owners)
- Separate **partner registration and login**
- **Partnership plans**: upgrade or cancel a plan, paid through PayHere
- **Create listings** with a category, facilities, room types, qualifications, multiple images and a rich-text description
- **Manage reservations** made on your listings
- **Partner notifications** and a profile area

---

## Screenshots

<!-- Add screenshots to a /screenshots folder and update the paths below -->

| Splash | Home | Listing details | Booking |
|:---:|:---:|:---:|:---:|
| <img src="https://media.licdn.com/dms/image/v2/D5622AQHFIHntyoEBjg/feedshare-shrink_800/B56aF5RtdwKoAo-/0/1793036048478?e=1796860800&v=beta&t=FK9JbycQ4YLahutlSiwdot2Y17d--Vv34A40OAMiXlc" width="200"/> | <img src="https://media.licdn.com/dms/image/v2/D5622AQG8GBiMXMO49g/feedshare-shrink_800/B56aF5RuNNJkAg-/0/1793036051525?e=1796860800&v=beta&t=XMychZNPGwf0E4LQ9fdW5B2Gasf8Pmk5YdphfULQ19U" width="200"/> | <img src="https://media.licdn.com/dms/image/v2/D5622AQHiGRj8C2RH6g/feedshare-shrink_800/B56aF5RuPqJMAk-/0/1793036051761?e=1796860800&v=beta&t=cNynbzRBDfioMXm5D83vYlv1CON-clRXfL7n3SSn8m4" width="200"/> | <img src="https://media.licdn.com/dms/image/v2/D5622AQG26RQwZK1Rwg/feedshare-shrink_800/B56aF5RuAqHQAc-/0/1793036050777?e=1796860800&v=beta&t=TnJlk37BOTm0N46c17HWfEgKlReS3_e9K2S4eQNuArA" width="200"/> |

---

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 11 |
| Build | Gradle 8.13, Android Gradle Plugin 8.13.2, version catalog (`libs.versions.toml`) |
| SDK | `minSdk 27` · `targetSdk 36` · `compileSdk 36` |
| UI | Material Components, ConstraintLayout, ViewBinding, Flexbox, DotsIndicator, SweetAlert2, RichEditor |
| Networking | Retrofit 3, Gson converter, OkHttp logging interceptor |
| Images | Glide 5 |
| Firebase | Authentication, Cloud Firestore, Cloud Storage, Cloud Messaging, Analytics |
| Google | Maps SDK, Location Services, Maps Utils, Google Maps Services (Directions), Google Sign-In |
| Payments | [PayHere Android SDK](https://github.com/PayHereDevs/payhere-android-sdk) |
| Other | Lombok, Country Code Picker (`com.hbb20:ccp`) |

---

## Architecture

```
┌────────────────────────┐        REST / JSON        ┌──────────────────────────┐
│   GuideMe Android App  │  ───────────────────────▶ │   GuideMe-Backend        │
│  Activities/Fragments  │       (Retrofit)          │   Jersey · Hibernate     │
│  Adapters · Models     │                           │   MySQL                  │
└──────────┬─────────────┘                           └──────────────────────────┘
           │
           ├──▶ Firebase Auth / Firestore / Storage / FCM
           ├──▶ Google Maps & Directions API
           └──▶ PayHere payment gateway
```

- **Backend API**: listings, bookings, users, partners, reviews and availability checks
- **Firebase**: authentication, some user and partner profile data, image storage and push notifications
- **Google Maps Platform**: maps, location and turn-by-turn route lines

---

## Project Structure

```
app/src/main/java/me/dinuka/guideme/
├── activity/    # Splash, Login/Register, Main, Partner*, Maps, Compass
├── adapter/     # RecyclerView & ViewPager adapters (listings, bookings, reviews, wishlist …)
├── api/         # Retrofit interfaces: Booking, Hotel, Listing, Partner, Service, ThingsToDo, User, Facility, Direction
├── client/      # RetrofitClient (base URL & Retrofit instance)
├── dto/         # Data transfer objects sent to / received from the API
├── fragment/    # Screens: Home, Hotels, Services, ThingsToDo, details, booking pickers, profile, partner screens
├── helper/      # ShakeDetector, WishlistHelper
├── model/       # Domain models (Listing, Hotel, Service, RoomType, Review, MyBooking …)
├── service/     # MessagingService (FCM), PartnerService
└── util/        # RegexValidator, NotificationTimeFormater
```

---

## Getting Started

### Prerequisites
- **Android Studio** (a recent stable release that supports AGP 8.13)
- **JDK 11+**
- A running instance of **[GuideMe-Backend](https://github.com/DinukaDilshan415/GuideMe-Backend)**
- A **Firebase** project
- A **Google Maps Platform** API key (Maps SDK for Android + Directions API)
- A **PayHere** sandbox merchant account

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/DinukaDilshan415/GuideMe-Android.git
   cd GuideMe-Android
   ```

2. **Open in Android Studio** and let Gradle sync.

3. **Set up the configuration** (see [Configuration](#configuration) below).

4. **Run** the `app` configuration on an emulator or a device running Android 8.1 (API 27) or newer.

---

## Configuration

### 1. Backend URL
The API base URL is set in `client/RetrofitClient.java`:

```java
private static final String ngrokURL = "https://<your-ngrok-or-server-host>";
public static final String BASE_URL = ngrokURL + "/guide_me/";
```

Replace the host with your backend's address, for example an ngrok tunnel or `http://10.0.2.2:8080` for a backend running on your computer while you use the emulator.

### 2. Google Maps API key
The project uses the [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin). Add your key to `local.properties` in the project root. Don't commit this file.

```properties
MAPS_API_KEY=your_google_maps_api_key
```

The manifest reads it through `${MAPS_API_KEY}`.

### 3. Firebase
1. Create a Firebase project and add an Android app with the package name **`me.dinuka.guideme`**.
2. Download `google-services.json` and place it in `app/`.
3. Turn on **Authentication** (Email/Password and Google), **Cloud Firestore**, **Storage** and **Cloud Messaging**.
4. For Google Sign-In, add your debug and release **SHA-1** fingerprints in the Firebase console.

### 4. PayHere
Payments run in **sandbox mode**. Put your own PayHere sandbox merchant details in the booking and partnership fragments before testing payments.

---

## API Endpoints Used

All paths are relative to `BASE_URL`.

| Module | Endpoints |
|---|---|
| **User** | `POST user/register` · `GET user/{email}` · `GET user/address/{email}` · `POST user/saveorupdateaddress` · `PUT user/updatemobile` · `PUT user/updatenameandimage` · `PUT user/updatecountry` |
| **Partner** | `POST partner/register` · `GET partner/{email}` · `GET partner/address/{email}` · `GET partner/checkpartnership/{email}` · `POST partner/upgradeplan` · `POST partner/cancelplan` · `POST partner/saveorupdateaddress` · `PUT partner/updatemobile` · `PUT partner/updatenameandimage` · `PUT partner/updatecountry` |
| **Listing** | `GET listing/types/getall` · `GET listing/{email}` · `POST listing/addnewlisting` · `GET listing/reviews/{lid}` · `POST listing/addreview` |
| **Hotels** | `GET hotels/{key}` · `POST hotels/checkavailability` |
| **Services** | `GET service/{key}` · `POST service/checkavailability` |
| **Things To Do** | `GET thingstodo/{key}` · `POST thingstodo/checkavailability` |
| **Booking** | `GET booking/{email}` · `GET booking/listingitem/{id}` · `POST booking/addnewbooking` |
| **Facility** | `GET facility/getall` |

---

## Permissions

| Permission | Why |
|---|---|
| `INTERNET`, `ACCESS_NETWORK_STATE` | API, Firebase and Maps |
| `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` | Current location, directions and compass |
| `POST_NOTIFICATIONS` | Push notifications (Android 13+) |
| `CALL_PHONE` | Calling a listing's contact number |

---

## Author

**Dinuka Dilshan**
- GitHub: [@DinukaDilshan415](https://github.com/DinukaDilshan415)

---
