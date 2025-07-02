import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:flutter_map_animations/flutter_map_animations.dart';
import 'package:flutter_map_tappable_polyline/flutter_map_tappable_polyline.dart';
// import 'package:flutter_polyline_points/flutter_polyline_points.dart';
import 'package:geolocator/geolocator.dart';
// import 'package:geolocator/geolocator.dart';
import 'package:latlong2/latlong.dart';
// import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:url_launcher/url_launcher_string.dart';

import '../cubit/connected_cubit.dart';
import '../theme.dart';
// import 'tracking.dart';

class DetailPage extends StatefulWidget {
  final Map<dynamic, dynamic> place;
  const DetailPage(
    this.place, {
    super.key,
  });

  @override
  State<DetailPage> createState() => _DetailPageState();
}

class _DetailPageState extends State<DetailPage>
    with SingleTickerProviderStateMixin {
  // GoogleMapController? mapController;
  // PolylinePoints polylinePoints = PolylinePoints();
  String googleApiKey = 'AIzaSyA88My8vsi2jeb9_AWZ74Fiyq_rLUJ7ezc';
  List<AnimatedMarker> markers = [];
  // Map<PolylineId, Polyline> polylines = {};
  double top = 0.0;
  bool isLoading = false;
  Position? position;
  double lat = 0, long = 0;
  LatLng center = const LatLng(-6.907731, 109.730173);
  late final _animatedMapController = AnimatedMapController(vsync: this);

  // void _onMapCreated(GoogleMapController controller) {
  //   mapController = controller;
  // }

  @override
  void initState() {
    getLocation();
    // getDirections();
    super.initState();
  }

  void getLocation() async {
    position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high);

    lat = position!.latitude;
    long = position!.longitude;

    // LocationSettings locationSettings = const LocationSettings(
    //   accuracy: LocationAccuracy.high,
    //   distanceFilter: 100,
    // );

    // StreamSubscription<Position> positionStream =
    //     Geolocator.getPositionStream(locationSettings: locationSettings)
    //         .listen((Position position) {});
    // lat = position!.latitude;
    // long = position!.longitude;

    setState(() {
      if (position == null) {
        isLoading = true;
      } else {
        center = LatLng(position!.latitude, position!.longitude);
      }
    });

    addMarkers();

    // _animatedMapController.mapController.move(
    //   center,
    //   13.0,
    // );
    // mapController?.animateCamera(CameraUpdate.newCameraPosition(CameraPosition(
    //   target: center,
    //   zoom: 15.0,
    // )));
  }

  addMarkers() async {
    markers.addAll([
      AnimatedMarker(
        point: center,
        width: 40.0,
        height: 40.0,
        builder: (_, animation) {
          final size = 32.0 * animation.value;
          return Icon(
            Icons.person_pin_circle_rounded,
            size: size,
            color: Colors.blue,
          );
        },
      ),
      AnimatedMarker(
        point: LatLng(
            widget.place['items'].latitude, widget.place['items'].longitude),
        // width: 40.0,
        // height: 40.0,
        builder: (_, animation) {
          final size = 32.0 * animation.value;
          return Image.asset(
            'assets/custom-mark.png',
            width: size,
            scale: 1.5,
          );
        },
      )
    ]);
    //   BitmapDescriptor customMarker = await BitmapDescriptor.asset(
    //       const ImageConfiguration(), 'assets/custom-mark.png');
    //   String name = widget.place['items'].name;
    //   markers.add(Marker(
    //       markerId: MarkerId(widget.place['items'].latitude.toString()),
    //       position: LatLng(
    //           widget.place['items'].latitude, widget.place['items'].longitude),
    //       infoWindow: InfoWindow(
    //         title: capitalize(name),
    //       ),
    //       icon: customMarker));
  }

  // getDirections() async {
  //   List<LatLng> polylineCoordinates = [];
  //   PolylineResult result = await polylinePoints.getRouteBetweenCoordinates(
  //     googleApiKey: googleApiKey,
  //     request: PolylineRequest(
  //       origin: PointLatLng(widget.place['lat'], widget.place['long']),
  //       destination: PointLatLng(
  //           widget.place['items'].latitude, widget.place['items'].longitude),
  //       mode: TravelMode.driving,
  //       wayPoints: [PolylineWayPoint(location: "Sabo, Yaba Lagos Nigeria")],
  //     ),
  //   );

  //   if (result.points.isNotEmpty) {
  //     for (var point in result.points) {
  //       polylineCoordinates.add(LatLng(point.latitude, point.longitude));
  //     }
  //   } else {
  //     print(result.errorMessage);
  //   }
  //   addPolyline(polylineCoordinates);
  // }

  // addPolyline(List<LatLng> polylineCoordinates) {
  //   PolylineId id = const PolylineId('poly');
  //   Polyline polyline = Polyline(
  //       polylineId: id,
  //       color: Colors.deepPurpleAccent,
  //       points: polylineCoordinates,
  //       width: 5);
  //   polylines[id] = polyline;
  //   setState(() {});
  // }

  @override
  Widget build(BuildContext context) {
    // final PlaceModel placeModel = widget.place['items'] as PlaceModel; // Assuming widget.place['items'] is indeed a PlaceModel
    // It's safer to cast, or ensure the type from the source.
    // For now, assuming it's PlaceModel based on previous usage.
    // If widget.place['items'] is Map, then we need PlaceModel.fromMap(widget.place['items'])
    // From SearchPage, widget.place is Map<dynamic,dynamic> where 'items' is PlaceModel
    final PlaceModel placeModel = widget.place['items'] as PlaceModel;
    final double distance = widget.place['distance'] as double? ?? -1.0;


    // String name = placeModel.name; // Use placeModel directly
    // String phoneNumber = placeModel.phoneNumber;
    // List<dynamic> vehicles = placeModel.vehicles; // This is List<String> now in PlaceModel

    return BlocBuilder<ConnectedCubit, ConnectedState>(
        builder: (context, state) {
      if ((state is ConnectedSuccess &&
              state.connectionType == ConnectionType.wifi) ||
          (state is ConnectedSuccess &&
              state.connectionType == ConnectionType.mobile)) {
        return Scaffold(
            backgroundColor: whiteColor,
            body: CustomScrollView(
              slivers: [
                SliverAppBar(
                    expandedHeight: 300.0,
                    backgroundColor: greenColor,
                    pinned: true,
                    leading: BackButton(
                      color: whiteColor,
                      onPressed: () => Navigator.pop(context),
                    ),
                    actions: [
                      IconButton(
                        icon: Icon(Icons.favorite_border, color: whiteColor), // Save icon (can be Icons.favorite for saved state)
                        onPressed: () {
                          // TODO: Implement save functionality
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Save feature not implemented yet.')),
                          );
                        },
                      ),
                    ],
                    flexibleSpace: LayoutBuilder(
                      builder: (context, constraints) {
                        top = constraints.biggest.height;
                        return FlexibleSpaceBar(
                          background: Stack(
                            children: [
                              Container(
                                width: double.infinity,
                                decoration: BoxDecoration(
                                    image: DecorationImage(
                                        image: NetworkImage(
                                          placeModel.imageUrl, // Use placeModel
                                        ),
                                        fit: BoxFit.cover)),
                              ),
                              Container(
                                  width: double.infinity,
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: 26.0, vertical: 16.0),
                                  decoration: BoxDecoration(
                                      gradient: LinearGradient(
                                          begin: Alignment.topCenter,
                                          end: Alignment.bottomCenter,
                                          colors: [
                                        blackColor.withOpacity(0),
                                        blackColor.withOpacity(0.7)
                                      ])))
                            ],
                          ),
                          title: Column(
                            crossAxisAlignment: top !=
                                    MediaQuery.of(context).padding.top +
                                        kToolbarHeight
                                ? CrossAxisAlignment.start
                                : CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              AnimatedOpacity(
                                  duration: const Duration(milliseconds: 300),
                                  opacity: top !=
                                          MediaQuery.of(context).padding.top +
                                              kToolbarHeight
                                      ? 1.0
                                      : 0.0,
                                  child: SizedBox(
                                    child: Row(
                                      children: [
                                        Flexible(
                                          child: Row(
                                            children: [
                                              Icon(Icons.watch_later_outlined,
                                                  size: 12.0,
                                                  color: whiteColor),
                                              const SizedBox(
                                                width: 4.0,
                                              ),
                                              Flexible(
                                                child: Text(
                                                  placeModel.openTime, // Use placeModel
                                                  overflow:
                                                      TextOverflow.ellipsis,
                                                  maxLines: 1,
                                                  style:
                                                      whiteTextStyle.copyWith(
                                                          fontSize: 9.0,
                                                          fontWeight: medium,
                                                          color: whiteColor),
                                                ),
                                              )
                                            ],
                                          ),
                                        ),
                                        const SizedBox(
                                          width: 10.0,
                                        ),
                                        Flexible(
                                          child: Row(
                                            children: [
                                              Icon(Icons.near_me_outlined,
                                                  size: 12.0,
                                                  color: whiteColor),
                                              const SizedBox(
                                                width: 4.0,
                                              ),
                                              Text(
                                                  distance >= 0 ? '${distance.toStringAsFixed(2)} km' : 'N/A', // Use distance
                                                  style:
                                                      whiteTextStyle.copyWith(
                                                    fontSize: 9.0,
                                                    fontWeight: medium,
                                                  ))
                                            ],
                                          ),
                                        ),
                                      ],
                                    ),
                                  )),
                              const SizedBox(
                                height: 6.0,
                              ),
                              Padding(
                                padding: EdgeInsets.only(
                                    left: top !=
                                            MediaQuery.of(context).padding.top +
                                                kToolbarHeight
                                        ? 0
                                        : 32.0),
                                child: Text(capitalize(placeModel.name), // Use placeModel
                                    style: whiteTextStyle.copyWith(
                                        fontSize: 16.0, fontWeight: semiBold),
                                    overflow: TextOverflow.ellipsis,
                                    maxLines: top !=
                                            MediaQuery.of(context).padding.top +
                                                kToolbarHeight
                                        ? 2
                                        : 1),
                              )
                            ],
                          ),
                          titlePadding: const EdgeInsets.all(16.0),
                        );
                      },
                    )),
                SliverToBoxAdapter(
                  child: Padding( // Changed Container to Padding for semantic correctness
                    padding: const EdgeInsets.all(16.0), // Consistent padding
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start, // Align content to start
                      children: [
                        // WorkshopInfo section (Name, Rating, Tags, Address, Hours)
                        _buildWorkshopInfoSection(placeModel, distance),
                        const SizedBox(height: 24),

                        // BadgeRow section
                        _buildBadgeRow(placeModel),
                        const SizedBox(height: 24),

                        // "Kendaraan" section (already exists, can be part of WorkshopInfo or BadgeRow)
                        Text('Kendaraan:', style: blackTextStyle.copyWith(fontSize: 16.0, fontWeight: semiBold)),
                        const SizedBox(height: 10.0),
                        Wrap( // Use Wrap for better layout if many vehicles
                            spacing: 10.0,
                            runSpacing: 10.0,
                            children: placeModel.vehicles.map((item) { // item is String
                                          return Container(
                                            padding: const EdgeInsets.all(10.0),
                                            width: 90.0, // Consider dynamic width or Chip widget
                                            decoration: BoxDecoration(
                                                color: const Color(0xffffffff),
                                                boxShadow: [
                                                  BoxShadow(
                                                    color: greenColor.withOpacity(0.4),
                                                    blurRadius: 12.0,
                                                    offset: const Offset(0, 4.0),
                                                  )
                                                ],
                                                borderRadius: BorderRadius.circular(10.0)),
                                            child: Column(
                                              children: [
                                                Image.asset(
                                                  'assets/$item.png', // Assumes item matches asset name (e.g. "Mobil.png")
                                                  width: 32.0,
                                                  semanticLabel: 'kendaraan $item',
                                                  errorBuilder: (context, error, stackTrace) => Icon(Icons.directions_car, size: 32), // Fallback icon
                                                ),
                                                const SizedBox(height: 4.0),
                                                Text(item, style: blackTextStyle.copyWith(fontSize: 12.0, fontWeight: medium)),
                                              ],
                                            ),
                                          );
                                        }).toList()),
                        const SizedBox(height: 24.0),

                        // "Terima Panggilan" (Home Service) Section
                        Text('Terima Panggilan:', style: blackTextStyle.copyWith(fontSize: 16.0, fontWeight: semiBold)),
                        const SizedBox(height: 10.0),
                        Row(
                          children: [
                            Icon(
                              placeModel.homeService ? Icons.check_box : Icons.check_box_outline_blank, // Use placeModel
                              color: placeModel.homeService ? greenColor : grayColor,
                            ),
                            const SizedBox(width: 8.0),
                            Text(placeModel.homeService ? 'Ya' : 'Tidak', style: blackTextStyle), // Use placeModel
                          ],
                        ),
                        const SizedBox(height: 24.0),

                        // "Layanan Lain" (Services) Section
                        Text('Layanan Lain:', style: blackTextStyle.copyWith(fontSize: 16.0, fontWeight: semiBold)),
                        const SizedBox(height: 10.0),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Icon(Icons.build_circle_outlined, color: greenColor, size: 20),
                            const SizedBox(width: 8.0),
                            Expanded(child: Text(placeModel.services.isNotEmpty ? placeModel.services : 'Tidak ada layanan tambahan yang disebutkan.', style: blackTextStyle)), // Use placeModel
                          ],
                        ),
                        const SizedBox(height: 24.0),

                        // Map Preview & External Link
                        _buildMapSection(context, placeModel),
                        const SizedBox(height: 24.0),

                        // Action Buttons
                        _buildActionButtons(context, placeModel),
                        const SizedBox(height: 80), // Space for FAB or bottom content
                      ],
                    ),
                  ),
                )
              ],
            ),
            floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
            floatingActionButton: FloatingActionButton.extended(
              backgroundColor: placeModel.phoneNumber.isNotEmpty && (placeModel.phoneNumber[0] == '0' || placeModel.phoneNumber[0] == '+')
                  ? greenColor
                  : grayColor.withOpacity(0.8),
              shape: const StadiumBorder(),
              onPressed: placeModel.phoneNumber.isNotEmpty && (placeModel.phoneNumber[0] == '0' || placeModel.phoneNumber[0] == '+')
                  ? () async {
                      String currentPhoneNumber = placeModel.phoneNumber;
                      var firstLetter = currentPhoneNumber[0];
                      var idPhoneCode = firstLetter == '0'
                          ? currentPhoneNumber.replaceFirst(firstLetter, '+62')
                          : currentPhoneNumber;
                      var urlWhatsapp = "whatsapp://send?phone=$idPhoneCode";
                      // For testing, can use https link: var urlWhatsapp = "https://wa.me/$idPhoneCode";
                      if (await canLaunchUrlString(urlWhatsapp)) {
                        await launchUrlString(urlWhatsapp);
                      } else {
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text('Could not launch WhatsApp for $idPhoneCode')),
                        );
                      }
                    }
                  : null,
              label: Text(
                placeModel.phoneNumber.isNotEmpty ? placeModel.phoneNumber : "No Phone",
                style: whiteTextStyle,
              ),
              icon: Icon(
                Icons.phone_outlined, // Changed to outlined
                                            padding: const EdgeInsets.all(10.0),
                                            width: 90.0,
                                            decoration: BoxDecoration(
                                                color: const Color(0xffffffff),
                                                boxShadow: [
                                                  BoxShadow(
                                                    color: greenColor
                                                        .withOpacity(0.4),
                                                    blurRadius: 12.0,
                                                    offset:
                                                        const Offset(0, 4.0),
                                                  )
                                                ],
                                                borderRadius:
                                                    BorderRadius.circular(
                                                        10.0)),
                                            child: Column(
                                              children: [
                                                Image.asset(
                                                  'assets/$item.png',
                                                  width: 32.0,
                                                  semanticLabel: 'kendaraan',
                                                ),
                                                const SizedBox(
                                                  height: 4.0,
                                                ),
                                                Text(item,
                                                    style:
                                                        blackTextStyle.copyWith(
                                                            fontSize: 12.0,
                                                            fontWeight: medium,
                                                            color: blackColor)),
                                              ],
                                            ),
                                          );
                                        }).toList()))
                              ],
                            )),
                        Container(
                            margin:
                                const EdgeInsets.only(top: 24.0, bottom: 16.0),
                            width: double.infinity,
                            child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text('Terima Panggilan:',
                                      style: blackTextStyle.copyWith(
                                          fontSize: 16.0,
                                          fontWeight: semiBold)),
                                  const SizedBox(
                                    height: 10.0,
                                  ),
                                  Row(
                                    children: [
                                      widget.place['items'].homeService
                                          ? Icon(
                                              Icons.check_box,
                                              color: greenColor,
                                            )
                                          : const Icon(
                                              Icons.call_rounded,
                                              color: Colors.red,
                                            ),
                                      const SizedBox(
                                        width: 4.0,
                                      ),
                                      Text(
                                        widget.place['items'].homeService
                                            ? 'Ya'
                                            : 'Tidak',
                                        style: blackTextStyle,
                                      )
                                    ],
                                  )
                                ])),
                        Container(
                            margin: const EdgeInsets.only(bottom: 16.0),
                            width: double.infinity,
                            child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text('Layanan Lain:',
                                      style: blackTextStyle.copyWith(
                                          fontSize: 16.0,
                                          fontWeight: semiBold)),
                                  const SizedBox(
                                    height: 10.0,
                                  ),
                                  Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Icon(
                                        Icons.build_circle,
                                        color: greenColor,
                                      ),
                                      const SizedBox(
                                        width: 4.0,
                                      ),
                                      Flexible(
                                        child: Text(
                                          widget.place['items'].services,
                                          style: blackTextStyle,
                                        ),
                                      )
                                    ],
                                  )
                                ])),
                        Container(
                            margin: const EdgeInsets.only(bottom: 16.0),
                            width: double.infinity,
                            child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text('Lokasi:',
                                      style: blackTextStyle.copyWith(
                                          fontSize: 16.0,
                                          fontWeight: semiBold)),
                                  const SizedBox(
                                    height: 10.0,
                                  ),
                                  Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Icon(
                                        Icons.location_on,
                                        color: greenColor,
                                      ),
                                      const SizedBox(
                                        width: 4.0,
                                      ),
                                      Flexible(
                                        child: Text(
                                          capitalize(
                                              widget.place['items'].address),
                                          style: blackTextStyle,
                                        ),
                                      )
                                    ],
                                  ),
                                  const SizedBox(
                                    height: 10.0,
                                  ),
                                  Container(
                                    width: double.infinity,
                                    height: 300.0,
                                    decoration: BoxDecoration(
                                        borderRadius:
                                            BorderRadius.circular(10.0)),
                                    child: FlutterMap(
                                      mapController:
                                          _animatedMapController.mapController,
                                      options: MapOptions(
                                        initialCenter: LatLng(
                                            widget.place['items'].latitude,
                                            widget.place['items']
                                                .longitude), // Center the map over London
                                        initialZoom: 9.2,
                                        // onMapReady: () {
                                        //   _animatedMapController
                                        //       .mapController.mapEventStream
                                        //       .listen((evt) {
                                        //     _animatedMapController.mapController
                                        //         .move(center, 8);
                                        //   }); // for example
                                        //   // Any* other `MapController` dependent methods
                                        // },
                                      ),
                                      children: [
                                        TileLayer(
                                          // Display map tiles from any source
                                          urlTemplate:
                                              'https://tile.openstreetmap.org/{z}/{x}/{y}.png', // OSMF's Tile Server
                                          userAgentPackageName:
                                              'com.example.app',
                                          maxNativeZoom:
                                              19, // Scale tiles when the server doesn't support higher zoom levels
                                          // And many more recommended properties!
                                        ),
                                        const RichAttributionWidget(
                                          // Include a stylish prebuilt attribution widget that meets all requirments
                                          attributions: [
                                            TextSourceAttribution(
                                              'OpenStreetMap contributors',
                                              // onTap: () => launchUrl(Uri.parse('https://openstreetmap.org/copyright')), // (external)
                                            ),
                                            // Also add images...
                                          ],
                                        ),
                                        TappablePolylineLayer(
                                            // Will only render visible polylines, increasing performance
                                            polylineCulling: true,
                                            pointerDistanceTolerance: 20,
                                            polylines: [
                                              TaggedPolyline(
                                                tag: 'My Polyline',
                                                // An optional tag to distinguish polylines in callback
                                                points: [
                                                  center,
                                                  LatLng(
                                                      widget.place['items']
                                                          .latitude,
                                                      widget.place['items']
                                                          .longitude)
                                                ],
                                                color: greenColor,
                                                strokeWidth: 3.0,
                                              ),
                                              // TaggedPolyline(
                                              //   tag: 'My 2nd Polyline',
                                              //   // An optional tag to distinguish polylines in callback
                                              //   points: getPoints(1),
                                              //   color: Colors.black,
                                              //   strokeWidth: 3.0,
                                              // ),
                                              // TaggedPolyline(
                                              //   tag: 'My 3rd Polyline',
                                              //   // An optional tag to distinguish polylines in callback
                                              //   points: getPoints(0),
                                              //   color: Colors.blue,
                                              //   strokeWidth: 3.0,
                                              // ),
                                            ],
                                            onTap: (polylines, tapPosition) =>
                                                print(
                                                    'Tapped: ${polylines.map((polyline) => polyline.tag).join(',')} at ${tapPosition.globalPosition}'),
                                            onMiss: (tapPosition) {
                                              print(
                                                  'No polyline was tapped at position ${tapPosition.globalPosition}');
                                            }),
                                        AnimatedMarkerLayer(markers: markers),
                                      ],
                                    ),
                                    // child: GoogleMap(
                                    //   onMapCreated: _onMapCreated,
                                    //   myLocationEnabled: true,
                                    //   liteModeEnabled: true,
                                    //   initialCameraPosition: CameraPosition(
                                    //       target: LatLng(
                                    //           widget.place['items'].latitude,
                                    //           widget.place['items'].longitude),
                                    //       zoom: 12.0),
                                    //   markers: markers,
                                    //   polylines:
                                    //       Set<Polyline>.of(polylines.values),
                                    //   mapType: MapType.normal,
                                    // ),
                                  ),
                                ])),
                      ],
                    ),
                  ),
                )
              ],
            ),
            floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
            floatingActionButton: FloatingActionButton.extended(
              backgroundColor: phoneNumber[0] == '0' || phoneNumber[0] == '+'
                  ? greenColor
                  : grayColor.withOpacity(0.8),
              shape: const StadiumBorder(),
              onPressed: phoneNumber[0] == '0' || phoneNumber[0] == '+'
                  ? () async {
                      var firstLetter = phoneNumber[0];
                      var idPhoneCode = firstLetter == '0'
                          ? phoneNumber.replaceFirst(firstLetter, '+62')
                          : phoneNumber;
                      var urlWhatsapp = "whatsapp://send?phone=$idPhoneCode";
                      if (Platform.isAndroid) {
                        await launchUrlString(urlWhatsapp);
                      }
                    }
                  : null,
              label: Text(
                phoneNumber,
                style: whiteTextStyle,
              ),
              icon: Icon(
                Icons.phone,
                color: whiteColor,
              ),
            )

            // floatingActionButton: Padding(
            //   padding: const EdgeInsets.all(16.0),
            //   child: Row(
            //     children: [
            //       Expanded(
            //         flex: 5,
            //         child: TextButton.icon(
            //           onPressed: phoneNumber[0] == '0' || phoneNumber[0] == '+'
            //               ? () async {
            //                   var firstLetter = phoneNumber[0];
            //                   var idPhoneCode = firstLetter == '0'
            //                       ? phoneNumber.replaceFirst(firstLetter, '+62')
            //                       : phoneNumber;
            //                   var urlWhatsapp =
            //                       "whatsapp://send?phone=$idPhoneCode";
            //                   if (Platform.isAndroid) {
            //                     await launchUrlString(urlWhatsapp);
            //                   }
            //                 }
            //               : null,
            //           style: TextButton.styleFrom(
            //             elevation: 5,
            //             fixedSize: const Size(double.infinity, 50.0),
            //             shape: const RoundedRectangleBorder(
            //                 borderRadius: BorderRadius.only(
            //                     topLeft: Radius.circular(30),
            //                     bottomLeft: Radius.circular(30))),
            //             backgroundColor:
            //                 phoneNumber[0] == '0' || phoneNumber[0] == '+'
            //                     ? greenColor
            //                     : grayColor.withOpacity(0.8),
            //           ),
            //           label:
            //               // Column(
            //               //   children: [
            //               //     Text(
            //               //       'Whatsapp',
            //               //       style: whiteTextStyle,
            //               //     ),
            //               //   ],
            //               // ),
            //               Text(
            //             phoneNumber,
            //             style: whiteTextStyle,
            //           ),
            //           icon: Icon(
            //             Icons.phone,
            //             color: whiteColor,
            //           ),
            //         ),
            //       ),
            //       Expanded(
            //         flex: 4,
            //         child: TextButton.icon(
            //           onPressed: () {
            //             Navigator.push(
            //                 context,
            //                 MaterialPageRoute(
            //                     builder: (context) =>
            //                         TrackingPage(widget.place)));
            //           },
            //           style: TextButton.styleFrom(
            //             elevation: 5,
            //             fixedSize: const Size(double.infinity, 50.0),
            //             shape: const RoundedRectangleBorder(
            //                 borderRadius: BorderRadius.only(
            //                     topRight: Radius.circular(30),
            //                     bottomRight: Radius.circular(30))),
            //             backgroundColor: greenColor,
            //           ),
            //           label: Text(
            //             'Telusuri',
            //             style: whiteTextStyle,
            //           ),
            //           icon: RotationTransition(
            //               turns: const AlwaysStoppedAnimation(40 / 360),
            //               child: Icon(
            //                 Icons.navigation_rounded,
            //                 color: whiteColor,
            //               )),
            //         ),
            //       ),
            //     ],
            //   ),
            // ),
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
