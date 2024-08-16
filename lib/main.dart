import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:firebase_core/firebase_core.dart';

import 'cubit/connected_cubit.dart';
import 'cubit/place_cubit.dart';
import 'pages/add_place.dart';
import 'pages/home.dart';
import 'pages/location_permission.dart';
import 'pages/search_page.dart';
import 'pages/splash.dart';
import 'pages/started.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Future.delayed(const Duration(milliseconds: 1000));

  MobileAds.instance.initialize();
  await Firebase.initializeApp();
  runApp(Builder(builder: (context) {
    return MyApp(
      connectivity: Connectivity(),
    );
  }));
}

class MyApp extends StatelessWidget {
  final Connectivity connectivity;
  const MyApp({super.key, required this.connectivity});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider(create: (context) => PlaceCubit()),
        BlocProvider(create: (context) => ConnectedCubit(connectivity)),
      ],
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        routes: {
          '/': (context) => const SplashPage(),
          '/location-permission': (context) => const LocationPermission(),
          '/started': (context) => const StartedPage(),
          '/home': (context) => const HomePage(),
          '/add-place': (context) => const AddPlace(),
          '/search': (context) => const SearchPage(),
        },
      ),
    );
  }
}
