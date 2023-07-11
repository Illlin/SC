import java.awt.* ;
import javax.swing.* ;

public class HPP {


    final static int NX = 100, NY = 100 ;  // Lattice dimensions
    final static int q = 4 ;  // population
     
    final static int NITER = 150 ;
    final static int DELAY = 50 ;

    final static double DENSITY = 1.0 ;  // initial state, between 0 and 1.0.
    final static double size = 4;

    static Display display = new Display() ;

    static int [] [] fin = new int [NX] [NY] ;
    static int [] [] fout = new int [NX] [NY] ;

    static int cols = 5;
    static int max = 255;
    static Color [] greys = new Color [cols];
    final static int CELL_SIZE = 10 ;
    final static int SUPER_CELL_SIZE = 1;

    public static void main(String args []) throws Exception {
        for(int c = 0; c < cols ; c++) { 
            int gs = 255-((max/(cols-1)) * c);
            greys[c] = new Color(gs,gs,gs);
        }

        // initialize - populate a subblock of grid
        for(int i = 0; i < NX/size ; i++) { 
            for(int j = 0; j < NY/size ; j++) { 

                int p = 0;
                for(int d = 0 ; d < q ; d++) {
                    if(Math.random() < DENSITY) {
                        p += 1 << d;
                    }
                }
                
                fin [i] [j] = p;
            }
        }


        display.repaint() ;
        Thread.sleep(DELAY) ;

        for(int iter = 0 ; iter < NITER ; iter++) {

            // Collision

            for(int i = 0; i < NX ; i++) { 
                for(int j = 0; j < NY ; j++) { 
                    int fin_ij = fin [i] [j] ;

                    // Verticle Collision
                    if (fin_ij == 3) {      // 1100
                        fout [i] [j] = 12;  // 0011
                    }
                    // Horisontal Collision
                    else if (fin_ij == 12){ // 0011
                        fout [i] [j] = 3;   // 1100
                    }
                    else {
                        // default, no collisions case:
                        fout [i] [j] = fin_ij ;
                    }
                }
            }

            // Streaming

            for(int i = 0; i < NX ; i++) { 
                int iP1 = (i + 1) % NX ;
                int iM1 = (i - 1 + NX) % NX ;
                for(int j = 0 ; j < NY ; j++) { 
                    int jP1 = (j + 1) % NY ;
                    int jM1 = (j - 1 + NY) % NY ;
                    fin [i] [j] = 
                        (fout [iP1] [j]   & 1) |
                        (fout [iM1] [j]   & 2) |
                        (fout [i]   [jP1] & 4) |
                        (fout [i]   [jM1] & 8) ;
                }
            }

            System.out.println("iter = " + iter) ;
            display.repaint() ;
      
            Thread.sleep(DELAY) ;
        }
    }
    public static int No_setbits(int n) {
        int cnt = 0;
        while (n != 0) {
            cnt++;
            n = n & (n - 1); // unsets the rightmost set bit.
        }
        return cnt;
    }
    
    static class Display extends JPanel {
        Display() {

            setPreferredSize(new Dimension(CELL_SIZE * NX/SUPER_CELL_SIZE, CELL_SIZE * NY/SUPER_CELL_SIZE)) ;

            JFrame frame = new JFrame("HPP");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(this);
            frame.pack();
            frame.setVisible(true);
        }

        public void paintComponent(Graphics g) {
            for(int i = 0 ; i < NX ; i+=SUPER_CELL_SIZE) {
                for(int j = 0 ; j < NY ; j+=SUPER_CELL_SIZE) {
                    int no = 0 ;
                    for (int k = 0 ; k < SUPER_CELL_SIZE; k += 1) {
                        for (int l = 0 ; l < SUPER_CELL_SIZE; l += 1) {
                            no += No_setbits(fin [i+k] [j+l]  ) ;
                        }
                    }
                    g.setColor(greys[no]) ;
                    g.fillRect((CELL_SIZE * i)/SUPER_CELL_SIZE, (CELL_SIZE * j)/SUPER_CELL_SIZE, CELL_SIZE, CELL_SIZE) ;
                }
            } 
        }
    }
}