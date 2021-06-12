package client;

import client.utility.AuthHandler;
import client.utility.UserHandler;
import common.exceptions.NotInDeclaredLimitsException;
import common.exceptions.WrongAmountOfElementsException;
import common.utility.Outputer;

import java.util.Scanner;

/**
 * Main client class. Creates all client instances.
 */
public class App {
    public static final String PS1 = "$ ";
    public static final String PS2 = "> ";

    private static final int RECONNECTION_TIMEOUT = 5 * 1000;
    private static final int MAX_RECONNECTION_ATTEMPTS = 5;

    private static String host = "127.0.0.1";
    private static int port = 3085;

    public static void main(String[] args) {
        if (!initialize(/*args*/)) return;
        Scanner userScanner = new Scanner(System.in);
        AuthHandler authHandler = new AuthHandler(userScanner);
        UserHandler userHandler = new UserHandler(userScanner);
        Client client = new Client(host, port, RECONNECTION_TIMEOUT, MAX_RECONNECTION_ATTEMPTS, userHandler, authHandler);
        client.run();
        userScanner.close();
    }

    /**
     * Controls initialization.
     */
    private static boolean initialize(/*String[] hostAndPortArgs*/) {
        try {
            if (host == null) throw new WrongAmountOfElementsException();
            //host = hostAndPortArgs[0];
            //port = Integer.parseInt(hostAndPortArgs[1]);
            if (port < 0) throw new NotInDeclaredLimitsException();
            return true;
        } catch (WrongAmountOfElementsException exception) {
            String jarName = new java.io.File(App.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            Outputer.println("Использование: 'java -jar " + jarName + " <host> <port>'");
        } catch (NumberFormatException exception) {
            Outputer.printerror("Порт должен быть представлен числом!");
        } catch (NotInDeclaredLimitsException exception) {
            Outputer.printerror("Порт не может быть отрицательным!");
        }
        return false;
    }
}

