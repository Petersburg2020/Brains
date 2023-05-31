package nx.peter.app.ui.button;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class ClickableBackground {
    public static final int[] STATE_PRESSED = new int[] {android.R.attr.state_pressed};
    public static final int[] STATE_CHECKED = new int[] {android.R.attr.state_checked};
    public static final int[] STATE_SELECTED = new int[] {android.R.attr.state_selected};

    public enum State {
        Pressed,
        Checked,
        Selected
    }

    State state;

    @ColorInt int normalColor, actionColor;

    ClickableBackground(
            @NonNull State state, @ColorInt int normalColor, @ColorInt int actionColor) {
        this.state = state;
        this.normalColor = normalColor;
        this.actionColor = actionColor;
    }

    public static ClickableBackground create(
            @NonNull State state, @ColorInt int normalColor, @ColorInt int actionColor) {
        return new ClickableBackground(state, normalColor, actionColor);
    }

    public StateListDrawable getDrawable() {
        ColorDrawable normal = new ColorDrawable();
        normal.setColor(normalColor);

        ColorDrawable action = new ColorDrawable();
        action.setColor(actionColor);

        int[] actionState = null;

        switch (state) {
            case Pressed:
                actionState = STATE_PRESSED;
                break;
            case Checked:
                actionState = STATE_CHECKED;
                break;
            case Selected:
                actionState = STATE_SELECTED;
                break;
        }

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(StateSet.NOTHING, normal);
        drawable.addState(actionState, action);
        return drawable;
    }

    public static int getColor(@NonNull StateListDrawable list, int index) {
        Drawable draw = getDrawable(list, index);
        return draw instanceof ColorDrawable ? ((ColorDrawable) draw).getColor() : draw instanceof GradientDrawable ? ((GradientDrawable) draw).getColor().getDefaultColor() : Color.DKGRAY;
    }

    public static Drawable getDrawable(@NonNull StateListDrawable list, int index) {
        return list.getStateDrawable(index);
    }

    public static int[] getStateSet(@NonNull State state) {
        switch (state) {
            case Pressed:
                return STATE_PRESSED;
            case Checked:
                return STATE_CHECKED;
            default:
                return STATE_SELECTED;
        }
    }
    
    public static int getColor(@NonNull StateListDrawable list) {
        int index = list.findStateDrawableIndex(StateSet.NOTHING);
        return getColor(list, index);
    }
    
    public static int getColor(StateListDrawable list, @NonNull State state) {
        int index = list.findStateDrawableIndex(getStateSet(state));
        return getColor(list, index);
    }

    public static Drawable getDrawable(@NonNull StateListDrawable list) {
        int index = list.findStateDrawableIndex(StateSet.NOTHING);
        return getDrawable(list, index);
    }

    public static Drawable getDrawable(StateListDrawable list, @NonNull State state) {
        int index = list.findStateDrawableIndex(getStateSet(state));
        return getDrawable(list, index);
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getNormalColor() {
        return this.normalColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public int getActionColor() {
        return this.actionColor;
    }

    public void setActionColor(int actionColor) {
        this.actionColor = actionColor;
    }
}
