package com.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.tmap.engine.navigation.SDKManager;
import com.skt.tmap.engine.navigation.network.ndds.CarOilType;
import com.skt.tmap.engine.navigation.network.ndds.TollCarType;
import com.skt.tmap.engine.navigation.network.ndds.dto.request.TruckType;
import com.skt.tmap.engine.navigation.route.RoutePlanType;
import com.skt.tmap.engine.navigation.route.data.MapPoint;
import com.skt.tmap.engine.navigation.route.data.WayPoint;
import com.skt.tmap.vsm.coordinates.VSMCoordinates;
import com.skt.tmap.vsm.data.VSMMapPoint;
import com.skt.tmap.vsm.map.MapEngine;
import com.skt.tmap.vsm.map.marker.MarkerImage;
import com.skt.tmap.vsm.map.marker.VSMMarkerBase;
import com.skt.tmap.vsm.map.marker.VSMMarkerManager;
import com.skt.tmap.vsm.map.marker.VSMMarkerPoint;
import com.tmapmobility.tmap.tmapsdk.ui.data.CarOption;
import com.tmapmobility.tmap.tmapsdk.ui.data.MapSetting;
import com.tmapmobility.tmap.tmapsdk.ui.data.TruckInfoKey;
import com.tmapmobility.tmap.tmapsdk.ui.fragment.NavigationFragment;
import com.tmapmobility.tmap.tmapsdk.ui.util.TmapUISDK;
import com.tmapmobility.tmap.tmapsdk.ui.view.MapConstant;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
//import androidx.lifecycle.Observer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TMapUISDKSample";

    private final static String CLIENT_ID = "";
    private final static String API_KEY = "";//발급받은 KEY
    private final static String USER_KEY = "";


    private NavigationFragment navigationFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private Button button1, button2, button3, button4, button5, button6, button7;
    private Button stopButton;

    private LinearLayout buttonLayout;

    boolean isEDC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

    }

    private void checkPermission() {

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initUI();
            initUISDK();
        } else {
            String[] permissionArr = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissionArr, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initUI();
            initUISDK();
        } else {
            Toast.makeText(this, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initUI() {
        fragmentManager = getSupportFragmentManager();

        navigationFragment = TmapUISDK.Companion.getFragment();


        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.tmapUILayout, navigationFragment);
        transaction.commitAllowingStateLoss();

        buttonLayout = findViewById(R.id.buttonLayout);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);

        button1.setOnClickListener(onClickButton);
        button2.setOnClickListener(onClickButton);
        button3.setOnClickListener(onClickButton);
        button4.setOnClickListener(onClickButton);
        button5.setOnClickListener(onClickButton);
        button6.setOnClickListener(onClickButton);
        button7.setOnClickListener(onClickButton);


        stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(onClickButton);
        stopButton.setVisibility(View.GONE);

        isEDC = false;
        //네비게이션 상태 변경 시 callback
        navigationFragment.setDrivingStatusCallback(new TmapUISDK.DrivingStatusCallback() {

            @Override
            public void onUserRerouteComplete() {
                // 사용자 재탐색 동작 완료 시 호출
                Log.e(TAG, "onUserRerouteComplete");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onUserRerouteComplete", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onStopNavigation() {
                // 네비게이션 종료 시 호출
                buttonLayout.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                Log.e(TAG, "onStopNavigation");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onStopNavigation", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onStartNavigation() {

                // 네비게이션 시작 시 호출
                Log.e(TAG, "onStartNavigation");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onStartNavigation", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onRouteChanged(int i) {
                // 경로 변경 완료 시 호출
                Log.e(TAG, "onRouteChanged " + i);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onRouteChanged", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onPermissionDenied(int i, @Nullable String s) {
                // 권한 에러 발생 시 호출
                Log.e(TAG, "onPermissionDenied " + i + "::" + s);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onPermissionDenied", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onPeriodicRerouteComplete() {
                // 정주기 재탐색 동작 완료 시 호출
                Log.e(TAG, "onPeriodicRerouteComplete");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onPeriodicRerouteComplete", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onPeriodicReroute() {
                // 정주기 재탐색 발생 시점에 호출
                Log.e(TAG, "onPeriodicReroute");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onPeriodicReroute", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onPassedViaPoint() {
                // 경유지 통과 시 호출
                Log.e(TAG, "onPassedViaPoint");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onPassedViaPoint", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onPassedTollgate(int i) {
                // 톨게이트 통과 시 호출
                // i 요금

                Log.e(TAG, "onPassedTollgate " + i);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onPassedTollgate", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onPassedAlternativeRouteJunction() {
                // 대안 경로 통과 시 호출
                Log.e(TAG, "onPassedAlternativeRouteJunction");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onPassedAlternativeRouteJunction", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onNoLocationSignal(boolean b) {
                // GPS 상태 변화 시점에 호출
                Log.e(TAG, "onPassedAlternativeRouteJunction :: " + b);

                runOnUiThread(() -> Toast.makeText(MainActivity.this, "onNoLocationSignal " + b, Toast.LENGTH_SHORT).show());


            }

            @Override
            public void onLocationChanged() {
                //위치 갱신 때 마다 호출
                //Log.e(TAG, "onLocationChanged");
            }

            @Override
            public void onFailRouteRequest(@NonNull String errorCode, @NonNull String errorMsg) {
                //경로 탐색 실패 시 호출
                Log.e(TAG, "onFailRouteRequest " + errorCode + "::" + errorMsg);

                runOnUiThread(() -> Toast.makeText(MainActivity.this, "onFailRouteRequest", Toast.LENGTH_SHORT).show());

            }

            @Override
            public void onDoNotRerouteToDestinationComplete() {
                //미리 종료 안내 동작 탐색 완료 시점에 호출
                Log.e(TAG, "onDoNotRerouteToDestinationComplete");
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "onDoNotRerouteToDestinationComplete", Toast.LENGTH_SHORT).show());


            }

            @Override
            public void onDestinationDirResearchComplete() {
                // 건너편 안내 동작 탐색 완료 시점에 호출
                Log.e(TAG, "onDestinationDirResearchComplete");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onDestinationDirResearchComplete", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onChangeRouteOptionComplete(@NonNull RoutePlanType routePlanType) {
                // 경로 옵션 변경 완료 시 호출
                Log.e(TAG, "onChangeRouteOptionComplete");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onChangeRouteOptionComplete", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onBreakawayFromRouteEvent() {
                // 경로 이탈 재탐색 발생 시점에 호출
                Log.e(TAG, "onBreakawayFromRouteEvent");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onBreakawayFromRouteEvent", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onBreakAwayRequestComplete() {
                //경로 이탈 재탐색 동작 완료 시점에 호출
                Log.e(TAG, "onBreakAwayRequestComplete");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onBreakAwayRequestComplete", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onArrivedDestination(@NonNull String dest, int drivingTime, int drivingDistance) {
                // 목적지 도착 시 호출
                // dest 목적지 명
                // drivingTime 운전시간
                // drivingDistance 운전거리

                Log.e(TAG, "onArrivedDestination");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onArrivedDestination", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onApproachingViaPoint() {
                // 경유지 접근 시점에 호출 (1km 이내)
                Log.e(TAG, "onApproachingViaPoint");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onApproachingViaPoint", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onApproachingAlternativeRoute() {
                // 대안 경로 접근 시 호출
                Log.e(TAG, "onApproachingAlternativeRoute");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onApproachingAlternativeRoute", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onForceReroute(@NonNull com.skt.tmap.engine.navigation.network.ndds.NddsDataType.DestSearchFlag destSearchFlag) {
                // 경로 재탐색 발생 시점에 호출
                Log.e(TAG, "onForceReroute");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onForceReroute", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    private final View.OnClickListener onClickButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.equals(button1)) {
                clickButton1();
            } else if (v.equals(button2)) {
                clickButton2();
            } else if (v.equals(button3)) {
                clickButton3();
            } else if (v.equals(button4)) {
                clickButton4();
            } else if (v.equals(button5)) {
                clickButton5();
            } else if (v.equals(button6)) {
                clickButton6();
            } else if (v.equals(button7)) {
                Toast.makeText(MainActivity.this, "현재 이 기능은 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                clickButton7();
            } else if (v.equals(stopButton)) {
                navigationFragment.stopDrive();
            }
        }
    };

    /**
     * 안전운행
     */
    private void clickButton1() {
        buttonLayout.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        navigationFragment.startSafeDrive();
    }

    /**
     * 경로안내
     */
    private void clickButton2() {

        buttonLayout.setVisibility(View.GONE);

        CarOption carOption = new CarOption();
        carOption.setCarType(TollCarType.Car);
        carOption.setOilType(CarOilType.PremiumGasoline);
        carOption.setHipassOn(true);

        //현재 위치
        Location currentLocation = SDKManager.getInstance().getCurrentPosition();
        String currentName = VSMCoordinates.getAddressOffline(currentLocation.getLongitude(), currentLocation.getLatitude());


        WayPoint startPoint = new WayPoint(currentName, new MapPoint(currentLocation.getLongitude(), currentLocation.getLatitude()));

        //목적지
        WayPoint endPoint = new WayPoint("강남역", new MapPoint(127.027813, 37.497999));
        //WayPoint endPoint = new WayPoint("판교역", new MapPoint(127.101869, 37.404611));

        navigationFragment.setCarOption(carOption);


        ArrayList<RoutePlanType> planTypeList = new ArrayList<>();
        planTypeList.add(RoutePlanType.Traffic_Recommend);
        planTypeList.add(RoutePlanType.Traffic_Free);

        navigationFragment.requestRoute(startPoint, null, endPoint, false, new TmapUISDK.RouteRequestListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "requestRoute Success");
                stopButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFail(int i, @Nullable String s) {
                Toast.makeText(MainActivity.this, i + "::" + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFail " + i + " :: " + s);
            }
        }, planTypeList);

    }

    /**
     * 경유지 추가 경로안내
     */
    private void clickButton3() {
        buttonLayout.setVisibility(View.GONE);

        //자동차 옵션 설정
        CarOption carOption = new CarOption();
        carOption.setCarType(TollCarType.Car);
        carOption.setOilType(CarOilType.Gasoline);
        carOption.setHipassOn(true);

        //현재 위치
        Location currentLocation = SDKManager.getInstance().getCurrentPosition();
        String currentName = VSMCoordinates.getAddressOffline(currentLocation.getLongitude(), currentLocation.getLatitude());
        WayPoint startPoint = new WayPoint(currentName, new MapPoint(currentLocation.getLongitude(), currentLocation.getLatitude()));

        //경유지 설정
        ArrayList<WayPoint> passList = new ArrayList<>();

        /*
        WayPoint pass1 = new WayPoint("가", new MapPoint(currentLocation.getLongitude() + 0.001, currentLocation.getLatitude() - 0.001));
        passList.add(pass1);

        WayPoint pass2 = new WayPoint("나", new MapPoint(currentLocation.getLongitude() + 0.002, currentLocation.getLatitude() - 0.002));
        passList.add(pass2);

        WayPoint pass3 = new WayPoint("다", new MapPoint(currentLocation.getLongitude() + 0.003, currentLocation.getLatitude() - 0.003));
        passList.add(pass3);
         */

        WayPoint pass1 = new WayPoint("가", new MapPoint(126.9628, 37.5382));
        passList.add(pass1);

        WayPoint pass2 = new WayPoint("나", new MapPoint(126.9385, 37.5199));
        passList.add(pass2);

        WayPoint pass3 = new WayPoint("다", new MapPoint(126.9620, 37.5223));
        passList.add(pass3);

        //목적지
        WayPoint endPoint = new WayPoint("강남역", new MapPoint(127.027813, 37.497999), "280181", (byte) 5);


        navigationFragment.setCarOption(carOption);

        ArrayList<RoutePlanType> planTypeList = new ArrayList<>();
        planTypeList.add(RoutePlanType.Traffic_Recommend);
        planTypeList.add(RoutePlanType.Traffic_Free);

        navigationFragment.requestRoute(startPoint, passList, endPoint, false, new TmapUISDK.RouteRequestListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "requestRoute Success");
                stopButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFail(int i, @Nullable String s) {
                Toast.makeText(MainActivity.this, i + "::" + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFail " + i + " :: " + s);
            }
        }, planTypeList);
    }

    private void clickButton4() {
        buttonLayout.setVisibility(View.GONE);

        CarOption carOption = new CarOption();
        carOption.setCarType(TollCarType.Car);
        carOption.setOilType(CarOilType.PremiumGasoline);
        carOption.setHipassOn(true);

        //트럭 경로 요청하기 위한 추가 정보
        HashMap<String, String> truckDetailInfo = new HashMap<>();
        truckDetailInfo.put(TruckInfoKey.TruckType.getValue(), TruckType.Truck.toString());

        truckDetailInfo.put(TruckInfoKey.TruckWeight.getValue(), "2000.0"); // 단위 kg 화물의 무게
        truckDetailInfo.put(TruckInfoKey.TruckHeight.getValue(), "400.0"); // 단위 cm 화물차 높이
        carOption.setTruckInfo(truckDetailInfo);

        //현재 위치
        Location currentLocation = SDKManager.getInstance().getCurrentPosition();
        String currentName = VSMCoordinates.getAddressOffline(currentLocation.getLongitude(), currentLocation.getLatitude());

        WayPoint startPoint = new WayPoint(currentName, new MapPoint(currentLocation.getLongitude(), currentLocation.getLatitude()));

        //목적지
        WayPoint endPoint = new WayPoint("강남역", new MapPoint(127.027813, 37.497999), "280181", (byte) 5);

        navigationFragment.setCarOption(carOption);

        ArrayList<RoutePlanType> planTypeList = new ArrayList<>();
        planTypeList.add(RoutePlanType.Traffic_Recommend);
        planTypeList.add(RoutePlanType.Traffic_Free);

        //길안내 바로 시작
        navigationFragment.requestRoute(startPoint, null, endPoint, true, new TmapUISDK.RouteRequestListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "requestRoute Success");
                stopButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFail(int i, @Nullable String s) {
                Toast.makeText(MainActivity.this, i + "::" + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFail " + i + " :: " + s);
            }
        }, planTypeList);
    }

    /**
     * 경유지 맥스
     */
    private void clickButton5() {
        buttonLayout.setVisibility(View.GONE);

        CarOption carOption = new CarOption();
        carOption.setCarType(TollCarType.Car);
        carOption.setOilType(CarOilType.PremiumGasoline);
        carOption.setHipassOn(true);

        //현재 위치
        Location currentLocation = SDKManager.getInstance().getCurrentPosition();
        String currentName = VSMCoordinates.getAddressOffline(currentLocation.getLongitude(), currentLocation.getLatitude());

        WayPoint startPoint = new WayPoint(currentName, new MapPoint(currentLocation.getLongitude(), currentLocation.getLatitude()));

        //목적지
        WayPoint endPoint = new WayPoint("강남역", new MapPoint(127.027813, 37.497999), "280181", (byte) 5);

        navigationFragment.setCarOption(carOption);

        ArrayList<RoutePlanType> planTypeList = new ArrayList<>();
        planTypeList.add(RoutePlanType.Traffic_Recommend);
        planTypeList.add(RoutePlanType.Traffic_Free);

        //길안내 시작
        navigationFragment.requestRoute(startPoint, TestWayPoint.point, endPoint, true, new TmapUISDK.RouteRequestListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "requestRoute Success");
                stopButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFail(int i, @Nullable String s) {
                Toast.makeText(MainActivity.this, i + "::" + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFail " + i + " :: " + s);
            }
        }, planTypeList);
    }


    private void clickButton6() {

        String markerID = "TEST";
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.poi);
        VSMMarkerPoint marker = new VSMMarkerPoint(markerID);

        marker.setIcon(MarkerImage.fromBitmap(icon));
        marker.setShowPriority(MapConstant.MarkerRenderingPriority.DEFAULT_PRIORITY);
        marker.setText("TEST");

        //현재 위치 보다 조금 옆에 마커를 찍는다.
        VSMMapPoint position = new VSMMapPoint(SDKManager.getInstance().getCurrentPosition().getLongitude() - 0.0002, SDKManager.getInstance().getCurrentPosition().getLatitude() + 0.0002);
        marker.setPosition(position);

        VSMMarkerManager markerManager = navigationFragment.getMapView().getMarkerManager();
        if (markerManager == null) {
            Log.e(TAG, "마커 매니저 NULL");
            return;
        }

        markerManager.removeMarker(markerID);
        markerManager.addMarker(marker);

        navigationFragment.setHitEventListener(
                new MapEngine.OnHitObjectListener() {
                    @Override
                    public boolean OnHitObjectPOI(String s, int i, VSMMapPoint vsmMapPoint, Bundle bundle) {
                        return false;
                    }

                    @Override
                    public boolean OnHitObjectMarker(VSMMarkerBase vsmMarkerBase, Bundle bundle) {
                        return false;
                    }

                    @Override
                    public boolean OnHitObjectOilInfo(String s, int i, VSMMapPoint vsmMapPoint) {
                        return false;
                    }

                    @Override
                    public boolean OnHitObjectTraffic(String s, int i, String s1, String s2, String s3, VSMMapPoint vsmMapPoint) {
                        return false;
                    }

                    @Override
                    public boolean OnHitObjectCctv(String s, int i, VSMMapPoint vsmMapPoint, Bundle bundle) {
                        return false;
                    }

                    @Override
                    public boolean OnHitObjectAlternativeRoute(String s, VSMMapPoint vsmMapPoint) {
                        return false;
                    }

                    @Override
                    public boolean OnHitObjectRouteFlag(String s, int i, VSMMapPoint vsmMapPoint) {
                        return false;
                    }

                    @Override
                    public boolean OnHitObjectRouteLine(String s, int i, VSMMapPoint vsmMapPoint) {
                        return false;
                    }

                    @Override
                    public boolean OnHitObjectNone(VSMMapPoint vsmMapPoint) {
                        return false;
                    }
                },

                new MapEngine.OnHitCalloutPopupListener() {
                    @Override
                    public void OnHitCalloutPopupPOI(String s, int i, VSMMapPoint vsmMapPoint, Bundle bundle) {

                    }

                    @Override
                    public void OnHitCalloutPopupMarker(VSMMarkerBase vsmMarkerBase) {

                    }

                    @Override
                    public void OnHitCalloutPopupTraffic(String s, int i, String s1, String s2, String s3, VSMMapPoint vsmMapPoint) {

                    }

                    @Override
                    public void OnHitCalloutPopupCctv(String s, int i, VSMMapPoint vsmMapPoint, Bundle bundle) {

                    }

                    @Override
                    public void OnHitCalloutPopupUserDefine(String s, int i, VSMMapPoint vsmMapPoint) {

                    }
                });
    }


    private void clickButton7() {
        subscribeEDCData();
    }


//    private Observer<Bundle> edcListener = new Observer<Bundle>() {
//        @Override
//        public void onChanged(Bundle bundle) {
//            if (bundle != null) {
//                Log.e(TAG, bundle.toString());
//            }
//        }
//    };

    private void subscribeEDCData() {
        //현재 이 기능은 지원X

        /*
        if (isEDC) {
            isEDC = false;
            TmapUISDK.Companion.getObservableEDCData().removeObserver(edcListener);
            button7.setText("EDC 수신 등록");
        } else {
            isEDC = true;
            TmapUISDK.Companion.getObservableEDCData().observe(this, edcListener);
            button7.setText("EDC 수신 해제");
            TmapUISDK.Companion.isInitialized();
        }
         */
    }


    private void initUISDK() {
        TmapUISDK.Companion.initialize(this, CLIENT_ID, API_KEY, USER_KEY, new TmapUISDK.InitializeListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "success initialize");
            }

            @Override
            public void onFail(int i, @Nullable String s) {
                Toast.makeText(MainActivity.this, i + "::" + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFail " + i + " :: " + s);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (!navigationFragment.onBackKeyPressed()) {
            buttonLayout.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.GONE);
        }
    }
}