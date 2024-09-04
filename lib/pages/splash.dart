import 'dart:async';

import 'package:flutter/material.dart';
// import 'package:in_app_update/in_app_update.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../theme.dart';

class SplashPage extends StatefulWidget {
  const SplashPage({super.key});

  @override
  State<SplashPage> createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> {
  // AppUpdateInfo? _updateInfo;
  // Future<void> checkForUpdate() async {
  //   InAppUpdate.checkForUpdate().then((info) {
  //     if (info.updateAvailability == UpdateAvailability.updateAvailable) {
  //       InAppUpdate.performImmediateUpdate();
  //     }
  //     // setState(() {
  //     //   _updateInfo = info;
  //     // });
  //   });
  // }

  void loadPermission() async {
    final prefs = await SharedPreferences.getInstance();

    bool hasPermission = prefs.getBool('PERMISSION_LOCATION') ?? false;
    // setState(() {
    // });

    Future.delayed(const Duration(milliseconds: 3000), () {
      if (hasPermission) {
        Navigator.pushNamedAndRemoveUntil(
          context,
          '/started',
          (route) => false,
        );
      } else {
        Navigator.pushNamedAndRemoveUntil(
          context,
          '/location-permission',
          (route) => false,
        );
      }
    });
  }

  @override
  void initState() {
    loadPermission();
    // checkForUpdate();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: whiteColor,
      body: Center(
          child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
        Image.asset(
          'assets/logo.png',
          width: 120.0,
          height: 120.0,
        ),
        const SizedBox(
          height: 24.0,
        ),
        Text('Tambal Ban \nOnline'.toUpperCase(),
            textAlign: TextAlign.center,
            style: blackTextStyle.copyWith(
              fontSize: 24.0,
              fontWeight: semiBold,
            ))
      ])),
    );
  }
}
