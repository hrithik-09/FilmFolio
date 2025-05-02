# 🎬 FilmFolio - Your Ultimate Movie Tracker

**FilmFolio** is a beautifully designed Android application that helps users explore, track, and manage their favorite movies. Powered by [TMDB](https://www.themoviedb.org/), FilmFolio provides a seamless experience to discover trending films, search with advanced filters, manage wishlists, and set reminders for upcoming movies — all in a modern, dark-themed UI.

---

## 🚀 Features

- 🔍 **Movie Discovery**
  - Browse popular, top-rated, and upcoming movies
  - View detailed movie info, cast, rating, and production info

- 🧠 **Smart Filtering**
  - Filter by genre, release year, rating, language, and sort order using a Bottom Sheet UI with tabbed Chips and sliders

- ❤️ **Wishlist Management**
  - Add/remove movies to your personal wishlist

- ⏰ **Smart Reminders**
  - Set reminders for movies and receive:
    - Notifications 1 hour before
    - Alarms with **Snooze/Dismiss** options at the exact time

- 🔁 **Real-Time Sync**
  - All reminders are synced across devices using **Firebase Cloud Firestore**

- 🔐 **Google Sign-In**
  - Secure login with Google account and personalized experience

- 🌙 **Dark Theme**
  - Modern maroon-accented dark theme UI 

---

## 🧱 Architecture

FilmFolio follows the **MVVM (Model-View-ViewModel)** architecture with clear separation of concerns and reactive design patterns.

### Layers:
- **View**: Activities, Fragments, and UI components
- **ViewModel**: Holds UI state and communicates with Repository
- **Repository**: Abstracts data sources (TMDB API, Room DB, Firebase)
- **Model**: POJO classes for Movie, Reminder, User

---

## 🧰 Tech Stack

| Technology         | Purpose                         |
|--------------------|---------------------------------|
| Java               | Primary language                |
| MVVM               | App architecture                |
| Retrofit           | Networking with TMDB API        |
| Glide              | Image loading and caching       |
| Room DB            | Local database for reminders    |
| Firebase Firestore | Cross-device sync of reminders  |
| Firebase Auth      | Google Sign-In authentication   |
| LiveData & ViewModel | Lifecycle-aware state mgmt    |
| ConstraintLayout & BottomSheet | Dynamic UI/UX       |

---

## 🔧 Setup Instructions

### Prerequisites
- Android Studio (latest recommended)
- A valid TMDB API key
- Firebase project with Firestore and Authentication enabled

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/filmfolio.git
cd filmfolio
```
### 2. Add Your API Keys
### 3. Connect to Firebase
- Add your google-services.json file to /app
- Ensure Firebase dependencies are added in build.gradle

### 4. Build and Run
- Open the project in Android Studio
- Sync Gradle and click Run

## 📂 Project Structure

```
com.rkdigital.filmfolio
├── model/             # POJO classes for Movie, Reminder, User
├── view/              # Activities and Fragments
├── viewmodel/         # LiveData-backed ViewModels
├── repository/        # TMDB and Firebase data operations
├── db/                # Room DB setup for local reminders
```
## 💡 Acknowledgements
- TMDB for the movie data API
- Firebase for authentication and real-time sync
- Icons from Material Design

## 🎥 FilmFolio — Because movie lovers deserve a smarter watchlist.
