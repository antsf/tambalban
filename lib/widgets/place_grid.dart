import 'package:flutter/material.dart';

import '../pages/detail.dart';
import '../theme.dart';

class PlaceGrid extends StatefulWidget {
  final Map<dynamic, dynamic> place;
  const PlaceGrid(
    this.place, {
    super.key,
  });

  @override
  State<PlaceGrid> createState() => _PlaceGridState();
}

class _PlaceGridState extends State<PlaceGrid> {
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => DetailPage(widget.place),
            ));
      },
      child: Container(
        decoration: BoxDecoration(boxShadow: [
          BoxShadow(
              color: greenColor.withOpacity(0.2),
              blurRadius: 30,
              offset: const Offset(0, 20))
        ], color: Colors.white, borderRadius: BorderRadius.circular(8.0)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Expanded(
            child: Container(
              constraints: const BoxConstraints(minHeight: 90.0),
              decoration: BoxDecoration(
                  borderRadius: const BorderRadius.only(
                      topRight: Radius.circular(8.0),
                      topLeft: Radius.circular(8.0)),
                  image: DecorationImage(
                    image: NetworkImage(
                      widget.place['items'].imageUrl,
                    ),
                    fit: BoxFit.cover,
                  )),
            ),
          ),
          const SizedBox(
            height: 8.0,
          ),
          Container(
            padding: const EdgeInsets.only(left: 8.0, right: 8.0, bottom: 8.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  capitalize(widget.place['items'].name),
                  maxLines: 1,
                  style: blackTextStyle.copyWith(
                    fontSize: 12.0,
                    fontWeight: semiBold,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(
                  height: 4.0,
                ),
                Text(
                  capitalize(widget.place['items'].address),
                  style: grayTextStyle.copyWith(fontSize: 10.0),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(
                  height: 6.0,
                ),
                Row(
                  children: [
                    Icon(Icons.watch_later_outlined,
                        size: 14.0, color: greenColor),
                    const SizedBox(
                      width: 4.0,
                    ),
                    Text(
                      widget.place['items'].openTime,
                      style: grayTextStyle.copyWith(
                          fontSize: 10.0, color: greenColor),
                    )
                  ],
                ),
                const SizedBox(
                  height: 4.0,
                ),
                Row(
                  children: [
                    Icon(Icons.near_me_outlined, size: 14.0, color: grayColor),
                    const SizedBox(
                      width: 4.0,
                    ),
                    Text('${widget.place['distance'].toStringAsFixed(2)} km',
                        style: grayTextStyle.copyWith(
                          fontSize: 10.0,
                        ))
                  ],
                ),
              ],
            ),
          ),
        ]),
      ),
    );
  }
}
