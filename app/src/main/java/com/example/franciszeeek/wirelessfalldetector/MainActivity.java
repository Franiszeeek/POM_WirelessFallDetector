package com.example.franciszeeek.wirelessfalldetector;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @OnClick(R.id.functionality1)
    void OnClick_ON() {
        turnOnLed();
    }

    @OnClick(R.id.functionality2)
    void OnClick_OFF() {
        turnOffLed();
    }

    @OnClick(R.id.functionality3)
    void OnClick_DIS() {
        Disconnect();
    }

    @OnClick(R.id.functionality4)
    void OnClick_F4() {
        Read();
    }

    @BindView(R.id.dataValue)
    TextView dataValue;

    @BindView(R.id.data)
    TextView datadata;

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "MY_APP_DEBUG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent newIntent = getIntent();
        address = newIntent.getStringExtra(DeviceList.EXTRA_ADDRESS);
        new ConnectBT().execute();
    }
    private void Read() {
        if (btSocket != null)
        {
            try {
                InputStream tmpIn = btSocket.getInputStream();
                DataInputStream mmInStream = new DataInputStream(tmpIn);

                byte[] buffer = new byte[64];
                int read = 0;
                read = mmInStream.read(buffer, 0, buffer.length);
                mmInStream.read(buffer);
                String data = new String(buffer);
                datadata.setText(data);

            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
        }
    }

    private void Disconnect() {
        if (btSocket != null)
        {
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
        }
        finish();
    }

    private void turnOffLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("F".toString().getBytes());
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
        }
    }

    private void turnOnLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("T".toString().getBytes());
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
        }
    }
        private class ConnectBT extends AsyncTask<Void, Void, Void> {
            private boolean ConnectSuccess = true;

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");
            }
            @Override
            protected Void doInBackground(Void... devices) {
                try {
                    if (btSocket == null || !isBtConnected) {
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();
                    }
                } catch (IOException e) {
                    ConnectSuccess = false;
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (!ConnectSuccess) {
                    Toast.makeText(getApplicationContext(), "Connection Failed. Is it a SPP Bluetooth? Try again.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_LONG).show();
                    isBtConnected = true;
                }
                progress.dismiss();
            }
        }
    }

/*
        InputStream tmpIn = null;
        try {
            tmpIn = btSocket.getInputStream();
        } catch (IOException e) {
            msg("Error");
        }
        DataInputStream mmInStream = new DataInputStream(tmpIn);
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.
                Message readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_READ, numBytes, -1,
                        mmBuffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
            mmInStream = (DataInputStream) tmpIn;
            dataValue.setText(mmInStream.toString());

        }
        */