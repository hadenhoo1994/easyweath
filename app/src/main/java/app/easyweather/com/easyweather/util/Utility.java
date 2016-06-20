package app.easyweather.com.easyweather.util;

import android.text.TextUtils;

import app.easyweather.com.easyweather.model.City;
import app.easyweather.com.easyweather.model.County;
import app.easyweather.com.easyweather.model.EasyWeatherDB;
import app.easyweather.com.easyweather.model.Province;

/**
 * Created by Haden on 2016/6/20.
 */
public class Utility {
    /*
    * 解析和处理服务器返回的省级数据
    * */
    public synchronized static boolean handleProvincesResponse(EasyWeatherDB easyWeatherDB, String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvince = response.split(",");
            if (allProvince != null && allProvince.length > 0){
                for (String p :allProvince){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[0]);
                    //将解析出来的数据存储到Province表
                    easyWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    
    /*
    * 解析和处理服务器返回的市级数据
    * */
    public static boolean handleCitiesResponse(EasyWeatherDB easyWeatherDB, String response, int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length >0){
                for (String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[0]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到city表
                    easyWeatherDB.saveCity(city);
                }return true;
            }
        }return false;
    }
    
    
    
    /*
    * 解析和处理服务器返回的县级数据
    * */
    public static boolean handleCountiesResponse(EasyWeatherDB easyWeatherDB, String response, int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length >0){
                for (String c : allCounties){
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[0]);
                    county.setCityId(provinceId);
                    //将解析出来的数据存储到county表
                    easyWeatherDB.saveCount(county);
                }return true;
            }
        }return false;
    }
}
