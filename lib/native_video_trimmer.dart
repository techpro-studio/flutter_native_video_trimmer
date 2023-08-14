import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';

class NativeVideoTrimmerErrorCodes {
  static const String trimErrorCode = "trim.error.default";
  static const String trimImpossibleWithVideoCode = "trim.error.impossible";
}

class NativeVideoTrimmer {
  static const MethodChannel _channel = const MethodChannel('native_video_trimmer');

  static Future<String?> trim(String path, {Rect? iPadPopoverSourceRect}) async {
    final Map<String, dynamic> params = {"path": path};
    if (iPadPopoverSourceRect != null) {
      params.addAll({
        "sourceRectOriginX": iPadPopoverSourceRect.left,
        "sourceRectOriginY": iPadPopoverSourceRect.top,
        "sourceRectSizeWidth": iPadPopoverSourceRect.width,
        "sourceRectSizeHeight": iPadPopoverSourceRect.height,
      });
    }
    return await _channel.invokeMethod('run', params);
  }
}
