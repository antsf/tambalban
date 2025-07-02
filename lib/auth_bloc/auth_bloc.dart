import 'dart:async';

import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:tambal_ban/services/auth_service.dart'; // Assuming this path

part 'auth_event.dart';
part 'auth_state.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final AuthService _authService;
  StreamSubscription<User?>? _userSubscription;

  AuthBloc({required AuthService authService})
      : _authService = authService,
        super(AuthInitial()) {
    // Listen to Firebase auth state changes
    _userSubscription = _authService.authStateChanges.listen((firebaseUser) {
      add(AuthUserChanged(firebaseUser));
    });

    on<AuthUserChanged>(_onAuthUserChanged);
    on<AuthGoogleSignInRequested>(_onGoogleSignInRequested);
    on<AuthEmailSignInRequested>(_onEmailSignInRequested);
    on<AuthWhatsAppSignInRequested>(_onWhatsAppSignInRequested);
    on<AuthSignOutRequested>(_onSignOutRequested);
  }

  Future<void> _onAuthUserChanged(AuthUserChanged event, Emitter<AuthState> emit) async {
    if (event.firebaseUser != null) {
      // User is signed in
      emit(AuthLoading()); // Optional: show loading while checking owner status
      try {
        final bool isOwner = await _authService.isCurrentUserOwner();
        emit(Authenticated(event.firebaseUser!, isOwner: isOwner));
      } catch (e) {
        emit(AuthFailure("Failed to check user role: ${e.toString()}"));
        // Fallback to authenticated but without owner status or unauthenticated
        // For simplicity, let's emit Authenticated with isOwner as false if role check fails
        emit(Authenticated(event.firebaseUser!, isOwner: false));
      }
    } else {
      // User is signed out
      emit(Unauthenticated());
    }
  }

  Future<void> _onGoogleSignInRequested(
      AuthGoogleSignInRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    try {
      final userCredential = await _authService.signInWithGoogle();
      // AuthUserChanged event will be triggered by the stream if successful
      // If signInWithGoogle doesn't result in an auth state change (e.g., user cancels),
      // we might need to revert to Unauthenticated or previous state.
      // For now, the stream handles success. If it's null, it means cancellation or immediate failure.
      if (userCredential == null && _authService.getCurrentUser() == null) {
         // If Google sign-in was cancelled and there's no current user
        emit(Unauthenticated());
      }
      // If there was an error, it should be caught by the catch block
    } catch (e) {
      emit(AuthFailure("Google Sign-In Failed: ${e.toString()}"));
      // Ensure we transition back to Unauthenticated if error occurs during the process
      if (_authService.getCurrentUser() == null) {
        emit(Unauthenticated());
      }
    }
  }

  Future<void> _onEmailSignInRequested(
      AuthEmailSignInRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    try {
      // In a real scenario, you'd get a UserCredential back
      await _authService.signInWithEmail(event.email, event.password);
      // AuthUserChanged event will be triggered by the stream if successful.
      // If the stub doesn't trigger stream, manually emit Unauthenticated if no user.
      if (_authService.getCurrentUser() == null) {
        emit(Unauthenticated()); // Or AuthFailure if signInWithEmail provides error feedback
      }
    } catch (e) {
      emit(AuthFailure("Email Sign-In Failed: ${e.toString()}"));
       if (_authService.getCurrentUser() == null) {
        emit(Unauthenticated());
      }
    }
  }

  Future<void> _onWhatsAppSignInRequested(
      AuthWhatsAppSignInRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    try {
      await _authService.signInWithWhatsApp(event.phoneNumber);
      // WhatsApp sign-in is complex and its success/failure handling
      // would depend on the specific Firebase Phone Auth implementation.
      // For now, assuming it might not immediately reflect in authStateChanges
      // without further setup (e.g. manual sign in with credential).
      // Re-evaluate based on actual implementation.
      // If stubbed, it likely won't change state, so we might revert.
      if (_authService.getCurrentUser() == null) {
        emit(Unauthenticated()); // Or a specific state like WhatsAppVerificationCodeSent
      }
    } catch (e) {
      emit(AuthFailure("WhatsApp Sign-In Failed: ${e.toString()}"));
      if (_authService.getCurrentUser() == null) {
        emit(Unauthenticated());
      }
    }
  }


  Future<void> _onSignOutRequested(
      AuthSignOutRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    try {
      await _authService.signOut();
      // AuthUserChanged event will be triggered by the stream, emitting Unauthenticated
    } catch (e) {
      emit(AuthFailure("Sign Out Failed: ${e.toString()}"));
      // Even if sign-out fails, try to determine current state
      if (_authService.getCurrentUser() != null) {
        final bool isOwner = await _authService.isCurrentUserOwner();
        emit(Authenticated(_authService.getCurrentUser()!, isOwner: isOwner));
      } else {
        emit(Unauthenticated());
      }
    }
  }

  @override
  Future<void> close() {
    _userSubscription?.cancel();
    return super.close();
  }
}
