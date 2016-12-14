package brahim.mallow.com.ddd.Model;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import brahim.mallow.com.ddd.R;


/**
 * Created by brahim on 12/12/16.
 */
public class DatabaseFillActivity extends Activity {


    ArrayList<Drawable> drawables;
    Button button, buttonShowListView;
    int i;
    TextView textView;
    SQLiteDatabase database;
    ProgressBar progressBarDatabase;
    boolean finished;

    public class FillAsyntask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            finished = false;
            try{
                drawables = getFields2();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            progressBarDatabase.setMax(drawables.size());
        }

        @Override
        protected Void doInBackground(Void... voids) {


            for(Drawable d: drawables){
                byte[] myBytes = toByte(d);
                if(saveImage(myBytes,d.toString())){
                    publishProgress();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressBarDatabase.incrementProgressBy(1);
            textView.setText("Database filling .... "+((progressBarDatabase.getProgress())*100)/progressBarDatabase.getMax()+ "%");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textView.setText("Database filling is finished !! ");
            database.close();
            finished = true;


        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_fill);

        textView = (TextView)findViewById(R.id.textview_fill_database);
        progressBarDatabase = (ProgressBar)findViewById(R.id.progress_bar1);
        button = (Button)findViewById(R.id.button_fill_database);
        buttonShowListView = (Button)findViewById(R.id.show_list_view_button);
        finished = false;


        database = openDatabase();
        createTableImage();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("Database filling ....");
                new FillAsyntask().execute();

            }
        });

        buttonShowListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finished){
                    Intent i = new Intent(DatabaseFillActivity.this, ListImageActivity.class);
                    i.putExtra("D_SIZE", drawables.size());
                    startActivity(i);
                }
                else{
                    Toast.makeText(getBaseContext(), "The database is empty or in filing state ", Toast.LENGTH_LONG).show();
                }
            }
        });












    }


    public ArrayList<Drawable> getFields() {
        Field[] drawablesFields = R.drawable.class.getFields();
        ArrayList<Drawable> drawables = new ArrayList<>();

        for (Field field : drawablesFields) {
            try {
                Log.i("LOG_TAG", "com.your.project.R.drawable." + field.getName());
                drawables.add(getResources().getDrawable(field.getInt(null)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return drawables;
    }

    public ArrayList<Drawable> getFields2() throws IOException{
        AssetManager am = getAssets();
        String[] imgPath = am.list("images");
        ArrayList<Drawable> drawables = new ArrayList<>();

        for (String file : imgPath) {
            Drawable d = Drawable.createFromStream(am.open("images/"+file), null);
            drawables.add(d);
        }
        return drawables;
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

    public boolean saveImage(byte[] bytes, String nomImage){

        boolean succes;
        try{
            database.beginTransaction();
            String query1 = "INSERT INTO mytable(caption,mydata) VALUES(?,?)";

            SQLiteStatement insertStatement = database.compileStatement(query1);
            insertStatement.clearBindings();
            insertStatement.bindString(1, nomImage);
            insertStatement.bindBlob(2, bytes);
            insertStatement.execute();
            succes = true;
            database.setTransactionSuccessful();
            database.endTransaction();
        }
        catch(Exception e){
            succes = false;
        }
        return  succes;
    }

    public byte[] toByte(Drawable d){

        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap b = bitDw.getBitmap() ;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }

    public void createTableImage()throws SQLiteException{
        database.beginTransaction();
        database.execSQL("drop table if exists mytable");
        database.execSQL("CREATE TABLE IF NOT EXISTS mytable(recID integer PRIMARY KEY autoincrement,caption text, mydata blob ); ");
        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
