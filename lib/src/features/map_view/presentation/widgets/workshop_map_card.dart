// lib/src/features/map_view/presentation/widgets/workshop_map_card.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:tambal_ban/src/features/map_view/domain/entities/workshop_location.dart';
import 'package:tambal_ban/src/features/map_view/presentation/controllers/map_view_controller.dart'; // For selectedWorkshopProvider

class WorkshopMapCard extends ConsumerWidget {
  final WorkshopLocation workshop;

  const WorkshopMapCard({Key? key, required this.workshop}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return GestureDetector(
      onTap: () {
        ref.read(selectedWorkshopProvider.notifier).state = workshop;
        // Potentially navigate to detail page or show more info on map
        print('Tapped on ${workshop.name}');
      },
      child: Card(
        margin: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
        child: Container(
          width: 250, // Adjust width as needed
          padding: const EdgeInsets.all(12.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                workshop.name,
                style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
              const SizedBox(height: 4.0),
              Text(
                workshop.address,
                style: Theme.of(context).textTheme.bodySmall,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              const SizedBox(height: 8.0),
              // Add more info like rating, distance, etc.
              Text(
                'Tap for details',
                style: Theme.of(context).textTheme.labelSmall?.copyWith(color: Theme.of(context).colorScheme.primary),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
