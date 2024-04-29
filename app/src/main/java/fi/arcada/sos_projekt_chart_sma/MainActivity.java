package fi.arcada.sos_projekt_chart_sma;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String currency, datefrom, dateto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TEMPORARY VALUES
        currency = "USD";
        datefrom = "2024-01-01";
        dateto = "2024-03-31";

        // Gettar currency values
        ArrayList<Double> currencyValues = getCurrencyValues(currency, datefrom, dateto);

        System.out.println("CurrencyValues: " + currencyValues.toString());

        // Vi sätter upp linje diagrammet
        setupLineChart(currencyValues);
    }

    // Metod för valutornas API
    public ArrayList<Double> getCurrencyValues(String currency, String from, String to) {
        CurrencyApi api = new CurrencyApi();
        ArrayList<Double> currencyData = null;

        String urlString = String.format("https://api.frankfurter.app/%s..%s",
                from.trim(),
                to.trim());

        // tar bort whitespace

        try {
            String jsonData = api.execute(urlString).get();

            if (jsonData != null) {
                currencyData = api.getCurrencyData(jsonData, currency.trim());
                Toast.makeText(getApplicationContext(), String.format("Fetched %s currency values from the server", currencyData.size()), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to fetch currency data from the server: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return currencyData;
    }

    // Metod för LineChart med valuta och glidande medelvärde
    private void setupLineChart(ArrayList<Double> currencyValues) {
        LineChart lineChart = findViewById(R.id.lineChart);

        // Linechart properties.
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        // Konverterar varje element i currencyValues till en entry och sätter de till currencyEntries array listen
        ArrayList<Entry> currencyEntries = new ArrayList<>();
        for (int i = 0; i < currencyValues.size(); i++) {
            currencyEntries.add(new Entry(i, currencyValues.get(i).floatValue()));
        }

        LineDataSet currencyDataSet = new LineDataSet(currencyEntries, "Currency Values");
        currencyDataSet.setColor(Color.BLUE);
        currencyDataSet.setCircleColor(Color.RED); // färgerna
        currencyDataSet.setDrawValues(false); // Vi visar inte "values" på punkterna

        // Glidande medelvärde.
        int windowSize = 5; // parameter för att räkna glidande mv
        ArrayList<Double> movingAverageData = Statistics.movingAverage(currencyValues, windowSize);

        // Vi uppdaterad LineChart med data från glidande medelvärde
        ArrayList<Entry> movingAverageEntries = new ArrayList<>();
        for (int i = 0; i < movingAverageData.size(); i++) {
            movingAverageEntries.add(new Entry(i + (windowSize / 2), movingAverageData.get(i).floatValue())); // Centerera glidande medelvärde datapunkterna
        }

        LineDataSet movingAverageDataSet = new LineDataSet(movingAverageEntries, "Moving Average");
        movingAverageDataSet.setColor(Color.GREEN);
        movingAverageDataSet.setCircleColor(Color.BLACK);
        movingAverageDataSet.setDrawValues(false); // Glidande medelvärde färger och gömmer värden igen som valutan

        // Vi kombinerar datasets för att få dom båda och synas
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(currencyDataSet);
        dataSets.add(movingAverageDataSet);

        // Vi sätter combined datasets till LineData
        LineData lineData = new LineData(dataSets);

        // Vi sätter LineData till LineChart.
        lineChart.setData(lineData);
        lineChart.invalidate(); // Vi uppdaterar grafen
    }
}











