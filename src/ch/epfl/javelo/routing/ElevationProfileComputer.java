package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {}

    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {

        Preconditions.checkArgument(maxStepLength > 0);

        int numberOfSamples = (int) Math.ceil(route.length() / maxStepLength) + 1;
        double spaceBetweenSamples = route.length() / (numberOfSamples - 1);
        float[] samplesArray = new float[numberOfSamples];

        int numberOfNanValue = 0;
        for (int i = 0; i < numberOfSamples; i++) {

            samplesArray[i] = (float) route.elevationAt(i * spaceBetweenSamples);

            if (Float.isNaN(samplesArray[i])) {
                numberOfNanValue += 1;
            }
        }

        if (numberOfNanValue == numberOfSamples) {
            return new ElevationProfile(route.length(), new float[numberOfSamples]);
        }

        //Bouchage des trous en tête du tableau
        int indexFirstCorrectValue = 0;
        while (indexFirstCorrectValue < samplesArray.length && Float.isNaN(samplesArray[indexFirstCorrectValue])) {
            indexFirstCorrectValue++;
        }
        Arrays.fill(samplesArray, 0, indexFirstCorrectValue, samplesArray[indexFirstCorrectValue]); //FAIRE ATTENTION A CETTE LIGNE

        //Bouchage des trous en queue du tableau
        int indexLastCorrectValue = samplesArray.length - 1;
        while (indexLastCorrectValue >= 0 && Float.isNaN(samplesArray[indexLastCorrectValue])) {
            indexLastCorrectValue--;
        }
        Arrays.fill(samplesArray, indexLastCorrectValue, samplesArray.length, samplesArray[indexLastCorrectValue]);


        List<Integer> indexes = new ArrayList<>();
        for (int i = indexFirstCorrectValue; i <= indexLastCorrectValue; i++) {

            //Creation d'un tableau contenant les index des extrémités des trous intermédiaires
            if (!Float.isNaN(samplesArray[i]) && ((Float.isNaN(samplesArray[i - 1]) || Float.isNaN(samplesArray[i + 1])))) {
                indexes.add(i);
            }
            if (!Float.isNaN(samplesArray[i]) && (Float.isNaN(samplesArray[i - 1]) && Float.isNaN(samplesArray[i + 1]))) {
                indexes.add(i);
            }
        }

        for (int i = 0; i<indexes.size() / 2; i++) {
            int length = indexes.get(2*i+1) - indexes.get(2*i);
            DoubleUnaryOperator function = Functions.sampled(new float[] {samplesArray[indexes.get(2*i)],
                    samplesArray[indexes.get(2*i+1)]}, length);

            for (int j=1; j<length; j++){
                samplesArray[indexes.get(2*i)+j] = (float) function.applyAsDouble(j);
            }
        }
        return new ElevationProfile(route.length(), samplesArray);
    }


}