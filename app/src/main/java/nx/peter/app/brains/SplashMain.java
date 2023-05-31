package nx.peter.app.brains;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import com.itsaky.androidide.logsender.LogSender;

public class SplashMain extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Remove this line if you don't want AndroidIDE to show this app's logs
        LogSender.startLogging(this);

        SplashScreen.Companion.installSplashScreen(this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        // Check Internet connection
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean connected =
                manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                                == NetworkInfo.State.CONNECTED
                        || manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                                == NetworkInfo.State.CONNECTED;

        if (!connected) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            try {
                wait();
            } catch (InterruptedException e) {
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            }
        }
        
        startActivity(new Intent(SplashMain.this, SplashActivity.class));
        
    }
    
}
