// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package visualobjectcommon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class VisualObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int historyLength = 7;
    private String name;
    private Speed speed;
    private Coordinate currentLocation;
    private Color currentColor;
    private Color historyColor;
    private int historyStartIndex;
    private ArrayList<Coordinate> locationHistory;
    private double rotation;

    public int historyLength() {
        return historyLength;
    }

    public String name() {
        return name;
    }

    public Speed speed() {
        return speed;
    }

    public Coordinate currentLocation() {
        return currentLocation;
    }

    public Color currentColor() {
        return currentColor;
    }

    public Color historyColor() {
        return historyColor;
    }

    public int historyStartIndex() {
        return historyStartIndex;
    }

    public ArrayList<Coordinate> locationHistory() {
        return locationHistory;
    }

    public double rotation() {
        return rotation;
    }

    public VisualObject(String name, Speed speed, Coordinate location, Color color, Color historyColor) {
        this(name, speed, location, color, historyColor, 0);
    }

    public VisualObject(String name, Speed speed, Coordinate location, Color color, Color historyColor, double rotation) {
        this.name = name;
        this.speed = speed;
        this.currentLocation = location;
        this.currentColor = color;
        this.historyColor = historyColor;
        this.rotation = rotation;
        this.locationHistory = new ArrayList<>();
        this.historyStartIndex = -1;
    }

    public VisualObject(VisualObject other) {
        this.name = other.name;
        this.speed = new Speed(other.speed);

        this.currentLocation = new Coordinate(other.currentLocation);
        this.locationHistory = new ArrayList<>(other.locationHistory.size());
        other.locationHistory.stream().forEach((c) -> {
            this.locationHistory.add(new Coordinate(c));
        });

        this.currentColor = new Color(other.currentColor);
        this.historyColor = new Color(other.historyColor);

        this.rotation = other.rotation;
    }

    public static VisualObject createRandom(String name, Random rand) {
        if (rand == null) {
            rand = new Random(name.hashCode());
        }

        return new VisualObject(
                name,
                Speed.createRandom(rand),
                Coordinate.createRandom(rand),
                Color.createRandom(Color.currentColorsPalette, rand),
                Color.createRandom(Color.historyColorsPalette, rand));
    }

    public void move() {
        this.move(true);
    }

    public void move(boolean rotate) {
        if (this.locationHistory.size() < historyLength) {
            this.historyStartIndex = (this.historyStartIndex + 1);
            this.locationHistory.add(new Coordinate(this.currentLocation));
        } else {
            this.historyStartIndex = (this.historyStartIndex + 1) % historyLength;
            this.locationHistory.set(this.historyStartIndex, this.currentLocation);
        }

        double xSpeed = this.speed.xSpeed();
        double ySpeed = this.speed.ySpeed();
        double zSpeed = this.speed.zSpeed();

        double x = this.currentLocation.x() + xSpeed;
        double y = this.currentLocation.y() + ySpeed;
        double z = this.currentLocation.z() + zSpeed;

        this.currentLocation = new Coordinate(x, y, z);

        // trim to edges
        this.speed = new Speed(
                checkForEdge(x, xSpeed),
                checkForEdge(y, ySpeed),
                checkForEdge(z, zSpeed));

        if (rotate) {
            this.rotation += 10;
        }
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        this.toJson(sb);

        return sb.toString();
    }

    public void toJson(StringBuilder builder) {
        builder.append("{");
        {
            builder.append("\"current\":");
            this.currentLocation.toJson(builder);
        }

        {
            builder.append(", \"history\":");
            builder.append("[");
            int currentIndex = this.historyStartIndex;
            if (currentIndex != -1) {
                boolean first = true;
                do {
                    currentIndex++;
                    if (currentIndex == this.locationHistory.size()) {
                        currentIndex = 0;
                    }

                    if (first) {
                        first = false;
                    } else {
                        builder.append(", ");
                    }

                    this.locationHistory.get(currentIndex).toJson(builder);
                } while (currentIndex != this.historyStartIndex);
            }
            builder.append("]");
        }

        {
            builder.append(", \"currentColor\":");
            this.currentColor.toJson(builder);
        }

        {
            builder.append(", \"historyColor\":");
            this.historyColor.toJson(builder);
        }

        {
            builder.append(", \"rotation\":");
            builder.append(this.rotation);
        }

        builder.append("}");
    }

    private static double checkForEdge(double point, double speed) {
        if (point < -1.0 || point > 1.0) {
            return speed * -1.0;
        }

        return speed;
    }
}
