package com.example.jsampaio.veloguiaversao2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class CalibrarSensor extends AppCompatActivity {

    protected static final String TAG = "location-updates-sample";



    /*
      graphico
   */
    private LineChart mHeartRateChart;

    private Queue<Entry> mReading;


    public BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;


    public Handler mHandler; // Our main handler that will receive callback notifications
    public ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    public BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    //Nome do meu dispositivo
    private static final String HTC_MEDIA = "HC-05";

    /*
    Variável indicadora:
        0 recebe nenhum dado
        1 Recebe dados da média dos batimentos
        2 recebe dados de calibração

     */

    protected int indicadorDeTipoDeDadosRecebidos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrar_sensor);


        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        mBTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mHeartRateChart = (LineChart)findViewById(R.id.heartRateChart);

        startThread();
        dadosBluetooth();


    }


    /*********************************************************************************
     * Conexões com o Bluetooth
     */

    //CRIA A THREAD QUE FAZ A CONEXÃO ENTRE OS DISPOSITIVOS ATRAVÉS DE UM SOCKET

    public void startThread()
    {
        // Spawn a new thread to avoid blocking the GUI one
        new Thread()
        {

            public void run() {
                Log.d("Cheguei aqui", "Cheguei aqui");
                boolean fail = false;

                BluetoothDevice findDevice = findBondedDeviceByName(mBTAdapter, "HC-05");

                BluetoothDevice device = mBTAdapter.getRemoteDevice(findDevice.getAddress());
                Log.d("Cheguei aqui", "Cheguei aqui");
                try {
                    mBTSocket = createBluetoothSocket(device);
                    Log.d("Cheguei aqui", "Cheguei aqui");
                } catch (IOException e) {
                    fail = true;
                    Log.d("Cheguei aqui", "Cheguei aqui");
                    Toast.makeText(getBaseContext(), "Falha na criação do Socket", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    mBTSocket.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        mBTSocket.close();
                        mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Falha na criação do Socket", Toast.LENGTH_SHORT).show();
                    }
                }
                if(fail == false) {
                    mConnectedThread = new CalibrarSensor.ConnectedThread(mBTSocket);
                    mConnectedThread.start();
                    mConnectedThread.write("<instant>\0");

                    //Toast.makeText(getBaseContext(), "Conexão feita com sucesso!", Toast.LENGTH_SHORT).show();


                }
            }
        }.start();

    }

    //CRIA O SOCKET DE COMUNICAÇÃO
    public BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public void dadosBluetooth()
    {

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    float value = 0;
                    try {
                        value = ((float) Integer.parseInt(readMessage)) / 1023F;
                    }catch (NumberFormatException e){
                        value = 0.5f;
                    }

                    LineDataSet dataSet = mHeartRateChart.getLineData().getDataSetByIndex(0);
                    LineData data = mHeartRateChart.getData();

                    dataSet.removeEntry(mReading.poll());

                    for (Entry read : mReading)
                        read.setXIndex(read.getXIndex() - 1);

                    Entry e = new Entry(value, dataSet.getEntryCount());
                    mReading.add(e);
                    dataSet.addEntry(e);

                    mHeartRateChart.notifyDataSetChanged();
                    mHeartRateChart.invalidate();
                }

            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(),"Dispositivo Bluetooth falhou!",Toast.LENGTH_SHORT).show();
        }
        else {

            mReading = new LinkedList<>();

            for (int i = 0; i < 256; i++) {
                mReading.add(new Entry(0f, i));
            }

            List<Entry> entries = new ArrayList<>();
            int i = 0;
            for (Entry mread : mReading) {
                entries.add(mread);
            }


            LineDataSet dataset = new LineDataSet(entries, "");
            dataset.setDrawCubic(false);
            dataset.setDrawCircles(false);
            dataset.setDrawCircleHole(false);
            dataset.setDrawValues(false);
            dataset.setColor(Color.RED);


            List<String> labels = new ArrayList<>();

            for (i = 0; i < 256; i++) {
                labels.add("");
            }


            LineData data = new LineData(labels, dataset);
            data.setHighlightEnabled(false);
            mHeartRateChart.setClickable(false);
            mHeartRateChart.setDrawGridBackground(false);
            mHeartRateChart.setData(data);
            mHeartRateChart.setTouchEnabled(false);
            mHeartRateChart.setDescription("Calibragem da pulsação");
            mHeartRateChart.getAxisLeft().setDrawLabels(false);
            mHeartRateChart.getAxisLeft().setDrawGridLines(false);
            mHeartRateChart.getAxisRight().setDrawLabels(false);
            mHeartRateChart.getAxisRight().setDrawGridLines(false);
            mHeartRateChart.getAxisLeft().setAxisMaxValue(1.2f);
            mHeartRateChart.getAxisLeft().setAxisMinValue(-0.2f);
            mHeartRateChart.getXAxis().setDrawGridLines(false);
            mHeartRateChart.getXAxis().setDrawAxisLine(true);
            mHeartRateChart.getLegend().setEnabled(false);
            mHeartRateChart.getLineData().getDataSets().get(0).setColor(Color.rgb(255, 128, 64));


        }

    }



    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[4];  // buffer store for the stream
            List<Integer> bufferArray = new ArrayList<>();
            boolean reading = false;
            // Keep listening to the InputStream until an exception occurs
            int next = 0;
            int bytes = 0; // bytes returned from read()
            while (true) {
                try {
                    next = mmInStream.read();
                    if(next == -1) {
                        SystemClock.sleep(100);
                        continue;
                    }
                    if(next == (int)'<') {
                        reading = true;
                        continue;
                    }
                    if(next == (int)'>') {
                        reading = false;
                        if(bytes > 0){
                            //buffer[bytes] = '\0';
                            while(bytes++ < 4) bufferArray.add(0, (int)'0');
                            int i = 0;
                            for(Integer in: bufferArray)
                                buffer[i++] = (byte) in.intValue();
                            mHandler.obtainMessage(CalibrarSensor.MESSAGE_READ, bytes-1, -1, buffer)
                                    .sendToTarget();
                        }
                        bufferArray.clear();
                        bytes = 0;
                        continue;
                    }
                    if(reading && next >= '0' && next <= '9'){
                        if(bytes < buffer.length) {
                            //buffer[bytes++] = (byte) next;
                            bufferArray.add(next);
                            bytes++;
                        }
                        else{
                            bytes = 0;
                            reading = false;
                        }
                        continue;
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }




    public static BluetoothDevice findBondedDeviceByName (BluetoothAdapter adapter, String name) {
        for (BluetoothDevice device : getBondedDevices(adapter)) {
            if (name.matches(device.getName())) {
                Log.v(TAG, String.format("Found device with name %s and address %s.", device.getName(), device.getAddress()));
                return device;
            }
        }
        Log.w(TAG, String.format("Unable to find device with name %s.", name));
        return null;
    }


    public static Set<BluetoothDevice> getBondedDevices (BluetoothAdapter adapter) {
        Set<BluetoothDevice> results = adapter.getBondedDevices();
        if (results == null) {
            results = new HashSet<BluetoothDevice>();
        }
        return results;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mBTSocket.isConnected())
            mConnectedThread.cancel();


    }


}
