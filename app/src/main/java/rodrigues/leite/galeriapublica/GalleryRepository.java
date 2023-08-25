package rodrigues.leite.galeriapublica;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.textclassifier.SelectionEvent;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class GalleryRepository {

    Context context;

    public GalleryRepository(Context context){
        this.context = context;
    }

    public List<ImageData> loadImageData(Integer limit /*Numero de elementos a serem criados*/, Integer offSet /*indice que os elementos vao ser criados*/) throws FileNotFoundException{
        List<ImageData> imageDataList = new ArrayList<>();

        //Dimensoes da imagem
        int w = (int)context.getResources().getDimension(R.dimen.im_width);
        int h = (int)context.getResources().getDimension(R.dimen.im_height);

        String[] projection = new String[]{
                MediaStore.Images.Media._ID, //id do arq foto para construir o endereço uri
                MediaStore.Images.Media.DISPLAY_NAME, //o nome do arq
                MediaStore.Images.Media.DATE_ADDED, //data que o arq foi criado
                MediaStore.Images.Media.SIZE}; //tamanho em bytes do arq

        String selection = null; //Subconjunto que queremos pegar (nulo = todas as fotos)
        String selectionArgs[] = null; //Como não definimos um selection, então também setamos selectionArgs como nulo.
        String sort = MediaStore.Images.Media.DATE_ADDED; //Ordena pela data de adição
        Cursor cursor = null;

        //Inicio da procura dentro do banco de dados do celular
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) { //Para versoes posteriores do android 11
            Bundle queryArgs = new Bundle();
            //definindo os parametros
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);
            // Sort
            queryArgs.putString(
                    ContentResolver.QUERY_ARG_SORT_COLUMNS,
                    sort
            );

            queryArgs.putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_ASCENDING
            );
            // limit, offset
            queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);
            queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, offSet);

            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    queryArgs,
                    null
            );
        }
        else{
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sort + "ASC + LIMIT " + String.valueOf(limit) + " OFFSET " + String.valueOf(offSet)
            );
        }
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
        while (cursor.moveToNext()){
            // Get values of columns for a given image.
            long id = cursor.getLong(idColumn);
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.EXTERNAL_CONTENT_URI, id);
            String name = cursor.getString(nameColumn);
            int dateAdded = cursor.getInt(dateAddedColumn);
            int size = cursor.getInt(sizeColumn);
            Bitmap thumb = Util.getBitmap(context, contentUri, w, h);

            // Stores column values and the contentUri in a local object that represents the media file.

            imageDataList.add(new ImageData(contentUri, thumb, name, new Date(dateAdded*1000L),size));
        }
        return imageDataList;
    }

}
