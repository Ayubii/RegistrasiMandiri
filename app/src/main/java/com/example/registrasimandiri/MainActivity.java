package com.example.registrasimandiri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.UnsupportedEncodingException;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.AttributeSet;

public class MainActivity extends AppCompatActivity {
    Bitmap bitmap, bitmap1;
    private int mImageWidht, mImageHeight;
    private Bitmap mbitmap;
    boolean check = true;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    Button SelectImageGallery, UploadImageServer;
    ImageView imageView;
    EditText imageName, InputJabatan, InputIDpegawai, InputNomorHP;
    ProgressDialog progressDialog;
    String GetImageNameEditText;
    String GetinputhpEditText;
    String GetinputidpegawaiEditText;
    String GetinputjabatanEditText;
    String Inputjabatan = "Input_jabatan";
    String inputIDpegawai = "Input_IDpegawai";
    String inputNomorHP = "Input_NomorHP";
    String ImageName = "image_name";
    String ImagePath = "image_path";
    String ServerUploadPath = "http://192.168.100.157/img_upload_to_server.php";
    ///String ServerUploadPath = "http://192.168.43.179/img_upload_to_server.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageName = (EditText) findViewById(R.id.inputnama);
        InputJabatan = (EditText) findViewById(R.id.inputjabatan);
        InputIDpegawai = (EditText) findViewById(R.id.inputIdpegawai);
        InputNomorHP  = (EditText) findViewById(R.id.inputnomorHP);
        SelectImageGallery = (Button) findViewById(R.id.buttonuploadimg);
        UploadImageServer = (Button) findViewById(R.id.buttonsendtoserver);

//        SelectImageGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);
//            }
//        });

        UploadImageServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetImageNameEditText = imageName.getText().toString();
                GetinputhpEditText = InputNomorHP.getText().toString();
                GetinputidpegawaiEditText = InputIDpegawai.getText().toString();
                GetinputjabatanEditText = InputJabatan.getText().toString();
                ImageUploadToServerFunction();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int RC, int RQC, Intent I) {
//        super.onActivityResult(RC, RQC, I);
//        if (RC == 1 && RQC == RESULT_OK && I != null && I.getData() != null) {
//            Uri uri = I.getData();
//
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                //PREVIEW SIZE IMAGEVIEW
//                Bitmap originalBitmap = bitmap;
//                Bitmap resizedBitmap = bitmap.createScaledBitmap(
//                        originalBitmap, 100, 100, false);
//                imageView.setImageBitmap(bitmap);
//                int ivWidth = imageView.getWidth();
//                int ivHeight = imageView.getHeight();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    public void Ambil(View view) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

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

        imageView.setImageBitmap(bitmap);
    }

//    FaceDetectorOptions highAccuracyOpts =
//            new FaceDetectorOptions.Builder()
//                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//                    .build();
//
//    private void FaceDetector111(Bitmap bitmap){
//        int rotationDegree = 90;
//
//        InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);
//        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
//        detector.process(image)
//                .addOnSuccessListener(
//                        new OnSuccessListener<List<Face>>() {
//                            @Override
//                            public void onSuccess(List<Face> faces) {
//
//                                Toast.makeText(getBaseContext(),"Wajah Dikenali", Toast.LENGTH_SHORT).show();
////                                canvas = new Canvas();
////                                draw(canvas);
//
//
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getBaseContext(),"Wajah Tidak Dikenali", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//
//    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri uri = data.getData();


        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(bitmap);
    }
    
    public void ImageUploadToServerFunction() {
        //2 bawah ini kalo mau komen

        float ivWidth = imageView.getWidth();
        float ivHeight = imageView.getHeight();
        float ivscale;
        float ivscale2;
        ivscale = 100 / ivWidth;
        ivscale2 = ivscale * ivHeight;
        Bitmap originalBitmap = bitmap;
        Bitmap resizedBitmap = bitmap.createScaledBitmap(originalBitmap, 100, (int)(ivscale2), false);
        ByteArrayOutputStream byteArrayOutputStreamObject;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        // resizedbitmap diganti bitmap
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MainActivity.this, "Image is Uploading", "Please Wait", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();
                // Printing uploading success message coming from server on android app.
                Toast.makeText(MainActivity.this, string1, Toast.LENGTH_LONG).show();
                // Setting image as transparent after done uploading.
                imageView.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(ImageName, GetImageNameEditText);
                HashMapParams.put(Inputjabatan, GetinputjabatanEditText);
                HashMapParams.put(inputIDpegawai, GetinputidpegawaiEditText);
                HashMapParams.put(inputNomorHP, GetinputhpEditText);
                HashMapParams.put(ImagePath, ConvertImage);
                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }


    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;
                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);
                OutPutStream = httpURLConnectionObject.getOutputStream();
                bufferedWriterObject = new BufferedWriter(
                        new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();
                RC = httpURLConnectionObject.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilderObject.append("&");
                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilderObject.toString();
        }

    }
}