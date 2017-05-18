package com.example.novita.cobata2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;

import static android.R.attr.bitmap;

/**
 * Created by novitarpl on 5/7/2017.
 */

public class TemplateMacing extends AppCompatActivity {
    ImageView image_source_crop;
    int R=0, x1, x2, x3, x4;
    String ImageSignaturePath;

    private Mat matchTemplate(String inFile, String templateFile, String outFile, int match_method){
        Log.v("log_tag", "masuk ke fungsi template matching");

        Mat img = Imgcodecs.imread(inFile);  //image source
        Mat templ = Imgcodecs.imread(templateFile); //image ttd


        //Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        //  Bitmap inFileBitmap = BitmapFactory.decodeFile(inFile);
        //  Bitmap templBitmap = BitmapFactory.decodeFile(templateFile);
        // / Do the Matching and Normalize
        //Utils.bitmapToMat(inFileBitmap, img);
        //Utils.bitmapToMat(templBitmap, templ);
        Imgproc.matchTemplate(img, templ, result, match_method);
        // Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        double minVal; double maxVal;
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        }
        else {
            matchLoc = mmr.maxLoc;
        }
        Log.v("log_tag", "matchLocnya -> "+matchLoc);



        // / Show me what you got
        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),matchLoc.y + templ.rows()), new Scalar(0, 0,0));

        // Save the visualized detection.
        Log.v("log_tag", "writing" + outFile);


        Imgcodecs.imwrite(outFile, img);
        return img;

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.view_signature);

        String ImageSourcePath = Environment.getExternalStorageDirectory().getPath() + "/DigitSign/TandaTangan/5113100111_Saddam/5113100016_Novita_1.png";
        String ImageSignaturePath = Environment.getExternalStorageDirectory().getPath()+ "/DigitSign/Absen/5113100122_Eriko.png";
        String outFile = Environment.getExternalStorageDirectory().getPath()+ "/DigitSign/Hasil/5113100111_Saddam_3.png";

        //for(int index=1; index<=5; index++)
        //{
            Bitmap img_ttd = BitmapFactory.decodeFile(ImageSignaturePath);
            crop_image(img_ttd, ImageSignaturePath);
            matchTemplate(ImageSourcePath, ImageSignaturePath, outFile, Imgproc.TM_CCOEFF);

            Bitmap bm = BitmapFactory.decodeFile(outFile);
         //   image_source_crop= (ImageView) findViewById(R.id.img_source_crop);
         //   image_source_crop.setImageBitmap(bm);

        //}

    }

    private String crop_image(Bitmap img_ttd, String ImageSignaturePath)
    {
        int tinggi_img_ttd =img_ttd.getHeight();
        int lebar_img_ttd= img_ttd.getWidth();
        String img_ttd_crop;


        Bitmap img_ttd_gray = Bitmap.createBitmap( img_ttd.getWidth(), img_ttd.getHeight(), img_ttd.getConfig());

        for(int ya=0; ya<tinggi_img_ttd; ya++) {
            for (int xa = 0; xa < lebar_img_ttd; xa++) {
                int pixel = img_ttd.getPixel(xa, ya);
                R = Color.red(pixel);
                if (R > 128)
                    R = 255;
                else
                    R = 0;
                img_ttd_gray.setPixel(xa, ya, Color.rgb(R, R, R));
                if (R == 0) {
                    x1 = ya;
                    //  Log.v("log_tag", "x1 ->" + x1);
                    break;}
            }

            if (R == 0) {break;}
        }


        for( int xb=0; xb<lebar_img_ttd; xb++){
            for(int yb=0; yb<tinggi_img_ttd; yb++) {
                int pixel = img_ttd.getPixel(xb, yb);
                R = Color.red(pixel);

                if (R > 128)
                    R = 255;
                else
                    R = 0;

                img_ttd_gray.setPixel(xb, yb, Color.rgb(R, R, R));

                if (R == 0) {
                    x2 = xb;
                    //   Log.v("log_tag", "x2 ->" + x2);
                    break; }
            }
            if (R == 0){break;}
        }

        for(int xc=lebar_img_ttd-1; xc >=0; xc--){
            for(int yc=0; yc < tinggi_img_ttd; yc++){
                int pixel = img_ttd.getPixel(xc, yc);
                R = Color.red(pixel);

                if (R > 128)
                    R = 255;
                else
                    R = 0;

                img_ttd_gray.setPixel(xc, yc, Color.rgb(R, R, R));

                if (R == 0) {
                    x3 = xc;
                    //   Log.v("log_tag", "x3 ->" + x3);
                    break;}
            }
            if (R == 0){break;}
        }

        for(int yd=tinggi_img_ttd-1; yd>=0; yd--){
            for(int xd=0; xd<lebar_img_ttd; xd++){
                int pixel = img_ttd.getPixel(xd, yd);
                R = Color.red(pixel);

                if (R > 128)
                    R = 255;
                else
                    R = 0;

                img_ttd_gray.setPixel(xd, yd, Color.rgb(R, R, R));

                if (R == 0) {
                    x4 = yd;
                    //    Log.v("log_tag", "x4 ->" + x4);
                    break;}
            }
            if (R == 0){break;}
        }

        int lebar_new = x3-x2;
        //  Log.v("log_tag", "lebar_new ->" + lebar_new);
        int tinggi_new = x4-x1;
        //  Log.v("log_tag", "tinggi_new ->" + tinggi_new);


        Bitmap new_img = Bitmap.createBitmap(lebar_new,tinggi_new,Bitmap.Config.RGB_565);

        for(int i=0; i<lebar_new; i++){
            //Log.v("log_tag", "lala");
            for(int j=0; j<tinggi_new; j++){
                int pixel = img_ttd.getPixel(x2+i, x1+j);
                new_img.setPixel(i,j,pixel);
            }

        }
        Mat img_ttd_crop_mat= new Mat();
        Utils.bitmapToMat(new_img, img_ttd_crop_mat);
        Imgcodecs.imwrite(ImageSignaturePath, img_ttd_crop_mat);
        return ImageSignaturePath;
//        img_ttd_gray.compress(Bitmap.CompressFormat.PNG,100, baos);
//        byte [] b=baos.toByteArray();
//        img_ttd_crop =Base64.encodeToString(b, Base64.DEFAULT);
//        Log.v("log_tag", "img_ttd_crop_fungsi -> "+ img_ttd_crop);
//        return img_ttd_crop;
    }

}
