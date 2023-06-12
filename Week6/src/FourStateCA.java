import java.awt.*;
import javax.swing.*;
import java.util.Random;


// 3 - Excited 
// 2 - Excited plateau
// 1 - Recovering
// 0 - Resting

// 5 - Non Excitable

public class FourStateCA {

    final static int N = 50;
    final static int CELL_SIZE = 20;
    final static int DELAY = 200;

    final static Random random = new Random();

    static int[][] state = new int[N][N];
    static int[][] timeToStateChange = new int[N][N];

    final static int[] transition_delays = {0,3,3,2};

    static boolean[][] excitedNeighbour = new boolean[N][N];

    static Display display = new Display();

    public static void main(String args[]) throws Exception {

        // Define initial state - excited bottom row / resting elsewhere.

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                state[i][j] = j == N - 1 ? 3 : 0;
                timeToStateChange[i][j] = 0;
            }
        }

        //state[N/2][N/2] = 3;
        

        /*
        for (int i = N/4; i < (N / 4)*3; i++) {
            for (int j = N/2; j < N/2+2; j++) {
                state[i][j] = 5;
            }
        }/* */

        display.repaint();
        pause();

        // Main update loop.
        int iter = 0;
        while (true) {

            System.out.println("iter = " + iter++);

            // Chop wave when half-way up.
            ///*
            if (iter == N / 2) {
                for (int i = 0; i < (N / 2)*1; i++) {
                    for (int j = 0; j < N; j++) {
                        state[i][j] = 0;
                    }
                }

            }/* */

            // Calculate which cells have excited neighbnours.
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {

                    // find neighbours...
                    int ip = Math.min(i + 1, N - 1);
                    int im = Math.max(i - 1, 0);

                    int jp = Math.min(j + 1, N - 1);
                    int jm = Math.max(j - 1, 0);

                    excitedNeighbour[i][j]
                    = state[i][jp] == 2 || state[i][jp] == 3
                    || state[i][jm] == 2 || state[i][jm] == 3
                    || state[ip][j] == 2 || state[ip][j] == 3
                    || state[im][j] == 2 || state[im][j] == 3
                    // check diagonal neighbours
                    || state[ip][jp] == 2 || state[ip][jp] == 3
                    || state[im][jm] == 2 || state[im][jm] == 3
                    || state[ip][jm] == 2 || state[ip][jm] == 3
                    || state[im][jp] == 2 || state[im][jp] == 3;
                }
            }

            // Update state.
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    switch (state[i][j]) {
                        case 5:
                            break;
                        case 0:
                            if (excitedNeighbour[i][j]) {
                                state[i][j] = 3;
                                timeToStateChange[i][j] =  transition_delays[3];
                            }
                            break;
                        default:
                            switch (timeToStateChange[i][j]) {
                                case 0:
                                    state[i][j] -= 1;
                                    timeToStateChange[i][j] = transition_delays[state[i][j]];
                                    break;
                                default:
                                    timeToStateChange[i][j] -= 1;
                                    break;
                            }
                            break;
                    }
                }
            }

            display.repaint();
            pause();
        }
    }

    static class Display extends JPanel {

        final static int WINDOW_SIZE = N * CELL_SIZE;

        Display() {

            setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));

            JFrame frame = new JFrame("Minimal excitable media model");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(this);
            frame.pack();
            frame.setVisible(true);
        }

        public void paintComponent(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    switch(state[i][j]) {
                        case 0:
                            break;
                        case 1:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 2:
                            g.setColor(Color.GRAY);
                            break;
                        case 3:
                            g.setColor(Color.BLACK);
                            break;
                        default:
                            g.setColor(Color.green);;
                            break;
                    }
                    if (state[i][j] > 0 ){
                        g.fillRect(CELL_SIZE * i, CELL_SIZE * j,
                            CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }
    }

    static void pause() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
