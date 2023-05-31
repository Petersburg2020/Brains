package nx.peter.app.ui.window.alert;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.util.StateSet;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import nx.peter.app.android_ui.view.MultiActionText;
import nx.peter.app.ui.button.ClickableBackground;
import nx.peter.app.ui.window.Alert;
import nx.peter.app.brains.R;
import nx.peter.app.ui.window.alert.PerformTask.OnClickListener;
import nx.peter.app.ui.window.alert.PerformTask.Builder;
import nx.peter.app.ui.window.alert.PerformTask;

public class PerformTask {
    Alert alert;
    MultiActionText titleTxt, messageTxt;
    IButton negativeBtn, positiveBtn, neutralBtn;
    OnDismissListener dismiss;

    PerformTask(
            Context context,
            CharSequence title,
            CharSequence message,
            CharSequence negative,
            CharSequence neutral,
            CharSequence positive,
            OnInitViewListener initListener,
            OnDismissListener dismissListener,
            OnClickListener negativeClicked,
            OnClickListener neutralClicked,
            OnClickListener positiveClicked) {
        titleTxt = null;
        messageTxt = null;
        positiveBtn = null;
        neutralBtn = null;
        negativeBtn = null;
        dismiss = dismissListener;
        alert =
                Alert.createAlert(context, R.layout.perform_task)
                        .setOnInitViewListener(
                                (a, v) -> {
                                    titleTxt = v.findViewById(R.id.title);
                                    messageTxt = v.findViewById(R.id.message);
                                    titleTxt.setText(title);
                                    messageTxt.setText(message);

                                    negativeBtn =
                                            new IButton(
                                                    Id.Negative,
                                                    PerformTask.this,
                                                    v.findViewById(R.id.negative),
                                                    negativeClicked);
                                    negativeBtn.setText(negative);

                                    neutralBtn =
                                            new IButton(
                                                    Id.Neutral,
                                                    PerformTask.this,
                                                    v.findViewById(R.id.neutral),
                                                    neutralClicked);
                                    neutralBtn.setText(neutral);

                                    positiveBtn =
                                            new IButton(
                                                    Id.Positive,
                                                    PerformTask.this,
                                                    v.findViewById(R.id.positive),
                                                    positiveClicked);
                                    positiveBtn.setText(positive);

                                    if (initListener != null)
                                        initListener.onInit(
                                                PerformTask.this,
                                                negativeBtn,
                                                neutralBtn,
                                                positiveBtn);
                                })
                        .setOnDismissListener(
                                a -> {
                                    if (dismiss != null) dismiss.onDismiss(PerformTask.this);
                                })
                        .build();
    }

    public static Builder createTask(@NonNull Context context) {
        return new Builder() {
            CharSequence title = "Perform Task",
                    message = "Do you want to perform this task?",
                    negative = "No",
                    neutral = "Cancel",
                    positive = "Yes";
            OnInitViewListener initListener = null;
            OnDismissListener dismissListener = null;
            OnClickListener negativeClicked = null, neutralClicked = null, positiveClicked = null;

            @Override
            public Builder setTitle(@NonNull CharSequence title) {
                this.title = title;
                Log.i("PerformTask.Builder", "Set Title here!");
                return this;
            }

            @Override
            public Builder setMessage(@NonNull CharSequence message) {
                this.message = message;
                Log.i("PerformTask.Builder", "Set Message here!");
                return this;
            }

            @Override
            public Builder setNeutralText(@NonNull CharSequence text) {
                neutral = text;
                Log.i("PerformTask.Builder", "Set Neutral Text here!");
                return this;
            }

            @Override
            public Builder setNegativeText(@NonNull CharSequence text) {
                negative = text;
                Log.i("PerformTask.Builder", "Set Negative Text here!");
                return this;
            }

            @Override
            public Builder setPositiveText(@NonNull CharSequence text) {
                positive = text;
                Log.i("PerformTask.Builder", "Set Positive Text here!");
                return this;
            }

            @Override
            public Builder setOnDismissListener(OnDismissListener listener) {
                dismissListener = listener;
                Log.i("PerformTask.Builder", "Set onDismiss here!");
                return this;
            }

            @Override
            public Builder setOnNegativeClickListener(OnClickListener listener) {
                negativeClicked = listener;
                Log.i("PerformTask.Builder", "Set onNegativeClicked here!");
                return this;
            }

            @Override
            public Builder setOnPositiveClickListener(OnClickListener listener) {
                positiveClicked = listener;
                Log.i("PerformTask.Builder", "Set onPositiveClicked here!");
                return this;
            }

            @Override
            public Builder setOnNeutralClickListener(OnClickListener listener) {
                neutralClicked = listener;
                Log.i("PerformTask.Builder", "Set onNeutralClicked here!");
                return this;
            }

            @Override
            public PerformTask build() {
                Log.i("PerformTask.Builder", "Build here!");
                return new PerformTask(
                        context,
                        title,
                        message,
                        negative,
                        neutral,
                        positive,
                        initListener,
                        dismissListener,
                        negativeClicked,
                        neutralClicked,
                        positiveClicked);
            }

            @Override
            public Builder setOnInitViewListener(OnInitViewListener listener) {
                initListener = listener;
                Log.i("PerformTask.Builder", "Set onInitView here!");
                return this;
            }
        };
    }

    public Button getNegativeButton() {
        return negativeBtn;
    }

    public Button getPositiveButton() {
        return positiveBtn;
    }

    public Button getNeutralButton() {
        return neutralBtn;
    }

    public void setTitle(@NonNull CharSequence title) {
        titleTxt.setText(title);
    }

    public void setMessage(@NonNull CharSequence message) {
        messageTxt.setText(message);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        Log.i("PerformTask", "Set OnDismiss here...");
        dismiss = listener;
    }

    public OnDismissListener getOnDismissListener() {
        return dismiss;
    }

    public void setOnNegativeClickListener(OnClickListener listener) {
        negativeBtn.setOnClickListener(listener);
    }

    public void setOnNeutralClickListener(OnClickListener listener) {
        neutralBtn.setOnClickListener(listener);
    }

    public void setOnPositiveClickListener(OnClickListener listener) {
        positiveBtn.setOnClickListener(listener);
    }

    public OnClickListener getOnNegativeClickListener() {
        return negativeBtn.getOnClickListener();
    }

    public OnClickListener getOnNeutralClickListener() {
        return neutralBtn.getOnClickListener();
    }

    public OnClickListener getOnPositiveClickListener() {
        return positiveBtn.getOnClickListener();
    }

    public CharSequence getTitle() {
        return titleTxt.getText();
    }

    public CharSequence getMessage() {
        return messageTxt.getText();
    }

    public void dismiss() {
        Log.i("PerformTask", "Dismiss here...");
        alert.dismiss();
    }

    public void show() {
        Log.i("PerformTask", "Show here...");
        alert.show();
    }

    public interface Builder {
        Builder setTitle(@NonNull CharSequence title);

        Builder setMessage(@NonNull CharSequence message);

        Builder setNeutralText(@NonNull CharSequence text);

        Builder setNegativeText(@NonNull CharSequence text);

        Builder setPositiveText(@NonNull CharSequence text);

        Builder setOnInitViewListener(OnInitViewListener listener);

        Builder setOnDismissListener(OnDismissListener listener);

        Builder setOnNegativeClickListener(OnClickListener listener);

        Builder setOnPositiveClickListener(OnClickListener listener);

        Builder setOnNeutralClickListener(OnClickListener listener);

        PerformTask build();
    }

    public enum Id {
        Negative,
        Neutral,
        Positive
    }

    public enum Visibility {
        Invisible,
        Visible
    }

    public interface Button {
        Id getId();

        boolean isEnabled();

        boolean isVisible();

        Visibility getVisibility();

        CharSequence getText();

        @ColorInt
        int getPressedColor();

        @ColorInt
        int getNormalColor();

        OnClickListener getOnClickListener();

        void setEnabled(boolean enable);

        void setVisibility(@NonNull Visibility visibility);

        void setText(@NonNull CharSequence text);

        void setPressedColor(@ColorInt int color);

        void setNormalColor(@ColorInt int color);

        void setOnClickListener(OnClickListener listener);
    }

    public interface OnClickListener {
        void onClick(PerformTask task, Button button);
    }

    public interface OnDismissListener {
        void onDismiss(PerformTask task);
    }

    public interface OnInitViewListener {
        void onInit(PerformTask task, Button negative, Button neutral, Button posiitive);
    }

    static class IButton implements Button {
        Id id;
        public PerformTask task;
        public android.widget.Button button;
        public OnClickListener listener;
        ClickableBackground.State state = ClickableBackground.State.Pressed;

        public IButton(
                Id id, PerformTask task, android.widget.Button button, OnClickListener listener) {
            this.id = id;
            this.task = task;
            this.button = button;
            setOnClickListener(listener);
        }

        @Override
        public OnClickListener getOnClickListener() {
            return listener;
        }

        @Override
        public Id getId() {
            return id;
        }

        @Override
        public boolean isVisible() {
            return getVisibility().equals(Visibility.Invisible);
        }

        @Override
        public CharSequence getText() {
            return button.getText();
        }

        @Override
        public void setOnClickListener(OnClickListener listener) {
            this.listener = listener;
            button.setOnClickListener(
                    v -> {
                        if (IButton.this.listener != null)
                            IButton.this.listener.onClick(task, IButton.this);
                    });
        }

        @Override
        public void setText(CharSequence text) {
            button.setText(text);
        }

        @Override
        public Visibility getVisibility() {
            return button.getVisibility() == View.INVISIBLE
                    ? Visibility.Invisible
                    : Visibility.Visible;
        }

        @Override
        public void setEnabled(boolean enable) {
            button.setEnabled(enable);
        }

        @Override
        public boolean isEnabled() {
            return button.isEnabled();
        }

        @Override
        public void setVisibility(Visibility visibility) {
            button.setVisibility(
                    visibility.equals(Visibility.Visible) ? View.VISIBLE : View.INVISIBLE);
        }

        StateListDrawable getBackground() {
            return (StateListDrawable) button.getBackground();
        }

        @Override
        public void setPressedColor(int color) {
            int normal = getNormalColor();
            button.setBackground(ClickableBackground.create(state, normal, color).getDrawable());
        }

        @Override
        public void setNormalColor(int color) {
            int pressed = getPressedColor();
            button.setBackground(ClickableBackground.create(state, color, pressed).getDrawable());
        }

        @Override
        public int getNormalColor() {
            return ClickableBackground.getColor(getBackground());
        }

        @Override
        public int getPressedColor() {
            return ClickableBackground.getColor(getBackground(), state);
        }
    }
}
