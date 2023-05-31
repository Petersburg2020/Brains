package nx.peter.app.util.model;

public interface OnSaveListener<M extends Model> {
    void onSaved(M model, String message, boolean success);
}
