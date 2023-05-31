package nx.peter.app.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import nx.peter.java.document.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestIntent {
    public static final int FOLDER = 101;
    public static final int FILES = 100;
    public static final int IMAGE = 98;
    public static final int AUDIO = 99;
    public static final int VIDEO = 97;

    protected RequestIntent() {}

    public static Requester fromActivity(RequestActivity act) {
        return new IRequester(act);
    }

    private static class IRequester implements Requester {
        protected RequestActivity act;
        protected List<Request> requests;

        public IRequester(RequestActivity act) {
            this.act = act;
            requests = new ArrayList<>();
        }

        @Override
        public Requester chooseAudio(OnResultListener listener) {
            Intent intent = chooseFile("audio/*", "Choose an audio");
            return makeRequest(AUDIO, intent, listener);
        }

        @Override
        public Requester chooseImage(OnResultListener listener) {
            Intent intent = chooseFile("image/*", "Choose an image");
            return makeRequest(IMAGE, intent, listener);
        }

        @Override
        public Requester chooseVideo(OnResultListener listener) {
            Intent intent = chooseFile("video/*", "Choose a video");
            return makeRequest(VIDEO, intent, listener);
        }

        @Override
        public Requester chooseFile(OnResultListener listener) {
            Intent intent = chooseFile("*/*", "Choose a video");
            return makeRequest(FILES, intent, listener);
        }

        protected Intent chooseFile(String mime, String message) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType(mime);
            chooseFile = Intent.createChooser(chooseFile, message);
            return chooseFile;
        }

        @Override
        public Requester makeRequest(
                final int code, final Intent intent, final OnResultListener listener) {
            Request request =
                    new Request() {
                        @Override
                        public int getCode() {
                            return code;
                        }

                        @Override
                        public Intent getIntent() {
                            return intent;
                        }

                        @Override
                        public OnResultListener getListener() {
                            return listener;
                        }
                    };
            if (!new Requests(requests).contains(request)) requests.add(request);
            act.addRequest(request);
            return this;
        }

        @Override
        public void initiate() {
            for (Request r : requests) act.startActivityForResult(r.getIntent(), r.getCode());
        }

        @Override
        public Requester chooseFolder(OnResultListener listener) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent = Intent.createChooser(intent, "Choose a Directory");
            return makeRequest(FOLDER, intent, listener);
        }
    }

    public interface Requester {
        void initiate();

        Requester chooseAudio(OnResultListener listener);

        Requester chooseFile(OnResultListener listener);

        Requester chooseFolder(OnResultListener listener);

        Requester chooseImage(OnResultListener listener);

        Requester chooseVideo(OnResultListener listener);

        Requester makeRequest(final int code, final Intent intent, final OnResultListener listener);
    }

    protected interface Coded {
        int getCode();
    }

    public interface Request extends Coded {
        Intent getIntent();

        OnResultListener getListener();
    }

    public static class Results extends Array<Result> {
        public Results(List<Result> results) {
            super(results);
        }
    }

    protected static class Array<T extends Coded> extends Document.Array<T> {
        public Array(List<T> items) {
            super(items);
        }

        @Override
        public T get(int code) {
            for (T item : items) if (item.getCode() == code) return item;
            return null;
        }

        public boolean contains(int code) {
            for (T i : items) if (i.getCode() == code) return true;
            return false;
        }

        @Override
        public boolean contains(T item) {
            return item != null && contains(item.getCode());
        }
    }

    public interface Media {
        Uri getUri();

        String getPath();

        Type getType();

        enum Type {
            Audio,
            Image,
            Video
        }
    }

    public interface Image extends Media {
        Bitmap getBitmap();
    }

    public interface Audio extends Media {
        Bitmap getBitmap();
    }

    public interface Video extends Media {
        String getTitle();

        String getGenre();

        String getAuthor();

        String getDescription();
    }

    public interface Result extends Coded {
        Request getRequest();

        Intent getIntent();

        Type getType();

        Feedback getFeedback();

        enum Feedback {
            Denied,
            Success
        }

        enum Type {
            Audio,
            Folder,
            Image,
            Others,
            Video
        }
    }

    public interface MediaResult extends Result {
        ContentResolver getResolver();
        Media getMedia();
    }

    public static class Requests extends Array<Request> {
        public Requests(List<Request> requests) {
            super(requests);
        }
    }

    public interface ResultSuccess extends Result {
        Uri getData();

        File getFile();

        String getPath();
    }

    public interface ResultDenied extends Result {
        String getCause();
    }

    public interface ImageSuccess extends MediaResult, ResultSuccess {
        Image getMedia();
    }

    public interface AudioSuccess extends MediaResult, ResultSuccess {
        Audio getMedia();
    }

    public interface FileSuccess extends ResultSuccess {
        long getSize();

        Bitmap getThumbnail();
    }

    public interface OnResultListener {
        void onActivityResult(Result result);
    }

    public abstract static class RequestActivity extends AppCompatActivity {
        private List<Result> results;
        private List<Request> requests;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            results = new ArrayList<>();
            requests = new ArrayList<>();
        }

        public void addRequest(Request r) {
            if (!requests.contains(r)) requests.add(r);
        }

        private String path;
        private ContentResolver resolver;
        private Bitmap bitmap;
        private Uri uri;
        private File file;
        private long size;

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);
            if (!requests.isEmpty()) {
                for (Request request : requests) {
                    if (request.getCode() == requestCode) {
                        Result result = null;
                        path = "";
                        file = null;
                        resolver = getContentResolver();
                        uri = intent.getData();
                        if (resultCode == RESULT_OK) {
                            if (requestCode == IMAGE) {
                                String[] imageProjection = {
                                    MediaStore.Images.Media.DISPLAY_NAME,
                                    MediaStore.Images.Media.RELATIVE_PATH
                                };
                                Cursor cursor =
                                        resolver.query(uri, imageProjection, null, null, null);

                                // resolver
                                if (cursor != null) {
                                    cursor.moveToFirst();
                                    int indexImageName = cursor.getColumnIndex(imageProjection[0]);
                                    // int indexImagePath =
                                    // cursor.getColumnIndex(imageProjection[1]);
                                    // Get the image file absolute path
                                    // String p = cursor.getString(indexImagePath);
                                    path = cursor.getString(indexImageName);

                                    file = new File(getCacheDir(), path);
                                    try {
                                        InputStream input = resolver.openInputStream(uri);
                                        OutputStream output = new FileOutputStream(file);

                                        Util.Feedback feedback = Util.copy(input, output);
                                        Log.i("RequestIntent.Image", feedback.message);

                                        path = file.getPath();
                                    } catch (IOException e) {
                                        Log.i("RequestIntent.Image", e.getMessage());
                                    }

                                    bitmap = null;
                                    Log.i("RequestIntent.Image", "Get Image Path (" + path + ")");

                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
                                        Toast.makeText(
                                                        this,
                                                        "Bitmap "
                                                                + (bitmap != null ? "" : "not ")
                                                                + "added!",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    } catch (IOException e) {
                                        Log.e("RequestIntent.Image", e.getMessage());
                                    }
                                    cursor.close();
                                }
                                result =
                                        new ImageSuccess() {
                                            @Override
                                            public int getCode() {
                                                return resultCode;
                                            }

                                            @Override
                                            public Uri getData() {
                                                return uri;
                                            }

                                            @Override
                                            public String getPath() {
                                                return path;
                                            }

                                            @Override
                                            public Request getRequest() {
                                                return request;
                                            }

                                            @Override
                                            public Intent getIntent() {
                                                return intent;
                                            }

                                            @Override
                                            public Type getType() {
                                                return Type.Image;
                                            }

                                            @Override
                                            public Feedback getFeedback() {
                                                return Feedback.Success;
                                            }

                                            @Override
                                            public Image getMedia() {
                                                return new Image() {

                                                    @Override
                                                    public Bitmap getBitmap() {
                                                        return bitmap;
                                                    }

                                                    @Override
                                                    public Type getType() {
                                                        return Type.Image;
                                                    }

                                                    @Override
                                                    public String getPath() {
                                                        return path;
                                                    }

                                                    @Override
                                                    public Uri getUri() {
                                                        return uri;
                                                    }
                                                };
                                            }

                                            @Override
                                            public ContentResolver getResolver() {
                                                return resolver;
                                            }

                                            @Override
                                            public File getFile() {
                                                return file;
                                            }
                                        };
                            } else if (requestCode == VIDEO) {
                                String[] videoProjection = {MediaStore.Video.Media.DATA};
                                Cursor cursor =
                                        resolver.query(uri, videoProjection, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    int indexVideo = cursor.getColumnIndex(videoProjection[0]);
                                    // Get the image file absolute path
                                    path = cursor.getString(indexVideo);
                                    // long id = cursor.getColumnIndex()
                                    String name = cursor.getColumnName(indexVideo);

                                    bitmap = null;
                                    bitmap =
                                            MediaStore.Video.Thumbnails.getThumbnail(
                                                    resolver,
                                                    indexVideo,
                                                    MediaStore.Video.Thumbnails.MINI_KIND,
                                                    null);
                                    cursor.close();
                                } else {
                                    path = uri.getLastPathSegment();
                                }
                            } else if (requestCode == FILES) {
                                String[] projection = {
                                    OpenableColumns.DISPLAY_NAME,
                                    OpenableColumns.SIZE,
                                    MediaStore.Files.FileColumns.DATA
                                };

                                // Return only video, audio and image metadata.
                                /*String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + " OR "
                                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR "
                                + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;*/

                                Cursor cursor = resolver.query(uri, projection, null, null, null);
                                if (cursor != null) {
                                    cursor.moveToFirst();
                                    int indexVideo = cursor.getColumnIndex(projection[0]);
                                    // Get the image file absolute path
                                    path = cursor.getString(indexVideo);
                                    int fileSize = cursor.getColumnIndex(projection[1]);

                                    size = cursor.getLong(fileSize);
                                    cursor.close();

                                } else {
                                    path = uri.getLastPathSegment();
                                    size = 0L;
                                }
                                result =
                                        new FileSuccess() {
                                            @Override
                                            public long getSize() {
                                                return size;
                                            }

                                            @Override
                                            public Uri getData() {
                                                return uri;
                                            }

                                            @Override
                                            public String getPath() {
                                                return path;
                                            }

                                            @Override
                                            public Request getRequest() {
                                                return request;
                                            }

                                            @Override
                                            public Intent getIntent() {
                                                return intent;
                                            }

                                            @Override
                                            public Type getType() {
                                                return Type.Others;
                                            }

                                            @Override
                                            public Feedback getFeedback() {
                                                return Feedback.Success;
                                            }

                                            @Override
                                            public int getCode() {
                                                return resultCode;
                                            }

                                            @Override
                                            public Bitmap getThumbnail() {
                                                return null;
                                            }

                                            @Override
                                            public File getFile() {
                                                return file;
                                            }
                                        };
                            } else
                                result =
                                        new ResultSuccess() {
                                            @Override
                                            public int getCode() {
                                                return resultCode;
                                            }

                                            @Override
                                            public Uri getData() {
                                                return uri;
                                            }

                                            @Override
                                            public String getPath() {
                                                return path;
                                            }

                                            @Override
                                            public Request getRequest() {
                                                return request;
                                            }

                                            @Override
                                            public Intent getIntent() {
                                                return intent;
                                            }

                                            @Override
                                            public Type getType() {
                                                return Type.Others;
                                            }

                                            @Override
                                            public Feedback getFeedback() {
                                                return Feedback.Success;
                                            }

                                            @Override
                                            public File getFile() {
                                                return file;
                                            }
                                        };
                        } else
                            result =
                                    new ResultDenied() {
                                        @Override
                                        public String getCause() {
                                            return intent.getAction();
                                        }

                                        @Override
                                        public int getCode() {
                                            return resultCode;
                                        }

                                        @Override
                                        public Intent getIntent() {
                                            return intent;
                                        }

                                        @Override
                                        public Request getRequest() {
                                            return request;
                                        }

                                        @Override
                                        public Type getType() {
                                            switch (requestCode) {
                                                case AUDIO:
                                                    return Type.Audio;
                                                case VIDEO:
                                                    return Type.Video;
                                                default:
                                                    return Type.Others;
                                            }
                                        }

                                        @Override
                                        public Feedback getFeedback() {
                                            return Feedback.Denied;
                                        }
                                    };
                        if (request.getListener() != null)
                            request.getListener().onActivityResult(result);
                    }
                }
            }
        }

        public Requests getRequests() {
            return new Requests(requests);
        }

        public Results getResults() {
            return new Results(results);
        }
    }
}
