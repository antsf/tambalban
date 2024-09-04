import 'package:flutter/painting.dart';
import 'package:flutter/widgets.dart';

Color blackColor = const Color(0xff061408);
Color whiteColor = const Color(0xffEBF9ED);
Color greenColor = const Color(0xff3BC24B);
Color grayColor = const Color(0xff8C8484);

TextStyle blackTextStyle = TextStyle(fontFamily: 'Sora', color: blackColor);
TextStyle whiteTextStyle = TextStyle(fontFamily: 'Sora', color: whiteColor);
TextStyle greenTextStyle = TextStyle(fontFamily: 'Sora', color: greenColor);
TextStyle grayTextStyle = TextStyle(fontFamily: 'Sora', color: grayColor);

FontWeight light = FontWeight.w300;
FontWeight medium = FontWeight.w500;
FontWeight semiBold = FontWeight.w600;
FontWeight bold = FontWeight.w700;

String capitalize(String words) {
  switch (words.length) {
    case 0:
      return words;
    case 1:
      return words.toUpperCase();
    default:
      return words.substring(0, 1).toUpperCase() + words.substring(1);
  }
}
