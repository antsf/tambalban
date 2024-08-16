part of 'connected_cubit.dart';

enum ConnectionType { wifi, mobile }

abstract class ConnectedState extends Equatable {
  const ConnectedState();

  @override
  List<Object> get props => [];
}

class ConnectedInitial extends ConnectedState {}

class ConnectedLoading extends ConnectedState {}

class ConnectedSuccess extends ConnectedState {
  final ConnectionType connectionType;

  const ConnectedSuccess({required this.connectionType});

  @override
  List<Object> get props => [connectionType];
}

class ConnectedFailed extends ConnectedState {
  // final String error;

  // const ConnectedFailed(this.error);

  // @override
  // List<Object> get props => [error];
}
