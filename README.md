# Cartify 🛒

**Cartify** is a full-featured e-commerce Android application built with Jetpack Compose and Material Design 3. It demonstrates modern Android development practices including offline-first architecture, real-time cloud sync, and payment gateway integration.

---

## Screenshots

<table>
  <tr>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/Cartify/blob/63c2f98388c38e4af204eb6335944bfae5696f8e/Cartify_Screen_Shots/register.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/Cartify/blob/63c2f98388c38e4af204eb6335944bfae5696f8e/Cartify_Screen_Shots/login.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/Cartify/blob/63c2f98388c38e4af204eb6335944bfae5696f8e/Cartify_Screen_Shots/home_page.png"/></td>
  </tr>
  <tr>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/Cartify/blob/63c2f98388c38e4af204eb6335944bfae5696f8e/Cartify_Screen_Shots/home_page_2.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/Cartify/blob/63c2f98388c38e4af204eb6335944bfae5696f8e/Cartify_Screen_Shots/product_details.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/Cartify/blob/63c2f98388c38e4af204eb6335944bfae5696f8e/Cartify_Screen_Shots/search.png"/></td>
  </tr>
  <tr>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/Cartify/blob/63c2f98388c38e4af204eb6335944bfae5696f8e/Cartify_Screen_Shots/cart.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/Cartify/blob/63c2f98388c38e4af204eb6335944bfae5696f8e/Cartify_Screen_Shots/wishlist.png"/></td>
    <td><img width="200" src="https://github.com/IbrahimAlsaudi/Cartify/blob/63c2f98388c38e4af204eb6335944bfae5696f8e/Cartify_Screen_Shots/profile.png"/></td>
  </tr>
</table>

---

## Features

### 🔐 Authentication
- Email/password login and registration
- Google Sign-In via Credential Manager
- Anonymous guest access with seamless upgrade to a full account
- Forgot password via Firebase email reset

### 🏠 Home & Browsing
- Paginated product feed using **Paging 3 + RemoteMediator** (API → Room → UI)
- Category filtering with a dedicated paging source
- Auto-scrolling banner carousel
- Shimmer loading states
- Wishlist toggle directly from product cards

### 🔍 Search
- Real-time search with debounce and `flatMapLatest` for automatic cancellation of stale queries

### 🛒 Cart
- Add, remove, and adjust item quantities
- Reactive total price calculation
- Room + Firestore sync for real users; Room-only for guests

### ❤️ Wishlist
- Add/remove items with reactive Room Flow
- Paginated from Room
- Room + Firestore sync

### 💳 Payment
- Integrated **Paymob** card payment gateway via the official Android SDK
- Single Intention API call returns a `client_secret` that launches the native payment sheet
- Payment status verified post-callback via Paymob's intention endpoint

### 📦 Orders
- Order creation on payment success — saved atomically to Room and Firestore via batch write
- Cart cleared in the same batch operation
- Order history with Paging 3 from Room
- Order detail screen with status tracking

### 👤 Profile
- Displays user info with initials avatar
- Sign out and account deletion (Auth + Firestore)
- Anonymous upgrade prompt for guest users

---

## Architecture

Cartify follows **Clean Architecture** with an **MVVM** presentation layer, organized into:
```
app/
├── core/
│   ├── data/          # Room entities, DAOs, Retrofit DTOs, Firebase sources
│   ├── domain/        # Domain models, shared mappers
│   └── util/          # Constants, extensions
├── di/                # Hilt modules (Network, Database, Firebase, Repository)
└── feature/
    ├── auth/          # Splash, Login, Register, Forgot Password
    ├── home/          # Home feed, Product Detail, RemoteMediator
    ├── search/        # Search screen
    ├── cart/          # Cart screen + Paymob payment flow
    ├── wishlist/      # Wishlist screen
    ├── orders/        # Order History, Order Detail
    └── profile/       # Profile screen
```

---

### Data Flow
- **Products** → Paging 3 + RemoteMediator (API → Room → UI)
- **Category products** → Direct API paging source (API → UI)
- **Cart & Wishlist** → Room (immediate) + Firestore (background sync)
- **Orders** → Room (primary) + Firestore (source of truth for status)
- **Anonymous users** → Room only, no Firestore writes

### Navigation
Type-safe nested Navigation Compose graphs with independent per-tab back stacks. Bottom bar auto-hides on nested screens.

---

## Tech Stack

| Category | Library |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Architecture | MVVM + Clean Architecture + Repository Pattern |
| DI | Hilt (KSP) |
| Local DB | Room |
| Networking | Retrofit + OkHttp + kotlinx.serialization |
| Pagination | Paging 3 + RemoteMediator |
| Image Loading | Coil |
| Navigation | Navigation Compose (type-safe, nested graphs) |
| Auth | Firebase Auth (Email, Google, Anonymous) |
| Cloud DB | Firestore |
| Payment | Paymob Android SDK |
| Async | Kotlin Coroutines + Flow + StateFlow |
| Build | KSP |

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Min SDK 26 (Android 8.0)

### Setup

1. **Clone the repository**
```bash
   git clone https://github.com/IbrahimAlsaudi/Cartify.git
```

2. **Firebase setup**
   - Create a project in the [Firebase Console](https://console.firebase.google.com/)
   - Add your `google-services.json` to the `app/` directory
   - Enable Authentication (Email/Password, Google, Anonymous) and Firestore

3. **Configure `local.properties`**
   - WEB_CLIENT_ID=your_google_web_client_id
   - PAYMOB_SECRET_KEY=your_paymob_secret_key
   - PAYMOB_PUBLIC_KEY=your_paymob_public_key
   - PAYMOB_INTEGRATION_ID=your_paymob_integration_id

4. **Paymob setup**
   - Create an account at [accept.paymob.com](https://accept.paymob.com)
   - Create a Card (Online) integration and note the Integration ID
   - Copy your Secret Key and Public Key from Settings → Account Info
   - Place the Paymob SDK `.aar` in `app/libs/`

5. **Build and run**
   - Open in Android Studio and run on a device or emulator (API 26+)

---

## API

Product data is sourced from [DummyJSON](https://dummyjson.com) — a free REST API that provides realistic e-commerce data.
