import java.io.FileReader ;
import java.io.StreamTokenizer ;
import java.io.IOException ;

public class ReadPGM {

    public static int N = 256 ;

    public static void main(String [] args) throws Exception {

        String fileName = args.length > 0 ? args [0] : "wolf.pgm" ;

        double [] [] density = new double [N] [N] ;
        read(density, fileName, N) ;

        DisplayDensity display = new DisplayDensity(density, N, fileName) ;
    }

    static double read(double [] [] density, String fileName, int n)
            throws IOException {

        // Read n x n PGM image

        StreamTokenizer tokens =
                new StreamTokenizer(new FileReader(fileName)) ;

        if(tokens.nextToken() != StreamTokenizer.TT_WORD ||
                !tokens.sval.equals("P2")) {
            System.out.println("Bad file format.") ;
            System.exit(1) ;
        }

        getNumber(tokens) ;
        int nx = (int) tokens.nval ;

        getNumber(tokens) ;
        int ny = (int) tokens.nval ;

        if(nx != n || ny != n) {
            System.out.println("Bad image size " + nx + " x " + ny +
                    ".  Expecting " + n + " x " + n) ;
            System.exit(1) ;
        }

        getNumber(tokens) ;
        double maxin = (double) tokens.nval ;

        for(int j = 0 ; j < nx ; j++) {
            for(int i = 0 ; i < ny ; i++) {
                getNumber(tokens) ;
                density [i] [ny - 1 - j] = (double) tokens.nval ;
            }
        }

        return maxin ;
    }

    static void getNumber(StreamTokenizer tokens) throws IOException {

        if(tokens.nextToken() != StreamTokenizer.TT_NUMBER) {
            System.out.println("Bad file format.") ;
            System.exit(1) ;
        }
    }
}
