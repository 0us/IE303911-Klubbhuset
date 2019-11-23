package no.ntnu.klubbhuset.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Preconditions;

import com.google.android.gms.common.images.Size;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.util.mlkit.CameraSource;

/** Utility class to retrieve shared preferences. */
public class PreferenceUtils {

    private static final String PREF_FILE_AUTH = "auth";
    private static final String PREF_PUBLIC_KEY = "public_key";
    public static final String PREF_NO_FILE_FOUND = "NO_FILE";

    static void saveString(Context context, @StringRes int prefKeyId, @Nullable String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getString(prefKeyId), value)
                .apply();
    }

    @Nullable
    public static CameraSource.SizePair getCameraPreviewSizePair(Context context, int cameraId) {
        Preconditions.checkArgument(
                cameraId == CameraSource.CAMERA_FACING_BACK
                        || cameraId == CameraSource.CAMERA_FACING_FRONT);
        String previewSizePrefKey;
        String pictureSizePrefKey;
        if (cameraId == CameraSource.CAMERA_FACING_BACK) {
            previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size);
            pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size);
        } else {
            previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size);
            pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size);
        }

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return new CameraSource.SizePair(
                    Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)),
                    Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)));
        } catch (Exception e) {
            return null;
        }
    }

    public static FirebaseVisionObjectDetectorOptions getObjectDetectorOptionsForStillImage(
            Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean enableMultipleObjects =
                sharedPreferences.getBoolean(
                        context.getString(
                                R.string.pref_key_still_image_object_detector_enable_multiple_objects),
                        false);

        boolean enableClassification =
                sharedPreferences.getBoolean(
                        context.getString(R.string.pref_key_still_image_object_detector_enable_classification),
                        true);

        FirebaseVisionObjectDetectorOptions.Builder builder =
                new FirebaseVisionObjectDetectorOptions.Builder()
                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE);
        if (enableMultipleObjects) {
            builder.enableMultipleObjects();
        }
        if (enableClassification) {
            builder.enableClassification();
        }
        return builder.build();
    }

    public static FirebaseVisionObjectDetectorOptions getObjectDetectorOptionsForLivePreview(
            Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableMultipleObjects =
                sharedPreferences.getBoolean(
                        context.getString(
                                R.string.pref_key_live_preview_object_detector_enable_multiple_objects),
                        false);
        boolean enableClassification =
                sharedPreferences.getBoolean(
                        context.getString(R.string.pref_key_live_preview_object_detector_enable_classification),
                        true);
        FirebaseVisionObjectDetectorOptions.Builder builder =
                new FirebaseVisionObjectDetectorOptions.Builder()
                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE);
        if (enableMultipleObjects) {
            builder.enableMultipleObjects();
        }
        if (enableClassification) {
            builder.enableClassification();
        }
        return builder.build();
    }

    public static FirebaseVisionFaceDetectorOptions getFaceDetectorOptionsForLivePreview(
            Context context) {
        int landmarkMode =
                getModeTypePreferenceValue(
                        context,
                        R.string.pref_key_live_preview_face_detection_landmark_mode,
                        FirebaseVisionFaceDetectorOptions.NO_LANDMARKS);
        int contourMode =
                getModeTypePreferenceValue(
                        context,
                        R.string.pref_key_live_preview_face_detection_contour_mode,
                        FirebaseVisionFaceDetectorOptions.ALL_CONTOURS);
        int classificationMode =
                getModeTypePreferenceValue(
                        context,
                        R.string.pref_key_live_preview_face_detection_classification_mode,
                        FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS);
        int performanceMode =
                getModeTypePreferenceValue(
                        context,
                        R.string.pref_key_live_preview_face_detection_performance_mode,
                        FirebaseVisionFaceDetectorOptions.FAST);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableFaceTracking =
                sharedPreferences.getBoolean(
                        context.getString(R.string.pref_key_live_preview_face_detection_face_tracking), false);
        float minFaceSize =
                Float.parseFloat(
                        sharedPreferences.getString(
                                context.getString(R.string.pref_key_live_preview_face_detection_min_face_size),
                                "0.1"));

        FirebaseVisionFaceDetectorOptions.Builder optionsBuilder =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setLandmarkMode(landmarkMode)
                        .setContourMode(contourMode)
                        .setClassificationMode(classificationMode)
                        .setPerformanceMode(performanceMode)
                        .setMinFaceSize(minFaceSize);
        if (enableFaceTracking) {
            optionsBuilder.enableTracking();
        }
        return optionsBuilder.build();
    }

    public static String getAutoMLRemoteModelName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String modelNamePrefKey =
                context.getString(R.string.pref_key_live_preview_automl_remote_model_name);
        String defaultModelName = "mlkit_flowers";
        String remoteModelName = sharedPreferences.getString(modelNamePrefKey, defaultModelName);
        if (remoteModelName.isEmpty()) {
            remoteModelName = defaultModelName;
        }
        return remoteModelName;
    }

    public static String getAutoMLRemoteModelChoice(Context context) {
        String modelChoicePrefKey =
                context.getString(R.string.pref_key_live_preview_automl_remote_model_choices);
        String defaultModelChoice = context.getString(R.string.pref_entries_automl_models_local);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(modelChoicePrefKey, defaultModelChoice);
    }

    /**
     * Mode type preference is backed by {@link android.preference.ListPreference} which only support
     * storing its entry value as string type, so we need to retrieve as string and then convert to
     * integer.
     */
    private static int getModeTypePreferenceValue(
            Context context, @StringRes int prefKeyResId, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey = context.getString(prefKeyResId);
        return Integer.parseInt(sharedPreferences.getString(prefKey, String.valueOf(defaultValue)));
    }

    public static boolean isCameraLiveViewportEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
        return sharedPreferences.getBoolean(prefKey, false);
    }

    /**
     * Saves public key to shared preferences
     *
     * @param publicKey The public key to save
     */
    public static void setPublicKey(String publicKey, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_AUTH, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PREF_PUBLIC_KEY, publicKey).apply();
    }

    /**
     * Retrieves the public key from shared preferences, if the public key can't be found
     * {PREF_NO_FILE_FOUND} will be returned.
     *
     * @param context the context
     * @return The public key or {PREF_NO_FILE_FOUND}
     */
    public static String getPublicKey(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_AUTH, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_PUBLIC_KEY, PREF_NO_FILE_FOUND);
    }
}
