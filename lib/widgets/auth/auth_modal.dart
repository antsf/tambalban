import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:tambal_ban/auth_bloc/auth_bloc.dart';

class AuthModal extends StatefulWidget {
  const AuthModal({super.key});

  @override
  State<AuthModal> createState() => _AuthModalState();
}

class _AuthModalState extends State<AuthModal> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController(); // Assuming password for email login

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return BlocListener<AuthBloc, AuthState>(
      listener: (context, state) {
        if (state is Authenticated) {
          Navigator.of(context).pop(); // Close modal on successful authentication
        } else if (state is AuthFailure) {
          ScaffoldMessenger.of(context)
            ..hideCurrentSnackBar()
            ..showSnackBar(
              SnackBar(content: Text('Authentication Failed: ${state.message}')),
            );
        }
      },
      child: Padding(
        padding: EdgeInsets.only(
          bottom: MediaQuery.of(context).viewInsets.bottom, // For keyboard
          left: 16,
          right: 16,
          top: 16,
        ),
        child: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              Text(
                'Sign In or Create Account',
                style: Theme.of(context).textTheme.headlineSmall,
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),

              // Google Sign-In Button
              ElevatedButton.icon(
                icon: const Icon(Icons.g_mobiledata_outlined), // Placeholder, replace with Google icon
                label: const Text('Sign in with Google'),
                onPressed: () {
                  context.read<AuthBloc>().add(AuthGoogleSignInRequested());
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.redAccent, // Google-like color
                  foregroundColor: Colors.white,
                ),
              ),
              const SizedBox(height: 12),

              // Email Sign-In
              TextFormField(
                controller: _emailController,
                decoration: const InputDecoration(labelText: 'Email'),
                keyboardType: TextInputType.emailAddress,
              ),
              const SizedBox(height: 8),
              TextFormField(
                controller: _passwordController,
                decoration: const InputDecoration(labelText: 'Password (Optional)'),
                obscureText: true,
              ),
              const SizedBox(height: 12),
              ElevatedButton(
                child: const Text('Sign in with Email'),
                onPressed: () {
                  if (_emailController.text.isNotEmpty) {
                    // Password can be optional if using email link, for now it's required if filled
                    context.read<AuthBloc>().add(AuthEmailSignInRequested(
                          _emailController.text.trim(),
                          _passwordController.text, // Send empty if not using password
                        ));
                  } else {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Please enter your email')),
                    );
                  }
                },
              ),
              const SizedBox(height: 12),

              // WhatsApp Sign-In Button (Placeholder)
              OutlinedButton.icon(
                icon: const Icon(Icons.chat_bubble_outline), // Placeholder, replace with WhatsApp icon
                label: const Text('Sign in with WhatsApp'),
                onPressed: () {
                  // For now, just a print statement. Actual implementation is complex.
                  print('WhatsApp Sign-In button pressed. Needs implementation.');
                  ScaffoldMessenger.of(context).showSnackBar(
                     const SnackBar(content: Text('WhatsApp Sign-In: Not implemented yet.')),
                  );
                  // Example: context.read<AuthBloc>().add(AuthWhatsAppSignInRequested("dummy_phone_number"));
                },
              ),
              const SizedBox(height: 24),
              const Text(
                'By continuing, you agree to our Terms of Service and Privacy Policy.',
                style: TextStyle(fontSize: 12, color: Colors.grey),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 16),
            ],
          ),
        ),
      ),
    );
  }
}

// Helper to show the modal
void showAuthModal(BuildContext context) {
  showModalBottomSheet(
    context: context,
    isScrollControlled: true, // Important for keyboard visibility
    builder: (_) {
      // Provide the AuthBloc to the modal, assuming it's available in the calling context
      return BlocProvider.value(
        value: BlocProvider.of<AuthBloc>(context),
        child: const AuthModal(),
      );
    },
  );
}
