package algorithms;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import supportGUI.Circle;
import supportGUI.Line;
import supportGUI.Variables;

public class DefaultTeam {

  // calculDiametre: ArrayList<Point> --> Line
  //   renvoie une paire de points de la liste, de distance maximum.
  public Line calculDiametre(ArrayList<Point> points) {
    if (points.size()<2) return null;
    Point p=points.get(0);
    Point q=points.get(1);
    for (Point s: points) for (Point t: points) if (s.distance(t)>p.distance(q)) {p=s;q=t;}
    return new Line(p,q);
  }

  // calculCercleMin: ArrayList<Point> --> Circle
  //   renvoie un cercle couvrant tout point de la liste, de rayon minimum.

  public Circle calculCercleMin(ArrayList<Point> points) {
    //ArrayList<Point> convexHull = tme1exercice8(points); // Utilise tme1exercice8 pour obtenir le polygone convexe
    //assert convexHull != null;
    return calculateMinEnclosingCircle(points);
  }

  public static Circle calculCercleMin1(ArrayList<Point> points) {
    ArrayList<Point> convexHull = tme1exercice8(points); // Utilise tme1exercice8 pour obtenir le polygone convexe
    assert convexHull != null;
    return algoNaif(convexHull);
  }

  public static Circle calculCercleMin2(ArrayList<Point> points) {
    ArrayList<Point> convexHull = tme1exercice8(points); // Utilise tme1exercice8 pour obtenir le polygone convexe
    assert convexHull != null;
    return calculateMinEnclosingCircle(convexHull);
  }
  private static Circle algoNaif(ArrayList<Point> inputPoints){
    ArrayList<Point> points = (ArrayList<Point>) inputPoints.clone();
    if (points.size()<1) return null;
    double cX,cY,cRadius,cRadiusSquared;
    for (Point p: points){
      for (Point q: points){
        cX = .5*(p.x+q.x);
        cY = .5*(p.y+q.y);
        cRadiusSquared = 0.25*((p.x-q.x)*(p.x-q.x)+(p.y-q.y)*(p.y-q.y));
        boolean allHit = true;
        for (Point s: points)
          if ((s.x-cX)*(s.x-cX)+(s.y-cY)*(s.y-cY)>cRadiusSquared){
            allHit = false;
            break;
          }
        if (allHit) return new Circle(new Point((int)cX,(int)cY),(int)Math.sqrt(cRadiusSquared));
      }
    }
    double resX=0;
    double resY=0;
    double resRadiusSquared=Double.MAX_VALUE;
    for (int i=0;i<points.size();i++){
      for (int j=i+1;j<points.size();j++){
        for (int k=j+1;k<points.size();k++){
          Point p=points.get(i);
          Point q=points.get(j);
          Point r=points.get(k);
          //si les trois sont colineaires on passe
          if ((q.x-p.x)*(r.y-p.y)-(q.y-p.y)*(r.x-p.x)==0) continue;
          //si p et q sont sur la meme ligne, ou p et r sont sur la meme ligne, on les echange
          if ((p.y==q.y)||(p.y==r.y)) {
            if (p.y==q.y){
              p=points.get(k); //ici on est certain que p n'est sur la meme ligne de ni q ni r
              r=points.get(i); //parce que les trois points sont non-colineaires
            } else {
              p=points.get(j); //ici on est certain que p n'est sur la meme ligne de ni q ni r
              q=points.get(i); //parce que les trois points sont non-colineaires
            }
          }
          //on cherche les coordonnees du cercle circonscrit du triangle pqr
          //soit m=(p+q)/2 et n=(p+r)/2
          double mX=.5*(p.x+q.x);
          double mY=.5*(p.y+q.y);
          double nX=.5*(p.x+r.x);
          double nY=.5*(p.y+r.y);
          //soit y=alpha1*x+beta1 l'equation de la droite passant par m et perpendiculaire a la droite (pq)
          //soit y=alpha2*x+beta2 l'equation de la droite passant par n et perpendiculaire a la droite (pr)
          double alpha1=(q.x-p.x)/(double)(p.y-q.y);
          double beta1=mY-alpha1*mX;
          double alpha2=(r.x-p.x)/(double)(p.y-r.y);
          double beta2=nY-alpha2*nX;
          //le centre c du cercle est alors le point d'intersection des deux droites ci-dessus
          cX=(beta2-beta1)/(double)(alpha1-alpha2);
          cY=alpha1*cX+beta1;
          cRadiusSquared=(p.x-cX)*(p.x-cX)+(p.y-cY)*(p.y-cY);
          if (cRadiusSquared>=resRadiusSquared) continue;
          boolean allHit = true;
          for (Point s: points)
            if ((s.x-cX)*(s.x-cX)+(s.y-cY)*(s.y-cY)>cRadiusSquared){
              allHit = false;
              break;
            }
          if (allHit) {System.out.println("Found r="+Math.sqrt(cRadiusSquared));resX=cX;resY=cY;resRadiusSquared=cRadiusSquared;}
        }
      }
    }
    return new Circle(new Point((int)resX,(int)resY),(int)Math.sqrt(resRadiusSquared));
  }

  // Function to return the euclidean distance between two points
  private static int dist(Point a, Point b) {
    return (int) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
  }

  // Function to check whether a point lies inside or on the boundaries of the circle
  private static boolean isInside(Circle c, Point p) {
    return dist(c.getCenter(), p) <= c.getRadius();
  }

  // Helper method to get a circle defined by 3 points
  private static Point getCircleCenter(double bx, double by, double cx, double cy) {
    double B = bx * bx + by * by;
    double C = cx * cx + cy * cy;
    double D = bx * cy - by * cx;
    return new Point((int) ((cy * B - by * C) / (2 * D)), (int) ((bx * C - cx * B) / (2 * D)));
  }

  // Function to return a unique circle that intersects three points
  private static Circle circleFrom(Point A, Point B, Point C) {
    Point I = getCircleCenter(B.x - A.x, B.y - A.y, C.x - A.x, C.y - A.y);
    I.x += A.x;
    I.y += A.y;
    return new Circle(I, dist(I, A));
  }

  // Function to return the smallest circle that intersects 2 points
  private static Circle circleFrom(Point A, Point B) {
    Point C = new Point((int) ((A.x + B.x) / 2.0), (int) ((A.y + B.y) / 2.0));
    return new Circle(C, dist(A, B) / 2);
  }

  // Function to check whether a circle encloses the given points
  private static boolean isValidCircle(Circle c, ArrayList<Point> P) {
    // Iterating through all the points to check whether the points lie inside the circle or not
    for (Point p : P) {
      if (!isInside(c, p)) {
        return false;
      }
    }
    return true;
  }

  // Function to return the minimum enclosing circle for N <= 3
  private static Circle minCircleTrivial(ArrayList<Point> P) {
    assert P.size() <= 3;
    if (P.isEmpty()) {
      return new Circle(new Point(0, 0), 0);
    } else if (P.size() == 1) {
      return new Circle(P.get(0), 0);
    } else if (P.size() == 2) {
      return circleFrom(P.get(0), P.get(1));
    }

    // To check if MEC can be determined by 2 points only
    for (int i = 0; i < 3; i++) {
      for (int j = i + 1; j < 3; j++) {
        Circle c = circleFrom(P.get(i), P.get(j));
        if (isValidCircle(c, P)) {
          return c;
        }
      }
    }
    return circleFrom(P.get(0), P.get(1), P.get(2));
  }

  // Returns the MEC using Welzl's algorithm
  // Takes a set of input points P and a set R
  // points on the circle boundary.
  // n represents the number of points in P that are not yet processed.
  private static Circle welzlHelper(ArrayList<Point> P, ArrayList<Point> R, int n) {
    // Base case when all points processed or |R| = 3
    if (n == 0 || R.size() == 3) {
      return minCircleTrivial(R);
    }

    // Pick a random point randomly
    int idx = (int) (Math.random() * n);
    Point p = P.get(idx);

    // Put the picked point at the end of P since it's more efficient than
    // deleting from the middle of the list
    Collections.swap(P, idx, n - 1);

    // Get the MEC circle d from the set of points P - {p}
    Circle d = welzlHelper(P, R, n - 1);

    // If d contains p, return d
    if (isInside(d, p)) {
      return d;
    }

    // Otherwise, must be on the boundary of the MEC
    R.add(p);

    // Return the MEC for P - {p} and R U {p}
    return welzlHelper(P, R, n - 1);
  }

  // Function to find the minimum enclosing circle for N integer points in a 2-D plane
  private static Circle welzl(ArrayList<Point> P) {
    ArrayList<Point> PCopy = new ArrayList<>(P);
    Collections.shuffle(PCopy);
    return welzlHelper(PCopy, new ArrayList<>(), PCopy.size());

    /*ArrayList<Point> PCopy = new ArrayList<>(P);
    Collections.shuffle(PCopy);

    int numThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    ArrayList<Future<Circle>> results = new ArrayList<>();

    // Split the points into equal parts for each thread
    int batchSize = PCopy.size() / numThreads;

    for (int i = 0; i < numThreads; i++) {
      int startIndex = i * batchSize;
      int endIndex = (i == numThreads - 1) ? PCopy.size() : (i + 1) * batchSize;
      List<Point> subList = PCopy.subList(startIndex, endIndex);

      Future<Circle> future = executor.submit(() -> welzlHelper(new ArrayList<>(subList), new ArrayList<>(), subList.size()));
      results.add(future);
    }

    // Collect the results from all threads
    Circle result = null;
    try {
      for (Future<Circle> future : results) {
        Circle circle = future.get();
        if (result == null || circle.getRadius() < result.getRadius()) {
          result = circle;
        }
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    } finally {
      executor.shutdown();
    }

    return result;

     */


  }

  private static Point[] getExtremums(ArrayList<Point> points) {
    if (points.size() < 4) return null; // Il faut au moins 4 points pour définir un quadrilatère

    Point[] extremums = new Point[4];

    // Trouver le point le plus à gauche
    Point leftmost = points.get(0);
    for (Point point : points) {
      if (point.x < leftmost.x || (point.x == leftmost.x && point.y < leftmost.y)) {
        leftmost = point;
      }
    }
    extremums[0] = leftmost;

    // Trouver le point le plus à droite
    Point rightmost = points.get(0);
    for (Point point : points) {
      if (point.x > rightmost.x || (point.x == rightmost.x && point.y > rightmost.y)) {
        rightmost = point;
      }
    }
    extremums[1] = rightmost;

    Point top = points.get(0);
    for (Point point : points) {
      if (point.y < top.y || (point.y == top.y && point.x > top.x)) {
        top = point;
      }
    }
    extremums[2] = top;

    Point bottom = points.get(0);
    for (Point point : points) {
      if (point.y > bottom.y || (point.y == bottom.y && point.x < bottom.x)) {
        bottom = point;
      }
    }
    extremums[3] = bottom;

    return extremums;
  }

  private static ArrayList<Point> tme1exercice8(ArrayList<Point> points) {
    if (points.size() < 4) return null; // Un quadrilatère Akl-Toussaint a besoin d'au moins 4 points

    // Initialiser les points extrêmes du quadrilatère Akl-Toussaint
    Point[] extremums = getExtremums(points);
    Point A = extremums[0];
    Point B = extremums[1];
    Point C = extremums[2];
    Point D = extremums[3];

    // Initialiser la liste des points du polygone convexe
    ArrayList<Point> convexHull = new ArrayList<>();
    convexHull.add(A);
    convexHull.add(B);
    convexHull.add(C);
    convexHull.add(D);

    // Appliquer l'algorithme QuickHull récursivement
    quickHull(points, A, B, convexHull);
    quickHull(points, B, C, convexHull);
    quickHull(points, C, D, convexHull);
    quickHull(points, D, A, convexHull);

    return convexHull;
  }

  private static void quickHull(ArrayList<Point> points, Point A, Point B, ArrayList<Point> convexHull) {
    ArrayList<Point> pointsLeft = new ArrayList<>();
    for (Point point : points) {
      if (isLeftOfLine(A, B, point)) {
        pointsLeft.add(point);
      }
    }

    if (!pointsLeft.isEmpty()) {
      Point C = getFarthestPoint(A, B, pointsLeft);
      convexHull.add(convexHull.indexOf(B), C);
      quickHull(pointsLeft, A, C, convexHull);
      quickHull(pointsLeft, C, B, convexHull);
    }
  }

  private static boolean isLeftOfLine(Point A, Point B, Point point) {
    return (B.x - A.x) * (point.y - A.y) - (B.y - A.y) * (point.x - A.x) > 0;
  }

  private static Point getFarthestPoint(Point A, Point B, ArrayList<Point> points) {
    double maxDistance = -1;
    Point farthest = null;

    for (Point point : points) {
      double distance = Line2D.ptLineDist(A.x, A.y, B.x, B.y, point.x, point.y);
      if (distance > maxDistance) {
        maxDistance = distance;
        farthest = point;
      }
    }

    return farthest;
  }

  public static void run(int n) {

    try (PrintWriter writer = new PrintWriter("src/res.csv")) {

      ArrayList<Point> points = new ArrayList<>();

      for(int i = 2; i <= n; i++) {
        System.out.println(i);
        String filename = "src/samples/test-" + i + ".points";

        try {
          Path filePath = Paths.get(filename);
          BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filePath.toFile())));

          try {
            String line;
            while((line = input.readLine()) != null) {
              String[] coordinates = line.split("\\s+");
              points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
            }

          } catch (IOException var16) {
            System.err.println("Exception: interrupted I/O.");
          } finally {
            try {
              input.close();
            } catch (IOException var14) {
              System.err.println("I/O exception: unable to close " + filename);
            }

          }
        } catch (FileNotFoundException var18) {
          System.err.println("Input file not found.");
        }

        Set<Point> uniquePoints = new LinkedHashSet<>(points);
        ArrayList<Point> uniquePointsList = new ArrayList<>(uniquePoints);

        // calculer le temps d'execution algo naif
        long startTime1 = System.currentTimeMillis();
        Circle res1 = calculCercleMin1(uniquePointsList);
        long endTime1 = System.currentTimeMillis();
        long executionTime1 = endTime1 - startTime1;

        // calculer le temps d'execution algo welzl
        long startTime2 = System.currentTimeMillis();
        Circle res2 = calculCercleMin2(uniquePointsList);
        long endTime2 = System.currentTimeMillis();
        long executionTime2 = endTime2 - startTime2;

        // Écrire dans le fichier de résultats
        writer.println(i + "," + points.size() + "," + executionTime1 + "," + executionTime2);
      }

    } catch (FileNotFoundException var16) {
      System.err.println("Exception: interrupted I/O.");
    }

  }

  private static Circle calculateMinEnclosingCircle(ArrayList<Point> points) {
    ArrayList<Point> shuffledPoints = new ArrayList<>(points);
    java.util.Collections.shuffle(shuffledPoints);
    return welzlBMinidisk(shuffledPoints, new ArrayList<>());
  }

  private static Circle welzlBMinidisk(ArrayList<Point> P, ArrayList<Point> R) {
    if (P.isEmpty() || R.size() == 3) {
      return bMinidisk(new ArrayList<>(), R);
    } else {
      Point p = P.remove(0);
      Circle D = welzlBMinidisk(new ArrayList<>(P), new ArrayList<>(R));
      if (!isInside2(p, D)) {
        R.add(p);
        D = welzlBMinidisk(new ArrayList<>(P), new ArrayList<>(R));
        R.remove(p);
      }
      P.add(0, p);
      return D;
    }
  }

  private static Circle bMinidisk(ArrayList<Point> P, ArrayList<Point> R) {
    if (P.isEmpty() || R.size() == 3) {
      return bMd(new ArrayList<>(), R);
    } else {
      Point p = P.remove(0);
      Circle D = bMinidisk(new ArrayList<>(P), new ArrayList<>(R));
      if (!isInside2(p, D)) {
        R.add(p);
        D = bMinidisk(new ArrayList<>(P), new ArrayList<>(R));
        R.remove(p);
      }
      P.add(0, p);
      return D;
    }
  }

  private static Circle bMd(ArrayList<Point> P, ArrayList<Point> R) {
    if (R.size() == 0) {
      return null;
    } else if (R.size() == 1) {
      return new Circle(R.get(0), 0);
    } else if (R.size() == 2) {
      Point p1 = R.get(0);
      Point p2 = R.get(1);
      double centerX = 0.5 * (p1.x + p2.x);
      double centerY = 0.5 * (p1.y + p2.y);
      int radius = (int) Math.ceil(p1.distance(p2) / 2.0);
      return new Circle(new Point((int) centerX, (int) centerY), radius);
    } else if (R.size() == 3) {
      return circumCircle(R.get(0), R.get(1), R.get(2));
    } else {
      return null;
    }
  }

  private static Circle circumCircle(Point A, Point B, Point C) {
    double d = 2 * (A.x * (B.y - C.y) + B.x * (C.y - A.y) + C.x * (A.y - B.y));
    double centerX = ((A.x * A.x + A.y * A.y) * (B.y - C.y)
            + (B.x * B.x + B.y * B.y) * (C.y - A.y)
            + (C.x * C.x + C.y * C.y) * (A.y - B.y)) / d;
    double centerY = ((A.x * A.x + A.y * A.y) * (C.x - B.x)
            + (B.x * B.x + B.y * B.y) * (A.x - C.x)
            + (C.x * C.x + C.y * C.y) * (B.x - A.x)) / d;
    int radius = (int) Math.ceil(A.distance(centerX, centerY));
    return new Circle(new Point((int) centerX, (int) centerY), radius);
  }

  private static boolean isInside2(Point p, Circle circle) {
    return circle != null && p.distance(circle.getCenter()) <= circle.getRadius();
  }

  private static List<Point> readPointsFromFile(String filename) throws IOException {
    List<Point> points = new ArrayList<>();

    try (BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {
      String line;
      while ((line = input.readLine()) != null) {
        String[] coordinates = line.split("\\s+");
        points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
      }
    }

    return points;
  }

  public static void main(String[] args) {
    run(Integer.parseInt(args[0]));
  }

}