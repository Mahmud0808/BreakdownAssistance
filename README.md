<img src="https://i.ibb.co.com/B41s2Zh/breakdown-assistance-banner.png" alt="Banner" />

# 🚗 Breakdown Assistance

Breakdown Assistance is a versatile Android application designed to provide vehicle servicing support with dual language support (English and Bengali). The app allows users to request vehicle services, mechanics to manage service requests, and admins to oversee operations. Built with Java and powered by Firebase, this app ensures seamless authentication, data management, and storage.

## Features

### 🌐 Multi-language Support
- English
- Bengali

### 🔑 Authentication
- Email login
- Phone login

### 👥 User Roles
- **Users**: Request different vehicle servicings, submit reviews, check nearby garages on the map.
- **Mechanics**: Accept or reject servicing requests, and view the history of all previously handled requests.
- **Admins**: Post new servicing options, add new garage locations on the map, manage user roles, view servicing history, and reviews.

## 🚀 Getting Started

### Prerequisites
- Android Studio
- Google Maps API Key
- Firebase account

### Setup

1. **Clone the repository:**
   ```sh
   git clone https://github.com/Mahmud0808/BreakdownAssistance.git
   cd BreakdownAssistance
   ```

2. **Google Maps API Key:**
   - Obtain your API key from the [Google Cloud Console](https://console.cloud.google.com/apis/credentials).
   - Add the API key to your `AndroidManifest.xml`:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="YOUR_API_KEY_HERE" />
     ```

3. **Firebase Setup:**
   - Go to [Firebase Console](https://console.firebase.google.com).
   - Create a new project.
   - Add your Android app to the Firebase project.

4. **Enable Firebase Services:**
   - **Authentication**: Enable Email/Password and Phone sign-in methods.
   - **Firestore**: Set up Firestore database.
   - **Storage**: Enable Firebase Storage.

5. **Initial Admin Setup:**
   - Create a new user account from the app.
   - Go to Firebase Firestore, find the user under `user_list`.
   - Change the accountType to `ADMIN`. This step is required for the first admin only.
   - Re-login in the app to see the admin changes.

## 📱 Usage

### Users
- **Request Servicing**: Choose the type of service needed and submit a request.
- **Submit Reviews**: Provide feedback on the services received.
- **Nearby Garages**: View and locate nearby garages on the map.

### Mechanics
- **Manage Requests**: Accept or reject servicing requests from users.
- **View History**: View the history of all previously handled requests.

### Admins
- **Post Servicing**: Add new servicing options available for users.
- **Manage Garages**: Add and manage garage locations on the map.
- **User Management**: View user list and change user roles.
- **Service History**: Monitor the history of all servicings.
- **View Reviews**: Access and review feedback provided by users.

## 🤝 Contributing
We welcome contributions to enhance the Breakdown Assistance app. Please fork the repository and submit pull requests.

## 🙏 Acknowledgements
- [Google Maps API](https://developers.google.com/maps)
- [Firebase](https://firebase.google.com)

## 📬 Contact
Wanna reach out to me? DM me at 👇

Email: mahmudul15-13791@diu.edu.bd
