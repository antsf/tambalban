// lib/src/features/map_view/data/repositories/workshop_map_repository_impl.dart
import 'package:dartz/dartz.dart';
import 'package:tambal_ban/src/core/errors/failures.dart';
// import 'package:tambal_ban/src/core/platform/network_info.dart'; // For checking internet connectivity
import 'package:tambal_ban/src/features/map_view/data/datasources/workshop_map_remote_datasource.dart';
// import 'package:tambal_ban/src/features/map_view/data/datasources/workshop_map_local_datasource.dart'; // If you have local caching
import 'package:tambal_ban/src/features/map_view/domain/entities/workshop_location.dart';
import 'package:tambal_ban/src/features/map_view/domain/repositories/workshop_map_repository.dart';

// Example NetworkInfo (should be in core/platform)
abstract class NetworkInfo {
  Future<bool> get isConnected;
}

class NetworkInfoImpl implements NetworkInfo {
  // final Connectivity connectivity; // from connectivity_plus package
  // NetworkInfoImpl(this.connectivity);

  @override
  Future<bool> get isConnected async {
    // var connectivityResult = await connectivity.checkConnectivity();
    // return connectivityResult != ConnectivityResult.none;
    return true; // Placeholder
  }
}


class WorkshopMapRepositoryImpl implements WorkshopMapRepository {
  final WorkshopMapRemoteDataSource remoteDataSource;
  // final WorkshopMapLocalDataSource localDataSource; // If caching
  final NetworkInfo networkInfo; // To check internet connection

  WorkshopMapRepositoryImpl({
    required this.remoteDataSource,
    // required this.localDataSource,
    required this.networkInfo,
  });

  @override
  Future<Either<Failure, List<WorkshopLocation>>> getNearbyWorkshops(double latitude, double longitude, double radius) async {
    if (await networkInfo.isConnected) {
      try {
        final remoteWorkshops = await remoteDataSource.getNearbyWorkshops(latitude, longitude, radius);
        // You could cache the data here using localDataSource if needed
        return Right(remoteWorkshops);
      } catch (e) { // Catch specific exceptions like ServerException
        return Left(ServerFailure('Failed to fetch nearby workshops: ${e.toString()}'));
      }
    } else {
      // Optionally, try to fetch from local cache if offline
      // try {
      //   final localWorkshops = await localDataSource.getLastNearbyWorkshops();
      //   return Right(localWorkshops);
      // } catch (e) { // Catch CacheException
      //   return Left(CacheFailure('No cached data found.'));
      // }
      return Left(NetworkFailure('No internet connection.'));
    }
  }

  @override
  Future<Either<Failure, List<WorkshopLocation>>> searchWorkshops(String query) async {
    if (await networkInfo.isConnected) {
      try {
        final remoteWorkshops = await remoteDataSource.searchWorkshops(query);
        return Right(remoteWorkshops);
      } catch (e) {
        return Left(ServerFailure('Failed to search workshops: ${e.toString()}'));
      }
    } else {
      return Left(NetworkFailure('No internet connection.'));
    }
  }
}
