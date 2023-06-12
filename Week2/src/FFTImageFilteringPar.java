import java.util.Arrays;
import java.util.concurrent.CyclicBarrier ;

public class FFTImageFilteringPar extends Thread{

    public static int N = 1024 ;
    public static int P = 8 ;

    public static boolean display = false;
    
    static CyclicBarrier barrier = new CyclicBarrier(P) ;

    static double[][] re;
    static double[][] im;
    static int isgn;

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

    int me;

    FFTImageFilteringPar(int me){
        this.me = me;
    }

    public void run() {
        // Apply 1D FFT on each row
        for (int i = me; i < N; i+=P) {
            FFT.fft1d(re[i], im[i], isgn);
        }    
        //synch();
        if (me == 0) {
            //transpose(re);
            //transpose(im);
        }
        //synch();
        // Apply 1D FFT on each column
        for (int i = me; i < N; i+=P) {
            FFT.fft1d(re[i], im[i], isgn);
        }
        //synch();
        if (me == 0) {
            //transpose(re);
            //transpose(im);
        }
    }

    public static void parfft2d() throws InterruptedException {
        FFTImageFilteringPar [] threads = new FFTImageFilteringPar [P] ;
        for(int me = 0 ; me < P ; me++) {
            threads [me] = new FFTImageFilteringPar(me) ;
            threads [me].start() ;
        }

        for(int me = 0 ; me < P ; me++) {
            threads [me].join() ;
        }
    }

    static void synch() {
        try {
            barrier.await() ;
        }
        catch(Exception e) {
            e.printStackTrace() ;
            System.exit(1) ;
        }
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

        re = CRe;
        im = CIm;
        isgn = 1;
        parfft2d() ;  // Fourier transform


        // Copy to avoid visual issues
        double [] [] cCRe = Arrays.stream(CRe).map(double[]::clone).toArray(double[][]::new);
        double [] [] cCIm = Arrays.stream(CIm).map(double[]::clone).toArray(double[][]::new);

        if (display){
            Display2dFT display2 = new Display2dFT(cCRe, cCIm, N, "Discrete FT") ;
        }

        // FILTER
        
        int cutoff = N/128 ;  // for example
        for(int k = 0 ; k < N ; k++) {
            int kSigned = k <= N/2 ? k : k - N ;
            for(int l = 0 ; l < N ; l++) {
                int lSigned = l <= N/2 ? l : l - N ;
                if(Math.abs(kSigned) < cutoff && Math.abs(lSigned) < cutoff) {
                    CRe [k] [l] = 0 ;
                    CIm [k] [l] = 0 ;
                }
            }
        }
        

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

        re = reconRe;
        im = reconIm;
        isgn = -1;
        parfft2d() ;  // Inverse Fourier transform

        if (display){
            DisplayDensity display3 = new DisplayDensity(reconRe, N, "Reconstructed Image") ;
        }
        
        System.out.println((System.nanoTime() - starttime)/1000000);
    }

}