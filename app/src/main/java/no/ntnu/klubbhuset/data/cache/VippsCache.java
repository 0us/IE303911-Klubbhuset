package no.ntnu.klubbhuset.data.cache;

import androidx.lifecycle.MutableLiveData;

import lombok.Data;
import lombok.Getter;
import no.ntnu.klubbhuset.data.Resource;

@Data
public class VippsCache {

    @Getter(lazy = true)
    private final MutableLiveData<Resource<String>> vippsToken = new MutableLiveData<>();


    private static volatile VippsCache ourInstance;
    public static VippsCache getInstance() {
        if (ourInstance == null) {
            ourInstance = new VippsCache();
        }
        return ourInstance;
    }
}
