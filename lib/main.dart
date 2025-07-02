import 'dart:io';

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:in_app_update/in_app_update.dart';

// Auth imports
import 'package:tambal_ban/services/auth_service.dart';
import 'package:tambal_ban/auth_bloc/auth_bloc.dart';

import 'cubit/connected_cubit.dart';
import 'cubit/place_cubit.dart';
import 'pages/add_place.dart';
// import 'pages/home.dart';
import 'pages/home.dart';
import 'pages/location_permission.dart';
// import 'pages/search_page.dart';
import 'pages/splash.dart';
import 'pages/started.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  // It's generally better to initialize Firebase before other plugins if they depend on it.
  // The delay here might not be necessary or could be part of your splash screen logic.
  await Firebase.initializeApp();

  MobileAds.instance.initialize();

  // thing to add
  RequestConfiguration configuration =
      RequestConfiguration(testDeviceIds: ["87E308D9A1E322B38E2750D713AC235A"]);
  MobileAds.instance.updateRequestConfiguration(configuration);

  // No need for the Builder here if MyApp is already a widget
  runApp(MyApp(
    connectivity: Connectivity(),
  ));
}

class MyApp extends StatefulWidget {
  final Connectivity connectivity;
  const MyApp({super.key, required this.connectivity});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  // This widget is the root of your application.
  AppUpdateInfo? _updateInfo;
  Future<void> checkForUpdate() async {
    try {
      if (Platform.isAndroid) {
        InAppUpdate.checkForUpdate().then((info) {
          setState(() {
            _updateInfo = info;
          });
        }).catchError((e) {
          if (kDebugMode) {
            print(e.toString());
          }
        });

        if (_updateInfo != null &&
            _updateInfo!.updateAvailability ==
                UpdateAvailability.updateAvailable) {
          InAppUpdate.performImmediateUpdate().catchError((e) {
            if (kDebugMode) {
              print(e.toString());
            }
            return e;
          });
        }
      }
    } catch (e) {
      if (kDebugMode) {
        print("AppUpdateInfo:$e");
      }
    }
  }

  @override
  void initState() {
    checkForUpdate();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    // Instantiate AuthService here or provide it via a RepositoryProvider if it has no Flutter dependencies
    final AuthService authService = AuthService();

    return MultiBlocProvider(
      providers: [
        // It's good practice to provide services that BLoCs depend on.
        // If AuthService itself doesn't need to be accessed directly by widgets,
        // you might not need a separate Provider for it if AuthBloc handles its creation.
        // However, making it available can be useful for other services or direct calls if necessary.
        // For simplicity here, AuthBloc will instantiate it or receive it.
        // Let's have AuthBloc manage its AuthService instance as per its constructor.
        BlocProvider<AuthBloc>(
          create: (context) => AuthBloc(authService: authService),
        ),
        BlocProvider<PlaceCubit>(create: (context) => PlaceCubit()),
        BlocProvider<ConnectedCubit>(
            create: (context) => ConnectedCubit(widget.connectivity)),
      ],
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        routes: {
          '/': (context) => const SplashPage(),
          '/location-permission': (context) => const LocationPermission(),
          '/started': (context) => const StartedPage(),
          '/home': (context) => const HomePage(),
          '/add-place': (context) => const AddPlace(),
          // '/search': (context) => const SearchPage(),
        },
        // Example: Use a BlocBuilder here to switch between Splash/Home and Auth flow
        // For now, SplashPage handles initial navigation.
        // We might want to listen to AuthBloc state here to redirect to login if unauthenticated
        // after splash, or directly to home if authenticated.
      ),
    );
  }
}
