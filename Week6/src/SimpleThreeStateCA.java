import java.awt.*;
import javax.swing.*;
import java.util.Random;


// 0 - Resting
// 2 - Excited 
// 1 - Recovering

// 5 - Non Excitable

public class SimpleThreeStateCA {

    final static int N = 50;
    final static int CELL_SIZE = 20;
    final static int DELAY = 200;

    final static Random random = new Random();

    static int[][] state = new int[N][N];

    static boolean[][] excitedNeighbour = new boolean[N][N];

    static Display display = new Display();

    public static void main(String args[]) throws Exception {

        // Define initial state - excited bottom row / resting elsewhere.
        //*
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                state[i][j] = j == N - 1 ? 2 : 0;
            }
        }/* */

        /*
        int zeroWeight = 70;
        int oneWeight = 20;
        int twoWeight = 10;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int randomNumber = random.nextInt(zeroWeight + oneWeight + twoWeight);
                if (randomNumber < zeroWeight) {
                    state[i][j] = 0;
                } else if (randomNumber < zeroWeight + oneWeight) {
                    state[i][j] = 1;
                } else {
                    state[i][j] = 2;
                }
            }
        }


        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                state[i][j] = random.nextInt(3);
            }
        }/* */
        //state[N/2][N/2] = 2; state[N/2 -1][N/2] = 1;
        

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
            /*
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
                            = state[i][jp] == 2
                            || state[i][jm] == 2
                            || state[ip][j] == 2
                            || state[im][j] == 2;
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
                                state[i][j] = 2;
                            }
                            break;
                        case 2:
                            state[i][j] = 1;
                            break;
                        default: // 1
                            state[i][j] = 0;
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
                    if (state[i][j] > 0) {
                        if (state[i][j] == 2) {
                            g.setColor(Color.BLACK);
                        } else {
                            g.setColor(Color.GRAY);
                        }
                        if (state[i][j]==5) {
                            g.setColor(Color.GREEN);
                        }
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
