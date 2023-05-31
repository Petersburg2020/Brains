package nx.peter.app.ui.window.alert;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nx.peter.app.android_ui.view.MultiActionText;
import nx.peter.app.android_ui.view.ScrollingTextView;
import nx.peter.app.brains.R;
import nx.peter.app.ui.button.ClickableBackground;
import nx.peter.app.ui.window.Alert;
import nx.peter.app.util.RequestIntent;
import nx.peter.app.util.RequestIntent.*;
import nx.peter.app.util.RequestIntent.Result.*;

import java.util.Objects;

public class FileUploader {
    public enum FileType {
        Audio,
        File,
        Image,
        Video
    }

    Alert alert;
    Bitmap image;
    CharSequence name;
    MultiActionText title;
    ScrollingTextView screen;
    View contentView;
    ImageView thumbnail;
    FileType fileType;
    RequestActivity activity;
    Requester requester;
    OnClickListener submitListener, cancelListener, clearListener;
    OnThumbnailClickListener onThumbnailClickListener;
    OnDismissListener dismissListener;
    OnPickListener pickListener;

    protected FileUploader(
            @NonNull RequestIntent.RequestActivity requestActivity, @NonNull FileType type) {
        submitListener = null;
        cancelListener = null;
        clearListener = null;
        onThumbnailClickListener = null;
        dismissListener = null;
        pickListener = null;
        fileType = type;
        activity = requestActivity;
        requester = RequestIntent.fromActivity(requestActivity);
        alert =
                Alert.createAlert(requestActivity, R.layout.upload_file_dialog)
                        .setOnInitViewListener(
                                (a, view) -> {
                                    contentView = view;
                                    screen = view.findViewById(R.id.screen);
                                    screen.setText("Upload file here...");
                                    title = view.findViewById(R.id.title);
                                    thumbnail = view.findViewById(R.id.thumbnail);
                                    thumbnail.setOnClickListener(
                                            v -> {
                                                if (onThumbnailClickListener != null)
                                                    onThumbnailClickListener.onClick(
                                                            FileUploader.this,
                                                            new Image() {

                                                                @Override
                                                                public CharSequence getName() {
                                                                    return name;
                                                                }

                                                                @Override
                                                                public Bitmap getBitmap() {
                                                                    return image;
                                                                }
                                                            });
                                            });
                                    view.findViewById(R.id.clear)
                                            .setOnClickListener(
                                                    v -> {
                                                        screen.setText("Upload file here...");
                                                        thumbnail.setImageResource(
                                                                R.drawable.no_image);
                                                        requester =
                                                                RequestIntent.fromActivity(
                                                                        requestActivity);
                                                        if (clearListener != null)
                                                            clearListener.onClick(
                                                                    FileUploader.this,
                                                                    new IButton(
                                                                            Id.Clear,
                                                                            FileUploader.this,
                                                                            (android.widget.Button)
                                                                                    v,
                                                                            clearListener));
                                                        name = null;
                                                        image = null;
                                                    });
                                    view.findViewById(R.id.cancel)
                                            .setOnClickListener(
                                                    v -> {
                                                        Log.i(
                                                                "FileUploader",
                                                                "OnCancelClickListener");
                                                        if (cancelListener != null)
                                                            cancelListener.onClick(
                                                                    FileUploader.this,
                                                                    new IButton(
                                                                            Id.Cancel,
                                                                            FileUploader.this,
                                                                            (android.widget.Button)
                                                                                    v,
                                                                            cancelListener));
                                                        a.dismiss();
                                                    });
                                    view.findViewById(R.id.submit)
                                            .setOnClickListener(
                                                    v -> {
                                                        Log.i(
                                                                "FileUploader",
                                                                "OnSubmitClickListener");
                                                        if (submitListener != null)
                                                            submitListener.onClick(
                                                                    FileUploader.this,
                                                                    new IButton(
                                                                            Id.Submit,
                                                                            FileUploader.this,
                                                                            (android.widget.Button)
                                                                                    v,
                                                                            submitListener));
                                                        a.dismiss();
                                                    });
                                    view.findViewById(R.id.pick)
                                            .setOnClickListener(
                                                    v -> {
                                                        Log.i("FileUploader", "OnPickListener");
                                                        Toast.makeText(
                                                                        activity,
                                                                        "Pick " + fileType + "!",
                                                                        Toast.LENGTH_SHORT)
                                                                .show();
                                                        switch (fileType) {
                                                            case Audio:
                                                                requester.chooseAudio(listener);
                                                                break;
                                                            case Image:
                                                                requester.chooseImage(listener);
                                                                break;
                                                            case Video:
                                                                requester.chooseVideo(listener);
                                                                break;
                                                            default:
                                                                requester.chooseFile(listener);
                                                        }
                                                        requester.initiate();
                                                    });
                                })
                        .setOnDismissListener(
                                a -> {
                                    Log.i("FileUploader", "OnDismissListener");
                                    if (dismissListener != null)
                                        dismissListener.onDismiss(FileUploader.this);
                                })
                        .build();
    }

    public static Builder createUploader(@NonNull RequestIntent.RequestActivity activity) {
        return createUploader(activity, FileType.File);
    }

    public static Builder createUploader(
            @NonNull RequestIntent.RequestActivity activity, @NonNull FileType fileType) {
        return new Builder() {
            OnClickListener submitListener = null, cancelListener = null, clearListener = null;
            OnPickListener pickListener = null;
            OnDismissListener dismissListener;
            CharSequence title = "File Uploader";
            FileType type = fileType;
            FileUploader uploader = new FileUploader(activity, type);

            @Override
            public Builder setTitle(@NonNull CharSequence title) {
                this.title = title;
                return this;
            }

            @Override
            public Builder setFileType(@NonNull FileType fileType) {
                type = fileType;
                uploader = new FileUploader(activity, type);
                return this;
            }

            @Override
            public Builder setOnCancelListener(@Nullable OnClickListener listener) {
                cancelListener = listener;
                return this;
            }

            @Override
            public Builder setOnSubmitListener(@Nullable OnClickListener listener) {
                submitListener = listener;
                return this;
            }

            @Override
            public Builder setOnDismissListener(@Nullable OnDismissListener listener) {
                dismissListener = listener;
                return this;
            }

            @Override
            public Builder setOnFilePickListener(@Nullable OnPickListener listener) {
                pickListener = listener;
                return this;
            }

            @Override
            public FileUploader build() {
                uploader.setOnDismissListener(dismissListener);
                uploader.setOnCancelListener(cancelListener);
                uploader.setOnSubmitListener(submitListener);
                uploader.setOnFilePickListener(pickListener);
                uploader.setOnClearListener(clearListener);
                uploader.setTitle(title);
                return uploader;
            }

            @Override
            public Builder setOnClearListener(OnClickListener listener) {
                clearListener = listener;
                return this;
            }
        };
    }

    RequestIntent.OnResultListener listener =
            result -> {
                if (pickListener != null) pickListener.onPick(FileUploader.this, result);
                if (Objects.requireNonNull(result.getFeedback()) == Feedback.Success) {
                    String path = ((ResultSuccess) result).getPath();
                    screen.setText(path.substring(path.lastIndexOf("/") + 1));
                    switch (result.getType()) {
                        case Image:
                            thumbnail.setImageBitmap(
                                    ((ImageSuccess) result).getMedia().getBitmap());
                            break;
                        case Audio:
                            break;
                    }
                } else
                    Toast.makeText(
                                    getActivity(),
                                    "File Upload " + result.getFeedback() + "!",
                                    Toast.LENGTH_SHORT)
                            .show();
            };

    public RequestIntent.RequestActivity getActivity() {
        return activity;
    }

    public void setOnFilePickListener(@Nullable OnPickListener listener) {
        pickListener = listener;
    }

    @Nullable
    public OnPickListener getOnFilePickListener() {
        return pickListener;
    }

    @Nullable
    public OnClickListener getOnSubmitListener() {
        return submitListener;
    }

    public void setOnSubmitListener(@Nullable OnClickListener submitListener) {
        this.submitListener = submitListener;
    }

    public OnDismissListener getOnDismissListener() {
        return dismissListener;
    }

    public void setOnDismissListener(OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Nullable
    public OnClickListener getOnCancelListener() {
        return cancelListener;
    }

    public OnClickListener getOnClearListener() {
        return this.clearListener;
    }

    public void setOnClearListener(OnClickListener clearListener) {
        this.clearListener = clearListener;
    }

    public void setOnCancelListener(@Nullable OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setTitle(@NonNull CharSequence title) {
        this.title.setText(title);
    }

    public void setPath(@NonNull CharSequence path) {
        screen.setText(path);
    }

    public BitmapDrawable getThumbnail() {
        return (BitmapDrawable) thumbnail.getDrawable();
    }

    @NonNull
    public CharSequence getTitle() {
        return screen.getText().equals("Upload file here...") ? "" : title.getText();
    }

    @NonNull
    public CharSequence getPath() {
        return screen.getText();
    }

    public void show() {
        alert.show();
    }

    public void dismiss() {
        alert.dismiss();
    }

    public enum Id {
        Cancel,
        Clear,
        Submit
    }

    public enum Visibility {
        Invisible,
        Visible
    }

    public interface OnClickListener {
        void onClick(FileUploader uploader, Button button);
    }

    public interface OnPickListener {
        void onPick(FileUploader uploader, RequestIntent.Result result);
    }

    public interface OnDismissListener {
        void onDismiss(FileUploader uploader);
    }

    public interface OnThumbnailClickListener {
        void onClick(FileUploader uploader, Image image);
    }

    public interface Image {
        CharSequence getName();

        Bitmap getBitmap();
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

    public interface Builder {
        Builder setTitle(@NonNull CharSequence title);

        Builder setFileType(@NonNull FileType fileType);

        Builder setOnCancelListener(@Nullable OnClickListener listener);

        Builder setOnSubmitListener(@Nullable OnClickListener listener);

        Builder setOnClearListener(@NonNull OnClickListener listener);

        Builder setOnDismissListener(@Nullable OnDismissListener listener);

        Builder setOnFilePickListener(@Nullable OnPickListener listener);

        FileUploader build();
    }

    static class IButton implements Button {
        Id id;
        public FileUploader task;
        public android.widget.Button button;
        public OnClickListener listener;
        ClickableBackground.State state = ClickableBackground.State.Pressed;

        public IButton(
                Id id, FileUploader task, android.widget.Button button, OnClickListener listener) {
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
