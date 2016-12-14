package brahim.mallow.com.ddd.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;

import brahim.mallow.com.ddd.R;

/**
 * Created by brahim on 13/12/16.
 */

public class ListImageActivity extends Activity {

    SQLiteDatabase database;
    ListView listView;
    ProgressBar progressBar;
    LinkedList<ImageModel> emptyimages = new LinkedList<>();
    TextView textView;
    int drawableSize = 0;

    public class IAdapter extends ArrayAdapter<ImageModel> {

        public IAdapter(Context context, LinkedList<ImageModel> images){
            super(context, 0, images);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.list_item_image, null);
            }
            ImageModel image =getItem(position);
            TextView caption = (TextView)convertView.findViewById(R.id.caption_item);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_item);
            imageView.setImageBitmap(image.getImage());
            caption.setText(image.getCaption());


            return convertView;
        }


    }

    public class readingImagesAsyntask extends AsyncTask<Void, ImageModel, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("ListView is loading...");
            progressBar.setMax(drawableSize);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            database.beginTransaction();
            Cursor c1 = database.rawQuery("select * from mytable", null);
            c1.moveToPosition(-1);
            while(c1.moveToNext()){
                String caption = c1.getString(1);
                Log.d("MESSAGES........", caption);
                byte[] im = c1.getBlob(2);
                ByteArrayInputStream imageStream = new ByteArrayInputStream(im);
                Bitmap theImage= BitmapFactory.decodeStream(imageStream);
                publishProgress(new ImageModel(caption, theImage));
                // images.add(new ImageModel(caption, theImage));


            }
            database.setTransactionSuccessful();
            database.endTransaction();
            return null;
        }

        @Override
        protected void onProgressUpdate(ImageModel... values) {
            super.onProgressUpdate(values);
            ImageModel im = values[0];
            ((IAdapter)listView.getAdapter()).add(im);
            progressBar.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textView.setText("ListView loading is finished !");
        }
    }




    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar2);
        listView = (ListView)findViewById(R.id.list_view_images);
        listView.setAdapter(new IAdapter(this, emptyimages));
        textView = (TextView)findViewById(R.id.reading_images_tex_view);
        drawableSize = getIntent().getIntExtra("D_SIZE",1);
        database = openDatabase();
        new readingImagesAsyntask().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageModel myImage = (ImageModel) listView.getAdapter().getItem(i);
                Bundle bundle = new Bundle();
                byte[] b = toByte(myImage.getImage());
                bundle.putByteArray("IMAGE_BYTES",b);
                bundle.putString("IMAGE_CAPTION", myImage.getCaption());
                Intent intent = new Intent(ListImageActivity.this, ImageDetailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });


    }

    public SQLiteDatabase openDatabase(){
        SQLiteDatabase d = null;
        try{
            File storagePath = getApplication().getFilesDir();
            String myDbPath = storagePath+"/"+"myImage";
            d =  SQLiteDatabase.openDatabase(myDbPath,null,SQLiteDatabase.CREATE_IF_NECESSARY);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return d;
    }

    public byte[] toByte(Bitmap b){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
