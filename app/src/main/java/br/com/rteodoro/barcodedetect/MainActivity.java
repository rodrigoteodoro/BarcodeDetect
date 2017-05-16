package br.com.rteodoro.barcodedetect;

import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import android.support.design.widget.Snackbar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BarcodeDetect";
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERAAPI_REQUEST = 1666;
    ImageView myImageView;
    View parentLayout;
    Bitmap photo;
    BarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myImageView = (ImageView) findViewById(R.id.imgview);
        detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE |
                                Barcode.EAN_13 | Barcode.EAN_8 | Barcode.PRODUCT)
                        .build();
        final TextView txtView = (TextView) findViewById(R.id.txtContent);
        if(!detector.isOperational()){
            txtView.setText("Could not set up the detector!");
            return;
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtView.setText("");
                parentLayout = view;
                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                //Funciona com codigo barras interno, ver posicionamento da camera horizontal
                /*
                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.barras);
                myImageView.setImageBitmap(myBitmap);
                lerbarcode(myBitmap);
                */
                //---
            }
        });
    }

    protected void lerbarcode(Bitmap imagem){
        try {
            Frame frame = new Frame.Builder().setBitmap(imagem).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);
            if (barcodes.size() > 0) {
                Barcode thisCode = barcodes.valueAt(0);
                TextView txtView = (TextView) findViewById(R.id.txtContent);
                txtView.setText(thisCode.rawValue);
            } else {
                Snackbar.make(parentLayout, "Não encontrou código de barras!", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Snackbar.make(parentLayout, "Erro ao decodificar o código", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            try {
                photo = (Bitmap) data.getExtras().get("data");
                myImageView.setImageBitmap(photo);
                lerbarcode(photo);
            } catch (Exception e){
                Snackbar.make(parentLayout, "Não pode ler a imagem!", Snackbar.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERAAPI_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            try{
                byte[] bytesImage = data.getByteArrayExtra("data");
                photo = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
                myImageView.setImageBitmap(photo);
                lerbarcode(photo);
            }catch (Exception e) {
                //Snackbar.make(parentLayout, "Não pode ler a imagem!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }else if (id == R.id.action_camera_api) {
            Intent intent = new Intent(this, AndroidCameraApi.class);
            //startActivity(intent);
            startActivityForResult(intent, CAMERAAPI_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
