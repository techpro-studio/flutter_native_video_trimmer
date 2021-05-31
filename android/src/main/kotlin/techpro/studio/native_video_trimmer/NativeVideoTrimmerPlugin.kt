package techpro.studio.native_video_trimmer


import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.NonNull
import com.gowtham.library.utils.TrimVideo
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


object TrimVideoErrors {
  const val trimErrorCode = "trim.error.default"
}

/** NativeVideoTrimmerPlugin */
class NativeVideoTrimmerPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
  private lateinit var channel : MethodChannel
  private lateinit var activityPluginBinding: ActivityPluginBinding
  private lateinit var result: Result

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "native_video_trimmer")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "run") {
      this.result = result
      trimVideo(call.argument("path")!!)
    } else {
      result.notImplemented()
    }
  }

  private fun trimVideo(path: String) {
    TrimVideo.activity("file://$path").setMinDuration(1).setAccurateCut(true).start(activityPluginBinding.activity)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (requestCode == TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
      try {
        result.success(TrimVideo.getTrimmedVideoPath(data))
      } catch (e: Exception) {
        result.error(TrimVideoErrors.trimErrorCode, e.message, null);
      }
      return true;
    }
    return false
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.activityPluginBinding = binding
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    this.activityPluginBinding.removeActivityResultListener(this)
    this.activityPluginBinding = binding
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivity() {
    activityPluginBinding.removeActivityResultListener(this)
  }
}
