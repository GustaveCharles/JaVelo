package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public record AttributeSet(long bits) {

    public AttributeSet {
        long bits1 = bits >>> 52;
        Preconditions.checkArgument(bits1 == 0);
    }

    public static AttributeSet of(Attribute... attributes) {
        long set = 0;

        for (Attribute a : attributes) {
            set += 1L << a.ordinal();
        }
        AttributeSet set1 = new AttributeSet(set);

        return set1;
    }

    public boolean contains(Attribute attribute) {

        long mask = 1L << attribute.ordinal();


        boolean ThisContainsAttribute = (mask & this.bits) != 0; // ==1 ou ==mask??

        return ThisContainsAttribute;
    }

    public boolean intersects(AttributeSet that) {
        //long mask = 1L << that.bits ;
        //long mask1 = 1L << this.bits ;

        if ((that.bits & this.bits) != 0) {
            return true;
        }
        return false;
    }

    public String toString() {

        StringJoiner j = new StringJoiner(",", "{", "}");

        for (int i = 0; i <Attribute.COUNT; i += 1) {

            Attribute attribute = Attribute.ALL.get(i);
            if (contains(attribute)){
                    j.add(attribute.keyValue());

            }
        }

        return j.toString();
    }
}
