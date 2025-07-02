// lib/src/features/map_view/domain/repositories/workshop_map_repository.dart
import 'package:tambal_ban/src/core/errors/failures.dart';
import 'package:tambal_ban/src/features/map_view/domain/entities/workshop_location.dart';
import 'package:dartz/dartz.dart'; // You'll need to add dartz: ^0.10.1 to pubspec.yaml

// Using dartz for functional error handling (Either<Failure, SuccessType>)
// If you prefer not to use dartz, you can return Future<List<WorkshopLocation>> and throw exceptions.

abstract class WorkshopMapRepository {
  Future<Either<Failure, List<WorkshopLocation>>> getNearbyWorkshops(double latitude, double longitude, double radius);
  Future<Either<Failure, List<WorkshopLocation>>> searchWorkshops(String query);
  // Add other methods like getWorkshopDetails, etc.
}
