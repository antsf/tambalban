part of 'place_cubit.dart';

abstract class PlaceState extends Equatable {
  const PlaceState();

  @override
  List<Object> get props => [];
}

class PlaceInitial extends PlaceState {}

class PlaceLoading extends PlaceState {}

class PlaceSuccess extends PlaceState {
  final List<PlaceModel> places;

  const PlaceSuccess(this.places);

  @override
  List<Object> get props => [places];
}

class PlaceFailed extends PlaceState {
  final String error;

  const PlaceFailed(this.error);

  @override
  List<Object> get props => [error];
}
