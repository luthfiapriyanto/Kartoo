package co.kartoo.app.cards.suggestioncard.applycard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import co.kartoo.app.R;
import co.kartoo.app.models.LoginPref_;

@EActivity(R.layout.activity_apply_scanning)
public class ApplyScanningActivity extends AppCompatActivity {
    @ViewById
    Toolbar mToolbar;
    @ViewById
    TextView mTVtitle, textIncome, textKtp, textNpwp;
    @ViewById
    Button mBtnNext;
    @ViewById
    ImageView mRVincome, mRVktp, mRVnpwp, imageIncome, imageKtp, imageNpwp;
    @ViewById
    ProgressBar progressBarIncome;
    @Pref
    LoginPref_ loginPref;

    String fileName;

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    String which;

    Bitmap bitMap;

    Integer count =1;

    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;AccountName=kartoostorage;AccountKey=uKFP4jlPuDGlmyH7WoPSXoi9plqPej2/43HaPEjiS9K9QR2P5wohYDZaaiPa4CLZdInXDGPEzjTSlOlQDffGFw==";

    @AfterViews
    void init() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        setUpNavDrawer();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String str = loginPref.email().get();
        fileName = str.replaceAll("[@.]", "");

        count =1;
        progressBarIncome.setProgress(0);
        progressBarIncome.setMax(10);
    }

    @Click(R.id.mBtnNext)
    public void mBtnNextClick() {
        Intent intent = new Intent(this, ApplyDoneActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.mRVincome)
    public void mRVincomeClick() {
        selectImage();
        progressBarIncome.setProgress(0);
        which = "income";
    }
    @Click(R.id.mRVktp)
    public void mRVktpClick() {
        selectImage();
        which = "ktp";
    }
    @Click(R.id.mRVnpwp)
    public void mRVnpwpClick() {
        selectImage();
        which = "npwp";
    }

    //image Profile Picture Class
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ApplyScanningActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE){
                imageIncome.setVisibility(View.GONE);
                onSelectFromGalleryResult(data);
                new BlobUpload().execute(10);
            }
            else if (requestCode == REQUEST_CAMERA){
                imageIncome.setVisibility(View.GONE);
                onCaptureImageResult(data);
                new BlobUpload().execute(10);
            }
        }
    }

    private class BlobUpload extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
                CloudBlobClient serviceClient = account.createCloudBlobClient();

                // Container name must be lower case.
                CloudBlobContainer container = serviceClient.getContainerReference("ccapp");
                container.createIfNotExists();

                // Upload an image file.
                CloudBlockBlob blob = container.getBlockBlobReference(which+"\\"+fileName+".png");

                File sourceFile = new File(Environment.getExternalStorageDirectory(), which+"\\"+fileName+".png");

                blob.upload(new FileInputStream(sourceFile), sourceFile.length());
            }
            catch(Exception e){
                Log.e("TAG", "UPLOADBLOB: "+e );
            }
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
            progressBarIncome.setVisibility(View.GONE);
            textIncome.setText("Upload Successfull");

            Log.e("TAG", "onPostExecute: "+"UPLOADSUCCES" );
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.e("TAG", "onProgressUpdate: "+ values[0]);
            textIncome.setText("Running..."+ values[0]);
            progressBarIncome.setProgress(values[0]);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), which+"\\"+fileName+".png");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(which.equals("income")){
            mRVincome.getRotation();
            mRVincome.setBackgroundDrawable(null);
            mRVincome.setBackgroundResource(0);
            mRVincome.setBackgroundColor(0);
            mRVincome.setImageBitmap(thumbnail);
        }
        else if (which.equals("ktp")){
            mRVktp.getRotation();
            mRVktp.setBackgroundDrawable(null);
            mRVktp.setBackgroundResource(0);
            mRVktp.setBackgroundColor(0);
            mRVktp.setImageBitmap(thumbnail);
        }else if (which.equals("npwp")){
            mRVnpwp.getRotation();
            mRVnpwp.setBackgroundDrawable(null);
            mRVnpwp.setBackgroundResource(0);
            mRVnpwp.setBackgroundColor(0);
            mRVnpwp.setImageBitmap(thumbnail);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);


        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 2000;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;

        bm = BitmapFactory.decodeFile(selectedImagePath, options);


        if(which.equals("income")){
            mRVincome.setBackgroundDrawable(null);
            mRVincome.setBackgroundResource(0);
            mRVincome.setBackgroundColor(0);
            mRVincome.setImageBitmap(bm);
        }
        else if (which.equals("ktp")){
            mRVktp.setBackgroundDrawable(null);
            mRVktp.setBackgroundResource(0);
            mRVktp.setBackgroundColor(0);
            mRVktp.setImageBitmap(bm);
        }else if (which.equals("npwp")){
            mRVnpwp.setBackgroundDrawable(null);
            mRVnpwp.setBackgroundResource(0);
            mRVnpwp.setBackgroundColor(0);
            mRVnpwp.setImageBitmap(bm);
        }
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_back_orange);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
