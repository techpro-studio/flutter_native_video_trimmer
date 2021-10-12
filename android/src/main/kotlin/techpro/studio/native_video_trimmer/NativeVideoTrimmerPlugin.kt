package techpro.studio.native_video_trimmer


import android.app.Activity
import android.content.Intent
import androidx.activity.result.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import com.gowtham.library.utils.LogMessage
import com.gowtham.library.utils.TrimVideo
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


object TrimVideoErrors {
  const val trimErrorCode = "trim.error.default"
}

/** NativeVideoTrimmerPlugin */
class NativeVideoTrimmerPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  private lateinit var channel : MethodChannel
  private lateinit var activityPluginBinding: ActivityPluginBinding
  private lateinit var result: Result
  private lateinit  var startForResult: ActivityResultLauncher<Intent>


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
    TrimVideo.activity("file://$path").setMinDuration(1).setAccurateCut(true).start(activityPluginBinding.activity, startForResult)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    startForResult = binding.activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult(), binding.activity.intent) {
        result: ActivityResult ->
      if (result.resultCode == Activity.RESULT_OK &&
        result.data != null) {
        try {
          this.result.success(TrimVideo.getTrimmedVideoPath(result.data))
        } catch (e: Exception) {
          this.result.error(TrimVideoErrors.trimErrorCode, e.message, null)
        }
      } else
        LogMessage.v("videoTrimResultLauncher data is null")
    }
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }
}
