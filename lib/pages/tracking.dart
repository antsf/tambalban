import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_polyline_points/flutter_polyline_points.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';

import '../ad_helper.dart';
import '../cubit/connected_cubit.dart';
import '../theme.dart';

class TrackingPage extends StatefulWidget {
  final Map<dynamic, dynamic> place;
  const TrackingPage(
    this.place, {
    super.key,
  });

  @override
  State<TrackingPage> createState() => _DetailPageState();
}

class _DetailPageState extends State<TrackingPage> {
  GoogleMapController? mapController;
  PolylinePoints polylinePoints = PolylinePoints();
  String googleApiKey = 'AIzaSyA88My8vsi2jeb9_AWZ74Fiyq_rLUJ7ezc';
  Set<Marker> markers = {};
  Map<PolylineId, Polyline> polylines = {};
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

  void onMapCreated(GoogleMapController controller) {
    mapController = controller;
  }

  @override
  void initState() {
    addMarkers();
    getDirections();
    _initBannerAd();
    super.initState();
  }

  addMarkers() async {
    BitmapDescriptor customMarker = await BitmapDescriptor.asset(
        const ImageConfiguration(), 'assets/custom-mark.png');
    markers.add(Marker(
        markerId: MarkerId(widget.place['items'].latitude.toString()),
        position: LatLng(
            widget.place['items'].latitude, widget.place['items'].longitude),
        infoWindow: InfoWindow(
          title: capitalize(widget.place['items'].name),
        ),
        icon: customMarker));
  }

  getDirections() async {
    List<LatLng> polylineCoordinates = [];
    PolylineResult result = await polylinePoints.getRouteBetweenCoordinates(
      googleApiKey: googleApiKey,
      request: PolylineRequest(
        origin: PointLatLng(widget.place['lat'], widget.place['long']),
        destination: PointLatLng(
            widget.place['items'].latitude, widget.place['items'].longitude),
        mode: TravelMode.driving,
        wayPoints: [PolylineWayPoint(location: "Sabo, Yaba Lagos Nigeria")],
      ),
    );

    if (result.points.isNotEmpty) {
      for (var point in result.points) {
        polylineCoordinates.add(LatLng(point.latitude, point.longitude));
      }
    } else {
      print(result.errorMessage);
    }
    addPolyline(polylineCoordinates);
  }

  addPolyline(List<LatLng> polylineCoordinates) {
    PolylineId id = const PolylineId('poly');
    Polyline polyline = Polyline(
        polylineId: id,
        color: Colors.deepPurpleAccent,
        points: polylineCoordinates,
        width: 5);
    polylines[id] = polyline;
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<ConnectedCubit, ConnectedState>(
        builder: (context, state) {
      if ((state is ConnectedSuccess &&
              state.connectionType == ConnectionType.wifi) ||
          (state is ConnectedSuccess &&
              state.connectionType == ConnectionType.mobile)) {
        return Scaffold(
          backgroundColor: whiteColor,
          appBar: AppBar(
            backgroundColor: greenColor,
            title: Text(
              capitalize(widget.place['items'].name),
              style: whiteTextStyle.copyWith(fontSize: 18.0),
              overflow: TextOverflow.ellipsis,
              maxLines: 1,
            ),
          ),
          body: Stack(children: [
            GoogleMap(
              onMapCreated: onMapCreated,
              myLocationEnabled: true,
              initialCameraPosition: CameraPosition(
                  target: LatLng(widget.place['items'].latitude,
                      widget.place['items'].longitude),
                  zoom: 12.0),
              markers: markers,
              polylines: Set<Polyline>.of(polylines.values),
              mapType: MapType.normal,
            ),
            if (_bannerAd != null)
              Align(
                alignment: Alignment.bottomCenter,
                child: SizedBox(
                  width: _bannerAd!.size.width.toDouble(),
                  height: _bannerAd!.size.height.toDouble(),
                  child: AdWidget(ad: _bannerAd!),
                ),
              )
          ]),
        );
      }
      return Scaffold(
        backgroundColor: whiteColor,
        body: Center(
            child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Internet tidak terhubung',
              style: blackTextStyle,
            ),
            const SizedBox(
              height: 12.0,
            ),
            TextButton(
                onPressed: () {
                  context.read<ConnectedCubit>().connectivityStreamSubcription;
                },
                style: TextButton.styleFrom(backgroundColor: greenColor),
                child: Text('Muat Ulang', style: whiteTextStyle)),
          ],
        )),
      );
    });
  }
}
