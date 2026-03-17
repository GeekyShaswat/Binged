Binged – Movie and Show Streaming App

Binged is a modern Android application that allows users to explore trending movies and shows, discover new content, and view detailed information about films using the TMDB API.
Built with a clean architecture and modern Android development practices.

🚀 Features

🔥 Browse trending and popular movies and shows

🎭 View detailed movie information

🔍 Search for movies and shows

🖼️ High-quality poster and backdrop images

📑 Smooth and modern UI using Jetpack Compose

⚡ Fast and efficient API handling with Retrofit

🛠️ Tech Stack

Kotlin

Jetpack Compose

MVVM Architecture

Retrofit – API calls

Coroutines & Flow – async handling

Room Database – local storage

Coil – image loading

Navigation Compose – screen navigation

DataStore – local preferences

🧱 Architecture

The app follows MVVM (Model-View-ViewModel) architecture:

UI (Compose)
   ↓
ViewModel
   ↓
Repository
   ↓
API (Retrofit) + Local DB (Room)

This ensures:

Separation of concerns

Testability

Scalability

🔑 API Setup

This project uses the TMDB API.

1️⃣ Get your API key

Create an account on TMDB and generate an API key.

2️⃣ Add it to local.properties
TMDB_API_KEY=your_api_key_here
TRAKT_TV_API_KEY=your_api_key_here
3️⃣ BuildConfig Usage

The API key is injected via BuildConfig:

BuildConfig.TMDB_API_KEY
▶️ Getting Started

Clone the repository:

git clone https://github.com/GeekyShaswat/Binged.git

Open in Android Studio

Add API keys in local.properties

Build and run 🚀

📸 Screenshots!
<p align="center">
  <img src="https://github.com/user-attachments/assets/8328a22e-fd8f-42b3-85a6-803c57ed785e" width="220"/>
  <img src="https://github.com/user-attachments/assets/0f18ede1-76d1-4d60-94c8-ebff26d2d4f8" width="220"/>
  <img src="https://github.com/user-attachments/assets/b5f1068a-4dc9-44b6-82d8-3bb213ca59fd" width="220"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/ce0146bd-5eb6-4996-818f-407e8982996b" width="220"/>
  <img src="https://github.com/user-attachments/assets/a119f3ff-8a77-4959-bc5e-73358fe696c7" width="220"/>
  <img src="https://github.com/user-attachments/assets/9c3310ba-ffdc-4cb3-8c2a-d8ac6f6ad4f5" width="220"/>
</p>

🔐 Security Note

API keys are stored using local.properties and injected via BuildConfig to avoid exposing them in the repository.
For production apps, API keys should be further restricted and secured.

👨‍💻 Author

Shaswat Kotnala
Android Developer
