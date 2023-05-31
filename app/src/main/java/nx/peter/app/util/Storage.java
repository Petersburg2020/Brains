package nx.peter.app.util;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

public class Storage {
    SharedPreferences.Editor edit;
    SharedPreferences pref;
    Context cxt;
    String name;
    
    public Storage(@NonNull Context cxt, @NonNull String name) {
        this.cxt = cxt;
        this.name = name;
        initiate();
    }
    
    public void initiate() {
        pref = Util.openPrefs(cxt, name);
    }
    
    public void add(String key, int value) {
        edit = pref.edit();
        edit.putInt(key, value);
        edit.apply();
    }
    
    public void add(String key, float value) {
        edit = pref.edit();
        edit.putFloat(key, value);
        edit.apply();
    }
    
    public void add(String key, boolean value) {
        edit = pref.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }
    
    public void add(String key, String value) {
        edit = pref.edit();
        edit.putString(key, value);
        edit.apply();
    }
    
    public void add(String key, long value) {
        edit = pref.edit();
        edit.putLong(key, value);
        edit.apply();
    }
    
    public String getString(String key) {
        return pref.getString(key, null);
    }
    
    public int getInt(String key, int def) {
        return pref.getInt(key, def);
    }
    
    public float getFloat(String key, float def) {
        return pref.getFloat(key, def);
    }
    
    public boolean getBoolean(String key, boolean def) {
        return pref.getBoolean(key, def);
    }
    
    public long getLong(String key, long def) {
        return pref.getLong(key, def);
    }
    
    public void dispose() {
        pref = null;
    }
    
}
