package rasterize;

import model.Polygon;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PolygonClipper {

    public Polygon clip(Polygon subjectPolygon, Polygon clipPolygon) {
        if (clipPolygon.getSize() < 3) return subjectPolygon;

        List<Point> outputList = new ArrayList<>(subjectPolygon.getPoints());

        // Postupně ořezáváme proti každé hraně ořezového polygonu
        for (int i = 0; i < clipPolygon.getSize(); i++) {
            Point clipEdgeStart = clipPolygon.getPoint(i);
            Point clipEdgeEnd = clipPolygon.getPoint((i + 1) % clipPolygon.getSize());

            List<Point> inputList = outputList;
            outputList = new ArrayList<>();

            if (inputList.isEmpty()) break;

            Point s = inputList.get(inputList.size() - 1);
            for (Point e : inputList) {
                if (isInside(e, clipEdgeStart, clipEdgeEnd)) {
                    if (!isInside(s, clipEdgeStart, clipEdgeEnd)) {
                        outputList.add(intersection(s, e, clipEdgeStart, clipEdgeEnd));
                    }
                    outputList.add(e);
                } else if (isInside(s, clipEdgeStart, clipEdgeEnd)) {
                    outputList.add(intersection(s, e, clipEdgeStart, clipEdgeEnd));
                }
                s = e;
            }
        }
        return new Polygon(outputList);
    }

    private boolean isInside(Point p, Point a, Point b) {
        // Vektorový součin pro určení, zda je bod nalevo/napravo od hrany
        return (b.x - a.x) * (p.y - a.y) - (b.y - a.y) * (p.x - a.x) <= 0;
    }

    private Point intersection(Point a, Point b, Point c, Point d) {
        double x1 = a.x, y1 = a.y, x2 = b.x, y2 = b.y;
        double x3 = c.x, y3 = c.y, x4 = d.x, y4 = d.y;

        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (denom == 0) return a; // Rovnoběžky

        double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denom;
        double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denom;

        return new Point((int) xi, (int) yi);
    }
}