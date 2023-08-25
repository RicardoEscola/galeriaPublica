package rodrigues.leite.galeriapublica;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

public class GalleryPagingSource extends ListenableFuturePagingSource<Integer, ImageData> {
    GalleryRepository galleryRepository;

    Integer initialLoadSize = 0;

    public GalleryPagingSource(GalleryRepository galleryRepository){
        this.galleryRepository = galleryRepository;
    }

    @Nullable
    @Override

    public Integer getRefreskKey(@NonNull PagingState<Integer,ImageData> pagingState){
        return null;
    }

}
