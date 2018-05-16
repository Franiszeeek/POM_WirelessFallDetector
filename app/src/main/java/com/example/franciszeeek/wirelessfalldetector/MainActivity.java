package com.example.franciszeeek.wirelessfalldetector;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    //@BindView(R.id.graphView)
    //View graphView;

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
    void OnClick_F4() { Read(); }

    @BindView(R.id.dataValue)
    TextView dataValue;


    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent newIntent = getIntent();
        address = newIntent.getStringExtra(DeviceList.EXTRA_ADDRESS); // Odebranie adresu urządzenia
        new ConnectBT().execute();


        /*
        try {
            InputStream tmpIn = btSocket.getInputStream();
            DataInputStream mmInStream = new DataInputStream(tmpIn);
            dataValue.setText(mmInStream.toString());
        } catch (Exception e) {
            msg("Error");
        }
        */
    }

    private void Read() {
        if (btSocket != null)
        {
            try {
                InputStream tmpIn = btSocket.getInputStream();
                DataInputStream mmInStream = new DataInputStream(tmpIn);
                byte[] buffer = new byte[128];
                int read = 0;
                while ((read = mmInStream.read(buffer, 0, buffer.length)) != -1) {
                    mmInStream.read(buffer);
                    System.out.println("Server says " + new String(buffer));
                    //dataValue.setText( new String (buffer));
                }
/*
                byte[] mmBuffer = new byte[1024];
                int numBytes = mmInStream.read();
                Message readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_READ, numBytes, -1,
                        mmBuffer);
                readMsg.sendToTarget();
*/
                //String tem = new String(mmInStream.readByte())
                //dataValue.setText( new String (mmInStream));
            } catch (IOException e) {
                msg("Error");
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

    private void Disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout
    }

    private void turnOffLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("F".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("T".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
        private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
        {
            private boolean ConnectSuccess = true; //if it's here, it's almost connected

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
            }
            @Override
            protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
            {
                try {
                    if (btSocket == null || !isBtConnected) {
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();//start connection
                    }
                } catch (IOException e) {
                    ConnectSuccess = false;//if the try failed, you can check the exception here
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);

                if (!ConnectSuccess) {
                    msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                    finish();
                } else {
                    msg("Connected.");
                    isBtConnected = true;
                }
                progress.dismiss();
            }
        }
    }

