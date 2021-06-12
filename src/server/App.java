package server;

import common.exceptions.NotInDeclaredLimitsException;
import common.exceptions.WrongAmountOfElementsException;
import common.utility.Outputer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.commands.*;
import server.utility.*;

/**
 * Main server class. Creates all server instances.
 */
public class App {
    private static final int MAX_CLIENTS = 1000;
    public static Logger logger = LogManager.getLogger("ServerLogger");
    private static String databaseUsername = "postgres";
    private static int port;
    private static String databaseHost;
    private static String databasePassword;
    private static String databaseAddress;

    public static void main(String[] args) {
        if (!initialize(/*args*/)) return;
        DatabaseHandler databaseHandler = new DatabaseHandler(databaseAddress, databaseUsername, databasePassword);
        DatabaseUserManager databaseUserManager = new DatabaseUserManager(databaseHandler);
        DatabaseCollectionManager databaseCollectionManager = new DatabaseCollectionManager(databaseHandler, databaseUserManager);
        CollectionManager collectionManager = new CollectionManager(databaseCollectionManager);
        CommandManager commandManager = new CommandManager(
                new HelpCommand(),
                new InfoCommand(collectionManager),
                new ShowCommand(collectionManager),
                new AddCommand(collectionManager, databaseCollectionManager),
                new UpdateCommand(collectionManager, databaseCollectionManager),
                new RemoveByIdCommand(collectionManager, databaseCollectionManager),
                new ClearCommand(collectionManager, databaseCollectionManager),
                new ExitCommand(),
                new ExecuteScriptCommand(),
                new AddIfMinCommand(collectionManager, databaseCollectionManager),
                new RemoveGreaterCommand(collectionManager, databaseCollectionManager),
                new HistoryCommand(),
                new SumOfHealthCommand(collectionManager),
                new MaxByMeleeWeaponCommand(collectionManager),
                new FilterByWeaponTypeCommand(collectionManager),
                new ServerExitCommand(),
                new LoginCommand(databaseUserManager),
                new RegisterCommand(databaseUserManager)
        );
        Server server = new Server(port, MAX_CLIENTS, commandManager);
        server.run();
        databaseHandler.closeConnection();
    }

    /**
     * Controls initialization.
     */
    private static boolean initialize(/*String[] */) {
        try {
            if (databaseHost == "0") throw new WrongAmountOfElementsException();
            port = 3085;
            if (port < 0) throw new NotInDeclaredLimitsException();
            databaseHost = "postgres";
            databasePassword = "0000";
            databaseAddress = "jdbc:postgresql://localhost:5432/postgres";
            return true;
        } catch (WrongAmountOfElementsException exception) {
            String jarName = new java.io.File(App.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            Outputer.println("Использование: 'java -jar " + jarName + " <port> <db_host> <db_password>'");
        } catch (NumberFormatException exception) {
            Outputer.printerror("Порт должен быть представлен числом!");
            App.logger.fatal("Порт должен быть представлен числом!");
        } catch (NotInDeclaredLimitsException exception) {
            Outputer.printerror("Порт не может быть отрицательным!");
            App.logger.fatal("Порт не может быть отрицательным!");
        }
        App.logger.fatal("Ошибка инициализации порта запуска!");
        return false;
    }
}
