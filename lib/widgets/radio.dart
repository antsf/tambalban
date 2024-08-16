import 'package:flutter/material.dart';

import '../theme.dart';

class LabeledRadio extends StatelessWidget {
  final String label;
  final EdgeInsets padding;
  final bool groupValue;
  final bool value;
  final ValueChanged<bool> onChanged;

  const LabeledRadio(
      {super.key,
      required this.label,
      required this.padding,
      required this.groupValue,
      required this.value,
      required this.onChanged});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
        onTap: () {
          if (value != groupValue) {
            onChanged(value);
          }
        },
        child: Padding(
            padding: padding,
            child: Row(children: [
              Radio(
                groupValue: groupValue,
                value: value,
                onChanged: (bool? newValue) {
                  onChanged(newValue!);
                },
              ),
              Text(
                label,
                style: blackTextStyle,
              )
            ])));
  }
}
