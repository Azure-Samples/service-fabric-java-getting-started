// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectcommon;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

public class Speed implements Serializable {

    private static final long serialVersionUID = 1L;
    private double xSpeed;
    private double ySpeed;
    private double zSpeed;

    public Speed(Speed other) {
        this.xSpeed = other.xSpeed;
        this.ySpeed = other.ySpeed;
        this.zSpeed = other.zSpeed;
    }

    public Speed(double x, double y, double z) {
        this.xSpeed = x;
        this.ySpeed = y;
        this.zSpeed = z;
    }

    public double xSpeed() {
        return this.xSpeed;
    }

    public double ySpeed() {
        return this.ySpeed;
    }

    public double zSpeed() {
        return this.zSpeed;
    }

    public static Speed createRandom() {
        return createRandom(null);
    }

    public static Speed createRandom(Random rand) {
        if (rand == null) {
            Calendar c = Calendar.getInstance();
            c.getTime().toInstant().getNano();
            rand = new Random(c.getTime().toInstant().getNano());
        }

        return new Speed(rand.nextDouble() * 0.03, rand.nextDouble() * 0.03, rand.nextDouble() * 0.03);
    }
}
