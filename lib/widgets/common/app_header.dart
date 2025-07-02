import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:tambal_ban/auth_bloc/auth_bloc.dart';
import 'package:tambal_ban/widgets/auth/auth_modal.dart';
import 'package:tambal_ban/theme.dart'; // Assuming theme.dart contains greenColor

class AppHeader extends StatelessWidget implements PreferredSizeWidget {
  const AppHeader({super.key});

  @override
  Widget build(BuildContext context) {
    return AppBar(
      backgroundColor: greenColor, // Or your desired header color
      elevation: 0, // Flat design
      title: Row(
        children: [
          // Assuming you have a logo in assets
          Image.asset(
            'assets/logo.png', // Replace with your actual logo path
            height: 32, // Adjust size as needed
            // Add error builder if you want to handle missing logo
          ),
          const SizedBox(width: 8),
          // Optional: Add App Name Text if logo is small
          // Text(
          //   'Tambal Ban',
          //   style: whiteTextStyle.copyWith(fontSize: 20, fontWeight: semiBold),
          // ),
        ],
      ),
      actions: [
        BlocBuilder<AuthBloc, AuthState>(
          builder: (context, state) {
            return IconButton(
              icon: Icon(
                state is Authenticated ? Icons.account_circle : Icons.login,
                color: Colors.white,
              ),
              onPressed: () {
                if (state is Authenticated) {
                  // Optional: Show user menu or profile page
                  // For now, let's offer a sign-out option directly for simplicity
                  showDialog(
                    context: context,
                    builder: (_) => AlertDialog(
                      title: Text('Signed In as ${state.firebaseUser.email ?? 'User'}'),
                      content: Text(state.isOwner ? 'You are an Owner.' : 'You are a Guest user (role check).'),
                      actions: [
                        TextButton(
                          child: const Text('Sign Out'),
                          onPressed: () {
                            context.read<AuthBloc>().add(AuthSignOutRequested());
                            Navigator.of(context).pop();
                          },
                        ),
                        TextButton(
                          child: const Text('Close'),
                          onPressed: () {
                            Navigator.of(context).pop();
                          },
                        ),
                      ],
                    ),
                  );
                } else {
                  showAuthModal(context);
                }
              },
            );
          },
        ),
      ],
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
