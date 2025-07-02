import 'package:equatable/equatable.dart';

class PlaceModel extends Equatable {
  final String id;
  final String name;
  final String address;
  final String openTime;
  final String phoneNumber;
  final double latitude;
  final double longitude;
  final List vehicles;
  final bool homeService;
  final String services;
  final String status; // e.g., 'pending', 'approved', 'rejected'. 'approved' can mean 'verified'.
  final String createdAt;
  final String updatedAt;
  final String imageUrl;
  final double rating; // New field
  final int reviewCount; // New field
  // Assuming 'vehicles' is List<String> for vehicle types like ['car', 'motorcycle']

  const PlaceModel({
    required this.id,
    this.name = '',
    this.address = '',
    this.openTime = '', // e.g., "08:00 - 17:00" or "24 Jam"
    this.phoneNumber = '',
    this.latitude = 0.0,
    this.longitude = 0.0,
    this.vehicles = const [], // Should ideally be List<String>
    this.homeService = true,
    this.services = '',
    this.status = 'pending', // Default status
    this.createdAt = '',
    this.updatedAt = '',
    this.imageUrl = '',
    this.rating = 0.0, // Default rating
    this.reviewCount = 0, // Default review count
  });

  factory PlaceModel.fromJson(String id, Map<String, dynamic> json) =>
      PlaceModel(
        id: id,
        name: json['name'] ?? '',
        address: json['address'] ?? '',
        openTime: json['openTime'] ?? '',
        phoneNumber: json['phoneNumber'] ?? '',
        latitude: (json['latitude'] as num?)?.toDouble() ?? 0.0,
        longitude: (json['longitude'] as num?)?.toDouble() ?? 0.0,
        vehicles: List<String>.from(json['vehicles'] ?? []), // Assuming List<String>
        homeService: json['homeService'] ?? true,
        services: json['services'] ?? '',
        status: json['status'] ?? 'pending',
        createdAt: json['createdAt'] ?? '',
        updatedAt: json['updatedAt'] ?? '',
        imageUrl: json['imageUrl'] ?? '',
        rating: (json['rating'] as num?)?.toDouble() ?? 0.0,
        reviewCount: (json['reviewCount'] as num?)?.toInt() ?? 0,
      );

  Map<String, dynamic> toJson() => {
        'id': id, // id is often not part of the document data itself in Firestore
        'name': name,
        'address': address,
        'openTime': openTime,
        'phoneNumber': phoneNumber,
        'latitude': latitude,
        'longitude': longitude,
        'vehicles': vehicles, // Expected to be List<String>
        'homeService': homeService,
        'services': services,
        'status': status,
        'createdAt': createdAt, // Should be Timestamp or server timestamp
        'updatedAt': updatedAt, // Should be Timestamp or server timestamp
        'imageUrl': imageUrl,
        'rating': rating,
        'reviewCount': reviewCount,
      };

  @override
  List<Object?> get props => [
        id,
        name,
        address,
        openTime,
        phoneNumber,
        latitude,
        longitude,
        vehicles, // Make sure this is List<String> or handle conversion
        homeService,
        services,
        status,
        createdAt,
        updatedAt,
        imageUrl,
        rating, // Added to props
        reviewCount, // Added to props
      ];

  // Helper to check if the workshop is "verified"
  bool get isVerified => status == 'approved';

  // Helper to check if the workshop is open now.
  // This is a simplified example. Real implementation needs robust time parsing.
  bool get isOpenNow {
    if (openTime.toLowerCase() == '24 jam') {
      return true;
    }
    // Example: "08:00 - 17:00"
    try {
      final parts = openTime.split(' - ');
      if (parts.length == 2) {
        final now = DateTime.now();
        final openHour = int.parse(parts[0].split(':')[0]);
        final openMinute = int.parse(parts[0].split(':')[1]);
        final closeHour = int.parse(parts[1].split(':')[0]);
        final closeMinute = int.parse(parts[1].split(':')[1]);

        final openDateTime = DateTime(now.year, now.month, now.day, openHour, openMinute);
        final closeDateTime = DateTime(now.year, now.month, now.day, closeHour, closeMinute);

        // Handle overnight case if close time is earlier than open time (e.g. 20:00 - 02:00)
        if (closeDateTime.isBefore(openDateTime)) {
          if (now.isAfter(openDateTime) || now.isBefore(closeDateTime.add(const Duration(days: 1)))) {
            return true;
          }
        } else {
           if (now.isAfter(openDateTime) && now.isBefore(closeDateTime)) {
            return true;
          }
        }
      }
    } catch (e) {
      // Parsing error, assume closed or handle error
      print("Error parsing openTime '$openTime': $e");
      return false;
    }
    return false;
  }
}
