package nx.peter.app.util.model;
import java.util.List;

public interface OnGetAllListener<M extends Model> {
    void onGetAll(List<M> list, String message);
}
