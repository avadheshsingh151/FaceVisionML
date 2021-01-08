package com.example.facevisionml;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceLandmark;
import java.util.List;

public class MainActivity extends AppCompatActivity   implements View.OnClickListener  {

    private static final String TAG = "FaceDetectorProcessor";
    private static final float FACE_POSITION_RADIUS = 8.0f;

    ImageView imageView;
    Button button;
    Button t1,t2,t3;
    Bitmap mBitmap;
    Paint boxPaint;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);
        t1 = (Button) findViewById(R.id.buttonIMG1);
        t2 = (Button) findViewById(R.id.buttonIMG2);
        t3 = (Button) findViewById(R.id.buttonIMG3);

        button.setOnClickListener(this);
        t1.setOnClickListener(this);
        t2.setOnClickListener(this);
        t3.setOnClickListener(this);

        //initializing Paint
        boxPaint = new Paint();
        boxPaint.setStrokeWidth(5);
        boxPaint.setColor(Color.GREEN);
        boxPaint.setStyle(Paint.Style.STROKE);

        mBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.image01);
        imageView.setImageBitmap(mBitmap);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button){
            //Facedetection

            FaceDetectorOptions faceDetectorOptions= new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build();

            InputImage image = InputImage.fromBitmap(mBitmap,0);
            FaceDetector detector = FaceDetection.getClient(faceDetectorOptions);

            Task<List<Face>> result = detector.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {

                            Bitmap tempBitmap = Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(),Bitmap.Config.RGB_565);
                            Canvas canvas=new Canvas(tempBitmap);
                            canvas.drawBitmap(mBitmap,0,0,null);

                            for (Face face : faces) {
                                Rect bounds = face.getBoundingBox();
                                float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                // Draws a circle at the position of the detected face, with the face's track id below.
                                float x = face.getBoundingBox().centerX();
                                float y = face.getBoundingBox().centerY();
                                //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, boxPaint);

                                // Calculate positions.
                                float left = x - (face.getBoundingBox().width() / 2.0f);
                                float top = y - (face.getBoundingBox().height() / 2.0f);
                                float right = x + (face.getBoundingBox().width() / 2.0f);
                                float bottom = y + (face.getBoundingBox().height() / 2.0f);
                                RectF rectF =new RectF(left, top, right, bottom);
                                canvas.drawRoundRect(rectF,2,2,boxPaint);

                                // If classification was enabled:
                                if (face.getSmilingProbability() != null) {
                                    float smileProb = face.getSmilingProbability();
                                    Toast.makeText(MainActivity.this, "FaceSmiling"+smileProb, Toast.LENGTH_SHORT).show();
                                canvas.drawText(""+smileProb,x,y,boxPaint);
                                }

                            }
                            imageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "FaceDeteactor not worked", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

        if(v.getId() == R.id.buttonIMG1){
            mBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.image01);
            imageView.setImageBitmap(mBitmap);
        }

        if(v.getId() == R.id.buttonIMG2){
            mBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.image02);
            imageView.setImageBitmap(mBitmap);
        }
        if(v.getId() == R.id.buttonIMG3){
            mBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.image03);
            imageView.setImageBitmap(mBitmap);
        }
    }
}