package com.example.jsampaio.veloguiaversao2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GeraGraficosActivity extends AppCompatActivity {
    protected LineChart maltLinearChart;
    protected LineChart mspdLinearChart;
    protected LineChart mPulseLinearChart;
    protected static final String TAG = "Gera Gráficos";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gera_graficos);

        maltLinearChart = (LineChart) findViewById(R.id.altLinearChart);
        mspdLinearChart = (LineChart) findViewById(R.id.spdLinearChart);
        mPulseLinearChart = (LineChart) findViewById(R.id.pulseLinearChart);
        Intent intent = getIntent();

        maltLinearChart.setNoDataText("");
        mspdLinearChart.setNoDataText("");
        mPulseLinearChart.setNoDataText("");

        float[] pulseArray = intent.getFloatArrayExtra("pulseArray");
        float pulseMax = intent.getFloatExtra("pulseMax", 1);
        float pulseMin = intent.getFloatExtra("pulseMin", 0);
        if(pulseMax == pulseMin) {pulseMax += 5; pulseMin -= 5;}

        float[] altArray = intent.getFloatArrayExtra("alt_dados");
        float altMax = intent.getFloatExtra("alt_max", 1.0f);
        float altMin = intent.getFloatExtra("alt_min", 0.0f);
        if(altMax == altMin) {altMax += 5; altMin -= 5;}

        float[] spdArray = intent.getFloatArrayExtra("velo_dados");
        float spdMax = intent.getFloatExtra("velo_max", 1.0f);
        float spdMin = intent.getFloatExtra("velo_min", 0.0f);
        if(spdMax == spdMin) {spdMax += 5; spdMin -= 5;}

        String[] timeArray = intent.getStringArrayExtra("timeArray");
//        Log.d("timeArray length2", String.valueOf(timeArray.length));
//        for(int j = 0; j < timeArray.length; j++)
//            Log.d("timeArray2", timeArray[j]);

//        geraGraficoYDoubleXint(altArray, maltLinearChart, timeArray, altMax, altMin);
//        geraGraficoYDoubleXint(spdArray, mspdLinearChart, timeArray, spdMax, spdMin);

        geraGraficoInt(altArray, maltLinearChart, timeArray, altMax, altMin, "Altitude");
        geraGraficoInt(spdArray, mspdLinearChart, timeArray, spdMax, spdMin, "Velocidade");
        geraGraficoInt(pulseArray, mPulseLinearChart, timeArray, pulseMax, pulseMin, "Frequência Cardíaca");

        Log.d("Pulsação Máxima", String.valueOf(pulseMax));
        Log.d("Pulsação minima", String.valueOf(pulseMin));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    protected void geraGraficoYDoubleXint(float[] floatList, LineChart mLinearChart, String[] timeLabels, float latMax, float latMin) {

        XAxis xAxis = mLinearChart.getXAxis();
        List<String> listString = new ArrayList<>();
        Legend legend = mLinearChart.getLegend();
        List<String> labels = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        LineDataSet dataset = new LineDataSet(entries, "");
        LineData data = new LineData(labels, dataset);


        int i = 0;
        float range = latMax - latMin;
        for (float mread : floatList) {
            entries.add(new Entry((mread - latMin) / (range), i++));
        }


        dataset.setDrawCubic(false);
        dataset.setDrawCircles(true);
        dataset.setDrawCircleHole(false);
        dataset.setDrawValues(false);


        for (i = 0; i < floatList.length; i++) {
            labels.add(timeLabels[i]);
        }


        data.setHighlightEnabled(false);
        //mLinearChart.setClickable(false);
        mLinearChart.setDrawGridBackground(false);
        mLinearChart.setData(data);
        mLinearChart.enableScroll();
        mLinearChart.getAxisLeft().setDrawLabels(false);
        mLinearChart.getAxisRight().setDrawLabels(false);
        mLinearChart.getAxisLeft().setAxisMaxValue(1.0f);
        mLinearChart.getAxisLeft().setAxisMinValue(-0.0f);
        mLinearChart.getXAxis().setDrawGridLines(false);
        mLinearChart.getXAxis().setDrawAxisLine(true);
        mLinearChart.setHorizontalScrollBarEnabled(true);
        mLinearChart.setMinimumHeight(200);
        mLinearChart.setMinimumWidth(200);
        mLinearChart.getLineData().getDataSets().get(0).setColor(Color.BLUE);
        mLinearChart.setScrollX(50);

        /*
            Interaction with the chart
         */

        mLinearChart.setTouchEnabled(true);



        /*
            Alterando o eixo X
         */
        mLinearChart.setEnabled(true);
        xAxis.setValues(listString);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(20f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);


        /*
            Legenda
         */

        legend.setEnabled(true);
        legend.setTextColor(R.color.black);
        legend.setTextSize(10f);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        //   legend.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[]{"Dado", "Dado2"});

        /*
            Permitir que quando tocado mostre o valor de x,y
         */
        // highlight the entry and x-position 50 in the first (0) DataSet
        Highlight highlight = new Highlight(50, 0);

        mLinearChart.highlightValue(highlight, false); // highlight this value, don't call listener

    }

    protected void geraGraficoInt(float[] floatEntry, LineChart mLinearChart, String[] timeLabels, float valueMax, float valueMin, String description) {
        List<String> listString = new ArrayList<>();
        Legend legend = mLinearChart.getLegend();
        List<String> labels = new ArrayList<>();

        //Entries
        List<Entry> entries = new ArrayList<>();
        int i = 0;
        for (float mread : floatEntry) {
            entries.add(new Entry(mread, i++));
        }

        //Dataset
        LineDataSet dataset = new LineDataSet(entries, "");
        for (i = 0; i < floatEntry.length; i++) {
            labels.add(timeLabels[i]);
        }

        dataset.setDrawCubic(true);
        dataset.setDrawCircles(false);
        dataset.setDrawCircleHole(false);
        dataset.setCircleColor(Color.LTGRAY);
        dataset.setDrawValues(false);
        dataset.setHighlightEnabled(true);
        dataset.setDrawHighlightIndicators(true);
        dataset.setHighLightColor(Color.BLUE);

        //Data
        LineData data = new LineData(labels, dataset);
        mLinearChart.setData(data);

        data.setHighlightEnabled(true);
        data.setDrawValues(false);
        data.setValueTextSize(6.0f);
        data.setValueTextColor(Color.BLUE);

        mLinearChart.setDrawGridBackground(false);
        mLinearChart.getAxisRight().setEnabled(false);
        mLinearChart.setDoubleTapToZoomEnabled(false);

        mLinearChart.getAxisLeft().setAxisMaxValue(valueMax*12/10);
        mLinearChart.getAxisLeft().setAxisMinValue(valueMin*8/10);
        mLinearChart.getAxisRight().setStartAtZero(false);
        mLinearChart.getAxisLeft().setStartAtZero(false);

        //mLinearChart.zoom(intList.length/10, valueMax/(valueMax-valueMin), 0, 0);
        //mLinearChart.moveViewTo(0, ((float) valueMax + valueMin) / 2, mLinearChart.getAxisLeft().getAxisDependency());
        mLinearChart.zoom(floatEntry.length > 10 ? floatEntry.length/10 : 1, 1, 0, 0);
        mLinearChart.moveViewToX(0);
        mLinearChart.setScaleEnabled(false);
        mLinearChart.enableScroll();

        mLinearChart.getXAxis().setDrawGridLines(true);
        mLinearChart.getXAxis().setDrawAxisLine(false);
        mLinearChart.getXAxis().setDrawLabels(true);
        mLinearChart.getXAxis().setTextSize(6);
        mLinearChart.getXAxis().setTextColor(Color.BLACK);

        mLinearChart.setHorizontalScrollBarEnabled(true);
        mLinearChart.setMinimumHeight(256);
        mLinearChart.setDescription(description);


        //Atualizar
//        mLinearChart.setClickable(false);
        mLinearChart.notifyDataSetChanged();
        mLinearChart.invalidate();
    }

    protected void geraGraficoYIntxint(Vector<Integer> integerVector) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("GeraGraficos Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
