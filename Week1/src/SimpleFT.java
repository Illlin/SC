import java.util.Arrays;

public class SimpleFT {

    public static int N = 256 ;

    public static void main(String [] args) throws Exception {
        double starttime = System.nanoTime();
        double [] [] X = new double [N] [N] ;
        ReadPGM.read(X, "wolf.pgm", N) ;

        //DisplayDensity display = new DisplayDensity(X, N, "Original Image") ;

        double [] [] CRe = new double [N] [N], CIm = new double [N] [N] ;

        for(int k = 0 ; k < N ; k++) {
            for(int l = 0 ; l < N ; l++) {
                double sumRe = 0, sumIm = 0 ;
                // Nested for loops performing sum over X elements
                for(int m = 0; m < N; m++) {
                    for(int n = 0; n < N; n++) {
                        double arg = -2*Math.PI*(((double)k*m)/N + ((double)l*n)/N);
                        double cos = Math.cos(arg);
                        double sin = Math.sin(arg);
                        sumRe += cos * X [m] [n] ;
                        sumIm += sin * X [m] [n] ;
                    }
                }
                CRe [k] [l] = sumRe ;
                CIm [k] [l] = sumIm ;
            }
            System.out.println("Completed FT line " + k + " out of " + N) ;
        }


        // Copy to avoid visual issues
        double [] [] cCRe = Arrays.stream(CRe).map(double[]::clone).toArray(double[][]::new);
        double [] [] cCIm = Arrays.stream(CIm).map(double[]::clone).toArray(double[][]::new);

        //Display2dFT display2 = new Display2dFT(cCRe, cCIm, N, "Discrete FT") ;

        // FILTER
        /*
        int cutoff = N/8 ;  // for example
        for(int k = 0 ; k < N ; k++) {
            int kSigned = k <= N/2 ? k : k - N ;
            for(int l = 0 ; l < N ; l++) {
                int lSigned = l <= N/2 ? l : l - N ;
                if(Math.abs(kSigned) > cutoff || Math.abs(lSigned) > cutoff) {
                    CRe [k] [l] = 0 ;
                    CIm [k] [l] = 0 ;
                }
            }
        } 
        */

        /*
        // Flip quadrent
        // Compute the indices of the center pixel in each dimension
        int centerX = N / 2;
        int centerY = N / 2;

        // Swap the quadrants of the FFT so that the origin is at the center of the image
        for (int y = 0; y < centerY; y++) {
            for (int x = 0; x < centerX; x++) {
                // Swap the four quadrants of the FFT
                double tmp = CRe[y][x];
                CRe[y][x] = CRe[centerY + y][centerX + x];
                CRe[centerY + y][centerX + x] = tmp;

                tmp = CIm[y][x];
                CIm[y][x] = CIm[centerY + y][centerX + x];
                CIm[centerY + y][centerX + x] = tmp;
            }
        }
        */

        /*
        // Calculate the new center coordinates
        int xOffset = 32;
        int yOffset = 32;
        int centerX = N / 2 + xOffset;
        int centerY = N / 2 + yOffset;

        // Create new arrays for the shifted image
        double[][] shiftedCRe = new double[N][N];
        double[][] shiftedCIm = new double[N][N];

        // Loop through each pixel in the original image
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                // Calculate the new coordinates for this pixel
                int newX = (x + centerX) % N;
                int newY = (y + centerY) % N;

                // Copy the pixel values to the new arrays at the new coordinates
                shiftedCRe[newX][newY] = CRe[x][y];
                shiftedCIm[newX][newY] = CIm[x][y];
            }
        }

        // Copy the shifted arrays back into the original arrays
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                CRe[x][y] = shiftedCRe[x][y];
                CIm[x][y] = shiftedCIm[x][y];
            }
        }
        */

        //Display2dFT display2a = new Display2dFT(CRe, CIm, N, "Truncated FT") ;

        double [] [] reconstructed = new double [N] [N] ;

        for(int m = 0 ; m < N ; m++) {
            for(int n = 0 ; n < N ; n++) {
                double sum = 0;
                for(int k = 0; k < N ; k++) {
                    for(int l = 0; l < N ; l++) {
                        double arg = (2*Math.PI/N)*(k * m + n * l);
                        double cos = Math.cos(arg);
                        double sin = Math.sin(arg);
                        sum += cos * CRe[k][l] - sin * CIm[k][l];
                    }
                }
                reconstructed [m] [n] = sum ;
            }
            System.out.println("Completed inverse FT line " + m + " out of " + N) ;
        }


        //DisplayDensity display3 = new DisplayDensity(reconstructed, N, "Reconstructed Image") ;

        System.out.println((System.nanoTime() - starttime)/1000000);
    }
}