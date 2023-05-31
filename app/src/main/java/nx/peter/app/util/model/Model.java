package nx.peter.app.util.model;

import android.content.Context;
import androidx.annotation.NonNull;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import nx.peter.app.util.Storage;
import nx.peter.java.util.advanced.Advanced;
import nx.peter.java.util.advanced.Advanced.ObjectDetail;

public abstract class Model<M extends Model> {
    Context cxt;
    long id;

    protected Model(@NonNull Context context) {
        cxt = context;
        getAll(
                (list, msg) -> {
                    id = getStore().getLong(getName(), 0) + 1;
                });
    }

    public M getModel() {
        return (M) this;
    }

    public long getId() {
        return id;
    }

    public Storage getStore() {
        return new Storage(cxt, getName());
    }

    public abstract M populate();

    public String getName() {
        return getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1);
    }

    public void getAllByOwner(@NonNull OnGetAllListener<M> listener) {
        ParseUser user = ParseUser.getCurrentUser();
        getAllBy("owner", user != null ? user.getUsername() : "", listener);
    }

    public void getAllBy(
            @NonNull String key, @NonNull Object value, @NonNull OnGetAllListener<M> listener) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getName());
        query.whereEqualTo(key, value)
                .findInBackground(
                        (objs, e) -> {
                            List<M> list = new ArrayList<>();
                            if (e == null && !objs.isEmpty())
                                for (ParseObject obj : objs) {
                                    Map<String, Object> map = new LinkedHashMap<>();
                                    if (map.containsKey("owner")) map.remove("owner");
                                    for (String name : obj.keySet()) map.put(name, obj.get(name));

                                    list.add((M) Advanced.getObject(map, getModel().getClass()));
                                }
                            listener.onGetAll(list, e == null ? "Success!" : e.getMessage());
                        });
    }

    public void getAll(@NonNull OnGetAllListener<M> listener) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getName());
        query.findInBackground(
                (objs, e) -> {
                    List<M> list = new ArrayList<>();
                    if (e == null && !objs.isEmpty())
                        for (ParseObject obj : objs) {
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.remove("owner");
                            for (String name : obj.keySet()) map.put(name, obj.get(name));

                            list.add((M) Advanced.getObject(map, getModel().getClass()));
                        }
                    listener.onGetAll(list, e == null ? "Success!" : e.getMessage());
                });
    }

    public void getByOwner(@NonNull OnGetListener<M> listener) {
        ParseUser user = ParseUser.getCurrentUser();
        getBy("owner", user != null ? user.getUsername() : null, listener);
    }

    public void getBy(String key, Object value, @NonNull OnGetListener<M> listener) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getName());
        query.whereEqualTo(key, value)
                .getFirstInBackground(
                        (obj, e) -> {
                            if (e == null && obj != null) {
                                Map<String, Object> map = new LinkedHashMap<>();
                                for (String name : obj.keySet()) map.put(name, obj.get(name));
                                listener.onGet(
                                        (M) Advanced.getObject(map, getModel().getClass()),
                                        "Success!");
                            } else listener.onGet(null, e.getMessage());
                        });
    }

    public void getById(long id, @NonNull OnGetListener<M> listener) {
        getBy("id", getName() + id, listener);
    }

    public void save(@NonNull OnSaveListener<M> listener) {
        ParseObject obj = new ParseObject(getName());
        ObjectDetail<M> det = Advanced.getObjectDetail(getModel());

        ParseUser user = ParseUser.getCurrentUser();
        obj.put(getName(), id);
        obj.put("owner", user != null ? user.getUsername() : "");

        for (String name : det.names) obj.put(name, det.get(name));

        obj.saveInBackground(
                e -> {
                    listener.onSaved(
                            getModel(), e == null ? "Success!" : e.getMessage(), e == null);
                });
    }

    public void delete(@NonNull OnDeleteListener<M> listener) {
        ParseObject obj = new ParseObject(getName());
        ObjectDetail<M> det = Advanced.getObjectDetail(getModel());

        for (String name : det.names) obj.put(name, det.get(name));

        obj.deleteInBackground(
                e -> {
                    listener.onDelete(
                            getModel(), e == null ? "Success!" : e.getMessage(), e == null);
                });
    }
}
