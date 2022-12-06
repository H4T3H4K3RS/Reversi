import jdk.jshell.execution.Util;

import java.util.*;

public class Game {

    private final Scanner scanner = new Scanner(System.in);
    private int player1 = Utils.HUMAN;
    private int player2;
    private int size;
    private int status = 0;

    private Board board;
    private Ai ai;
    private ArrayList<Board> boards;
    private ArrayList<Integer> scores;

    Game(int player1, int player2, int size) {
        this.size = size;
        this.board = new Board(size);
        this.player1 = player1;
        this.player2 = player2;
        this.ai = new Ai(Utils.LIGHT_AI);
        this.status = Utils.PLAYER1;
        this.scores = new ArrayList<>();
        this.boards = new ArrayList<>();
    }

    public void chooseMode() {
        Set<Integer> allowedModes = new HashSet<>();
        allowedModes.add(Utils.LIGHT_AI);
        allowedModes.add(Utils.STRONG_AI);
        allowedModes.add(Utils.HUMAN);
        boolean choice = true;
        Integer mode = Utils.LIGHT_AI;
        while (choice) {
            System.out.print("""
                    Choose Mode:\s
                    0. Human
                    1. Light AI
                    2. Strong AI
                    >\s""");
            mode = Utils.tryParse(this.scanner.nextLine());
            choice = false;
            if (mode == null || !allowedModes.contains(mode)) {
                System.out.println("Incorrect choice! Try again.");
                choice = true;
            }
        }
        this.player2 = mode;
        this.ai = new Ai(mode);
    }

    public void chooseMove() {
        boolean moveChoice = true;
        String player = this.status == Utils.PLAYER1 ? "Player 1" : "Player 2";
        player += " (" + (this.getPlayer() == Utils.HUMAN ? "Human" : (this.getPlayer() == Utils.LIGHT_AI ? "Light AI" : "Strong AI")) + ")";
        while (moveChoice) {
            System.out.println(player + " move.");
            this.board.recountPredicts(this.status);
            if ((this.status == Utils.PLAYER2 && this.player2 != Utils.HUMAN) ||
                    (this.status == Utils.PLAYER1 && this.player1 != Utils.HUMAN)) {
                ArrayList<Integer> move = this.ai.getSolution(this.board, this.status);
                System.out.println(player + " Made Move: " + (char) ('H' - move.get(0)) + (move.get(1) + 1));
                this.boards.add(this.board.copy());
                this.board.setCell(move.get(0), move.get(1), this.status);
                break;
            }
            boolean choice = true;
            char row = 'A';
            Integer column = 1;
            while (choice) {
                System.out.print("""
                        Choose Row (A-H). Type Letter R, to cancel the last move:\s
                        >\s""");
                try {
                    row = this.scanner.nextLine().charAt(0);
                } catch (Exception e) {
                    row = ' ';
                }
                choice = false;
                if (!(row == 'R') && ((row - 'A') > 7 || (row - 'A') < 0)) {
                    System.out.println("Incorrect row! Try again.");
                    choice = true;
                }
                if (row == 'R' && this.boards.size() == 0) {
                    System.out.println("You cannot make move back! Try again.");
                    choice = true;
                }
            }
            if (row == 'R') {
                if (this.player2 != Utils.HUMAN) {
                    this.boards.remove(this.boards.size() - 1);
                    this.switchStatus();
                }
                this.board.loadBoard(this.boards.get(this.boards.size() - 1));
                System.out.println("Successfully moved back!");
                break;
            }
            choice = true;
            while (choice) {
                System.out.print("""
                        Choose Column (1-8):\s
                        >\s""");
                column = Utils.tryParse(this.scanner.nextLine());
                choice = false;
                if (column == null || column < 1 || column > 8) {
                    System.out.println("Incorrect column! Try again.");
                    choice = true;
                }
            }
            this.boards.add(this.board.copy());
            moveChoice = false;
            if (!this.board.setCell(row, column - 1, this.status)) {
                this.boards.remove(this.boards.size() - 1);
                System.out.println("Incorrect move! Try again.");
                moveChoice = true;
            }
            System.out.println(player + " Made Move: " + row + column);
        }
    }

    private void setup() {
        this.status = Utils.PLAYER1;
        this.board = new Board(this.size);
        this.chooseMode();
        this.board.recountPredicts(this.status);
    }

    private void switchStatus() {
        this.status = this.status == Utils.PLAYER1 ? Utils.PLAYER2 : Utils.PLAYER1;
        if (!this.board.canMakeTurn(this.status)) {
            if (!this.board.canMakeTurn(this.status == Utils.PLAYER1 ? Utils.PLAYER2 : Utils.PLAYER1)) {
                return;
            }
            this.switchStatus();
        }
    }

    private void move() {
        this.chooseMove();
        this.switchStatus();
        try {
            Thread.sleep((long) (0.1 * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public Integer getPlayer() {
        return this.status == Utils.PLAYER1 ? this.player1 : this.player2;
    }

    public void run() {
        System.out.println("Welcome to Reversi Game by Vsevolod Ovchinnikov!");
        boolean running = true;
        while (running) {
            this.setup();
            boolean gameRunning = true;
            while (gameRunning) {
                this.board.recountPredicts(this.status);
                this.board.printBoard();
                if (this.checkEnd()) {
                    gameRunning = false;
                }
                this.move();
            }
            String winner = "Player 1";
            Integer winnerId = this.board.getWinner();
            if (winnerId != Utils.PLAYER1) {
                if (this.player2 == Utils.HUMAN) winner = "Player 2";
                else if (this.player2 == Utils.STRONG_AI) winner = "Strong AI";
                else if (this.player2 == Utils.LIGHT_AI) winner = "Light AI";
            } else {
                this.scores.add(this.board.getCount(Utils.PLAYER1));
            }
            System.out.println("Winner: " + winner +
                    "\nPlayer 1 Score: " + this.board.getCount(Utils.PLAYER1) +
                    "\nPlayer 2 Score: " + this.board.getCount(Utils.PLAYER2)
            );
            if (winnerId == Utils.PLAYER1) {
                System.out.println("Best score: " + Collections.max(this.scores));
            }
            boolean choice = true;
            Integer again = 0;
            while (choice) {
                System.out.print("""
                        Would you like to play again?\s
                        0. Yes
                        1. No
                        >\s""");
                again = Utils.tryParse(this.scanner.nextLine());
                choice = false;
                if (again == null || (again != 0 && again != 1)) {
                    System.out.println("Incorrect choice! Try again.");
                    choice = true;
                }
            }
            running = again == 0;
        }
    }

    private boolean checkEnd() {
        for (int i = 0; i < this.board.size; i++) {
            for (int j = 0; j < this.board.size; j++) {
                if (this.board.cells.get(i).get(j) == Utils.PREDICT)
                    return false;
            }
        }
        return true;
    }

}
