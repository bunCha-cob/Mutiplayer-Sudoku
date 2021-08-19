package Multiplayer.Sudoku.CLI;

import Multiplayer.Sudoku.Net.GameServer;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "msudoku", mixinStandardHelpOptions = true, version = "msudoku 1.0",
         description = "Starts the Multiplayer Sudoku Server")
public class Server implements Callable<Integer> {

    @Option(names = { "-p", "--port" }, paramLabel = "PORT", description = "Integer port to host the server on (Default 25678).")
    int port = 25678;
 
    @Parameters(paramLabel = "PLAYERCOUNT", arity = "1", description = "Number of players that can join the Server.")
    int playerCount = 4;

    @Parameters(paramLabel = "DIFFICULTY", arity = "1", description = "Difficulty (Between 1-3 inclusive) for the Game.")
    int difficulty = 1;

    public static void main(String args[]) throws Exception {
        int exitCode = new CommandLine(new Server()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Game Server stated with these options:\n");
        System.out.printf("Port: %d\nPlayers: %d\nDiff: %d\n", port, playerCount, difficulty);

        Thread serverThread = new Thread(new GameServer(port, playerCount, difficulty));

        serverThread.start();
        serverThread.join();

        return 0;
    }
}