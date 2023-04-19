package techpro.studio.native_video_trimmer


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
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
  private var activity: FragmentActivity? = null
  private lateinit var result: Result

  private lateinit var startForResult: ActivityResultLauncher<Intent>
  
  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "native_video_trimmer")
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {

  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "run") {
      this.result = result

      trimVideo(call.argument("path")!!)
    } else {
      result.notImplemented()
    }
  }

  private fun trimVideo(path: String) {
    TrimVideo.activity("file://$path").setMinDuration(1).setAccurateCut(true).start(activity, startForResult);
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity as? FragmentActivity
    startForResult = activity!!.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      result: ActivityResult ->
      if (result.resultCode == Activity.RESULT_OK &&
              result.data != null) {
        try {
          this.result.success(TrimVideo.getTrimmedVideoPath(result.data));
        } catch (e: Exception) {
          this.result.error(TrimVideoErrors.trimErrorCode, e.message, null);
        }
      }
    }
    print("Attached")
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity as? FragmentActivity
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

}
