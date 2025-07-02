// lib/src/features/map_view/data/models/workshop_location_model.dart
import 'package:tambal_ban/src/features/map_view/domain/entities/workshop_location.dart';

class WorkshopLocationModel extends WorkshopLocation {
  const WorkshopLocationModel({
    required String id,
    required String name,
    required double latitude,
    required double longitude,
    required String address,
    // Add other fields that might come from the API
  }) : super(
          id: id,
          name: name,
          latitude: latitude,
          longitude: longitude,
          address: address,
        );

  factory WorkshopLocationModel.fromJson(Map<String, dynamic> json) {
    return WorkshopLocationModel(
      id: json['id'] as String,
      name: json['name'] as String,
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      address: json['address'] as String,
      // Parse other fields
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'latitude': latitude,
      'longitude': longitude,
      'address': address,
      // Add other fields
    };
  }
}
