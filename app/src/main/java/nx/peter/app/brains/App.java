package nx.peter.app.brains;

import android.app.Application;
import com.parse.Parse;


import nx.peter.app.brains.R;
// import com.parse.facebook.ParseFacebookUtils;
// import com.parse.twitter.ParseTwitterUtils;

public class App extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    Parse.initialize(
        new Parse.Configuration.Builder(this)
            .applicationId(getString(R.string.back4app_app_id))
            .clientKey(getString(R.string.back4app_client_key))
            .server(getString(R.string.back4app_server_url))
            .build());
            
       // Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        // ParseFacebookUtils.initialize(this);
        
        // Optional - If you don't want to allow Twitter login, you can
        // remove this line (and other related ParseTwitterUtils calls)
        // ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
        //      getString(R.string.twitter_consumer_secret));
  }
}
