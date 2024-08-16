import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path/path.dart';

import '../ad_helper.dart';
import '../cubit/connected_cubit.dart';
import '../cubit/place_cubit.dart';
import '../model/place_model.dart';
import '../theme.dart';
import '../widgets/checkbox.dart';
import '../widgets/input.dart';
import '../widgets/radio.dart';

class AddPlace extends StatefulWidget {
  const AddPlace({super.key});

  @override
  State<AddPlace> createState() => _AddPlaceState();
}

class _AddPlaceState extends State<AddPlace> {
  bool isLoading = false;
  bool isCheckedBycicle = false;
  bool isCheckedMotorbike = false;
  bool isCheckedCar = false;
  Position? position;
  bool homeServiceSelected = false;

  String helperTextImage = '';
  String helperTextName = '';
  String helperTextAddress = '';
  String helperTextOpenTime = '';
  String helperTextPhoneNumber = '';
  String helperTextLatitude = '';
  String helperTextLongitude = '';
  String helperTextVehicle = '';
  String helperTextServices = '';
  final TextEditingController nameController = TextEditingController(text: '');
  final TextEditingController addressController =
      TextEditingController(text: '');
  final TextEditingController openTimeController =
      TextEditingController(text: '');
  final TextEditingController phoneNumberController =
      TextEditingController(text: '');
  final TextEditingController latitudeController =
      TextEditingController(text: '');
  final TextEditingController longitudeController =
      TextEditingController(text: '');
  final TextEditingController servicesController =
      TextEditingController(text: '');

  BannerAd? _bannerAd;

  void getLocation() async {
    setState(() {
      isLoading = true;
    });
    position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high);

    setState(() {
      latitudeController.text = position!.latitude.toString();
      longitudeController.text = position!.longitude.toString();
      isLoading = false;
      helperTextLatitude = '';
      helperTextLongitude = '';
    });
  }

  File? image;
  final picker = ImagePicker();

  Future _openCamera() async {
    try {
      final pickedImage = await picker.pickImage(
          source: ImageSource.camera, maxHeight: 300, maxWidth: 500);
      if (pickedImage != null) {
        setState(() {
          image = File(pickedImage.path);
          helperTextImage = '';
        });
      }
    } catch (e) {
      print('error: $e');
    }
  }

  Future _openGalley() async {
    final imageGallery = await picker.pickImage(
        source: ImageSource.gallery, maxHeight: 300, maxWidth: 500);
    if (imageGallery != null) {
      setState(() {
        image = File(imageGallery.path);
        helperTextImage = '';
      });
    }
  }

  _initBannerAd() {
    BannerAd(
        adUnitId: AdHelper.bannerAdUnitId,
        request: const AdRequest(),
        size: AdSize.banner,
        listener: BannerAdListener(
          onAdLoaded: (ad) {
            setState(() {
              _bannerAd = ad as BannerAd;
            });
          },
          onAdFailedToLoad: (ad, err) {
            debugPrint('Failed to load a banner ad: ${err.message}');
            ad.dispose();
          },
        )).load();
  }

  @override
  void initState() {
    _initBannerAd();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    Widget chooseImage() {
      return Container(
          margin: const EdgeInsets.only(bottom: 16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Foto Lokasi', style: blackTextStyle),
              const SizedBox(
                height: 6.0,
              ),
              Stack(
                children: [
                  ClipRRect(
                    borderRadius: BorderRadius.circular(8.0),
                    child: image != null
                        ? Image.file(
                            image!,
                            height: 170.0,
                            width: double.infinity,
                            fit: BoxFit.cover,
                            semanticLabel: 'Foto tambal ban',
                          )
                        : Image.asset(
                            'assets/image-default.png',
                            height: 170.0,
                            width: double.infinity,
                            fit: BoxFit.cover,
                            semanticLabel: 'Foto default',
                          ),
                  ),
                  Container(
                    width: double.infinity,
                    height: 170.0,
                    padding: EdgeInsets.symmetric(
                        vertical: 65.0,
                        horizontal:
                            (MediaQuery.of(context).size.width / 2) - 70.0),
                    decoration: BoxDecoration(
                      color: whiteColor.withOpacity(0.3),
                    ),
                    child: TextButton(
                      style: TextButton.styleFrom(
                        backgroundColor: greenColor,
                      ),
                      onPressed: () {
                        showModalBottomSheet(
                            context: context,
                            builder: ((builder) {
                              return Container(
                                height: 180.0,
                                width: MediaQuery.of(context).size.width,
                                padding: const EdgeInsets.all(16.0),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.center,
                                  children: [
                                    Text(
                                      'Pilih Gambar',
                                      style: grayTextStyle,
                                      textAlign: TextAlign.center,
                                    ),
                                    const Divider(),
                                    MaterialButton(
                                      onPressed: () {
                                        Navigator.pop(context);
                                        _openCamera();
                                      },
                                      height: 40.0,
                                      child: Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.center,
                                        children: [
                                          Icon(
                                            Icons.camera_alt_outlined,
                                            color: greenColor,
                                          ),
                                          const SizedBox(
                                            width: 6.0,
                                          ),
                                          Text(
                                            'Buka Kamera',
                                            style: blackTextStyle,
                                          ),
                                        ],
                                      ),
                                    ),
                                    const Divider(),
                                    MaterialButton(
                                      onPressed: () {
                                        Navigator.pop(context);
                                        _openGalley();
                                      },
                                      height: 40.0,
                                      child: Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.center,
                                        children: [
                                          Icon(
                                            Icons.image_outlined,
                                            color: greenColor,
                                          ),
                                          const SizedBox(
                                            width: 6.0,
                                          ),
                                          Text(
                                            'Ambil dari Galeri',
                                            style: blackTextStyle,
                                          ),
                                        ],
                                      ),
                                    ),
                                  ],
                                ),
                              );
                            }));
                      },
                      child: Text(
                        'Pilih Gambar',
                        style: whiteTextStyle.copyWith(fontSize: 12.0),
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(
                height: 4.0,
              ),
              helperTextImage.isNotEmpty
                  ? Text(helperTextImage,
                      style: blackTextStyle.copyWith(
                          color: Colors.red, fontSize: 12.0))
                  : const SizedBox(),
            ],
          ));
    }

    Widget nameInput() {
      return Input(
          text: 'Nama',
          controller: nameController,
          hintText: 'contoh: Tambal Ban Sentosa',
          helperText: helperTextName,
          onChange: (text) => setState(() {
                helperTextName = '';
              }));
    }

    Widget addressInput() {
      return Input(
          text: 'Alamat',
          controller: addressController,
          keyboardType: TextInputType.streetAddress,
          hintText: 'contoh: Jalan Jend. Sudirman',
          helperText: helperTextAddress,
          onChange: (text) => setState(() {
                helperTextAddress = '';
              }));
    }

    Widget openTimeInput() {
      return Input(
          text: 'Buka',
          controller: openTimeController,
          hintText: 'contoh: 09:00 - 17:00',
          helperText: helperTextOpenTime,
          onChange: (text) => setState(() {
                helperTextOpenTime = '';
              }));
    }

    Widget phoneNumberInput() {
      return Input(
          text: 'Nomer WhatsApp',
          controller: phoneNumberController,
          keyboardType: TextInputType.number,
          hintText: 'contoh: +62813xxx',
          helperText: helperTextPhoneNumber,
          onChange: (text) => setState(() {
                helperTextPhoneNumber = '';
              }));
    }

    Widget locationInput() {
      return Container(
          margin: const EdgeInsets.only(bottom: 16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Lokasi/Koordinat', style: blackTextStyle),
              const SizedBox(
                height: 6.0,
              ),
              SizedBox(
                width: double.infinity,
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Flexible(
                        flex: 3,
                        child: Column(
                          children: [
                            TextFormField(
                              controller: latitudeController,
                              keyboardType: TextInputType.number,
                              cursorColor: greenColor,
                              onChanged: (text) => setState(() {
                                helperTextLatitude = '';
                              }),
                              // toolbarOptions: const ToolbarOptions(
                              //     copy: true,
                              //     cut: true,
                              //     paste: true,
                              //     selectAll: true),
                              style: blackTextStyle,
                              decoration: InputDecoration(
                                  hintText: 'Latitude',
                                  hintStyle: grayTextStyle.copyWith(
                                      fontSize: 14.0, fontWeight: light),
                                  border: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(8.0),
                                      borderSide: BorderSide(
                                          width: 1, color: grayColor)),
                                  focusedBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(8.0),
                                      borderSide: BorderSide(
                                          width: 1, color: greenColor)),
                                  contentPadding: const EdgeInsets.symmetric(
                                      vertical: 8.0, horizontal: 10.0)),
                            ),
                            const SizedBox(
                              height: 4.0,
                            ),
                            helperTextLatitude.isNotEmpty
                                ? Text(helperTextLatitude,
                                    style: blackTextStyle.copyWith(
                                        color: Colors.red, fontSize: 12.0))
                                : const SizedBox(),
                          ],
                        )),
                    const SizedBox(
                      width: 6.0,
                    ),
                    Flexible(
                      flex: 3,
                      child: Column(
                        children: [
                          TextFormField(
                            controller: longitudeController,
                            keyboardType: TextInputType.number,
                            cursorColor: greenColor,
                            onChanged: (text) => setState(() {
                              helperTextLongitude = '';
                            }),
                            // toolbarOptions: const ToolbarOptions(
                            //     copy: true,
                            //     cut: true,
                            //     paste: true,
                            //     selectAll: true),
                            style: blackTextStyle,
                            decoration: InputDecoration(
                                hintText: 'Longitude',
                                hintStyle: grayTextStyle.copyWith(
                                    fontSize: 14.0, fontWeight: light),
                                border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(8.0),
                                    borderSide:
                                        BorderSide(width: 1, color: grayColor)),
                                focusedBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(8.0),
                                    borderSide: BorderSide(
                                        width: 1, color: greenColor)),
                                contentPadding: const EdgeInsets.symmetric(
                                    vertical: 8.0, horizontal: 10.0)),
                          ),
                          const SizedBox(
                            height: 4.0,
                          ),
                          helperTextLongitude.isNotEmpty
                              ? Text(helperTextLongitude,
                                  style: blackTextStyle.copyWith(
                                      color: Colors.red, fontSize: 12.0))
                              : const SizedBox(),
                        ],
                      ),
                    ),
                    const SizedBox(
                      width: 6.0,
                    ),
                    Flexible(
                        child: Container(
                      padding: isLoading
                          ? const EdgeInsets.symmetric(
                              horizontal: 8.0, vertical: 6.0)
                          : const EdgeInsets.all(0),
                      decoration: BoxDecoration(
                          color: greenColor,
                          borderRadius: BorderRadius.circular(8.0)),
                      child: isLoading
                          ? CircularProgressIndicator(
                              color: whiteColor,
                              strokeWidth: 2,
                            )
                          : IconButton(
                              onPressed: () {
                                getLocation();
                              },
                              icon: const Icon(Icons.location_on_outlined),
                              color: whiteColor),
                    ))
                  ],
                ),
              ),
            ],
          ));
    }

    Widget vehicleCheckBox() {
      return Container(
          margin: const EdgeInsets.only(bottom: 24.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Kendaraan', style: blackTextStyle),
              const SizedBox(
                height: 6.0,
              ),
              Container(
                decoration: BoxDecoration(
                    border: Border.all(width: 1, color: grayColor),
                    borderRadius: BorderRadius.circular(8.0)),
                child: Column(
                  children: [
                    LabeledCheckbox(
                        label: 'Sepeda',
                        value: isCheckedBycicle,
                        onChanged: (bool? value) {
                          setState(() {
                            isCheckedBycicle = value!;
                          });
                        }),
                    LabeledCheckbox(
                        label: 'Motor',
                        value: isCheckedMotorbike,
                        selected: true,
                        onChanged: (bool? value) {
                          setState(() {
                            isCheckedMotorbike = value!;
                          });
                        }),
                    LabeledCheckbox(
                        label: 'Mobil',
                        value: isCheckedCar,
                        onChanged: (bool? value) {
                          setState(() {
                            isCheckedCar = value!;
                          });
                        }),
                  ],
                ),
              ),
              const SizedBox(
                height: 4.0,
              ),
              helperTextVehicle.isNotEmpty
                  ? Text(helperTextVehicle,
                      style: blackTextStyle.copyWith(
                          color: Colors.red, fontSize: 12.0))
                  : const SizedBox(),
            ],
          ));
    }

    Widget homeServiceRadio() {
      return Container(
          margin: const EdgeInsets.only(bottom: 24.0),
          child:
              Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text('Terima Panggilan', style: blackTextStyle),
            const SizedBox(
              height: 6.0,
            ),
            Container(
                decoration: BoxDecoration(
                    border: Border.all(width: 1, color: grayColor),
                    borderRadius: BorderRadius.circular(8.0)),
                child: Column(children: [
                  LabeledRadio(
                    label: 'Ya',
                    padding: const EdgeInsets.all(10.0),
                    groupValue: homeServiceSelected,
                    value: true,
                    onChanged: (bool newValue) {
                      setState(() {
                        homeServiceSelected = newValue;
                      });
                    },
                  ),
                  LabeledRadio(
                    label: 'Tidak',
                    padding: const EdgeInsets.all(10.0),
                    groupValue: homeServiceSelected,
                    value: false,
                    onChanged: (bool newValue) {
                      setState(() {
                        homeServiceSelected = newValue;
                      });
                    },
                  )
                ]))
          ]));
    }

    Widget servicesInput() {
      return Input(
          text: 'Layanan',
          controller: servicesController,
          keyboardType: TextInputType.multiline,
          hintText: 'contoh: Ganti ban, Nitrogen, Tubeless',
          helperText: helperTextServices,
          onChange: (text) => setState(() {
                helperTextServices = '';
              }));
    }

    Widget buttonSave() {
      return BlocConsumer<PlaceCubit, PlaceState>(
        listener: (context, state) {
          if (state is PlaceSuccess) {
            ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                backgroundColor: greenColor,
                duration: const Duration(milliseconds: 1500),
                content: Text(
                  'Data berhasil disimpan!',
                  style: whiteTextStyle,
                )));
            Timer(
              const Duration(milliseconds: 2000),
              () {
                Navigator.pushNamedAndRemoveUntil(
                    context, '/started', (route) => false);
              },
            );
          } else if (state is PlaceFailed) {
            ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                backgroundColor: Colors.red,
                content: Text(
                  state.error,
                  style: whiteTextStyle,
                )));
          }
        },
        builder: (context, state) {
          if (state is PlaceLoading) {
            return Center(
                child: CircularProgressIndicator(
              color: greenColor,
            ));
          }
          return MaterialButton(
            color: greenColor,
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8.0)),
            elevation: 0,
            height: 50.0,
            onPressed: () async {
              String imagePath = image?.path ?? '';
              if (imagePath == '') {
                setState(() {
                  helperTextImage = 'Foto lokasi harus diisi!';
                });
              }
              if (nameController.text.isEmpty) {
                setState(() {
                  helperTextName = 'Nama harus diisi!';
                });
              }
              if (addressController.text.isEmpty) {
                setState(() {
                  helperTextAddress = 'Alamat harus diisi!';
                });
              }
              if (openTimeController.text.isEmpty) {
                setState(() {
                  helperTextOpenTime = 'Jam buka harus diisi!';
                });
              }
              if (phoneNumberController.text.isEmpty) {
                setState(() {
                  helperTextPhoneNumber = 'Nomer WhatsApp boleh diisi "-"!';
                });
              }
              if (latitudeController.text.isEmpty) {
                setState(() {
                  helperTextLatitude = 'Latitude harus diisi!';
                });
              }
              if (longitudeController.text.isEmpty) {
                setState(() {
                  helperTextLongitude = 'Longitude harus diisi!';
                });
              }
              if (!isCheckedBycicle && !isCheckedMotorbike && !isCheckedCar) {
                setState(() {
                  helperTextVehicle = 'Kendaraan harus diisi!';
                });
              }
              if (servicesController.text.isEmpty) {
                setState(() {
                  helperTextServices = 'Layanan harus diisi!';
                });
              }
              final String id =
                  DateTime.now().microsecondsSinceEpoch.toString();
              final String dateNow = DateTime.now().toString();
              final String fileName = basename(image!.path);
              String fullName = '';
              String prefixName = nameController.text.substring(0, 6);
              print(prefixName);
              if (prefixName.toLowerCase() != 'tambal') {
                fullName = 'Tambal Ban ${nameController.text}';
              } else {
                fullName = nameController.text;
              }
              List vehicle = [];
              if (isCheckedBycicle) {
                vehicle.add('Sepeda');
              }
              if (isCheckedMotorbike) {
                vehicle.add('Motor');
              }
              if (isCheckedCar) {
                vehicle.add('Mobil');
              }
              try {
                PlaceModel place = PlaceModel(
                  id: id,
                  name: fullName,
                  address: addressController.text,
                  openTime: openTimeController.text,
                  phoneNumber: phoneNumberController.text,
                  latitude: double.parse(latitudeController.text),
                  longitude: double.parse(longitudeController.text),
                  vehicle: vehicle,
                  homeService: homeServiceSelected,
                  services: servicesController.text,
                  status: 'pending',
                  createdAt: dateNow,
                  updatedAt: dateNow,
                );
                context.read<PlaceCubit>().createPlace(place, image, fileName);
              } catch (err) {
                print(err);
              }
            },
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.save_outlined,
                  color: whiteColor,
                ),
                const SizedBox(
                  width: 6.0,
                ),
                Text(
                  'Tambah Data',
                  style: whiteTextStyle.copyWith(fontSize: 16.0),
                ),
              ],
            ),
          );
        },
      );
    }

    return Scaffold(
        backgroundColor: whiteColor,
        body: BlocBuilder<ConnectedCubit, ConnectedState>(
            builder: (context, state) {
          if ((state is ConnectedSuccess &&
                  state.connectionType == ConnectionType.wifi) ||
              (state is ConnectedSuccess &&
                  state.connectionType == ConnectionType.mobile)) {
            return CustomScrollView(slivers: [
              SliverAppBar(
                backgroundColor: greenColor,
                elevation: 0,
                floating: true,
                title: Text(
                  'Tambah Data Tambal Ban',
                  style: whiteTextStyle.copyWith(fontSize: 16.0),
                ),
              ),
              SliverToBoxAdapter(
                child: Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 16.0,
                  ),
                  child: Column(
                    children: [
                      if (_bannerAd != null)
                        SizedBox(
                          width: _bannerAd!.size.width.toDouble(),
                          height: _bannerAd!.size.height.toDouble(),
                          child: AdWidget(ad: _bannerAd!),
                        ),
                      const SizedBox(
                        height: 24.0,
                      ),
                      chooseImage(),
                      nameInput(),
                      addressInput(),
                      openTimeInput(),
                      phoneNumberInput(),
                      locationInput(),
                      servicesInput(),
                      vehicleCheckBox(),
                      homeServiceRadio(),
                      buttonSave(),
                      const SizedBox(
                        height: 24.0,
                      ),
                    ],
                  ),
                ),
              ),
            ]);
          }

          return Center(
              child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                'Internet tidak terhubung',
                style: blackTextStyle,
              ),
              const SizedBox(
                height: 12.0,
              ),
              TextButton(
                  onPressed: () {
                    context
                        .read<ConnectedCubit>()
                        .connectivityStreamSubcription;
                  },
                  style: TextButton.styleFrom(backgroundColor: greenColor),
                  child: Text('Muat Ulang', style: whiteTextStyle)),
            ],
          ));
        }));
  }
}
