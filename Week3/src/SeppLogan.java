public class SeppLogan {
    // Shepp-Logan Phantom:
    //
    //   https://en.wikipedia.org/wiki/Shepp%E2%80%93Logan_phantom

    static final Ellipse [] sheppLoganEllipses = {
        new Ellipse(0.0, 0.0, 0.69, 0.92, 0, 2.0),
        new Ellipse(0.0, -0.0184, 0.6624, 0.874, 0, -0.98),
        new Ellipse(0.22, 0, 0.11, 0.31, -18.0, -0.02),
        new Ellipse(-0.22, 0, 0.16, 0.41, 18.0, -0.02),
        new Ellipse(0, 0.35, 0.21, 0.25, 0, 0.01),
        new Ellipse(0, 0.1, 0.046, 0.046, 0, 0.01),
        new Ellipse(0, -0.1, 0.046, 0.046, 0, 0.01),
        new Ellipse(-0.08, -0.605, 0.046, 0.023, 0, 0.01),
        new Ellipse(0, -0.605, 0.023, 0.023, 0, 0.01),
        new Ellipse(0.06, -0.605, 0.023, 0.046, 0, 0.01),
    } ;

    static final Ellipse [] sheppLoganEllipses2 = {
        new Ellipse(0.0, 0.0, 0.69, 0.92, 0, 2),
        new Ellipse(0.0, -0.0184, 0.6624, 0.874, 0, -0.98),
        new Ellipse(0.22, 0, 0.11, 0.31, -18.0, -2.0),
        new Ellipse(-0.22, 0, 0.16, 0.41, 18.0, -2.0),
        new Ellipse(0, 0.35, 0.21, 0.25, 0, 1.0),
        new Ellipse(0, 0.1, 0.046, 0.046, 0, 1.0),
        new Ellipse(0, -0.1, 0.046, 0.046, 0, 1.0),
        new Ellipse(-0.08, -0.605, 0.046, 0.023, 0, 1.0),
        new Ellipse(0, -0.605, 0.023, 0.023, 0, 1.0),
        new Ellipse(0.06, -0.605, 0.023, 0.046, 0, 1.0),
    } ;

    static double sheppLoganPhantom (double x, double y) {

        double total = 0 ;
        for(Ellipse ellipse : sheppLoganEllipses) {
            total += ellipse.localDensity(x, y) ;
        }
        return total ;
    }

    static class Ellipse {

        double centreX ;
        double centreY ;
        double major ;
        double minor ;
        double theta ;
        double density ;
        double cos, sin ;

        Ellipse(double centreX, double centreY,
                double major, double minor, double theta, double density) {

            this.centreX = centreX ;
            this.centreY = centreY ;
            this.major = major ;
            this.minor = minor ;
            this.theta = theta ;
            if(theta == 0) {
                cos = 1 ;
                sin = 0 ;
            }
            else {
               double rad = Math.PI * theta / 180 ;
               cos = Math.cos(rad) ; 
               sin = Math.sin(rad) ; 
            }
            this.density = density;
        }

        double localDensity(double x, double y) {

            double xOff, yOff ;
            xOff = x - centreX ;
            yOff = y - centreY ;

            double xRot, yRot ;
            if(theta == 0) {
                xRot = xOff ;
                yRot = yOff ;
            }
            else {
                // Rotate so x/y aligned with major/minor axes.
                xRot = cos * xOff - sin * yOff ;
                yRot = sin * xOff + cos * yOff ;
            }
            double xNorm = xRot / major ;
            double yNorm = yRot / minor ;
            if(xNorm * xNorm + yNorm * yNorm < 1) {
                return density ;
            }
            else {
                return 0 ;
            }
        }
    }
}
