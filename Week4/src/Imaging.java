import java.awt.* ;
import javax.swing.* ;

import java.io.FileInputStream ;
import java.io.InputStreamReader ;
import java.io.BufferedReader ;
import java.io.File ;
import java.io.IOException ;

public class Imaging {

    static final int N = 128 ;  // size of image in cells

    static final double CELL = 0.000774 ;  // angular cell size (radians)
                                           // [based on Broderick/Bell]

    static final float WAVELENGTH = 2.15f ; // meters

    public static void main(String [] args) throws IOException {

        float reVis, imVis ;
        float U, V ;
 
        // Read data from "vis-and-uv.txt"

        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream("vis-and-uv.txt"))) ;

        // FT of visibilities

        double rawImage [] [] = new double [N] [N] ;
        double dirtyBeam [] [] = new double [N] [N] ;

        int k = 0 ;

        DisplayUV display1 = new DisplayUV(15000.0f) ;

        while(true) {

            String line = in.readLine() ;
            if(line == null) break ;

            String [] fields = line.split("\\s+") ;

            float XXAmp = Float.parseFloat(fields [0]) ;
            float XXPhs = Float.parseFloat(fields [1]) ;
            float XYAmp = Float.parseFloat(fields [2]) ;
            float XYPhs = Float.parseFloat(fields [3]) ;
            float YXAmp = Float.parseFloat(fields [4]) ;
            float YXPhs = Float.parseFloat(fields [5]) ;
            float YYAmp = Float.parseFloat(fields [6]) ;
            float YYPhs = Float.parseFloat(fields [7]) ;
            float u = Float.parseFloat(fields [8]) ;
            float v = Float.parseFloat(fields [9]) ;

            double XXPhsRad = Math.PI * (XXPhs / 180) ;
            double YYPhsRad = Math.PI * (YYPhs / 180) ;

            // Take sum of XX and YY polarizations
            reVis = (float) (XXAmp * Math.cos(XXPhsRad) +
                             YYAmp * Math.cos(YYPhsRad)) ;
            imVis = (float) (XXAmp * Math.sin(XXPhsRad) +
                             YYAmp * Math.sin(YYPhsRad)) ;

            U = u / WAVELENGTH ;
            V = v / WAVELENGTH ;

            display1.addPoint(U, V) ;

            for (int i = 0 ; i < N ; i++) {
                double l = (i - N / 2) * CELL ;

                for (int j = 0 ; j < N ; j++) {
                    double m = (j -(N / 2)) * CELL ;

                    double arg = 2 * Math.PI * (l * U + m * V) ;
                    double cos = Math.cos(arg) ;
                    double sin = Math.sin(arg) ;

                    rawImage  [i] [N - j - 1] += reVis * cos - imVis * sin ;
                    dirtyBeam [i] [N - j - 1] += cos ;
                }
            }

            k++ ;
            if(k % 10000 == 0) {
                System.out.println("k = " + k) ;
                display1.repaint() ;
            }
        }

        System.out.println("k = " + k) ;

        // Plot image

        DisplayDensity display2 =
                new DisplayDensity(rawImage, N, "Dirty image") ;

        // Plot dirty beam

        DisplayDensity display3 =
                new DisplayDensity(dirtyBeam, N,
                                   "Point spread function") ;
        
        // Declare a variable to store the cleaned image
        double cleanedImage [] [] = new double [N] [N] ;

        // Loop over the raw image pixels
        for (int i = 0 ; i < N ; i++) {
        for (int j = 0 ; j < N ; j++) {

        // Get the value of the raw image pixel
        double rawPixel = rawImage [i] [j] ;

        // Get the value of the dirty beam pixel
        double beamPixel = dirtyBeam [i] [j] ;

        // Apply a threshold to the dirty beam pixel
        // You can adjust this value according to your data
        double threshold = 0.01 ;

        // If the dirty beam pixel is above the threshold, divide the raw image pixel by it
        // This will reduce the effect of the point spread function on the image
        if (beamPixel > threshold) {
        cleanedImage [i] [j] = rawPixel / beamPixel ;
        }

        // Otherwise, set the cleaned image pixel to zero
        // This will remove any noise or artifacts from the image
        else {
        cleanedImage [i] [j] = 0 ;
        }
        }
        }

        // Plot the cleaned image

        DisplayDensity display4 =
        new DisplayDensity(cleanedImage, N, "Cleaned image") ;
    }

}

class DisplayUV extends JPanel {

    public static int N = 512 ;
    public static int CELL_SIZE = 1 ;

    float limit ;

    boolean [] [] covered ;

    DisplayUV(float limit) {

        setPreferredSize(new Dimension(CELL_SIZE * N, CELL_SIZE * N)) ;

        JFrame frame = new JFrame("U-V coverage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true);

        this.limit = limit ;

        covered = new boolean [N] [N] ;

        repaint() ;
    }

    void addPoint(float U, float V) {
        int i = N/2 + (int) (N * U / (2 * limit)) ;
        int j = N/2 + (int) (N * V / (2 * limit)) ;
        if(i >= 0 && i < N && j >= 0 && j < N) {
            covered [i] [j] = true ;
        }
    }

    public void paintComponent(Graphics g) {

        for(int i = 0 ; i < N ; i++) {
            for(int j = 0 ; j < N ; j++) {
                if(covered [i] [j]) { 
                    g.setColor(Color.BLUE) ;
                }
                else {
                    g.setColor(Color.WHITE) ;
                }
                g.fillRect(CELL_SIZE * i, CELL_SIZE * j,
                           CELL_SIZE, CELL_SIZE) ;
            }
        }
    }
}
