package rodrigues.leite.galeriapublica;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import kotlinx.coroutines.CoroutineScope;

public class MainViewModel extends AndroidViewModel {
    int navigationOpSelect = R.id.gridViewOp;
    public int getNavigationOpSelect() {
        return navigationOpSelect;
    }

    public void setNavigationOpSelect(int navigationOpSelect) {
        this.navigationOpSelect = navigationOpSelect;
    }

    LiveData<PagingData<ImageData>> pageLv; // Guardar a página de dados atual
    public MainViewModel(@NonNull Application application) {
        super(application);
        GalleryRepository galleryRepository = new GalleryRepository(application);
        GalleryPagingSource galleryPagingSource = new GalleryPagingSource(galleryRepository);
        Pager<Integer, ImageData> pager = new Pager(new PagingConfig(10),() -> galleryPagingSource);
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        pageLv = PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewModelScope);
    }

    public LiveData<PagingData<ImageData>> getPageLv(){
        // Um metood para que outros apps tenham acesso a o container LiveData
        return pageLv;
    }

}
