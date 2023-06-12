import java.awt.* ;
import javax.swing.* ;


public class DisplayDensity extends JPanel {

    public static int CELL_SIZE = 1 ;

    int n ;

    double [] [] density ;

    double greyScaleLo, greyScaleHi ;

    boolean doScale ;

    DisplayDensity(double [] [] density, int n, String title) {
        this(density, n, title, Double.MIN_VALUE, Double.MAX_VALUE) ;
    }

    DisplayDensity(double [] [] density, int n, String title,
                   double greyScaleLo, double greyScaleHi) {

        setPreferredSize(new Dimension(CELL_SIZE * n, CELL_SIZE * n)) ;

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true);

        this.density = density ;
        this.n = n ;

        this.greyScaleLo = greyScaleLo ;
        this.greyScaleHi = greyScaleHi ;

        repaint() ;
    }

    public void paintComponent(Graphics g) {

        // If eithe hi or lo clipping unspecified, find min and max
        // data values ;

        double minVal = Double.MAX_VALUE ;
        double maxVal = Double.MIN_VALUE ;

        if(greyScaleLo == Double.MIN_VALUE ||
                greyScaleHi == Double.MAX_VALUE) {
            for(int i = 0 ; i < n ; i++) {
                for(int j = 0 ; j < n ; j++) {
                    double densityVal = density [i] [j] ;
                    if(densityVal > maxVal) {
                        maxVal = densityVal ;
                    }
                    if(densityVal < minVal) {
                        minVal = densityVal ;
                    }
                }
            }
        }
        if(greyScaleLo == Double.MIN_VALUE) {
            greyScaleLo = minVal ;
        }
        if(greyScaleHi == Double.MAX_VALUE) {
            greyScaleHi = maxVal ;
        }

        for(int i = 0 ; i < n ; i++) {
            for(int j = 0 ; j < n ; j++) {
                double intensity = density [i] [n - j - 1] ;

                float grey ;
                if(greyScaleHi == greyScaleLo) {
                    grey = 0.5F ;
                }
                else if(intensity <= greyScaleLo) {
                    grey = 0 ;
                } else if(intensity >= greyScaleHi) {
                    grey = 1 ;
                }
                else {
                    grey = (float) ((intensity - greyScaleLo) /
                            (greyScaleHi - greyScaleLo)) ;
                }
                Color c = new Color(grey, grey, grey) ;
                g.setColor(c) ;
                g.fillRect(CELL_SIZE * i, CELL_SIZE * j,
                        CELL_SIZE, CELL_SIZE) ;
            }
        }
    }
}