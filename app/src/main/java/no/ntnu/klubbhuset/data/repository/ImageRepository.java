package no.ntnu.klubbhuset.data.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import lombok.val;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.cache.Cache;
import no.ntnu.klubbhuset.data.model.Club;

import static no.ntnu.klubbhuset.util.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.util.CommunicationConfig.IMAGE;

public class ImageRepository {
    private static volatile ImageRepository ourInstance;
    private final String TAG = "ImageRepository";

    public static ImageRepository getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new ImageRepository(context);
        }
        return ourInstance;
    }

    private Context context;
    private RequestQueue requestQueue;
    private final String ENDPOINT = API_URL + IMAGE + "/";

    private Cache cache = Cache.getInstance();

    private ImageRepository(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public MutableLiveData<Resource<Bitmap>> getImage(String path) {
        val url = (ENDPOINT + path).replace("\\", "/");
        Bitmap image = null;
        MutableLiveData res = new MutableLiveData();
        AsyncTask<Object, Object, Bitmap> task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    val url2 = new URL(url);
                    val conn = url2.openConnection();
                    val in = conn.getInputStream();
                    val image = BitmapFactory.decodeStream(in);
                    return image;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }
        };
        task.execute();
        try {
            image = task.get();
            res.setValue(Resource.success(image));
        } catch (ExecutionException | InterruptedException e) {
            res.setValue(Resource.error("failed to load image", null));
            e.printStackTrace();
        }
        return res;
    }

    public void pairImageAndClub(MutableLiveData<Resource<List<Club>>> data, LifecycleOwner owner) {
        val clubsList = data.getValue().getData();
        MediatorLiveData<Bitmap> mediator = new MediatorLiveData<>();
        for (Club club : clubsList) {
            val images = club.getOrgImages();
            for (int i = 0; i < images.length; i++) {
                val image = images[i];
                getImage(image.getUrl()).observe(owner, response -> {
                    if (response.getStatus() == Status.SUCCESS) {
                        if (data != null) {
                            image.setImage(response.getData());
                            data.setValue(Resource.success(clubsList));
                            System.out.println(clubsList);
                        }
                    }
                });
            }
        }
    }
}
