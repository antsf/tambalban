import 'package:flutter/material.dart';

import '../theme.dart';

class PlaceList extends StatefulWidget {
  final Map<dynamic, dynamic> place;
  final Function() onPressed;
  const PlaceList(this.place, {super.key, required this.onPressed});

  @override
  State<PlaceList> createState() => _PlaceListState();
}

class _PlaceListState extends State<PlaceList> {
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: widget.onPressed,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12.0),
        width: double.infinity,
        constraints: const BoxConstraints(minHeight: 70.0),
        decoration: BoxDecoration(boxShadow: [
          BoxShadow(
              color: greenColor.withOpacity(0.2),
              blurRadius: 30,
              offset: const Offset(0, 20))
        ], color: Colors.white, borderRadius: BorderRadius.circular(8.0)),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: Container(
                constraints: const BoxConstraints(minHeight: 70.0),
                decoration: BoxDecoration(
                    borderRadius: const BorderRadius.only(
                        bottomLeft: Radius.circular(8.0),
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
              width: 16.0,
            ),
            Expanded(
              flex: 2,
              child: Container(
                padding:
                    const EdgeInsets.only(top: 8.0, right: 8.0, bottom: 8.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(capitalize(widget.place['items'].name),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: blackTextStyle.copyWith(
                          fontSize: 14.0,
                          fontWeight: semiBold,
                        )),
                    const SizedBox(
                      height: 2.0,
                    ),
                    Text(capitalize(widget.place['items'].address),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: grayTextStyle.copyWith(
                          fontSize: 10.0,
                        )),
                    const SizedBox(
                      height: 6.0,
                    ),
                    SizedBox(
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
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
                          Row(
                            children: [
                              Icon(Icons.near_me_outlined,
                                  size: 14.0, color: grayColor),
                              const SizedBox(
                                width: 4.0,
                              ),
                              Text(
                                  '${widget.place['distance'].toStringAsFixed(2)} km',
                                  style: grayTextStyle.copyWith(
                                    fontSize: 10.0,
                                  ))
                            ],
                          ),
                        ],
                      ),
                    )
                  ],
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
