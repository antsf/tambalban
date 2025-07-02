import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'package:shimmer/shimmer.dart';
import 'package:skeletonizer/skeletonizer.dart';

import '../ad_helper.dart';
import '../cubit/connected_cubit.dart';
import '../cubit/place_cubit.dart';
import '../model/place_model.dart';
import '../theme.dart';
import '../widgets/place_grid.dart';
import '../widgets/place_list.dart';
import 'detail.dart';

class SearchPage extends StatefulWidget {
  final Position? position;
  const SearchPage({super.key, this.position});

  @override
  State<SearchPage> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  // bool isLoading = false; // Managed by PlaceCubit and Skeletonizer
  TextEditingController searchController = TextEditingController();
  String filter = ""; // For text search from searchController
  bool isShowGrid = false;

  double lat = 0, long = 0; // Initialized from widget.position in initState
  // double distanceInMeters = 0.0; // Not needed as a state variable
  BannerAd? _bannerAd;

  // State for filters
  Set<String> _selectedVehicleTypes = {};
  bool? _filterOpenNow; // null for 'Any', true for 'Open', false for 'Closed'
  bool? _filterVerified; // null for 'Any', true for 'Verified', false for 'Not Verified'

  final List<String> _allVehicleTypes = ['Mobil', 'Motor', 'Sepeda']; // Example types

  // State for sorting
  String _currentSortOrder = 'Nearest';
  final List<String> _sortOptions = ['Nearest', 'Top Rated', 'Most Reviewed'];

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

  // Renamed and modified to accept List<PlaceModel>
  // Calculates distance and maps to the structure expected by list/grid views.
  // Sorts by distance if that's the primary sort criteria.
  List<Map<dynamic, dynamic>> mapPlacesWithDistance(List<PlaceModel> places, {bool sortByDist = false}) {
    List<Map<dynamic, dynamic>> placesWithCalculatedDistance = [];

    if (lat == 0 && long == 0 && widget.position == null) {
      return places.map((place) => {'items': place, 'distance': -1.0}).toList();
    }

    for (PlaceModel place in places) {
      final distance = Geolocator.distanceBetween(
              lat, long, place.latitude, place.longitude) / 1000; // in km
      placesWithCalculatedDistance.add({
        'items': place,
        'distance': distance,
      });
    }

    if (sortByDist) {
      placesWithCalculatedDistance.sort((a, b) => (a['distance'] as double).compareTo(b['distance'] as double));
    }

    return placesWithCalculatedDistance;
  }

  @override
  void initState() {
    searchController.addListener(() {
      setState(() {
        filter = searchController.text;
      });
    });

    lat = widget.position?.latitude ?? 0;
    long = widget.position?.longitude ?? 0;
    _initBannerAd();
    // context.read<PlaceCubit>().fetchPlaces(); // Assuming places are fetched globally or on home
    super.initState();
  }

  @override
  void dispose() {
    searchController.dispose();
    _bannerAd?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    Widget listViewWidget(List<Map<dynamic, dynamic>> dataToDisplay, bool isLoadingData) {
      // Data is already filtered/sorted.
      return SliverList(
          delegate: SliverChildBuilderDelegate((context, index) {
        return PlaceList(
          dataToDisplay[index],
          onPressed: () {
            Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => DetailPage(dataToDisplay[index]),
                ));
          },
        );
      }, childCount: dataToDisplay.length));
    }

    Widget gridViewWidget(List<Map<dynamic, dynamic>> dataToDisplay, bool isLoadingData) {
      // Data is already filtered/sorted.
      return SliverGrid(
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            mainAxisSpacing: 12.0,
            crossAxisSpacing: 12.0,
            childAspectRatio: 0.83,
          ),
          delegate: SliverChildBuilderDelegate((context, index) {
            return PlaceGrid(dataToDisplay[index]);
          }, childCount: dataToDisplay.length));
    }

    Widget searchBar() {
      return Container(
          height: 50.0,
          padding: const EdgeInsets.symmetric(horizontal: 16.0),
          decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(30.0),
              boxShadow: [
                BoxShadow(
                    color: greenColor.withOpacity(0.2),
                    offset: const Offset(0, 4),
                    blurRadius: 12.0)
              ]),
          child: Row(
            children: [
              InkWell(
                onTap: () => Navigator.pop(context),
                child: Icon(Icons.arrow_back, color: blackColor, size: 20.0),
              ),
              Expanded(
                child: Container(
                  padding: const EdgeInsets.only(left: 16.0),
                  child: Row(
                    children: [
                      Expanded(
                          child: TextFormField(
                        controller: searchController,
                        cursorColor: greenColor,
                        style: blackTextStyle.copyWith(fontWeight: medium),
                        decoration: InputDecoration.collapsed(
                          hintText: 'Cari tempat tambal ban...',
                          hintStyle: grayTextStyle.copyWith(fontWeight: light),
                        ),
                      )),
                      Icon(Icons.search_outlined, color: blackColor, size: 28.0)
                    ],
                  ),
                ),
              ),
            ],
          ));
    }

    Widget titleAndToggle() {
      return Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            'All Workshops',
            style: grayTextStyle.copyWith(fontSize: 18.0, fontWeight: semiBold),
          ),
          InkWell(
            onTap: () => setState(() => isShowGrid = !isShowGrid),
            child: Container(
                padding: const EdgeInsets.all(4.0),
                decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(12.0),
                    border: Border.all(width: 1, color: grayColor)),
                child: Row(
                  children: [
                    AnimatedOpacity(
                        opacity: !isShowGrid ? 1 : 0.1,
                        duration: const Duration(milliseconds: 300), // Faster animation
                        child: Icon(Icons.list_alt_outlined, color: grayColor)),
                    AnimatedOpacity(
                        opacity: isShowGrid ? 1 : 0.1,
                        duration: const Duration(milliseconds: 300),
                        child: Icon(Icons.grid_view_outlined, color: grayColor)),
                  ],
                )),
          ),
        ],
      );
    }

    return BlocConsumer<ConnectedCubit, ConnectedState>(
        listener: (context, stateConnected) {
      // No specific listener action needed here for now
    }, builder: (context, stateConnected) {
      if (stateConnected is ConnectedFailed) {
        return Scaffold(
          backgroundColor: whiteColor,
          body: Center( /* ... No internet UI ... */ )
        );
      }
      // Assuming connected or initial state
      return Scaffold(
          backgroundColor: whiteColor,
          body: BlocBuilder<PlaceCubit, PlaceState>(
              builder: (context, statePlace) {
            List<PlaceModel> allPlaces = statePlace is PlaceSuccess ? List<PlaceModel>.from(statePlace.places) : [];
            List<PlaceModel> filteredPlaces = allPlaces;

            // 1. Apply text filter
            if (filter.isNotEmpty) {
              filteredPlaces = filteredPlaces
                  .where((place) => place.name.toLowerCase().contains(filter.toLowerCase()))
                  .toList();
            }

            // 2. Apply chip filters
            if (_selectedVehicleTypes.isNotEmpty) {
              filteredPlaces = filteredPlaces.where((place) {
                final placeVehicleTypes = place.vehicles.map((v) => v.toString().toLowerCase()).toList();
                return _selectedVehicleTypes.any((selectedType) => placeVehicleTypes.contains(selectedType.toLowerCase()));
              }).toList();
            }
            if (_filterOpenNow != null) {
              filteredPlaces = filteredPlaces.where((place) => place.isOpenNow == _filterOpenNow).toList();
            }
            if (_filterVerified != null) {
              filteredPlaces = filteredPlaces.where((place) => place.isVerified == _filterVerified).toList();
            }

            // 3. Sort and map for display
            List<Map<dynamic, dynamic>> displayData;
            if (_currentSortOrder == 'Nearest') {
              displayData = mapPlacesWithDistance(filteredPlaces, sortByDist: true);
            } else {
              if (_currentSortOrder == 'Top Rated') {
                filteredPlaces.sort((a, b) => b.rating.compareTo(a.rating));
              } else if (_currentSortOrder == 'Most Reviewed') {
                filteredPlaces.sort((a, b) => b.reviewCount.compareTo(a.reviewCount));
              }
              displayData = mapPlacesWithDistance(filteredPlaces, sortByDist: false); // Map, don't re-sort by distance
            }

            bool isLoading = statePlace is PlaceInitial || statePlace is PlaceLoading;

            return Skeletonizer(
              enabled: isLoading && displayData.isEmpty, // Show skeleton only if loading AND no data yet
              child: CustomScrollView(
                slivers: [
                  SliverAppBar(
                    automaticallyImplyLeading: false,
                    floating: true,
                    backgroundColor: whiteColor,
                    elevation: 0,
                    toolbarHeight: 70.0,
                    title: searchBar(),
                  ),
                  SliverPadding(
                      padding: const EdgeInsets.all(16.0),
                      sliver: SliverToBoxAdapter(child: titleAndToggle())),
                  SliverPadding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                    sliver: SliverToBoxAdapter(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text("Filter by:", style: blackTextStyle.copyWith(fontWeight: medium)),
                          const SizedBox(height: 8.0),
                          Wrap(
                            spacing: 8.0, runSpacing: 4.0,
                            children: _allVehicleTypes.map((type) => FilterChip(
                                label: Text(type), selectedColor: greenColor.withOpacity(0.3),
                                selected: _selectedVehicleTypes.contains(type),
                                onSelected: (sel) => setState(() => sel ? _selectedVehicleTypes.add(type) : _selectedVehicleTypes.remove(type)),
                              )).toList()
                            ..addAll([
                              FilterChip(
                                label: Text(_filterOpenNow == null ? 'Open: Any' : (_filterOpenNow! ? 'Open: Yes' : 'Open: No')),
                                selectedColor: greenColor.withOpacity(0.3), selected: _filterOpenNow != null,
                                onSelected: (_) => setState(() {
                                  if (_filterOpenNow == null) _filterOpenNow = true;
                                  else if (_filterOpenNow == true) _filterOpenNow = false;
                                  else _filterOpenNow = null;
                                }),
                              ),
                              FilterChip(
                                label: Text(_filterVerified == null ? 'Verified: Any' : (_filterVerified! ? 'Verified: Yes' : 'Verified: No')),
                                selectedColor: greenColor.withOpacity(0.3), selected: _filterVerified != null,
                                onSelected: (_) => setState(() {
                                  if (_filterVerified == null) _filterVerified = true;
                                  else if (_filterVerified == true) _filterVerified = false;
                                  else _filterVerified = null;
                                }),
                              ),
                            ]),
                          ),
                          const SizedBox(height: 16.0),
                          Text("Sort by:", style: blackTextStyle.copyWith(fontWeight: medium)),
                          const SizedBox(height: 8.0),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 12.0),
                            decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(8.0),
                                border: Border.all(color: grayColor.withOpacity(0.5)), color: whiteColor),
                            child: DropdownButtonHideUnderline(
                              child: DropdownButton<String>(
                                value: _currentSortOrder, isExpanded: true,
                                icon: Icon(Icons.arrow_drop_down, color: grayColor),
                                items: _sortOptions.map((String val) => DropdownMenuItem<String>(value: val, child: Text(val, style: blackTextStyle))).toList(),
                                onChanged: (String? newVal) => setState(() => _currentSortOrder = newVal!),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  SliverPadding( // Add padding around the list/grid
                    padding: const EdgeInsets.all(16.0),
                    sliver: !isShowGrid
                      ? listViewWidget(displayData, isLoading)
                      : gridViewWidget(displayData, isLoading),
                  ),
                  SliverPadding( // Banner Ad
                    padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 16.0),
                    sliver: SliverToBoxAdapter(
                        child: _bannerAd != null
                            ? Container(
                                alignment: Alignment.center,
                                width: _bannerAd!.size.width.toDouble(),
                                height: _bannerAd!.size.height.toDouble(),
                                child: AdWidget(ad: _bannerAd!),
                              )
                            : const SizedBox()),
                  ),
                ],
              ),
            );
          })
        );
      }
      // Fallback for initial connection check or other states
      return Scaffold(
          backgroundColor: whiteColor,
          body: Center(child: CircularProgressIndicator(color: greenColor)));
    });
  }
}
