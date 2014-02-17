package com.bungholes.rfid.akka;

import akka.actor.UntypedActor;
import com.bungholes.rfid.messages.TagPosition;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.EvictingQueue;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class TagPositionActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagPositionActor.class);

    private final EvictingQueue<TagPosition> tagPositionQueue = EvictingQueue.create(10);

    private String tid;
    private String tagId;

    @Override
    public void onReceive(Object message) throws Exception {
        //LOGGER.debug("received {}", message);

        if (message instanceof TagPosition) {
            TagPosition position = (TagPosition) message;

            saveTidAndTagId(position);

            tagPositionQueue.add(position);

            averagePosition();
        } else {
            unhandled(message);
        }
    }

    private void averagePosition() {
        if (tagPositionQueue.remainingCapacity() == 0) {
            double[] doubles = getRelativePositions();

            DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(doubles);

            LOGGER.debug("{} mean:{}, stddev: {}",
                    tid, descriptiveStatistics.getMean(), descriptiveStatistics.getStandardDeviation());
        }
    }

    private double[] getRelativePositions() {
        Collection<Double> positions = Collections2.transform(tagPositionQueue, new Function<TagPosition, Double>() {
            @Override
            public Double apply(TagPosition tagPosition) {
                return tagPosition.getRelativePosition();
            }
        });

        return ArrayUtils.toPrimitive(positions.toArray(new Double[positions.size()]));
    }

    private void saveTidAndTagId(TagPosition tagPosition) {
        if (tid == null) {
            tid = tagPosition.getTid();
            tagId = tagPosition.getTagId();
        }
    }
}
