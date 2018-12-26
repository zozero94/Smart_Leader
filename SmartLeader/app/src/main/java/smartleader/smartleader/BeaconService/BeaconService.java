package smartleader.smartleader.BeaconService;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

public class BeaconService extends Service {


    static final String TAG = "Beacon";
    BeaconManager beaconManager;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        BeaconManagerSetting();
        setNotification();
        super.onCreate();
    }
    @Override
    public void onDestroy() {
        beaconManager.disconnect();
    }
    private void BeaconManagerSetting(){
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(10000,0);
        beaconManager.setForegroundScanPeriod(10000,0);

        //해당 위치에 들어왔을 때
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.e(TAG,"비콘 감지"+list.get(0).getRssi());
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.e(TAG,"비콘벗어남");
            }
        });
        //거리 탐색 설정
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                for(Beacon beacon : list){
                    Log.e(TAG,"RSSI"+beacon.getRssi());
                }
            }
        });
        //서비스 준비를 위한 콜백 설정
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Region region = new Region("monitored region",UUID.fromString("74278bda-b644-4520-8f0c-720eaf059935"),null,null);
                beaconManager.startMonitoring(region);
                beaconManager.startRanging(region);
            }
        });
    }
    //Notification을 추가함으로써 ForeGround에 Service가 실행되고있음을 보임으로써 Service 종료 방지
    private void setNotification() {
        startForeground(1, new Notification());
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}