import 'package:flutter/material.dart';

import '../theme.dart';

class Input extends StatelessWidget {
  final String text;
  final TextEditingController controller;
  final TextInputType keyboardType;
  final String hintText;
  final String helperText;
  final void Function(String) onChange;
  const Input(
      {super.key,
      required this.text,
      required this.controller,
      required this.hintText,
      required this.helperText,
      required this.onChange,
      this.keyboardType = TextInputType.text});

  @override
  Widget build(BuildContext context) {
    return Container(
        margin: const EdgeInsets.only(bottom: 16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(text, style: blackTextStyle),
            const SizedBox(
              height: 6.0,
            ),
            TextFormField(
              controller: controller,
              style: blackTextStyle,
              cursorColor: greenColor,
              textCapitalization: TextCapitalization.words,
              onChanged: onChange,
              keyboardType: keyboardType,
              // toolbarOptions: const ToolbarOptions(
              //     copy: true, cut: true, paste: true, selectAll: true),
              decoration: InputDecoration(
                  hintText: hintText,
                  hintStyle:
                      grayTextStyle.copyWith(fontSize: 14.0, fontWeight: light),
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(8.0),
                      borderSide: BorderSide(width: 1, color: grayColor)),
                  focusedBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(8.0),
                      borderSide: BorderSide(width: 1, color: greenColor)),
                  contentPadding: const EdgeInsets.symmetric(
                      vertical: 8.0, horizontal: 16.0)),
            ),
            const SizedBox(
              height: 4.0,
            ),
            helperText.isNotEmpty
                ? Text(helperText,
                    style: blackTextStyle.copyWith(
                        color: Colors.red, fontSize: 12.0))
                : const SizedBox(),
          ],
        ));
  }
}
