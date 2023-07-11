
import java.awt.* ;
import javax.swing.* ;

public class FHP {

    final static int NX = 100, NY = 100 ;  // Lattice dimensions
    final static int q = 6 ;  // population
     
    final static int NITER = 125 ;
    final static int DELAY = 50 ;

    final static double DENSITY = 1.0 ;  // initial state, between 0 and 1.0.

    final static int CELL_SIZE = 1;
    static int HEX_W = 8 * CELL_SIZE;
    static int HEX_H = 9 * CELL_SIZE;
    static int HEX_HH = 6 * CELL_SIZE;
    static int HEX_HW = 4 * CELL_SIZE;

    static int [][] hex_lines = {
        {0,3,8,3},
        {1,2,6,5},
        {2,1,4,7},
        {3,0,2,9}
    };


    static Display display = new Display() ;

    static boolean [] [] [] fin = new boolean [NX] [NY] [q] ;
    static boolean [] [] [] fout = new boolean [NX] [NY] [q] ;

    static int cols = 7;
    static int max = 255;
    static Color [] greys = new Color [cols];

    public static void main(String args []) throws Exception {
        for(int c = 0; c < cols ; c++) { 
            int gs = 255-((max/(cols-1)) * c);
            greys[c] = new Color(gs,gs,gs);
        }

        // initialize - populate a subblock of grid
        for(int i = 0; i < NX/4 ; i++) { 
            for(int j = 0; j < NY/4 ; j++) { 
                boolean [] fin_ij = fin [i] [j] ;
                for(int d = 0 ; d < q ; d++) {
                    if(Math.random() < DENSITY) {
                        fin_ij [d] = true ;
                    }
                }
            }
        }

        display.repaint() ;
        Thread.sleep(DELAY) ;

        for(int iter = 0 ; iter < NITER ; iter++) {

            // Collision

            for(int i = 0; i < NX ; i++) { 
                for(int j = 0; j < NY ; j++) { 
                    boolean [] fin_ij = fin [i] [j] ;
                    boolean [] fout_ij = fout [i] [j] ;
                    int pop = 0 ;
                    for(int d = 0 ; d < q ; d++) {
                        if(fin_ij [d]) pop++ ;
                        fout_ij [d] = fin_ij [d] ;
                    }
                    if(pop == 2) {
                        // head on collisions
                        if(fin_ij [0] && fin_ij [1]) {
                            fout_ij [0] = false ;
                            fout_ij [1] = false ;
                            if(Math.random() < 0.5) {
                                fout_ij [2] = true ;
                                fout_ij [3] = true ;
                            }
                            else {
                                fout_ij [4] = true ;
                                fout_ij [5] = true ;
                            }
                        }    
                        if(fin_ij [2] && fin_ij [3]) {
                            fout_ij [2] = false ;
                            fout_ij [3] = false ;
                            if(Math.random() < 0.5) {
                                fout_ij [4] = true ;
                                fout_ij [5] = true ;
                            }
                            else {
                                fout_ij [0] = true ;
                                fout_ij [1] = true ;
                            }
                        }    
                        if(fin_ij [4] && fin_ij [5]) {
                            fout_ij [4] = false ;
                            fout_ij [5] = false ;
                            if(Math.random() < 0.5) {
                                fout_ij [0] = true ;
                                fout_ij [1] = true ;
                            }
                            else {
                                fout_ij [2] = true ;
                                fout_ij [3] = true ;
                            }
                        }    
                    }
                    else if(pop == 4) {
                        // double head on collisions
                        if(!fin_ij [0] && !fin_ij [1]) {
                            fout_ij [0] = true ;
                            fout_ij [1] = true ;
                            if(Math.random() < 0.5) {
                                fout_ij [2] = false ;
                                fout_ij [3] = false ;
                            }
                            else {
                                fout_ij [4] = false ;
                                fout_ij [5] = false ;
                            }
                        }    
                        if(!fin_ij [2] && !fin_ij [3]) {
                            fout_ij [2] = true ;
                            fout_ij [3] = true ;
                            if(Math.random() < 0.5) {
                                fout_ij [4] = false ;
                                fout_ij [5] = false ;
                            }
                            else {
                                fout_ij [0] = false ;
                                fout_ij [1] = false ;
                            }
                        }    
                        if(!fin_ij [4] && !fin_ij [5]) {
                            fout_ij [4] = true ;
                            fout_ij [5] = true ;
                            if(Math.random() < 0.5) {
                                fout_ij [0] = false ;
                                fout_ij [1] = false ;
                            }
                            else {
                                fout_ij [2] = false ;
                                fout_ij [3] = false ;
                            }
                        }    
                    }
                    else if(pop == 3) {
                        // three way collisions
                        if(fin_ij [0] && fin_ij [3] && fin_ij [4]) {
                            fout_ij [0] = false ;
                            fout_ij [1] = true ;
                            fout_ij [2] = true ;
                            fout_ij [3] = false ;
                            fout_ij [4] = false ;
                            fout_ij [5] = true ;
                        }
                        if(fin_ij [1] && fin_ij [2] && fin_ij [5]) {
                            fout_ij [0] = true ;
                            fout_ij [1] = false ;
                            fout_ij [2] = false ;
                            fout_ij [3] = true ;
                            fout_ij [4] = true ;
                            fout_ij [5] = false ;
                        }
                        // head on with spectator
                        if(fin_ij [0] && fin_ij [1]) {
                            fin_ij [0] = false ;
                            fin_ij [1] = false ;
                            if(fin_ij [2] || fin_ij [3]) {
                                fin_ij [4] = true ;
                                fin_ij [5] = true ;
                            }
                            else {
                                fin_ij [2] = true ;
                                fin_ij [3] = true ;
                            }
                        }
                        if(fin_ij [2] && fin_ij [3]) {
                            fin_ij [2] = false ;
                            fin_ij [3] = false ;
                            if(fin_ij [4] || fin_ij [5]) {
                                fin_ij [0] = true ;
                                fin_ij [1] = true ;
                            }
                            else {
                                fin_ij [4] = true ;
                                fin_ij [5] = true ;
                            }
                        }
                        if(fin_ij [4] && fin_ij [5]) {
                            fin_ij [4] = false ;
                            fin_ij [5] = false ;
                            if(fin_ij [0] || fin_ij [1]) {
                                fin_ij [2] = true ;
                                fin_ij [3] = true ;
                            }
                            else {
                                fin_ij [0] = true ;
                                fin_ij [1] = true ;
                            }
                        }
                    }
                }
            }

            // Streaming

            for(int i = 0; i < NX ; i++) { 
                int iP1 = (i + 1) % NX ;
                int iM1 = (i - 1 + NX) % NX ;
                for(int j = 0; j < NY ; j++) { 
                    int jP1 = (j + 1) % NY ;
                    int jM1 = (j - 1 + NY) % NY ;

                    fin [iM1] [j] [0] = fout [i] [j] [0] ;
                    fin [iP1] [j] [1] = fout [i] [j] [1] ;
                    fin [i] [jM1] [2] = fout [i] [j] [2] ;
                    fin [i] [jP1] [3] = fout [i] [j] [3] ;
                    fin [iP1] [jM1] [4] = fout [i] [j] [4] ;
                    fin [iM1] [jP1] [5] = fout [i] [j] [5] ;
                }
            }

            System.out.println("iter = " + iter) ;

            display.repaint() ;
            Thread.sleep(DELAY) ;
        }
    }

    
    static class Display extends JPanel {

        int displaySizeX = HEX_W * NX ;
        int displaySizeY = (int) (HEX_HH * NY + 0.5);

        public static final int ARROW_START = 2 ;
        public static final int ARROW_END   = 7 ;
        public static final int ARROW_WIDE  = 3 ;

        public static final int DIAG_X_0 = -1 ;
        public static final int DIAG_X_1 = 3 ;
        public static final int DIAG_X_2 = 4 ;
        public static final int DIAG_Y_0 = 4 ;
        public static final int DIAG_Y_1 = 0 ;
        public static final int DIAG_Y_2 = 6 ;

        Display() {

            setPreferredSize(new Dimension(displaySizeX, displaySizeY)) ;

            JFrame frame = new JFrame("HPP");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(this);
            frame.pack();
            frame.setVisible(true);
        }

        public static int No_setbits(int n) {
            int cnt = 0;
            while (n != 0) {
                cnt++;
                n = n & (n - 1); // unsets the rightmost set bit.
            }
            return cnt;
        }

        public void draw_hex(Graphics g, int n, int x, int y){
            g.setColor(greys[n]);
            // Draw grid pos
            // get top left
            int originX = ((HEX_W * x + HEX_HW * y) % displaySizeX );
            int originY = y * HEX_HH;
            
            for (int i = 0; i < hex_lines.length; i += 1){
                g.fillRect(
                    hex_lines[i][0] * CELL_SIZE + originX, 
                    hex_lines[i][1] * CELL_SIZE + originY, 
                    hex_lines[i][2] * CELL_SIZE, 
                    hex_lines[i][3] * CELL_SIZE
                );    
            }

        }

        public void paintComponent(Graphics g) {
            g.setColor(Color.WHITE) ;
            //g.fillRect(0, 0, CELL_SIZE * NX, CELL_SIZE * NY) ;

            for(int i = 0 ; i < NX ; i++) {
                for(int j = 0 ; j < NY ; j++) {
                    //boolean [] f_ij = fout [i] [j] ;
                    boolean [] f_ij = fin [i] [j] ;
                    int n = 0;
                    for (int k = 0; k < f_ij.length; k += 1){
                        if (f_ij[k]) {
                            n += 1;
                        }
                    }
                    draw_hex(g, n, i, j);
                }
            } 
        }
    }
}

