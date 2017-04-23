package com.example.sami.fyp16;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter bluetooth;
    private String myName;
    private String myaddr;
    private BluetoothSocket transferSocket;
    private boolean listening;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        bluetooth = BluetoothAdapter.getDefaultAdapter();

    }
    private void enable_bluetooth(){
        startActivityForResult(
                new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
        myName  = bluetooth.getName();
        myaddr = bluetooth.getAddress();
    }
    private void allow_discovery(){
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }
    private UUID startServerSocket(BluetoothAdapter bluetooth) {
        Log.d("Server","Server_Started");
        UUID uuid = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
        String name = "bluetoothserver";
        try {
            final BluetoothServerSocket btserver =
                    bluetooth.listenUsingRfcommWithServiceRecord(name, uuid);
            Thread acceptThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        // Block until client connection established.
                        BluetoothSocket serverSocket = btserver.accept();
                        // Start listening for messages.
                        listenForMessages(serverSocket);
                        // Add a reference to the socket used to send messages.
                        transferSocket = serverSocket;
                    } catch (IOException e) {
                        Log.e("BLUETOOTH", "Server connection IO Exception", e);
                    }
                }
            });
            acceptThread.start();
            Log.d("Server","SocketCreated");
        } catch (IOException e) {
            Log.e("BLUETOOTH", "Socket listener IO Exception", e);
        }
        return uuid;
    }

    public void play_bluetooth(View v){

        if(!bluetooth.isEnabled()) {
            enable_bluetooth();
        }
        allow_discovery();
        listening =true;
        startServerSocket(bluetooth);

    }
    private void listenForMessages(BluetoothSocket socket) {
        listening = true;
        int bufferSize = 4096;
        byte[] buffer = new byte[bufferSize];
        try {
            InputStream instream = socket.getInputStream();

            int bytesRead = -1;
            while (listening) {
                bytesRead = instream.read(buffer);
                if (bytesRead != -1) {
                    String result = "";
                    Log.d("Server","Listening");
                    while ((bytesRead == bufferSize) &&
                            (buffer[bufferSize-1] != 0)){
                        result = result + new String(buffer, 0, bytesRead - 1);
                        bytesRead = instream.read(buffer);
                    }
                    result = result + new String(buffer, 0, bytesRead - 1);
                    Log.d("New_result",result);
                }
                //socket.close();
            }
        } catch (IOException e) {
            Log.d("Error","Message Recieve failed");
        }
        finally {
        }
    }
}
