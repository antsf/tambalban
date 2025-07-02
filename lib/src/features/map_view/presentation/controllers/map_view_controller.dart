// lib/src/features/map_view/presentation/controllers/map_view_controller.dart
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:tambal_ban/src/features/map_view/domain/entities/workshop_location.dart';
import 'package:tambal_ban/src/features/map_view/domain/usecases/get_nearby_workshops.dart';
import 'package:tambal_ban/src/features/map_view/data/datasources/workshop_map_remote_datasource.dart'; // For di
import 'package:tambal_ban/src/features/map_view/data/repositories/workshop_map_repository_impl.dart'; // For di
// import 'package:connectivity_plus/connectivity_plus.dart'; // For NetworkInfo di

part 'map_view_controller.g.dart'; // Ensure this matches your file name

// This is a placeholder for NetworkInfo. In a real app, provide it properly.
final networkInfoProvider = Provider<NetworkInfo>((ref) => NetworkInfoImpl());

// Provider for WorkshopMapRemoteDataSource
final workshopMapRemoteDataSourceProvider = Provider<WorkshopMapRemoteDataSource>((ref) {
  // If it had dependencies, e.g. http.Client or FirebaseFirestore instance:
  // final httpClient = ref.watch(httpClientProvider);
  // return WorkshopMapRemoteDataSourceImpl(client: httpClient);
  return WorkshopMapRemoteDataSourceImpl();
});

// Provider for WorkshopMapRepository
final workshopMapRepositoryProvider = Provider<WorkshopMapRepository>((ref) {
  final remoteDataSource = ref.watch(workshopMapRemoteDataSourceProvider);
  final networkInfo = ref.watch(networkInfoProvider);
  // final localDataSource = ref.watch(workshopMapLocalDataSourceProvider); // if you have one
  return WorkshopMapRepositoryImpl(
    remoteDataSource: remoteDataSource,
    networkInfo: networkInfo,
    // localDataSource: localDataSource,
  );
});

// Provider for GetNearbyWorkshops UseCase
final getNearbyWorkshopsUseCaseProvider = Provider<GetNearbyWorkshops>((ref) {
  final repository = ref.watch(workshopMapRepositoryProvider);
  return GetNearbyWorkshops(repository);
});


// The state for our map view
class MapViewState extends AsyncValue<List<WorkshopLocation>> {
  MapViewState() : super.loading(); // Initial state is loading
}


// The controller/provider using riverpod_generator for async operations
@riverpod
class NearbyWorkshopsController extends _$NearbyWorkshopsController {
  @override
  Future<List<WorkshopLocation>> build() async {
    // Initially, load workshops around a default location or user's current location
    // For now, let's use a fixed location and radius for demonstration
    // In a real app, you'd get this from a location service/provider
    final useCase = ref.watch(getNearbyWorkshopsUseCaseProvider);
    final result = await useCase.call(NearbyWorkshopsParams(latitude: -6.200000, longitude: 106.816666, radius: 5.0));

    return result.fold(
      (failure) => throw failure, // Propagate failure to AsyncError
      (workshops) => workshops,
    );
  }

  Future<void> fetchNearbyWorkshops(double lat, double lon, double radius) async {
    state = const AsyncLoading(); // Set state to loading
    final useCase = ref.read(getNearbyWorkshopsUseCaseProvider); // Use read for one-off calls
    final result = await useCase.call(NearbyWorkshopsParams(latitude: lat, longitude: lon, radius: radius));

    state = result.fold(
      (failure) => AsyncError(failure, StackTrace.current),
      (workshops) => AsyncData(workshops),
    );
  }

  // Add other methods for searching, etc.
}

// If you need to manage selected workshop or other UI state specific to map view
final selectedWorkshopProvider = StateProvider<WorkshopLocation?>((ref) => null);
