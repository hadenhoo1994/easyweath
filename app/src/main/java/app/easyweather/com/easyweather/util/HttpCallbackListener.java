package app.easyweather.com.easyweather.util;

/**
 * Created by Haden on 2016/6/20.
 */
public interface HttpCallbackListener {
    void onFinish(String response) ;

    void onError(Exception e) ;

}
