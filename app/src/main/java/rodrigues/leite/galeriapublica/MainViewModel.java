package rodrigues.leite.galeriapublica;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainViewModel extends AndroidViewModel {
    int navigationOpSelect = R.id.gridViewOp;

    public MainViewModel(@NonNull Application application){
        super(application);
    }
    public int getNavigationOpSelect() {
        return navigationOpSelect;
    }

    public void setNavigationOpSelect(int navigationOpSelect) {
        this.navigationOpSelect = navigationOpSelect;
    }
}
