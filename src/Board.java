import java.util.ArrayList;
import java.util.Random;

public class Board {
    public int size = 0;
    public ArrayList<ArrayList<Integer>> cells = new ArrayList<>(new ArrayList<>());
    private ArrayList<ArrayList<Integer>> shifts = new ArrayList<>(new ArrayList<>());

    Board(int size) {
        this.size = size;
        for (int i = 0; i < this.size; i++) {
            ArrayList<Integer> tmp = new ArrayList<>();
            for (int j = 0; j < this.size; j++) {
                tmp.add(Utils.EMPTY);
            }
            this.cells.add(tmp);
        }
        this.cells.get(3).set(3, Utils.PLAYER1);
        this.cells.get(3).set(4, Utils.PLAYER2);
        this.cells.get(4).set(3, Utils.PLAYER2);
        this.cells.get(4).set(4, Utils.PLAYER1);
        this.generateShifts();
    }

    public Board copy() {
        Board newBoard = new Board(this.size);
        newBoard.cells = new ArrayList<>();
        for (ArrayList<Integer> row : this.cells) {
            newBoard.cells.add(new ArrayList<Integer>(row));
        }
        return newBoard;
    }

    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            //  Handle any exceptions.
        }
    }

    public void generateCells() {
        Random rand = new Random();
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                cells.get(i).set(j, rand.nextInt(4) - 1);
            }
        }
    }
    public boolean canMakeTurn(int player) {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.cells.get(i).get(j) == Utils.PREDICT) {
                    return true;
                }
            }
        }
        return false;
    }

    public void generateShifts() {
        ArrayList<Integer> deltas = new ArrayList<Integer>();
        deltas.add(0);
        deltas.add(1);
        deltas.add(-1);
        ArrayList<ArrayList<Integer>> shifts = new ArrayList<ArrayList<Integer>>();
        shifts.add(new ArrayList<Integer>());
        shifts.add(new ArrayList<Integer>());
        for (Integer delta1 : deltas) {
            for (Integer delta2 : deltas) {
                if (delta1.equals(delta2) && delta1 == 0) continue;
                shifts.get(0).add(delta1);
                shifts.get(1).add(delta2);
            }
        }
        this.shifts = shifts;
    }

    public ArrayList<Integer> getCellDirections(int i, int j, int player) {
        ArrayList<Integer> direction = new ArrayList<Integer>();
        for (int k = 0; k < 8; k++) {
            direction.add(0);
            int tmpY = i;
            int tmpX = j;
            while (tmpY + this.shifts.get(1).get(k) >= 0 && tmpY + this.shifts.get(1).get(k) < this.size && tmpX + this.shifts.get(0).get(k) >= 0 && tmpX + this.shifts.get(0).get(k) < this.size && this.cells.get(tmpY + this.shifts.get(1).get(k)).get(tmpX + this.shifts.get(0).get(k)) != player && this.cells.get(tmpY + this.shifts.get(1).get(k)).get(tmpX + this.shifts.get(0).get(k)) != Utils.EMPTY && this.cells.get(tmpY + this.shifts.get(1).get(k)).get(tmpX + this.shifts.get(0).get(k)) != Utils.PREDICT) {
                tmpY += this.shifts.get(1).get(k);
                tmpX += this.shifts.get(0).get(k);
                direction.set(k, direction.get(k) + 1);
            }
            if (!(tmpY + this.shifts.get(1).get(k) >= 0 && tmpY + this.shifts.get(1).get(k) < this.size && tmpX + this.shifts.get(0).get(k) >= 0 && tmpX + this.shifts.get(0).get(k) < 8 && this.cells.get(tmpY + this.shifts.get(1).get(k)).get(tmpX + this.shifts.get(0).get(k)) == player)) {
                direction.set(k, 0);
            }
        }
        return direction;
    }

    public void recountPredicts(int player) {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.cells.get(i).get(j) != Utils.PLAYER1 && this.cells.get(i).get(j) != Utils.PLAYER2) {
                    this.cells.get(i).set(j, Utils.EMPTY);
                }
            }
        }
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                ArrayList<Integer> directions = this.getCellDirections(i, j, player);
                for (Integer direction : directions) {
                    if (direction != 0 && this.cells.get(i).get(j) != Utils.PLAYER1 && this.cells.get(i).get(j) != Utils.PLAYER2) {
                        this.cells.get(i).set(j, Utils.PREDICT);
                    }
                }
            }
        }
    }

    public void setCell(int row, int column, int value) {
//        System.out.println(row + " " + column + " " + value);
        cells.get(row).set(column, value);
        ArrayList<Integer> directions = new ArrayList<>(this.getCellDirections(row, column, value));
        for (int i = 0; i < directions.size(); i++) {
            int tX = column;
            int tY = row;
            for (int j = 0; j < directions.get(i); j++) {
                tX += this.shifts.get(0).get(i);
                tY += this.shifts.get(1).get(i);
                this.cells.get(tY).set(tX, value);
            }
        }
    }

    public boolean setCell(char row, int column, int value) {
        int normalizedValue = this.size - row + 'A' - 1;
        if (!(this.cells.get(normalizedValue).get(column) == Utils.PREDICT)) {
            return false;
        }
        this.setCell(normalizedValue, column, value);
        return true;
    }

    public int getWinner() {
        return this.getCount(Utils.PLAYER1) > this.getCount(Utils.PLAYER2) ? Utils.PLAYER1 : Utils.PLAYER2;
    }

    public int getCount(int player) {
        int count = 0;
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                count += this.cells.get(i).get(j) == player ? 1 : 0;
            }
        }
        return count;
    }

    public void loadBoard(Board board) {
        this.cells = new ArrayList<>();
        for (ArrayList<Integer> row : board.cells) {
            this.cells.add(new ArrayList<Integer>(row));
        }
    }

    public void printBoard() {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        int delta = alphabet.length - this.cells.size() + 1;
        for (int i = 0; i < this.size; i++) {
            System.out.print(Utils.ANSI_WHITE + alphabet[alphabet.length - delta - i] + "  " + Utils.ANSI_RESET);
            for (int j = 0; j < this.size; j++) {
                Integer currentCell = this.cells.get(i).get(j);
                String cellValue = "   ";
                String currentCellColor = Utils.ANSI_PURPLE_BACKGROUND;
                if ((i + j) % 2 == 0) {
                    currentCellColor = Utils.ANSI_WHITE_BACKGROUND;
                }
                if (currentCell == Utils.PLAYER1) {
                    currentCellColor = Utils.ANSI_YELLOW_BACKGROUND;
                    cellValue = " o ";
                } else if (currentCell == Utils.PLAYER2) {
                    currentCellColor = Utils.ANSI_BLUE_BACKGROUND;
                    cellValue = " o ";
                } else if (currentCell == Utils.PREDICT) {
                    currentCellColor = Utils.ANSI_CYAN_BACKGROUND;
                    cellValue = " · ";
                }
                System.out.print(Utils.ANSI_WHITE + currentCellColor + cellValue + Utils.ANSI_RESET);
            }
            System.out.print("\n");
        }
        System.out.print("    ");
        for (int i = 0; i < this.size; i++) {
            System.out.print(Utils.ANSI_WHITE + (i + 1) + "  " + Utils.ANSI_RESET);
        }
        System.out.print("\n\n\n");
    }
}
