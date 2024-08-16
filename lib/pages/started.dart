import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'package:permission_handler/permission_handler.dart';

import '../ad_helper.dart';
import '../theme.dart';

class StartedPage extends StatefulWidget {
  const StartedPage({super.key});

  @override
  State<StartedPage> createState() => _StartedPageState();
}

class _StartedPageState extends State<StartedPage> {
  bool serviceEnabled = false;
  bool hasPermission = false;
  LocationPermission? permission;
  BannerAd? _bannerAd;

  _initBannerAd() {
    BannerAd(
        adUnitId: AdHelper.bannerAdUnitId,
        request: const AdRequest(),
        size: AdSize.banner,
        listener: BannerAdListener(
          onAdLoaded: (ad) {
            setState(() {
              _bannerAd = ad as BannerAd;
            });
          },
          onAdFailedToLoad: (ad, err) {
            print('Failed to load a banner ad: ${err.message}');
            ad.dispose();
          },
        )).load();
  }

  @override
  void initState() {
    checkGps();
    _initBannerAd();
    super.initState();
  }

  checkPermission() async {
    Map<Permission, PermissionStatus> statuses =
        await [Permission.camera, Permission.storage].request();

    if (statuses[Permission.camera]!.isGranted) {
      print('Camera permission is granted');
    } else if (statuses[Permission.storage]!.isGranted) {
      print('Storage permission is granted');
    }
  }

  checkGps() async {
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (serviceEnabled) {
      permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission == await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) {
          print('Location permissions are permanently denied');
        } else {
          hasPermission = true;
        }
      } else {
        hasPermission = true;
      }
    } else {
      print('GPS Service is not enabled, turn on GPS location.');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          Container(
            width: double.infinity,
            height: double.infinity,
            decoration: const BoxDecoration(
              image: DecorationImage(
                  image: AssetImage('assets/bg.png'), fit: BoxFit.cover),
            ),
          ),
          Container(
            width: double.infinity,
            height: double.infinity,
            padding: const EdgeInsets.only(
                top: 120.0, right: 26.0, bottom: 60.0, left: 26.0),
            decoration: BoxDecoration(
                gradient: LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    colors: [greenColor, greenColor.withOpacity(0)])),
            child: Column(
              children: [
                Text(
                  'Aplikasi Tambal Ban Online',
                  style: TextStyle(
                      fontFamily: 'Oswald',
                      fontSize: 50.0,
                      fontWeight: FontWeight.w600,
                      color: whiteColor),
                ),
                const Spacer(),
                GestureDetector(
                  onTap: () {
                    setState(() {
                      checkGps();
                    });
                    Navigator.pushNamed(context, '/home');
                  },
                  child: Container(
                    height: 50.0,
                    width: 250.0,
                    padding: const EdgeInsets.symmetric(horizontal: 26.0),
                    decoration: BoxDecoration(
                        gradient: LinearGradient(colors: [
                          greenColor.withOpacity(0.5),
                          greenColor,
                        ]),
                        borderRadius: BorderRadius.circular(30.0),
                        boxShadow: [
                          BoxShadow(
                              color: greenColor.withOpacity(0.5),
                              offset: const Offset(0, 10),
                              blurRadius: 30.0)
                        ]),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          'Cari Tambal Ban',
                          style: whiteTextStyle.copyWith(
                              fontSize: 18.0, fontWeight: semiBold),
                        ),
                        Icon(
                          Icons.arrow_right_alt,
                          color: whiteColor,
                        )
                      ],
                    ),
                  ),
                ),
                const SizedBox(
                  height: 16.0,
                ),
                Text(
                  'atau',
                  style: whiteTextStyle,
                ),
                TextButton(
                  onPressed: () {
                    Navigator.pushNamed(context, '/add-place');
                  },
                  child: Text(
                    'Tambah tambal ban',
                    style: whiteTextStyle.copyWith(
                        fontSize: 16.0,
                        fontWeight: light,
                        decoration: TextDecoration.underline),
                  ),
                )
              ],
            ),
          ),
          // display bannerAds when ready
          if (_bannerAd != null)
            Align(
              alignment: Alignment.topCenter,
              child: SizedBox(
                width: _bannerAd!.size.width.toDouble(),
                height: _bannerAd!.size.height.toDouble(),
                child: AdWidget(ad: _bannerAd!),
              ),
            )
        ],
      ),
    );
  }
}
