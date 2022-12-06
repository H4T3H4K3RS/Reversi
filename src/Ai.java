import java.net.URI;
import java.util.ArrayList;

public class Ai {
    private final int type;

    Ai(int type) {
        this.type = type;
    }

    public ArrayList<Integer> getSolution(Board board, int player) {
        if (this.type == Utils.LIGHT_AI) {
            return this.getLightSolution(board, player);
        }
        return this.getStrongSolution(board, player);
    }

    private ArrayList<Integer> getLightSolution(Board board, int player) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(0);
        result.add(0);
        double mx = -1;
        for (int i = 0; i < board.size; i++) { // board.size должно быть инкапсулировано
            for (int j = 0; j < 8; j++) {
                if (board.cells.get(i).get(j) == Utils.PREDICT) {
                    double curr = functionCount(i, j, board, -player);
                    if (i == 7 && j == 7 || i == 7 && j == 0 || i == 0 && j == 7 || i == 0 && j == 0) {
                        curr += 0.8;
                    } else if (i == 7 || j == 7 || i == 0 || j == 0) {
                        curr += 0.4;
                    }
                    if (curr >= mx) {
                        mx = curr;
                        result.set(0, i);
                        result.set(1, j);
                    }
                }
            }
        }
        return result;
    }

    private ArrayList<Integer> getStrongSolution(Board board, int player) {
        Ai shadow = new Ai(-player);
        ArrayList<Integer> result = new ArrayList<>();
        result.add(0);
        result.add(0);
        double mx = 0;
        for (int i = 0; i < board.size; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.cells.get(i).get(j) == Utils.PREDICT) {
                    double curr = functionCount(i, j, board, -player);
                    if (i == 7 && j == 7 || i == 7 && j == 0 || i == 0 && j == 7 || i == 0 && j == 0) {
                        curr += 0.8;
                    } else if (i == 7 || j == 7 || i == 0 || j == 0) {
                        curr += 0.4;
                    }
                    Board newBoard = board.copy();
                    newBoard.setCell(i, j, -player);
                    ArrayList<Integer> shadowMove = shadow.getLightSolution(board, player);
                    double penalty = functionCount(shadowMove.get(0), shadowMove.get(1), newBoard, -player);
                    if (curr - penalty >= mx) {
                        mx = curr;
                        result.set(0, i);
                        result.set(1, j);
                    }
                }
            }
        }
        return result;
    }

    private double functionCount(int i, int j, Board board, int player) {
        double ans = 0;
        for (int incR = -1; incR < 2; incR++) {
            for (int incC = -1; incC < 2; incC++) {
                try {
                    if (board.cells.get(i + incR).get(j + incC) == -player) {
                        int ch = 0;
                        for (int r = i + incR, c = j + incC; r < board.size && r >= 0 && c >= 0 && c < board.size; r += incR, c += incC) {
                            if (board.cells.get(r).get(c) == 0) {
                                break;
                            }
                            if (board.cells.get(r).get(c) == player) {
                                ans += ch;
                                break;
                            }
                            if (r == 0 || r == 7 || c == 7 || c == 0) {
                                ch += 2;
                            } else {
                                ch++;
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return ans;
    }

    public ArrayList<Integer> getSolution(Board board) {
        return this.getSolution(board, Utils.PLAYER2);
    }
}
