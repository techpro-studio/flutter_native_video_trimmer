#import "NativeVideoTrimmerPlugin.h"
#import <UIKit/UIKit.h>

@interface NativeVideoTrimmerPlugin () <UINavigationControllerDelegate, UIVideoEditorControllerDelegate>

@property (copy, nonatomic) FlutterResult result;

@end


@implementation NativeVideoTrimmerPlugin

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"native_video_trimmer"
            binaryMessenger:[registrar messenger]];
  NativeVideoTrimmerPlugin* instance = [[NativeVideoTrimmerPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

-(void)videoEditorControllerDidCancel:(UIVideoEditorController *)editor {
    self.result(nil);
    [editor dismissViewControllerAnimated:YES completion:nil];
}

- (void)videoEditorController:(UIVideoEditorController *)editor didFailWithError:(NSError *)error {
    self.result([FlutterError errorWithCode:@"trim.error" message:error.localizedDescription details:nil]);
    [editor dismissViewControllerAnimated:YES completion:nil];
}

- (void)videoEditorController:(UIVideoEditorController *)editor didSaveEditedVideoToPath:(NSString *)editedVideoPath {
    self.result(editedVideoPath);
    [editor dismissViewControllerAnimated:YES completion:nil];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"run" isEqualToString:call.method]) {
      NSString* videoPath = call.arguments[@"path"];
      UIVideoEditorController *videoEditor = [[UIVideoEditorController alloc] init];
      videoEditor.videoPath = videoPath;
      videoEditor.videoQuality = UIImagePickerControllerQualityTypeHigh;
      videoEditor.delegate = self;
      videoEditor.modalPresentationStyle = UIModalPresentationFullScreen;
      self.result = result;
      [UIApplication.sharedApplication.keyWindow.rootViewController presentViewController:videoEditor animated:YES completion:nil];
  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
