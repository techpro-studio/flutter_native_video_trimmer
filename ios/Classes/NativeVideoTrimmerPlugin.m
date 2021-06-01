#import "NativeVideoTrimmerPlugin.h"
#import <UIKit/UIKit.h>

@interface NativeVideoTrimmerPlugin () <UINavigationControllerDelegate, UIVideoEditorControllerDelegate>

@property (copy, nonatomic) FlutterResult result;

@end

static NSString * kDefaultTrimErrorCode = @"trim.error.default";
static NSString * kImpossibleToTrimErrorCode = @"trim.error.impossible";

#define CALL_FLOAT_ARG(key) ((NSNumber *) call.arguments[key]).floatValue

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
    self.result([FlutterError errorWithCode:kDefaultTrimErrorCode message:error.localizedDescription details:nil]);
    [editor dismissViewControllerAnimated:YES completion:nil];
}

- (void)videoEditorController:(UIVideoEditorController *)editor didSaveEditedVideoToPath:(NSString *)editedVideoPath {
    self.result(editedVideoPath);
    [editor dismissViewControllerAnimated:YES completion:nil];
}


- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"run" isEqualToString:call.method]) {
      NSString* videoPath = call.arguments[@"path"];
      if ([UIVideoEditorController canEditVideoAtPath:videoPath]){
      UIVideoEditorController *videoEditor = [[UIVideoEditorController alloc] init];
      videoEditor.videoPath = videoPath;
      videoEditor.videoQuality = UIImagePickerControllerQualityTypeHigh;
      videoEditor.delegate = self;
      if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad ){
            CGRect popoverSourceRect = CGRectMake(
                CALL_FLOAT_ARG(@"sourceRectOriginX"),
                CALL_FLOAT_ARG(@"sourceRectOriginY"),
                CALL_FLOAT_ARG(@"sourceRectSizeWidth"),
                CALL_FLOAT_ARG(@"sourceRectSizeHeight")
            );
            videoEditor.modalPresentationStyle = UIModalPresentationPopover;
            videoEditor.popoverPresentationController.sourceView = UIApplication.sharedApplication.keyWindow.rootViewController.view;
            videoEditor.popoverPresentationController.sourceRect = popoverSourceRect;
      } else {
          videoEditor.modalPresentationStyle = UIModalPresentationFullScreen;
      }
      self.result = result;
      [UIApplication.sharedApplication.keyWindow.rootViewController presentViewController:videoEditor animated:YES completion:nil];
      } else {
          result([FlutterError errorWithCode:kImpossibleToTrimErrorCode message:@"" details:nil]);
      }
  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
