package com.example.franciszeeek.wirelessfalldetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;

public class DeviceList extends AppCompatActivity {

    @BindView(R.id.listViewDevices)
    ListView listViewDevices;

    @OnClick(R.id.btnSearchDevices)
    void OnClick(){
        pairedDevicesList();
    }

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        ButterKnife.bind(this);

        // Przypisanie bluetooth urządzenia
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        // Wiadomość kiedy urządzenie nie ma bluetooth, zamknięcie aplikacji
        if(myBluetooth == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            finish();
        }
        else if(!myBluetooth.isEnabled()) // Włącz bluetooth
        {
            Intent turnBTon = new Intent(ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

    }

    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                // Uzyskanie nazwę urządzenia i adres
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        listViewDevices.setAdapter(adapter);
        listViewDevices.setOnItemClickListener(myListClickListener); // Metoda wywoływana po kliknięciu urządzenia z listy
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            Intent intent = new Intent(DeviceList.this, MainActivity.class);
            intent.putExtra(EXTRA_ADDRESS, address);
            startActivity(intent);
        }
    };
}

