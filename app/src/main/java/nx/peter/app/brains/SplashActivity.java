package nx.peter.app.brains;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import nx.peter.app.brains.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
  ActivitySplashBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    Window window = getWindow();
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(getColor(nx.peter.app.android_ui.R.color.grey));
    }

    binding = ActivitySplashBinding.inflate(getLayoutInflater());

    setContentView(binding.getRoot());
  }
}
