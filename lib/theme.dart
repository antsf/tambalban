import 'package:flutter/painting.dart';
import 'package:flutter/widgets.dart';

Color blackColor = const Color(0xff061408);
Color whiteColor = const Color(0xffEBF9ED);
Color greenColor = const Color(0xff3BC24B);
Color grayColor = const Color(0xff8C8484);

TextStyle blackTextStyle = TextStyle(fontFamily: 'Sora', color: blackColor);
TextStyle whiteTextStyle = TextStyle(fontFamily: 'Sora', color: whiteColor);
TextStyle grayTextStyle = TextStyle(fontFamily: 'Sora', color: grayColor);

FontWeight light = FontWeight.w300;
FontWeight medium = FontWeight.w500;
FontWeight semiBold = FontWeight.w600;
FontWeight bold = FontWeight.w700;

String capitalize(words) {
  return words
      .toLowerCase()
      .split(" ")
      .map((word) => word[0].toUpperCase() + word.substring(1, word.length))
      .join(" ");
}
