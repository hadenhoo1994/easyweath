package app.easyweather.com.easyweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import app.easyweather.com.easyweather.R;
import app.easyweather.com.easyweather.model.City;
import app.easyweather.com.easyweather.model.County;
import app.easyweather.com.easyweather.model.EasyWeatherDB;
import app.easyweather.com.easyweather.model.Province;
import app.easyweather.com.easyweather.util.HttpCallbackListener;
import app.easyweather.com.easyweather.util.HttpUtil;
import app.easyweather.com.easyweather.util.Utility;

/**
 * Created by Haden on 2016/6/20.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private EasyWeatherDB easyWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /*
    * 省列表
    * */

    private  List<Province> provincesList;
    /*
    * 市列表
    * */
    private List<City> cityList;
    /*
    * 县列表
    * */
    private List<County> countyList;
    /*
    * 选中的省份
    * */
    private Province selectedProvince;
    /*
    * 选中的城市
    * */
    private City selectedCity;
    /*
    *当前选中的级别
    * */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        easyWeatherDB = EasyWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provincesList.get(index);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(index);
                    queryCounties();
                }
            }
        });
        queryProvinces();//加载省级数据
    }

    /*
    * 查询全国所有省,优先从数据库查询,如果没有,再到服务器上查询
    * */
    private void queryProvinces(){
        provincesList = easyWeatherDB.loadProvinces();
        if (provincesList.size() >0){
            dataList.clear();
            for (Province province : provincesList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null, "province");
        }
    }


    /*
    * 查选中的省内所有的市,优先从数据库查询,如果没有,再到服务器上查询
    * */
    private void queryCities(){
        cityList = easyWeatherDB.loadCities(selectedProvince.getId());
        if (provincesList.size() >0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /*
    * 查选中的市内所有的县,优先从数据库查询,如果没有,再到服务器上查询
    * */
    private  void queryCounties(){
        countyList = easyWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() >0){
            dataList.clear();;
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();;
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }


    /*
    * 根据传入的代号和类型从服务器上查询省市县数据
    * */
    private void queryFromServer(final String code,final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xlm";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(easyWeatherDB, response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(easyWeatherDB, response ,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountiesResponse(easyWeatherDB,response ,selectedCity.getId());
                }
                if (result){
                    //通过runOnUiThread方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            }else if ("city".equals(type)) {
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread方法回到主线程处理器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    /*
    * 显示进度对话框
    * */
    private void showProgressDialog() {
        if (progressDialog == null){
         progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("loading...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*
    * 关闭进度对话框
    * */
    private void closeProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /*
    * 捕获back按键,根据当前级别判断返回市级列表/省级列表/直接退出
    * */
    @Override
    public   void onBackPressed(){
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }

}
