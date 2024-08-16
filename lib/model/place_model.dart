import 'package:equatable/equatable.dart';

class PlaceModel extends Equatable {
  final String id;
  final String name;
  final String address;
  final String openTime;
  final String phoneNumber;
  final double latitude;
  final double longitude;
  final List vehicle;
  final bool homeService;
  final String services;
  final String status;
  final String createdAt;
  final String updatedAt;
  final String imageUrl;

  const PlaceModel({
    required this.id,
    this.name = '',
    this.address = '',
    this.openTime = '',
    this.phoneNumber = '',
    this.latitude = 0.0,
    this.longitude = 0.0,
    this.vehicle = const [],
    this.homeService = true,
    this.services = '',
    this.status = '',
    this.createdAt = '',
    this.updatedAt = '',
    this.imageUrl = '',
  });

  factory PlaceModel.fromJson(String id, Map<String, dynamic> json) =>
      PlaceModel(
        id: id,
        name: json['name'],
        address: json['address'],
        openTime: json['openTime'],
        phoneNumber: json['phoneNumber'],
        latitude: json['latitude'],
        longitude: json['longitude'],
        vehicle: json['vehicle'],
        homeService: json['homeService'],
        services: json['services'],
        status: json['status'],
        createdAt: json['createdAt'],
        updatedAt: json['updatedAt'],
        imageUrl: json['imageUrl'],
      );

  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'address': address,
        'openTime': openTime,
        'phoneNumber': phoneNumber,
        'latitude': latitude,
        'longitude': longitude,
        'vehicle': vehicle,
        'homeService': homeService,
        'services': services,
        'status': status,
        'createdAt': createdAt,
        'updatedAt': updatedAt,
        'imageUrl': imageUrl,
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
        vehicle,
        homeService,
        services,
        status,
        createdAt,
        updatedAt,
        imageUrl,
      ];
}
