package brahim.mallow.com.ddd.Model;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.File;

import brahim.mallow.com.ddd.R;

/**
 * Created by brahim on 10/12/16.
 */

public class ImageDetailsActivity extends Activity {



    private TextView textView;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        textView =(TextView)findViewById(R.id.text_view_details);
        imageView = (ImageView)findViewById(R.id.image_view_details);
        Bundle b = getIntent().getExtras();
        byte[] imageBytes= b.getByteArray("IMAGE_BYTES");
        String caption = b.getString("IMAGE_CAPTION");

        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
        Bitmap theImage= BitmapFactory.decodeStream(imageStream);

        textView.setText(caption);
        imageView.setImageBitmap(theImage);



    }
}
