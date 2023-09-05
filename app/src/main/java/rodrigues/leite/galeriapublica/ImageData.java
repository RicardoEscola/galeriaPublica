package rodrigues.leite.galeriapublica;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

public class ImageData {
    public Uri uri;
    public Bitmap thumb;
    public String fileName;
    public Date date;
    public int size;

    public ImageData(Uri uri, Bitmap thumb, String fileName, Date date,int size) {
        this.uri = uri;  //endereço uri do arquivo de foto
        this.thumb = thumb; //img em minuatura
        this.fileName = fileName; //nome do arq foto
        this.date = date; //data que a img foi criada
        this.size = size; //tamanho em bytes
    }
}
