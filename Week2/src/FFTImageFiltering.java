import java.util.Arrays;

public class FFTImageFiltering {

    public static int N = 1024 ;
    public static boolean display = false;

    static void transpose(double [] [] a) {
        int N = a.length;
        double temp;
        for(int i = 0 ; i < N ; i++) {
            for(int j = 0 ; j < i ; j++) {
                temp = a[i][j];
                a[i][j] = a[j][i];
                a[j][i] = temp;
            }
        }
    }

    public static void fft2d(double[][] re, double[][] im, int isgn) {
        int N = re[0].length;
    
        // Apply 1D FFT on each row
        for (int i = 0; i < N; i++) {
            FFT.fft1d(re[i], im[i], isgn);
        }

        transpose(re);
        transpose(im);

        // Apply 1D FFT on each column
        for (int i = 0; i < N; i++) {
            FFT.fft1d(re[i], im[i], isgn);
        }

        transpose(re);
        transpose(im);
    }

    public static void main(String [] args) throws Exception {
        double starttime = System.nanoTime();
        double [] [] X = new double [N] [N] ;
        ReadPGM.read(X, "bird.pgm", N) ;

        if (display){
            DisplayDensity display = new DisplayDensity(X, N, "Original Image") ;
        }

        // create array for in-place FFT, and copy original data to it
        double [] [] CRe = new double [N] [N], CIm = new double [N] [N] ;
        for(int k = 0 ; k < N ; k++) {
            for(int l = 0 ; l < N ; l++) {
                CRe [k] [l] = X [k] [l] ;
            }
        }

        fft2d(CRe, CIm, 1) ;  // Fourier transform


        // Copy to avoid visual issues
        double [] [] cCRe = Arrays.stream(CRe).map(double[]::clone).toArray(double[][]::new);
        double [] [] cCIm = Arrays.stream(CIm).map(double[]::clone).toArray(double[][]::new);

        if (display){
            Display2dFT display2 = new Display2dFT(cCRe, cCIm, N, "Discrete FT") ;
        }

        // FILTER
        /*
        int cutoff = N/64 ;  // for example
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

        if (display){
            Display2dFT display2a = new Display2dFT(CRe, CIm, N, "Truncated FT") ;
        }

        // create array for in-place inverse FFT, and copy FT to it
        double [] [] reconRe = new double [N] [N],
                     reconIm = new double [N] [N] ;
        for(int k = 0 ; k < N ; k++) {
            for(int l = 0 ; l < N ; l++) {
                reconRe [k] [l] = CRe [k] [l] ;
                reconIm [k] [l] = CIm [k] [l] ;
            }
        }

        fft2d(reconRe, reconIm, -1) ;  // Inverse Fourier transform

        if (display){
            DisplayDensity display3 = new DisplayDensity(reconRe, N, "Reconstructed Image") ;
        }

        System.out.println((System.nanoTime() - starttime)/1000000);
    }

}