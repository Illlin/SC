import java.util.Arrays ;

import java.awt.* ;
import javax.swing.* ;


public class Sinogram {

    static final int N = 512 ;

    static final int CELL_SIZE = 1 ;

    static final double SCALE = 0.0045 ;  // think of a better way to
                                          // parametrize this later...

    static int CUTOFF = N/4 ;  // in ramp filter

    static final float GREY_SCALE_LO = 0.95f, GREY_SCALE_HI = 1.05f ;
    //static final float GREY_SCALE_LO = 0f, GREY_SCALE_HI = 2f ;
        // Clipping, for display only.  See for example Figure 1 in:
        //    http://bigwww.epfl.ch/thevenaz/shepplogan/

    public static void main(String [] args) {

        double [] [] density = new double [N] [N] ;

        for(int i = 0 ; i < N ; i++) {
            double x = SCALE * (i - N/2) ;
            for(int j = 0 ; j < N ; j++) {
                double y = SCALE * (j - N/2) ;

                density [i] [j] = SeppLogan.sheppLoganPhantom(x, y) ;
            }
        } 

        DisplayDensity display1 =
                new DisplayDensity(density, N, "Source Model",
                                   GREY_SCALE_LO, GREY_SCALE_HI) ;

        // Radon tranform of density (as measured by detectors):

        double [] [] sinogram = new double [N] [N] ;

        for(int iTheta = 0 ; iTheta < N ; iTheta++) {
            double theta = (Math.PI * iTheta) / N ;
            double cos = Math.cos(theta) ;
            double sin = Math.sin(theta) ;
            for(int iR = 0 ; iR < N ; iR++) {
                double r = SCALE * (iR - N/2) ;
                double sum = 0 ;
                for(int iS = 0 ; iS < N ; iS++) {
                    double s = SCALE * (iS - N/2) ;
                    double x = r * cos + s * sin ;
                    double y = r * sin - s * cos ;
                    sum += SeppLogan.sheppLoganPhantom(x, y) ;
                }
                sinogram [iTheta] [iR] = sum ;
            }
        }

        DisplayDensity display2 = new DisplayDensity(sinogram, N, "Sinogram") ;

        // inferred integral of density points (actually sum of density
        // points, here) for laternormalization of reconstruction

        double normDensity = norm1(sinogram [0]) ;


        double [] [] sinogramFTRe = new double [N] [N],
                     sinogramFTIm = new double [N] [N] ;
        for(int iTheta = 0 ; iTheta < N ; iTheta++) {
            for(int iR = 0 ; iR < N ; iR++) {
                sinogramFTRe [iTheta] [iR] = sinogram [iTheta] [iR] ;
            }
        }

        // FFT
        for(int iTheta = 0 ; iTheta < N ; iTheta++) {
            FFT.fft1d(sinogramFTRe[iTheta], sinogramFTIm[iTheta], +1);
        }

        DisplaySinogramFT display3 =
        new DisplaySinogramFT(sinogramFTRe, sinogramFTIm, N,
                                "Sinogram radial Fourier Transform") ;

        
        // Ram Lak Filter
        for (int iTheta = 0; iTheta < N; iTheta++) {
            for (int iK = 0; iK < N; iK++) {
                int kSigned = iK <= N/2 ? iK : iK - N;
                double filter = Math.abs(kSigned);
                if (filter > CUTOFF){filter = 0;}
                sinogramFTRe[iTheta][iK] *= filter;
                sinogramFTIm[iTheta][iK] *= filter;
            }
        } /* */
        
        /*/ Ramp filter
        for (int iTheta = 0; iTheta < N; iTheta++) {
            for (int iK = 0; iK < N; iK++) {
                int kSigned = iK <= N/2 ? iK : iK - N;
                double filter = Math.abs(kSigned);
                sinogramFTRe[iTheta][iK] *= filter;
                sinogramFTIm[iTheta][iK] *= filter;
            }
        } /**/

        
        /*/ Low Pass Cosign filter
        int CUTOFF = 427; // N/1.2
        for (int iTheta = 0; iTheta < N; iTheta++) {
            for (int iK = 0; iK < N; iK++) {
                int kSigned = iK <= CUTOFF ? iK : iK - N;
                double filter = Math.abs(kSigned)*Math.cos(Math.PI*kSigned/(2*CUTOFF));
                if (filter > CUTOFF){filter = 0;}
                sinogramFTRe[iTheta][iK] *= filter;
                sinogramFTIm[iTheta][iK] *= filter;
            }
        }/* */

        // Hann Filter 
        /*
        double[] hannWindow = new double[N];
        for (int i = 0; i < N; i++) {
            hannWindow[i] = 0.5 - 0.5 * Math.cos(2 * Math.PI * i / (N - 1));
        }
        
        for (int iTheta = 0; iTheta < N; iTheta++) {
            for (int iK = 0; iK < N; iK++) {
                int kSigned = iK <= N/2 ? iK : iK - N;
                double filterValue = Math.abs(kSigned) < N/2 ? hannWindow[Math.abs(kSigned)] : 0;
                sinogramFTRe[iTheta][iK] *= filterValue;
                sinogramFTIm[iTheta][iK] *= filterValue;
            }
        } */


        for(int iTheta = 0 ; iTheta < N ; iTheta++) {
            FFT.fft1d(sinogramFTRe[iTheta], sinogramFTIm[iTheta], -1);
        }

        DisplayDensity display4 =
                new DisplayDensity(sinogramFTRe, N,
                                   "Filtered sinogram") ;
                                

        double [] [] backProjection = new double [N] [N] ;
        backProject(backProjection, sinogramFTRe) ;
        //backProject(backProjection, sinogram) ;

        // Normalize reconstruction, to have same sum as inferred for
        // original density

        double factor = normDensity / norm2(backProjection) ;
        for(int i = 0 ; i < N ; i++) {
            for(int j = 0 ; j < N ; j++) {
                backProjection [i] [j] *= factor ;
            }
        }

        DisplayDensity display5 =
                new DisplayDensity(backProjection, N,
                           "Back projected sinogram"
                           ,GREY_SCALE_LO, GREY_SCALE_HI
                           ) ;
    }

    static void backProject(double [] [] projection, double [] [] sinogram) {

        // Back Projection operation on sinogram

        for(int i = 0 ; i < N ; i++) {
            double x = SCALE * (i - N/2) ;
            for(int j = 0 ; j < N ; j++) {
                double y = SCALE * (j - N/2) ;

                double sum = 0 ;
                for(int iTheta = 0 ; iTheta < N ; iTheta++) {
                    double theta = (Math.PI * iTheta) / N ;
                    double cos = Math.cos(theta) ;
                    double sin = Math.sin(theta) ;

                    double r = x * cos + y * sin ;

                    double rBox = N/2 + r/SCALE ;

                    if(rBox < 0) continue ;  // assume centred object, with
                                             // support radius < N/2

                    int iR = (int) rBox ; 

                    double offR = rBox - iR ;
                    int iPlusR = iR + 1 ; 

                    if(iPlusR >= N) continue ;  // ditto.

                    // linear interpolation
                    double sinogramVal =
                            (1 - offR) * sinogram [iTheta] [iR] +
                            offR * sinogram [iTheta] [iPlusR] ;
                    sum += sinogramVal ;
                }
                projection [i] [j] = sum ;
            }
        }
    }

    static double norm1(double [] density) {

        double norm = 0 ;
        for(int i = 0 ; i < N ; i++) {
            norm += density [i] ;
        }
        return norm ;
    }

    static double norm2(double [] [] density) {

        double norm = 0 ;
        for(int i = 0 ; i < N ; i++) {
            for(int j = 0 ; j < N ; j++) {
                if(density [i] [j] > 0) {
                    norm += density [i] [j] ;
                }
            }
        }
        return norm ;
    }

}

