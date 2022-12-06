public class Main {
    public static void main(String[] args) {
        Game game = new Game(Utils.HUMAN, Utils.HUMAN, 8);
//        Game game = new Game(Utils.LIGHT_AI, Utils.STRONG_AI, 8);
        game.run();
    }
}
