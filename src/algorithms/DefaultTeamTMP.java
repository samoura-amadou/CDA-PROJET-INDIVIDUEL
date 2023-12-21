package algorithms;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import supportGUI.Circle;
import supportGUI.Line;
public class DefaultTeamTMP{


  // calculDiametre: ArrayList<Point> --> Line
  // renvoie une paire de points de la liste, de distance maximum.
  public Line calculDiametre(ArrayList<Point> points) {
    ArrayList<Point> convexHull = tme1exercice8(points); // Utilise tme1exercice8 pour obtenir le polygone convexe
    return tme1exercice6(convexHull); // Utilise tme1exercice
  }

  // calculCercleMin: ArrayList<Point> --> Circle
  //   renvoie un cercle couvrant tout point de la liste, de rayon minimum.
  public Circle calculCercleMin(ArrayList<Point> points) {
    ArrayList<Point> convexHull = tme1exercice8(points); // Utilise tme1exercice8 pour obtenir le polygone convexe
    return tme1exercice4(convexHull);

  }
  private Circle tme1exercice4(ArrayList<Point> inputPoints){
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
  private Circle tme1exercice5(ArrayList<Point> points){
    if (points.size()<1) return null;
    ArrayList<Point> rest = (ArrayList<Point>)points.clone();
    Point dummy=rest.get(0);
    Point p=dummy;
    for (Point s: rest) if (dummy.distance(s)>dummy.distance(p))
      p=s;
    Point q=p;
    for (Point s: rest) if (p.distance(s)>p.distance(q)) q=s;
    double cX=.5*(p.x+q.x);
    double cY=.5*(p.y+q.y);
    double cRadius=.5*p.distance(q);
    rest.remove(p);
    rest.remove(q);
    while (!rest.isEmpty()){
      Point s=rest.remove(0);
      double distanceFromCToS=Math.sqrt((s.x-cX)*(s.x-cX)+(s.y-cY)*(s.y-cY));
      if (distanceFromCToS<=cRadius) continue;
      double cPrimeRadius=.5*(cRadius+distanceFromCToS);
      double alpha=cPrimeRadius/(double)(distanceFromCToS);
      double beta=(distanceFromCToS-cPrimeRadius)/(double)(distanceFromCToS);
      double cPrimeX=alpha*cX+beta*s.x;
      double cPrimeY=alpha*cY+beta*s.y;
      cRadius=cPrimeRadius;
      cX=cPrimeX;
      cY=cPrimeY;
    }
    return new Circle(new Point((int)cX,(int)cY),(int)cRadius);
  }
  private Line tme1exercice6(ArrayList<Point> points) {
    if (points.size()<2) return null;
    Point p=points.get(0);
    Point q=points.get(1);
    for (Point s: points) for (Point t: points) if (s.distance(t)>p.distance(q)) {p=s;q=t;}
    return new Line(p,q);
  }
  private ArrayList<Point> tme1exercice7(ArrayList<Point> points){
    //VOIR CORRECTION TME2EXERCICE3
    return null;
  }
  private ArrayList<Point> tme1exercice8(ArrayList<Point> points) {
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
  private Point[] getExtremums(ArrayList<Point> points) {
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
  // Algorithme QuickHull récursif
  private void quickHull(ArrayList<Point> points, Point A, Point B, ArrayList<Point> convexHull) {
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

  private boolean isLeftOfLine(Point A, Point B, Point point) {
    return (B.x - A.x) * (point.y - A.y) - (B.y - A.y) * (point.x - A.x) > 0;
  }

  private Point getFarthestPoint(Point A, Point B, ArrayList<Point> points) {
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
}
