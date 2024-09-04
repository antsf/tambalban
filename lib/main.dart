import 'dart:io';

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:in_app_update/in_app_update.dart';

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
  await Future.delayed(const Duration(milliseconds: 1000));

  MobileAds.instance.initialize();

  // thing to add
  RequestConfiguration configuration =
      RequestConfiguration(testDeviceIds: ["87E308D9A1E322B38E2750D713AC235A"]);
  MobileAds.instance.updateRequestConfiguration(configuration);

  await Firebase.initializeApp();
  runApp(Builder(builder: (context) {
    return MyApp(
      connectivity: Connectivity(),
    );
  }));
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
    return MultiBlocProvider(
      providers: [
        BlocProvider(create: (context) => PlaceCubit()),
        BlocProvider(create: (context) => ConnectedCubit(widget.connectivity)),
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
      ),
    );
  }
}
