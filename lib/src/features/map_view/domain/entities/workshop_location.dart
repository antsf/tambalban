// lib/src/features/map_view/domain/entities/workshop_location.dart
import 'package:equatable/equatable.dart';

class WorkshopLocation extends Equatable {
  final String id;
  final String name;
  final double latitude;
  final double longitude;
  final String address;
  // Add other relevant fields like rating, type, etc.

  const WorkshopLocation({
    required this.id,
    required this.name,
    required this.latitude,
    required this.longitude,
    required this.address,
  });

  @override
  List<Object?> get props => [id, name, latitude, longitude, address];
}
