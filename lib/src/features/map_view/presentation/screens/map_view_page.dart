// lib/src/features/map_view/presentation/screens/map_view_page.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:tambal_ban/src/features/map_view/presentation/controllers/map_view_controller.dart';
import 'package:tambal_ban/src/features/map_view/presentation/widgets/workshop_map_card.dart';
// import 'package:google_maps_flutter/google_maps_flutter.dart'; // For GoogleMap widget

class MapViewPage extends ConsumerWidget {
  const MapViewPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final nearbyWorkshopsAsyncValue = ref.watch(nearbyWorkshopsControllerProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Workshop Finder'),
        // Add profile icon / drawer later
      ),
      body: Column(
        children: [
          // SearchBar placeholder
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Search for workshops...',
                prefixIcon: Icon(Icons.search),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8.0),
                ),
              ),
              onSubmitted: (query) {
                // TODO: Implement search functionality
                // ref.read(nearbyWorkshopsControllerProvider.notifier).searchWorkshops(query);
              },
            ),
          ),
          Expanded(
            // Placeholder for Google Map View
            child: Container(
              color: Colors.grey[300],
              child: Center(
                child: Text('Map View Placeholder'),
                // child: GoogleMap(
                //   initialCameraPosition: CameraPosition(
                //     target: LatLng(-6.200000, 106.816666), // Default to Jakarta
                //     zoom: 12,
                //   ),
                //   markers: nearbyWorkshopsAsyncValue.when(
                //     data: (workshops) => workshops.map((ws) => Marker(
                //       markerId: MarkerId(ws.id),
                //       position: LatLng(ws.latitude, ws.longitude),
                //       infoWindow: InfoWindow(title: ws.name),
                //       onTap: () => ref.read(selectedWorkshopProvider.notifier).state = ws,
                //     )).toSet(),
                //     loading: () => {},
                //     error: (err, stack) => {},
                //   ),
                // ),
              ),
            ),
          ),
          // WorkshopCardList placeholder (bottom-docked cards)
          SizedBox(
            height: 150, // Adjust height as needed
            child: nearbyWorkshopsAsyncValue.when(
              data: (workshops) {
                if (workshops.isEmpty) {
                  return const Center(child: Text('No workshops found nearby.'));
                }
                return ListView.builder(
                  scrollDirection: Axis.horizontal,
                  itemCount: workshops.length,
                  itemBuilder: (context, index) {
                    final workshop = workshops[index];
                    return WorkshopMapCard(workshop: workshop);
                  },
                );
              },
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (error, stackTrace) => Center(
                child: Text('Error loading workshops: ${error.toString()}'),
              ),
            ),
          ),
          // NearestWorkshopBar placeholder (bottom fixed component)
          // Container(
          //   padding: EdgeInsets.all(16.0),
          //   color: Colors.blue,
          //   child: Text('Nearest Workshop Bar Placeholder', style: TextStyle(color: Colors.white)),
          // ),
        ],
      ),
    );
  }
}
