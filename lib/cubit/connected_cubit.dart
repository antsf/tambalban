import 'dart:async';

import 'package:bloc/bloc.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:equatable/equatable.dart';

part 'connected_state.dart';

class ConnectedCubit extends Cubit<ConnectedState> {
  final Connectivity connectivity;
  StreamSubscription? connectivityStreamSubcription;

  ConnectedCubit(this.connectivity) : super(ConnectedLoading()) {
    connectivityStreamSubcription =
        connectivity.onConnectivityChanged.listen((connectivityResult) {
      if (connectivityResult.contains(ConnectivityResult.wifi)) {
        emmitConnectedSuccess(ConnectionType.wifi);
      } else if (connectivityResult.contains(ConnectivityResult.mobile)) {
        emmitConnectedSuccess(ConnectionType.mobile);
      } else if (connectivityResult.contains(ConnectivityResult.none)) {
        emmitConnectedFailed();
      }
      // switch (connectivityResult) {
      //   case ConnectivityResult.wifi:
      //     emmitConnectedSuccess(ConnectionType.wifi);
      //     break;
      //   case ConnectivityResult.mobile:
      //     emmitConnectedSuccess(ConnectionType.mobile);
      //     break;
      //   case ConnectivityResult.none:
      //     emmitConnectedFailed();
      //     break;
      //   default:
      //     emmitConnectedFailed();
      // }
    });
  }

  void emmitConnectedSuccess(ConnectionType connectionType) =>
      emit(ConnectedSuccess(connectionType: connectionType));

  void emmitConnectedFailed() => emit(ConnectedFailed());

  @override
  Future<void> close() {
    connectivityStreamSubcription?.cancel();
    return super.close();
  }
}
