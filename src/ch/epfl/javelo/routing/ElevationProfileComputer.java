package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Calculate the longitudinal profile of a given route
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {
    }

    /**
     * returns the longitudinal profile of the route, ensuring that the spacing between
     * profile samples is at most maxStepLength meters
     * @param route a route
     * @param maxStepLength the maximum step length for the route
     * @throws IllegalArgumentException if the maximum step length is not strictly positive
     * @return the longitudinal profile of the route
     */
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
        Arrays.fill(samplesArray, 0, indexFirstCorrectValue, samplesArray[indexFirstCorrectValue]);

        //Bouchage des trous en queue du tableau
        int indexLastCorrectValue = samplesArray.length - 1;
        while (indexLastCorrectValue >= 0 && Float.isNaN(samplesArray[indexLastCorrectValue])) {
            indexLastCorrectValue--;
        }
        Arrays.fill(samplesArray, indexLastCorrectValue, samplesArray.length, samplesArray[indexLastCorrectValue]);


        List<Integer> indexes = new ArrayList<>();

        if (!Float.isNaN(samplesArray[0]) && Float.isNaN(samplesArray[1])){
            indexes.add(0);
        }

        if (!Float.isNaN(samplesArray[samplesArray.length-1]) && Float.isNaN(samplesArray[samplesArray.length-2])){
            indexes.add(samplesArray.length-1);
        }

        for (int i = 1; i < samplesArray.length - 1; i++) {
            //Creation d'un tableau contenant les index des extrémités des trous intermédiaires
            if (!Float.isNaN(samplesArray[i]) && Float.isNaN(samplesArray[i - 1]) && Float.isNaN(samplesArray[i + 1])) {
                indexes.add(i);
                indexes.add(i);
            }
            else if (!Float.isNaN(samplesArray[i]) && (Float.isNaN(samplesArray[i - 1]) || Float.isNaN(samplesArray[i + 1]))) {
                indexes.add(i);
            }
        }

        for (int i = 0; i < indexes.size() / 2; i++) {
            int length = indexes.get(2 * i + 1) - indexes.get(2 * i);
            DoubleUnaryOperator function = Functions.sampled(new float[]{samplesArray[indexes.get(2 * i)],
                    samplesArray[indexes.get(2 * i + 1)]}, length);

            for (int j = 1; j < length; j++) {
                samplesArray[indexes.get(2 * i) + j] = (float) function.applyAsDouble(j);
            }
        }
        return new ElevationProfile(route.length(), samplesArray);
    }
}