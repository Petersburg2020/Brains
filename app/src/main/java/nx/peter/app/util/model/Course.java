package nx.peter.app.util.model;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.parse.ParseObject;

public class Course extends Model<Course> {
    String title, author, pdfUrl, videoUrl;

    public Course(@NonNull Context cxt, String title, String author, String pdfUrl, String videoUrl) {
        super(cxt);
        this.title = title;
        this.author = author;
        this.pdfUrl = pdfUrl;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPdfUrl() {
        return this.pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public Course populate() {
        getByOwner(
                (model, message) -> {
                    Log.i("Course.populate", message);
                    if (model != null) {
                        videoUrl = model.videoUrl;
                        pdfUrl = model.pdfUrl;
                        title = model.title;
                        author = model.author;
                    }
                    // courses = model != null ? model.courses : new ArrayList<>();
                });
        return this;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
