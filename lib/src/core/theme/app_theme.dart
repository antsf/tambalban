// lib/src/core/theme/app_theme.dart
import 'package:flutter/material.dart';

class AppTheme {
  static ThemeData get lightTheme {
    return ThemeData(
      primarySwatch: Colors.blue, // Example: Google Blue
      scaffoldBackgroundColor: Colors.white,
      fontFamily: 'Sora', // Assuming Sora is set up in pubspec.yaml
      colorScheme: const ColorScheme.light(
        primary: Color(0xFF4285F4), // Google Blue
        secondary: Color(0xFF34A853), // Google Green
        surface: Colors.white,
        background: Colors.white,
        error: Color(0xFFEA4335), // Google Red
        onPrimary: Colors.white,
        onSecondary: Colors.white,
        onSurface: Colors.black,
        onBackground: Colors.black,
        onError: Colors.white,
      ),
      appBarTheme: const AppBarTheme(
        elevation: 0,
        backgroundColor: Color(0xFF4285F4), // Google Blue
        iconTheme: IconThemeData(color: Colors.white),
        titleTextStyle: TextStyle(
          color: Colors.white,
          fontSize: 20,
          fontWeight: FontWeight.w600,
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8.0),
          borderSide: const BorderSide(color: Colors.grey),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8.0),
          borderSide: const BorderSide(color: Color(0xFF4285F4)), // Google Blue
        ),
        labelStyle: const TextStyle(color: Colors.grey),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: const Color(0xFF4285F4), // Google Blue
          foregroundColor: Colors.white,
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
          textStyle: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8.0),
          ),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          side: const BorderSide(color: Color(0xFF4285F4)), // Google Blue
          foregroundColor: const Color(0xFF4285F4), // Google Blue
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
          textStyle: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8.0),
          ),
        ),
      ),
      chipTheme: ChipThemeData(
        backgroundColor: Colors.grey[200],
        selectedColor: const Color(0xFF4285F4), // Google Blue
        labelStyle: TextStyle(color: Colors.grey[700]),
        secondaryLabelStyle: const TextStyle(color: Colors.white),
        padding: const EdgeInsets.symmetric(horizontal: 12.0, vertical: 8.0),
      ),
      // Add other theme properties as needed
    );
  }

  // If you need a dark theme:
  // static ThemeData get darkTheme {
  //   return ThemeData.dark().copyWith(
  //     // Customize dark theme properties
  //   );
  // }
}
