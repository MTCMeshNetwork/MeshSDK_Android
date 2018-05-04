package io.mesh.sdk.demo;

import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import io.mesh.sdk.bluetooth.MessageManager;
import io.mesh.sdk.bluetooth.scan.ScanMessage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_MESH_SDK_PERMISSION = 100;

    private Button btnMeshOpen;
    private EditText editContent;
    private Button btnSend;
    private ListView listView;
    private Button btnClearRecv;
    private MessageManager mm;

    private LinkedList<String> recvContentList = new LinkedList<>();
    private ArrayAdapter<String> recvContentAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMeshOpen = (Button) findViewById(R.id.btn_mesh);
        btnSend = (Button) findViewById(R.id.btn_send);
        editContent = (EditText) findViewById(R.id.edit_content);
        listView = (ListView) findViewById(R.id.listView);
        btnClearRecv = (Button) findViewById(R.id.btn_clear_recv);

        btnMeshOpen.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnClearRecv.setOnClickListener(this);
        mm = new MessageManager(onMessageListener);

        recvContentAdapter = new ArrayAdapter<>(this, R.layout.item_recv_content, recvContentList);
        listView.setAdapter(recvContentAdapter);

        if (checkNeedRequestPermission()) {
            return;
        }

        checkNeedOpenBluetooth();

    }

    private boolean checkNeedOpenBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            return true;
        }
        return false;
    }

    private boolean checkNeedRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断当前系统的SDK版本是否大于23
            List<String> permissionNeedRequest = new LinkedList<>();
            for (String permssion: permissionsNeedCheck) {
                if(ActivityCompat.checkSelfPermission(this, permssion) != PackageManager.PERMISSION_GRANTED) {
                    permissionNeedRequest.add(permssion);
                }
            }
            if (permissionNeedRequest.isEmpty()) {
                return false;
            }

            ActivityCompat.requestPermissions(this, permissionNeedRequest.toArray(new String[0]), REQUEST_CODE_MESH_SDK_PERMISSION);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在requestPermissions时传入
            case REQUEST_CODE_MESH_SDK_PERMISSION:
                boolean isAllGrant = true;
                for (int grantResult: grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isAllGrant = false;
                        break;
                    }
                }
                if (!isAllGrant) {
                    Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动前往设置开启", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkNeedOpenBluetooth();

                break;
            default:
                break;
        }
    }

    private static final List<String> permissionsNeedCheck;
    static {
        permissionsNeedCheck = new LinkedList<>();
        permissionsNeedCheck.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsNeedCheck.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mesh: {
                if (!mm.isStart()) {
                    mm.start();
                    btnSend.setEnabled(true);
                } else {
                    mm.stop();
                    btnSend.setEnabled(false);
                }
                break;
            }

            case R.id.btn_send: {
                String content = editContent.getText().toString();
                mm.startAdvertising(content.getBytes());
                break;
            }

            case R.id.btn_clear_recv: {
                recvContentList.clear();
                recvContentAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private MessageManager.OnMessageListener onMessageListener = new MessageManager.OnMessageListener() {
        @Override
        public void onMessage(final ScanMessage message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder(255);
                    sb.append(sdf.format(Calendar.getInstance().getTime()));
                    sb.append("\n");
                    sb.append(message.address);
                    sb.append("\n");
                    sb.append(new String(message.data));
                    recvContentList.addFirst(sb.toString());

                    while (recvContentList.size() > 10) {
                        recvContentList.removeLast();
                    }

                    recvContentAdapter.notifyDataSetChanged();


                }
            });
        }
    };

}
