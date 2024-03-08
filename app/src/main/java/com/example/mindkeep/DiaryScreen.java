package com.example.mindkeep;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.core.content.ContextCompat;

public class DiaryScreen extends AppCompatActivity {

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;
    TextView mood_slider_value;
    Slider mood_rating_slider;

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_screen);


        mood_rating_slider = findViewById(R.id.mood_rating_slider);
        mood_slider_value = findViewById(R.id.mood_slider_value);


        mood_rating_slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                mood_slider_value.setText(Float.toString(value));
            }
        });

        TextView calendartext = findViewById(R.id.calendartext);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy ", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        calendartext.setText(currentDate);

        ImageButton saveButton = findViewById(R.id.confirm_button);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                saveDiaryEntry();
            }


        });
        ImageButton captureButton = findViewById(R.id.btnCapture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(DiaryScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DiaryScreen.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    openCamera(); // Implement this method based on your camera opening logic
                }
            }
        });
    }

    private void saveDiaryEntry() {
        EditText diaryContentEditText = findViewById(R.id.diaryinput);
        String diaryContent = diaryContentEditText.getText().toString();
        float moodRating = mood_rating_slider.getValue();
        int moodRatingAsInt = Math.round(moodRating);


        Map<String, Object> diaryEntry = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(new Date());
        diaryEntry.put("date", dateString);
        diaryEntry.put("content", diaryContent);
        diaryEntry.put("mood", moodRatingAsInt);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("diaryEntries")
                .add(diaryEntry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("DiaryActivity", "Diary entry added with ID: " + documentReference.getId());
                        Toast.makeText(DiaryScreen.this, "Entry saved!", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DiaryActivity", "Error adding diary entry", e);
                        Toast.makeText(DiaryScreen.this, "Save failed!", Toast.LENGTH_SHORT).show();
                    }
                });


        textureView = findViewById(R.id.textureView);
        ImageButton captureButton = findViewById(R.id.btnCapture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DiaryScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DiaryScreen.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    openCamera();
                }
            }
        });
    }
    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {


        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            // TextureView is available, perform camera setup here
            setupCamera(surface, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Handle surface texture size change if needed
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // Handle surface texture destruction if needed
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // Handle surface texture update if needed
        }
    };
    // Replace with this new method
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[0]; // Assuming you want the first camera
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, you can ask for permission here or beforehand
                return; // Do not proceed further without the permission
            }

            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;

                    // Here, instead of createCameraPreviewSession, we will call createCameraPreview
                    // Assuming that textureView is already initialized and available
                    if (textureView.isAvailable()) {
                        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
                        assert surfaceTexture != null;
                        surfaceTexture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
                        Surface surface = new Surface(surfaceTexture);
                        createCameraPreview(surface); // Call your existing createCameraPreview method
                    }
                    // If textureView isn't available yet, we set the SurfaceTextureListener so that
                    // we can start the preview as soon as the view becomes available.
                    else {
                        textureView.setSurfaceTextureListener(textureListener);
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    cameraDevice = null;
                    // Handle the error, for example, by showing a Toast or logging it
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    private void setupCamera(SurfaceTexture surfaceTexture, int width, int height) {
        // Adjusted setupCamera method without redundant permission checks
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                // Check if this camera is the one we want to use
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue; // Skip if it's a front facing camera.
                }
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }
                // Assume the size and format are suitable for your use case
                Size imageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), width, height);
                imageReader = ImageReader.newInstance(imageSize.getWidth(), imageSize.getHeight(), ImageFormat.JPEG, 1);
                // Your existing camera setup logic...
                openCamera();
                break; // Break out of the loop once camera is opened
            }
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private Size chooseOptimalSize(Size[] choices, int width, int height) {
        // Add your logic to choose the best size based on your requirements
        // For simplicity, just return the first available size
        return choices[0];
    }

    private void createCameraPreview(Surface surface) {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                CaptureRequest request = builder.build();
                                cameraCaptureSession = session; // Assign the created session to cameraCaptureSession
                                session.setRepeatingRequest(request, null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            // Handle configuration failure
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureImage() {
        if (cameraCaptureSession != null) {
            try {
                // Create a CaptureRequest.Builder for still capture
                CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(imageReader.getSurface());

                // Configure the capture request with appropriate settings (e.g., auto focus, flash, etc.)
                captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                // Determine the rotation of the captured image
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation(rotation));

                // Capture the image
                cameraCaptureSession.capture(captureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        // Image captured, handle the captured image if needed
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Camera session not initialized", Toast.LENGTH_SHORT).show();
        }
    }


    private int getJpegOrientation(int rotation) {
        CameraCharacteristics characteristics;
        try {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = manager.getCameraIdList()[0];
            characteristics = manager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return 0;
        }

        int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        int deviceOrientation = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                deviceOrientation = 0;
                break;
            case Surface.ROTATION_90:
                deviceOrientation = 90;
                break;
            case Surface.ROTATION_180:
                deviceOrientation = 180;
                break;
            case Surface.ROTATION_270:
                deviceOrientation = 270;
                break;
        }

        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;
        return jpegOrientation;
    }

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            // Get the captured image from the ImageReader
            Image image = reader.acquireLatestImage();
            if (image != null) {
                // Process the image data (e.g., save it to storage)
                // Example: Save the image to the Downloads folder
                String fileName = "captured_image.jpg";
                File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File imageFile = new File(downloadsDirectory, fileName);
                try (FileOutputStream output = new FileOutputStream(imageFile)) {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    output.write(bytes);
                    Toast.makeText(DiaryScreen.this, "Image saved to Downloads folder", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    image.close();
                }
            }
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, setup the camera
                setupCamera(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
            } else {
                // Permission was denied. Handle the failure to have permission.
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
