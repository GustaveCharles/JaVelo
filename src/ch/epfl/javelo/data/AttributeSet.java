package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import java.util.StringJoiner;

/**
 * represents a set of OpenStreetMap attributes
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

public record AttributeSet(long bits) {

    /**
     * creates a long bit the size of the Attribute enum which will represent the attributes in a
     * given set
     *
     * @throws IllegalArgumentException if the value passed to the constructor contains a bit
     *                                  at 1 which does not correspond to any valid attribute.
     */
    public AttributeSet {
        long bits1 = bits >>> Attribute.COUNT;
        Preconditions.checkArgument(bits1 == 0);
    }

    /**
     * computes a loop and adds a 1 in the index position of the attribute when the attribute is present
     * in the list
     *
     * @param attributes an ellipse of attributes
     * @return returns a set containing only the attributes given as argument
     */
    public static AttributeSet of(Attribute... attributes) {
        long set = 0;
        long attribute = 0 ;
        for (Attribute a : attributes) {
            attribute += 1L << a.ordinal();
            set = set | attribute;
        }

        return new AttributeSet(set);
    }

    /**
     * made to check if an AttributeSet contains a given Attribute
     *
     * @param attribute the Attribute which needs to be checked
     * @return returns true iff the receiver set (this) contains the given attribute
     */
    public boolean contains(Attribute attribute) {

        long mask = 1L << attribute.ordinal();


        return (mask & this.bits) != 0;
    }

    /**
     * checks if at least one Attribute is in both AttributeSets
     *
     * @param that the other AttributeSet we want to compare
     * @return returns true iff the intersection of the receiver set (this)
     * * with the one passed as argument (that) is not empty
     */
    public boolean intersects(AttributeSet that) {

        return (that.bits & this.bits) != 0;
    }

    /**
     * overrides the toString method
     *
     * @return returns a string consisting of the textual representation
     * of the elements of the set enclosed in braces ({}) and separated by commas
     */
    public String toString() {

        StringJoiner j = new StringJoiner(",", "{", "}");

        for (int i = 0; i < Attribute.COUNT; i += 1) {

            Attribute attribute = Attribute.ALL.get(i);
            if (contains(attribute)) {
                j.add(attribute.keyValue());
            }
        }

        return j.toString();
    }
}
