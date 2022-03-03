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
        System.out.println(mask);
        System.out.println(this.bits);

        boolean ThisContainsAttribute = (mask & this.bits) == mask; // ==1 ou ==mask??

        return ThisContainsAttribute;
    }

    public boolean intersects(AttributeSet that) {
        //long mask = 1L << that.bits ;
        //long mask1 = 1L << this.bits ;

        if ((that.bits & this.bits) == that.bits) {
            return true;
        }
        return false;
    }

    public String toString() {

        StringJoiner j = new StringJoiner(",", "{", "}");


        for (int i = 1; i <= Attribute.COUNT; i += 1) {

            AttributeSet attributs = AttributeSet.of(Attribute.ALL.get(i));

            long mask = 1L << this.bits ;
            long mask1 = 1L << attributs.bits;

            if ((mask & mask1) == mask) {
                j.add(Byte.toString((byte)1));
            }
        }

        return toString();
    }
}
