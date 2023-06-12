
public class FFT {

    public static void fft1d(double [] re, double [] im, int isgn) {

        // One-dimensional FFT, or inverse FFT (in-place algorithm).

        // When this method is called, the arrays re and im should contain
        // the real and imaginary parts of the input data.

        // When this method returns the values in these arrays are
        // are overwritten with the real and imaginary parts of the
        // transformed data.

        // isgn = +1 or -1 for forward or inverse transform.

        // Size of arrays should be a power or two.

        final double pi = Math.PI ;

        final int N = re.length ;  // im better be the same size

        bitReverse(re, im) ;

        int ln2   = ilog2(N)  ;  // Base 2 log of the leading dimension.

        // Danielson-Lanczos algorithm for FFT.

        for(int ilevel = 1 ; ilevel <= ln2 ; ilevel++) {
            int le   = ipow(2,ilevel) ;
            int lev2 = le / 2 ;

            double uRe = 1.0F ;
            double uIm = 0.0F ;

            double wRe = Math.cos(isgn * pi / lev2) ;
            double wIm = Math.sin(isgn * pi / lev2) ;

            for(int jj = 0 ; jj < lev2 ; jj++) {
                for(int ii = jj ; ii < N ; ii += le) {
                    int jndex = ii + lev2 ;
                    int index = ii ;

                    //tmp      = u * a(jndex) ;
                    double tmpRe = uRe * re [jndex] - uIm * im [jndex] ;
                    double tmpIm = uRe * im [jndex] + uIm * re [jndex] ;

                    //a(jndex) = a(index) - tmp ;
                    re [jndex] = re [index] - tmpRe ;
                    im [jndex] = im [index] - tmpIm ;

                    //a(index) = a(index) + tmp ;
                    re [index] = re [index] + tmpRe ;
                    im [index] = im [index] + tmpIm ;
                }
                //tmp = u * w ;
                double tmpRe = uRe * wRe - uIm * wIm ;
                double tmpIm = uRe * wIm + uIm * wRe ;

                //u   = tmp ;
                uRe   = tmpRe ;
                uIm   = tmpIm ;
            }
        }
    }

    static void bitReverse(double [] re, double [] im) {

        // In place permutation to bit-reversed ordering.

        final int N = re.length ;  // im better be the same size

        int nm1   = N - 1 ;
        int nv2   = N / 2 ;

        for(int index = 0, jndex = 0 ; index < nm1 ; index++) {
            if(jndex > index) {

                // Swap entries

                double tmpRe = re [jndex] ;
                double tmpIm = im [jndex] ;

                re [jndex] = re [index] ;
                im [jndex] = im [index] ;

                re [index] = tmpRe ;
                im [index] = tmpIm ;
            }

            int m = nv2 ;
            while ((m >= 2) && (jndex >= m)) {
                jndex = jndex - m ;
                m = m / 2 ;
            }
            jndex = jndex + m ;
        }
    }

    static int ipow(int i, int j) {

        int k, tmp ;

        tmp = 1 ;
        for(k = 1 ; k <= j ; k++)
            tmp = tmp * i ;
        return tmp ;
    }

    static int ilog2(int n) {

        int i, n2, result ;

        n2     = n ;
        result = 0 ;
        for(i = 1 ; i <= n ; i++) {
            if(n2 > 1) {
                result = result + 1 ;
                n2 = n2 / 2 ;
            }
            else
                break ;
        }
        return result ;
    }

}