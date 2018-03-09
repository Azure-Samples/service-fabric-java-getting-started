// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectcommon;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

public class Color implements Serializable {

    private static final long serialVersionUID = 1L;
    private double r;
    private double g;
    private double b;
    private double a;

    public static double[][] currentColorsPalette
            = {
                {0.0, 0.0, 1.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {1.0, 0.0, 0.0, 0.0}
            };

    public static double[][] historyColorsPalette
            = {
                {1.0, 0.0, 0.0, 0.0},
                {1.0, 1.0, 0.0, 0.0},
                {1.0, 1.0, 1.0, 0.0}
            };

    public Color(double r, double g, double b, double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(Color other) {
        this.r = other.r;
        this.g = other.g;
        this.b = other.b;
        this.a = other.a;
    }

    public double r() {
        return this.r;
    }

    public double g() {
        return this.g;
    }

    public double b() {
        return this.b;
    }

    public double a() {
        return this.a;
    }

    public static Color createRandom(double[][] colorPalette, Random rand) {
        if (rand == null) {
            Calendar c = Calendar.getInstance();
            c.getTime().toInstant().getNano();
            rand = new Random(c.getTime().toInstant().getNano());
        }

        int colorIndex = rand.nextInt(colorPalette.length);
        return new Color(colorPalette[colorIndex][0] + rand.nextDouble(),
                colorPalette[colorIndex][1] + rand.nextDouble(),
                colorPalette[colorIndex][2] + rand.nextDouble(),
                colorPalette[colorIndex][3] + rand.nextDouble()
        );
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        this.toJson(sb);

        return sb.toString();
    }

    public void toJson(StringBuilder builder) {
        builder.append(String.format(
                "{ \"r\":%f, \"g\":%f, \"b\":%f, \"a\":%f }",
                this.r,
                this.g,
                this.b,
                this.a));
    }
}
