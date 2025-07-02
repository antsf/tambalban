part of 'auth_bloc.dart';

abstract class AuthEvent extends Equatable {
  const AuthEvent();

  @override
  List<Object?> get props => [];
}

// Fired when the app starts or auth state changes from service
class AuthUserChanged extends AuthEvent {
  final User? firebaseUser;
  const AuthUserChanged(this.firebaseUser);

  @override
  List<Object?> get props => [firebaseUser];
}

// Fired to request sign-in with Google
class AuthGoogleSignInRequested extends AuthEvent {}

// Fired to request sign-in with Email
class AuthEmailSignInRequested extends AuthEvent {
  final String email;
  final String password; // Assuming password for now, can be adjusted for email link/OTP

  const AuthEmailSignInRequested(this.email, this.password);

  @override
  List<Object?> get props => [email, password];
}

// Fired to request sign-in with WhatsApp (placeholder)
class AuthWhatsAppSignInRequested extends AuthEvent {
  final String phoneNumber;
  const AuthWhatsAppSignInRequested(this.phoneNumber);

  @override
  List<Object?> get props => [phoneNumber];
}

// Fired to request sign-out
class AuthSignOutRequested extends AuthEvent {}
