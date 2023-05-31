package nx.peter.app.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import nx.peter.app.android_ui.view.util.Size;
import nx.peter.app.ui.OnSavedListener;
import nx.peter.java.util.data.encryption.Password;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static SharedPreferences openPrefs(@NonNull Context cxt, @NonNull String name) {
        return cxt.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor openPrefsEditor(
            @NonNull Context cxt, @NonNull String name) {
        return openPrefs(cxt, name).edit();
    }

    public static DisplayMetrics getScreen(@NonNull Context cxt) {
        DisplayMetrics d = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) cxt.getDisplay().getMetrics(d);
        return d;
    }

    public static Size getScreenSizePixels(@NonNull Context cxt) {
        DisplayMetrics d = getScreen(cxt);
        return new Size(d.widthPixels, d.heightPixels);
    }

    public static boolean isEmail(@NonNull CharSequence email) {
        Pattern p = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-zA-Z)]+\\.[(a-zA-Z)]{2,3}$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean userExists(@NonNull CharSequence username) {
        return false;
    }

    public static Feedback isPassword(@NonNull CharSequence text) {
        try {
            new Password(text, 8, Password.LengthVariant.Greater, Password.Restriction.All);
            return new Feedback("\"" + text + "\" is a valid password!", true);
        } catch (Password.InvalidPasswordException e) {
            return new Feedback(e.getMessage(), false);
        }
    }

    public static void saveUser(@NonNull CharSequence username, @NonNull CharSequence email) {
        ParseObject obj = new ParseObject("Users");
        obj.put("username", username.toString());
        obj.put("email", email.toString());
        obj.saveInBackground();
    }

    public static void removeUser(@NonNull CharSequence email) {
        ParseQuery<ParseObject> query =
                ParseQuery.getQuery(ParseObject.class).whereEqualTo("email", email.toString());
        try {
            ParseObject obj = query.getFirst();
            if (obj != null) obj.deleteInBackground();
        } catch (ParseException e) {
            Log.e("Parse", e.getMessage());
        }
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri
                && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri =
                        ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[] {split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor =
                        context.getContentResolver()
                                .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void uploadFile(
            @NonNull File url, @NonNull ProgressBar bar, OnSavedListener listener) {
        ParseFile file = new ParseFile(url);
        file.saveInBackground(
                e -> {
                    bar.setVisibility(ProgressBar.INVISIBLE);
                    if (listener != null) listener.onSaved(url, e);
                },
                progress -> {
                    if (progress < 100) bar.setProgress(progress);
                    else bar.setProgress(progress);
                });
    }

    public static Feedback copy(InputStream input, OutputStream output) {
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer)) != -1) output.write(buffer, 0, read);
            input.close();

            // write the output file
            output.flush();
            output.close();
            return new Feedback("Copied Successfully!", false);
        } catch (IOException e) {
            return new Feedback(e.getMessage(), false);
        }
    }

    public static class Feedback {
        public final String message;
        public final boolean result;

        public Feedback(String message, boolean result) {
            this.message = message;
            this.result = result;
        }
    }
}
