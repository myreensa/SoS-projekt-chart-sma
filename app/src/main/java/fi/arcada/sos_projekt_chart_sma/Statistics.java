package fi.arcada.sos_projekt_chart_sma;

import java.util.ArrayList;

public class Statistics {

    public static ArrayList<Double> movingAverage(ArrayList<Double> data, int windowSize) {
        ArrayList<Double> movingAverageData = new ArrayList<>();

        // Glidande medelvärde
        for (int i = 0; i <= data.size() - windowSize; i++) {
            double sum = 0;
            for (int j = i; j < i + windowSize; j++) {
                sum += data.get(j);
            }
            double average = sum / windowSize;
            movingAverageData.add(average);
        }

        return movingAverageData;
    }
}

