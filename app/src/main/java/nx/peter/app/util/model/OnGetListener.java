package nx.peter.app.util.model;

public interface OnGetListener<M extends Model> {
    void onGet(M model, String message);
}
