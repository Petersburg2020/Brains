package nx.peter.app.ui;
import java.io.File;

public interface OnSavedListener {
    void onSaved(File file, Exception e);
}
