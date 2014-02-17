package com.bungholes.rfid.messaging.akka;

import com.bungholes.rfid.messages.TagReading;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.math.DoubleMath;

import java.util.List;

import static com.bungholes.rfid.messages.TagReading.dateOrdering;
import static com.google.common.collect.Lists.newArrayList;

public class PhaseCalculations {

    public static double calculatePosition(List<TagReading> antenna1, List<TagReading> antenna2) {
        List<Float> phaseDifferences = calculatePhaseDifferencesBetweenAntennas(antenna1, antenna2);
        double phaseDifferenceMean = DoubleMath.mean(phaseDifferences);

        return relativePositionFromPhaseDifferenceMean(phaseDifferenceMean);
    }

    public static double relativePositionFromPhaseDifferenceMean(double phaseDifferenceMean) {
        ///return Math.floor((phaseDifferenceMean + 90) * 7.6);
        return phaseDifferenceMean + 90;
    }

    public static List<Float> calculatePhaseDifferencesBetweenAntennas(
            List<TagReading> antenna1,
            List<TagReading> antenna2) {

        ImmutableList<TagReading> sortedAntenna1 = ImmutableSortedSet.orderedBy(dateOrdering())
                .addAll(antenna1)
                .build()
                .asList();

        ImmutableList<TagReading> sortedAntenna2 = ImmutableSortedSet.orderedBy(dateOrdering())
                .addAll(antenna2)
                .build()
                .asList();

        List<Float> phaseDifferences = calculatePhaseDifferencesBetweenSortedLists(sortedAntenna1, sortedAntenna2);

        return phaseDifferences;
    }

    public static List<Float> calculatePhaseDifferencesBetweenSortedLists(
            List<TagReading> antenna1Readings,
            List<TagReading> antenna2Readings) {

        List<Float> phaseDifferenceResults = newArrayList();

        for (int i = 0; i < antenna1Readings.size(); i++) {
            if (i < antenna2Readings.size() && i < antenna2Readings.size()) {
                TagReading tagReading1 = antenna1Readings.get(i);
                TagReading tagReading2 = antenna2Readings.get(i);

                float phaseDifference = calculatePhaseDifferenceBetweenTwoReadings(tagReading1, tagReading2);

                phaseDifferenceResults.add(phaseDifference);
            }
        }

        return phaseDifferenceResults;
    }

    public static float calculatePhaseDifferenceBetweenTwoReadings(TagReading tagReading1, TagReading tagReading2) {
        float phaseDifference = tagReading2.getPhaseAngle() - tagReading1.getPhaseAngle();

        if (Math.abs(phaseDifference) > 90) {
            phaseDifference = phaseDifference - ( 180 * Math.signum(phaseDifference));
        }

        return phaseDifference;
    }

}
