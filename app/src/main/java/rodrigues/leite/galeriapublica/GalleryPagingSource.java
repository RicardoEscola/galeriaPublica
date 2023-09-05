package rodrigues.leite.galeriapublica;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GalleryPagingSource extends ListenableFuturePagingSource<Integer, ImageData> { // Herda de uma classe que pertence à biblioteca Paging
    GalleryRepository galleryRepository;

    Integer initialLoadSize = 0;

    public GalleryPagingSource(GalleryRepository galleryRepository) { // Recebe uma instancia  para consultar os dados e monsta-los
        this.galleryRepository = galleryRepository;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, ImageData> pagingState) {
        //Não usamos, mas o ListenableFuturePagingSource precisa dele criado, por isso nulo
        return null;
    }

    @Nullable
    @Override
    public ListenableFuture<LoadResult<Integer, ImageData>> loadFuture(@NonNull LoadParams<Integer> loadParams) {
        // Carrega uma pagina do GalleryRepository e retorna ListenableFuture
        Integer nextPageNumber = loadParams.getKey(); //Obtemos a página de dados que deve ser obtida neste momento.

        // Inicio do key e loadSize para calcular LIMIT e OFFSET, por conta do caso da pagina 1, o offset é 3x maior.
        if (nextPageNumber == null) { //Obtendo a pagina 1
            nextPageNumber = 1;
            initialLoadSize = loadParams.getLoadSize(); //Atributo que guarda o numero de itens que deve ser guardado na pagina corrente
        }
        Integer offSet = 0;
        if (nextPageNumber == 2) {
            offSet = initialLoadSize;
        } else {
            offSet = ((nextPageNumber - 1) * loadParams.getLoadSize()) + (initialLoadSize - loadParams.getLoadSize());
        }

        //Termino do codigo acima

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor()); // Criação de uma nova thread e guardamos ela na service
        Integer finalOffSet = offSet;
        Integer finalNextPageNumber = nextPageNumber;
        ListenableFuture<LoadResult<Integer, ImageData>> lf = service.submit(new Callable<LoadResult<Integer, ImageData>>() {
            //Indicamos oq o codigo vai ser executado dentro dela usando o submit para setar um Callable que fala que a thread vai ser executada quando iniciada
            @Override
            public LoadResult<Integer, ImageData> call() {
                // O objetivo desse método é obter uma página de dados de GalleryRepository.
                List<ImageData> imageDataList = null;
                try {
                    //Calcular a proxima pagina
                    imageDataList = galleryRepository.loadImageData(loadParams.getLoadSize(), finalOffSet);
                    Integer nextKey = null;
                    if (imageDataList.size() >= loadParams.getLoadSize()) {
                        nextKey = finalNextPageNumber + 1;
                    }
                    //Monta o LoadResult que armazena os dados da pag obtidos e o valor da proxima pagina
                    return new LoadResult.Page<Integer, ImageData>(imageDataList, null, nextKey);

                }

                catch (FileNotFoundException e) {
                    return new LoadResult.Error<>(e);
                }
            }
        });
        return lf; //Esse objeto é um container que serve para guardar qualquer tipo de dado e que pode ser observado por outros objetos.
    }
}
