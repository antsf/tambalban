// lib/src/features/map_view/domain/usecases/get_nearby_workshops.dart
import 'package:dartz/dartz.dart';
import 'package:tambal_ban/src/core/errors/failures.dart';
import 'package:tambal_ban/src/features/map_view/domain/entities/workshop_location.dart';
import 'package:tambal_ban/src/features/map_view/domain/repositories/workshop_map_repository.dart';

class GetNearbyWorkshops {
  final WorkshopMapRepository repository;

  GetNearbyWorkshops(this.repository);

  Future<Either<Failure, List<WorkshopLocation>>> call(NearbyWorkshopsParams params) async {
    // You can add business logic here before calling the repository
    // For example, validating the radius or coordinates
    return await repository.getNearbyWorkshops(params.latitude, params.longitude, params.radius);
  }
}

class NearbyWorkshopsParams {
  final double latitude;
  final double longitude;
  final double radius; // in kilometers, for example

  NearbyWorkshopsParams({
    required this.latitude,
    required this.longitude,
    required this.radius,
  });
}
