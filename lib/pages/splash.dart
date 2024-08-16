import 'dart:async';

import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../theme.dart';

class SplashPage extends StatefulWidget {
  const SplashPage({super.key});

  @override
  State<SplashPage> createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> {
  void loadPermission() async {
    late bool hasPermission;
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      hasPermission = prefs.getBool('PERMISSION_LOCATION') ?? false;
    });

    Timer(const Duration(milliseconds: 3000), () {
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
