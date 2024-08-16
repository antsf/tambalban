import 'dart:io';

import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';

import '../model/place_model.dart';
import '../services/place_service.dart';

part 'place_state.dart';

class PlaceCubit extends Cubit<PlaceState> {
  PlaceCubit() : super(PlaceInitial());

  void createPlace(PlaceModel place, File? image, String fileName) async {
    try {
      emit(PlaceLoading());
      await PlaceService().createPlace(place, image, fileName);
      emit(const PlaceSuccess([]));
    } catch (err) {
      emit(PlaceFailed(err.toString()));
    }
  }

  void fetchPlaces() async {
    try {
      emit(PlaceLoading());
      final places = await PlaceService().fetchPlaces();
      emit(PlaceSuccess(places));
    } catch (err) {
      emit(PlaceFailed(err.toString()));
    }
  }
}
