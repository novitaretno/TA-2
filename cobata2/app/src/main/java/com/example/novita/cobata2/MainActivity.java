package com.example.novita.cobata2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.backup.FileBackupHelper;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.novita.cobata2.utlity.NetworkUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button btn_signature, btn_clear, btn_sign, btn_cancel;
    File file;
    Dialog dialog;
    LinearLayout mContent;
    signature mSignature;  // fungsi untuk menampilkan dialog pop up
    View view; // tampilan -> menampilkan mContent
    Bitmap bitmap;
    private ArrayList<String> encodedImageList;
    ImageView image_source, img_signature;
    private ProgressDialog progressDialog;
    private String KEY_IMAGE = "image";
    private String KEY_IMAGE_PHOTO_NAME = "image_name";
    private String KEY_USER_ID = "user_id";



    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/DigitSign/Absen/";
    //String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + "5113100016_Novita" + ".png";
    String DIRECTORY_DATASET_TTD = Environment.getExternalStorageDirectory().getPath() + "/DigitSign/TandaTangan/5113100016_Novita/";

    private static final String TAG = "MainActivity";

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.v("log_tag", "OpenCV Loaded Successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    static {
        System.loadLibrary("MyLibs");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setting ToolBar as ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_signature = (Button) findViewById(R.id.button_signature);

        // Method to create Directory, if the Directory doesn't exists
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

        // Dialog Function
        dialog = new Dialog(MainActivity.this);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true); // agar tidak muncul dialognya

        //muncul dialognya
        btn_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_action();
                Log.v("log_tag nama", StoredPath);

            }
        });
    }


    // Function for Appear Digital Signature Layout
    public void dialog_action() {
        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);

        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btn_clear = (Button) dialog.findViewById(R.id.btn_clear);
        btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_sign = (Button) dialog.findViewById(R.id.btn_sign);
        btn_sign.setEnabled(false);
        view = mContent;

        btn_clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                btn_sign.setEnabled(false);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class
                recreate();
            }
        });

        btn_sign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                mSignature.save(view, StoredPath);
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                // Calling the same class
                recreate();

            }
        });
        dialog.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());

            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }


        }

        public void clear() {
            path.reset();
            invalidate();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            btn_sign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }


        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_resize:
                Toast.makeText(this, "preprocessing", Toast.LENGTH_SHORT).show();
                viewImage(Preprocessing.class);
                break;
            case R.id.action_daftar:
                Toast.makeText(this, "daftar", Toast.LENGTH_SHORT).show();
                viewImage(Daftar_TandaTangan.class);
                break;

            case R.id.action_kirim_server:
                Toast.makeText(this, "kirimdb", Toast.LENGTH_SHORT).show();
                encodedImageList = new ArrayList<>();
                encodedImageList = getAllEncodedImageFormat();
                uploadImages(encodedImageList);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void viewImage(Class x) {
        Intent intent = new Intent(getBaseContext(), x);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        if (!OpenCVLoader.initDebug()) {
            Log.v("log_tag", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.v("log_tag", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public ArrayList<String> getAllEncodedImageFormat() {
        ArrayList<String> encodedImage = new ArrayList<>();

        FileHelper imageFiles = new FileHelper(DIRECTORY_DATASET_TTD);
        File[] imageList = imageFiles.getListOfFiles();
        Log.d("get All Image", String.valueOf(imageList.length));

        if (imageList != null && imageList.length > 0) {
            for (File image : imageList) {
                String imagePath = image.getAbsolutePath();
                Log.d("imagePath", imagePath);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage.add(Base64.encodeToString(imageBytes, Base64.DEFAULT));

            }
            return encodedImage;
        } else {
            return null;
        }
    }


    // Upload image to server
    private void uploadImages(final ArrayList<String> encodedImagesList) {

        final int[] totalUploaded = {0};
        StringRequest stringRequest;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.d("Masuk ke fungsi upload","Masuk ke fungsi upload");
        for (int i =0; i < encodedImagesList.size(); i++) {
            final int index = i;

            //Showing the progress dialog
          //  progressDialog.setMessage("Mengunggah Data Set ke Server ..." + index + " dari " + encodedImagesList.size());
          //  progressDialog.show();

            stringRequest = new StringRequest(Request.Method.POST, NetworkUtils.UPLOAD_DATASET_SIKEMAS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Showing toast message of the response
                            Log.d("VolleyResponse", "Dapat ResponseVolley Upload Images");
                            totalUploaded[0]++;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            // Dismissing the progress dialog
                         //   progressDialog.dismiss();
                            Log.d("VolleyErroyResponse", "Error");
                            //Showing toast

                            Toast.makeText(MainActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Get encoded Image
                    String image = encodedImagesList.get(index);

                 //   Log.d("getParamImage", image);
                //   Log.d("getParamImageName", "Novita" + "_" + index);
                //    Log.d("getParamUserId", "5113100016");
                    // Creating parameters
                    Map<String, String> params = new HashMap<>();
                    // Adding parameters

                    params.put(KEY_IMAGE, image); //file gambar
                    params.put(KEY_IMAGE_PHOTO_NAME, "Novita" + "_" + index);  //nama file
                    params.put(KEY_USER_ID, "5113100016"); //user id buat

                    Log.v("log_tag", "param ->" + params);
                    //returning parameters
                    return params;
                }
            };
            //Adding request to the queue
            requestQueue.add(stringRequest);
        }
        //Disimissing the progress dialog
  //      progressDialog.dismiss();
        if(totalUploaded[0] == encodedImagesList.size()) {
            Toast.makeText(MainActivity.this, "Berhasil Kirim Seluruh Data Set ke Server", Toast.LENGTH_LONG).show();
        }
    }

}



