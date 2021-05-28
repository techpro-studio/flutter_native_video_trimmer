import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:image_picker/image_picker.dart';
import 'package:native_video_trimmer/native_video_trimmer.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> trimVideo() async {
    final imagePicker = ImagePicker();
    final video = await imagePicker.getVideo(source: ImageSource.camera);
    // Platform messages may fail, so we use a try/catch PlatformException.
    final trimmedPath = await NativeVideoTrimmer.trim(video.path);
    print("Trimmed video $trimmedPath");
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: IconButton(
            iconSize: 50.0,
            icon: Icon(Icons.camera),
            color: Theme.of(context).primaryColor,
            onPressed: trimVideo,
          ),
        ),
      ),
    );
  }
}
