package no.ntnu.klubbhuset.data.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import lombok.val;
import no.ntnu.klubbhuset.data.Resource;
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
                    res.postValue(Resource.success(image));
                } catch (IOException e) {
                    res.postValue(Resource.error(null, e));
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }
        };
        task.execute();
        return res;
    }

    public void pairImageAndClub(MutableLiveData<Resource<List<Club>>> data) {
        val clubsList = data.getValue().getData();
        if (clubsList != null) {
            for (Club club : clubsList) {
                val images = club.getOrgImages();
                if (images != null) {
                    for (int i = 0; i < images.length; i++) {
                        val image = images[i];
                        getImage(image.getUrl()).observeForever(response -> {
                            if (response.getStatus() == no.ntnu.klubbhuset.data.Status.SUCCESS) {
                                if (data != null) {
                                    image.setImage(response.getData());
                                    data.setValue(Resource.success(clubsList));
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
