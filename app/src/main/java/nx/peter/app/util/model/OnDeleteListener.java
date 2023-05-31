package nx.peter.app.util.model;

public interface OnDeleteListener<M extends Model> {
    void onDelete(M model, String message, boolean deleted);
}
