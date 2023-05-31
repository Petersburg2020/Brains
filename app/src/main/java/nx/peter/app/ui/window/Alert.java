package nx.peter.app.ui.window;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class Alert {
    private final Context context;
    protected AlertDialog.Builder builder;
    protected AlertDialog dialog;
    protected OnDismissListener onDismissListener;

    protected Alert(Context context, View view, OnInitViewListener init) {
        this.context = context;
        builder = new AlertDialog.Builder(context);
        builder.setView(view);
        dialog = builder.create();

        dialog.setOnDismissListener(d -> {
            if (onDismissListener != null) onDismissListener.onDismiss(Alert.this);
        });

        Log.i("Alert", "Create AlertDialog here!");

        if (init != null) init.onInit(this, view);
    }

    public static Builder createAlert(@NonNull Context context, int layoutRes) {
        return createAlert(context).setCustomView(layoutRes);
    }

    public static Builder createAlert(@NonNull View view) {
        return createAlert(view.getContext()).setCustomView(view);
    }

    public static Builder createAlert(@NonNull Context context) {
        return new Builder() {
            OnInitViewListener init = null;
            OnDismissListener dismiss = null;
            View v = null;

            @Override
            public Builder setCustomView(@LayoutRes int layoutRes) {
                return setCustomView(LayoutInflater.from(context).inflate(layoutRes, null, false));
            }

            @Override
            public Builder setCustomView(View view) {
                v = view;
                Log.i("Alert.Builder", "Set Custom View here!");
                return this;
            }

            @Override
            public Builder setOnDismissListener(OnDismissListener listener) {
                dismiss = listener;
                Log.i("Alert.Builder", "Set onDismiss here!");
                return this;
            }

            @Override
            public Alert build() {
                return new Alert(context, v, init);
            }

            @Override
            public Builder setOnInitViewListener(OnInitViewListener listener) {
                init = listener;
                Log.i("Alert.Builder", "Set onInit here!");
                return this;
            }
        };
    }

    public Context getContext() {
        return context;
    }

    public void dismiss() {
        Log.i("Alert", "Dismiss AlertDialog here!");
        dialog.dismiss();
    }

    public void show() {
        Log.i("Alert", "Show AlertDialog here!");
        dialog.show();
    }


    public interface Builder {
        Builder setCustomView(@LayoutRes int layoutRes);

        Builder setCustomView(View view);

        Builder setOnDismissListener(OnDismissListener listener);

        Builder setOnInitViewListener(OnInitViewListener listener);

        Alert build();
    }

    public interface OnInitViewListener {
        void onInit(Alert alert, View view);
    }

    public interface OnClickListener {
        void onClick(Alert alert, View view);
    }

    public interface OnDismissListener {
        void onDismiss(Alert alert);
    }

}
