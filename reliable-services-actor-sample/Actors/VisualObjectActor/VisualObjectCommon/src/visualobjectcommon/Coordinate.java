// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectcommon;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

public class Coordinate implements Serializable {

    private static final long serialVersionUID = 1L;
    private double x;
    private double y;
    private double z;

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public Coordinate(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coordinate(Coordinate other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public static Coordinate createRandom(Random rand) {
        if (rand == null) {
            Calendar c = Calendar.getInstance();
            c.getTime().toInstant().getNano();
            rand = new Random(c.getTime().toInstant().getNano());
        }

        return new Coordinate(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        this.toJson(sb);

        return sb.toString();
    }

    public void toJson(StringBuilder builder) {
        builder.append(String.format(
                "{ \"x\":%f, \"y\":%f, \"z\":%f }",
                this.x,
                this.y,
                this.z));
    }
}
