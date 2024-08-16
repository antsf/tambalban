import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../theme.dart';

class LocationPermission extends StatefulWidget {
  const LocationPermission({super.key});

  @override
  State<LocationPermission> createState() => _LocationPermissionState();
}

class _LocationPermissionState extends State<LocationPermission> {
  static const hasPermissionLocationKey = 'PERMISSION_LOCATION';

  @override
  void initState() {
    super.initState();
  }

  void requestPermission() async {
    final prefs = await SharedPreferences.getInstance();
    Map<Permission, PermissionStatus> statuses = await [
      Permission.locationWhenInUse,
    ].request();

    if (statuses[Permission.locationWhenInUse]!.isGranted) {
      print('Location permission is granted');
      prefs.setBool(hasPermissionLocationKey, true);
      Navigator.pushNamedAndRemoveUntil(context, '/started', (route) => false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: whiteColor,
      body: Container(
        padding: const EdgeInsets.symmetric(horizontal: 24.0),
        margin: const EdgeInsets.only(top: 100.0, bottom: 30.0),
        child: Center(
          child: Column(
            children: [
              Icon(
                Icons.location_on_outlined,
                size: 20.0,
                color: greenColor,
              ),
              const SizedBox(
                height: 16.0,
              ),
              Text(
                'Use Your Location',
                style:
                    blackTextStyle.copyWith(fontSize: 20.0, fontWeight: medium),
              ),
              const SizedBox(
                height: 16.0,
              ),
              Text(
                'Aplikasi Tambal Ban Online menggunakan akses lokasi untuk mengaktifkan pencarian lokasi tambal ban terdekat saat aplikasi sedang digunakan.',
                textAlign: TextAlign.center,
                style: blackTextStyle,
              ),
              Expanded(
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(8.0),
                  child: Image.asset(
                    'assets/map.png',
                  ),
                ),
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  TextButton(
                      onPressed: () {
                        Navigator.pushNamedAndRemoveUntil(
                            context, '/started', (route) => false);
                      },
                      child: Text(
                        'Skip',
                        style: grayTextStyle,
                      )),
                  ElevatedButton(
                      onPressed: requestPermission,
                      style: TextButton.styleFrom(backgroundColor: greenColor),
                      child: Text('Turn on', style: whiteTextStyle))
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}
