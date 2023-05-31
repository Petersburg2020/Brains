package nx.peter.app.brains.ui;
import androidx.fragment.app.Fragment;
import nx.peter.app.util.RequestIntent;

public class RequestFragment extends Fragment {
    public RequestIntent.RequestActivity getRequestActivity() {
        return (RequestIntent.RequestActivity) requireActivity();
    }
}
