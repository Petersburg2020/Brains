package nx.peter.app.util.model;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Library extends Model<Library> {
    public List<String> courses;

    public Library(Context cxt, List<String> courses) {
        super(cxt);
        this.courses = courses;
    }

    public Library(Context cxt) {
        this(cxt, new ArrayList<>());
    }

    @Override
    public Library populate() {
        getByOwner((model, message) -> {
                    Log.i("Library.populate", message);
                    courses = model != null ? model.courses : new ArrayList<>();
                });
        return this;
    }

    public List<String> getCourses() {
        return this.courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
}
