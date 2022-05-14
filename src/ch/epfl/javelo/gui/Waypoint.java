package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Represents a waypoint
 */
public record Waypoint(PointCh crossingPosition, int closestJaVeloNode) {
}
