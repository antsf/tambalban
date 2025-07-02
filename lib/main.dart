// import 'package:connectivity_plus/connectivity_plus.dart'; // Keep if needed by other parts, or remove if NetworkInfoImpl handles it
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart'; // Added Riverpod
import 'package:tambal_ban/src/core/theme/app_theme.dart'; // Import new AppTheme
import 'package:tambal_ban/src/features/map_view/presentation/screens/map_view_page.dart'; // Import new home page
// import 'package:tambal_ban/cubit/connected_cubit.dart'; // Removing BLoC
// import 'package:tambal_ban/cubit/place_cubit.dart'; // Removing BLoC
// import 'package:tambal_ban/pages/add_place.dart'; // Old page
// import 'package:tambal_ban/pages/home.dart'; // Old page
// import 'package:tambal_ban/pages/location_permission.dart'; // Old page
// import 'package:tambal_ban/pages/search_page.dart'; // Old page
// import 'package:tambal_ban/pages/splash.dart'; // Old page
// import 'package:tambal_ban/pages/started.dart'; // Old page
import 'package:firebase_core/firebase_core.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  MobileAds.instance.initialize(); // Keep
  await Firebase.initializeApp(); // Keep

  runApp(
    const ProviderScope( // Wrap with ProviderScope
      child: MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    // return MultiBlocProvider( // Removing MultiBlocProvider
    //   providers: [
    //     BlocProvider(create: (context) => PlaceCubit()),
    //     BlocProvider(create: (context) => ConnectedCubit(connectivity)),
    //   ],
    //   child: MaterialApp(
    //     debugShowCheckedModeBanner: false,
    //     routes: {
    //       '/': (context) => const SplashPage(),
    //       '/location-permission': (context) => const LocationPermission(),
    //       '/started': (context) => const StartedPage(),
    //       '/home': (context) => const HomePage(),
    //       '/add-place': (context) => const AddPlace(),
    //       '/search': (context) => const SearchPage(),
    //     },
    //   ),
    // );
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme, // Apply the new theme
      // darkTheme: AppTheme.darkTheme, // Optionally add dark theme
      // themeMode: ThemeMode.system, // Or your preferred theme mode
      home: const MapViewPage(), // Set MapViewPage as the new home
      // TODO: Setup named routes using a router solution like GoRouter later
      // routes: {
      //   // '/': (context) => const SplashPage(), // Old Splash
      //   // '/location-permission': (context) => const LocationPermission(),
      //   // '/started': (context) => const StartedPage(),
      //   // '/home': (context) => const HomePage(),
      //   // '/add-place': (context) => const AddPlace(),
      //   // '/search': (context) => const SearchPage(),
      // },
    );
  }
}
