package nx.peter.app.ui.button;
import android.content.res.Resources;
import android.graphics.drawable.RotateDrawable;
import androidx.annotation.DrawableRes;
import nx.peter.java.context.Context;

import nx.peter.app.brains.R;

public class Progress {
    private Progress() {}
    
    
    public static RotateDrawable getDrawable(@DrawableRes int drawableRes) {
        RotateDrawable drawable = new RotateDrawable();
        // drawable.inflate(Resources.getSystem(), XmlPullP);
        drawable.setDrawable(Resources.getSystem().getDrawable(drawableRes));
        return drawable;
    }
    
}
