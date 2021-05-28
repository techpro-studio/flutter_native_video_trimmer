import 'dart:async';

import 'package:flutter/services.dart';

class NativeVideoTrimmer {
  static const MethodChannel _channel = const MethodChannel('native_video_trimmer');

  static Future<String> trim(String path) async {
    return await _channel.invokeMethod('run', {"path": path});
  }
}
