# Cartify 🛒

**Cartify** is a modern, high-performance E-commerce Android application built with Jetpack Compose and Material 3. It leverages a robust clean architecture and the latest Android development practices to provide a premium shopping experience.

---

⚠️ **Project Status: Work in Progress (WIP)**  
*This project is currently under active development. Core features like authentication and product browsing are functional, but some sections are still being refined.*

---

##  Features

- **Luxury UI/UX**: Crafted with Jetpack Compose and Material 3 for a fluid, elegant interface.
- **Seamless Authentication**: Support for Email/Password, Google Sign-In, and Anonymous guest access.
- **Real-time Synchronization**: Cart and Wishlist data sync seamlessly between local Room database and Firebase Firestore.
- **Product Collection**: Browse a curated list of products with pagination (Paging 3) and sorting capabilities.
- **Local Persistence**: Full offline support using Room Database.
- **Clean Architecture**: Organized into data, domain, and presentation layers for maintainability and testability.

## 🛠 Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Theme**: Material 3
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **Backend**: [Firebase](https://firebase.google.com/) (Authentication, Firestore, Storage)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Asynchronous Flow**: Kotlin Coroutines & Flow

##  Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Cartify.git
   ```
2. **Setup Firebase**:
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add your `google-services.json` to the `app/` directory.
   - Enable Authentication (Email, Google, Anonymous) and Firestore.
3. **Configure Google Sign-In**:
   - Add your `WEB_CLIENT_ID` to `local.properties`.
4. **Build and Run**:
   - Open in Android Studio and run on a device or emulator.

##  Upcoming Tasks
- [ ] Order History and Tracking
- [ ] Advanced Product Filtering
- [ ] User Profile Customization
- [ ] Payment Gateway Integration

---
