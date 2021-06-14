package server.utility;

import common.data.MeleeWeapon;
import common.data.SpaceMarine;
import common.data.Weapon;
import common.exceptions.CollectionIsEmptyException;
import common.exceptions.DatabaseHandlingException;
import common.utility.Outputer;
import server.App;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Operates the collection itself.
 */
public class CollectionManager {
    private NavigableSet<SpaceMarine> marinesCollection;
    private LocalDateTime lastInitTime;
    private DatabaseCollectionManager databaseCollectionManager;

    public CollectionManager(DatabaseCollectionManager databaseCollectionManager) {
        this.databaseCollectionManager = databaseCollectionManager;

        loadCollection();
    }

    /**
     * @return Marines collection.
     */
    public NavigableSet<SpaceMarine> getCollection() {
        Outputer.println("getCollection(CollectionManager)");
        return marinesCollection;
    }

    /**
     * @return Last initialization time or null if there wasn't initialization.
     */
    public LocalDateTime getLastInitTime() {
        Outputer.println("getLastInitTime(CollectionManager)");
        return lastInitTime;
    }

    /**
     * @return Name of the collection's type.
     */
    public String collectionType() {
        Outputer.println("collectionType(CollectionManager)");
        return marinesCollection.getClass().getName();
    }

    /**
     * @return Size of the collection.
     */
    public int collectionSize() {
        Outputer.println("collectionSize(CollectionManager)");
        return marinesCollection.size();
    }

    /**
     * @return The first element of the collection or null if collection is empty.
     */
    public SpaceMarine getFirst() {
        Outputer.println("getFirst(CollectionManager)");
        return marinesCollection.stream().findFirst().orElse(null);
    }

    /**
     * @param id ID of the marine.
     * @return A marine by his ID or null if marine isn't found.
     */
    public SpaceMarine getById(Long id) {
        Outputer.println("getById(CollectionManager)");
        return marinesCollection.stream().filter(marine -> marine.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * @param marineToFind A marine who's value will be found.
     * @return A marine by his value or null if marine isn't found.
     */
    public SpaceMarine getByValue(SpaceMarine marineToFind) {
        Outputer.println("getByValue(CollectionManager)");
        return marinesCollection.stream().filter(marine -> marine.equals(marineToFind)).findFirst().orElse(null);
    }

    /**
     * @return Sum of all marines' health or 0 if collection is empty.
     */
    public double getSumOfHealth() {
        Outputer.println("getSumOfHealth(CollectionManager)");
        return marinesCollection.stream()
                .reduce(0.0, (sum, p) -> sum += p.getHealth(), Double::sum);
    }

    /**
     * @return Collection content or corresponding string if collection is empty.
     */
    public String showCollection() {
        Outputer.println("showCollection(CollectionManager)");
        if (marinesCollection.isEmpty()) return "Коллекция пуста!";
        return marinesCollection.stream().reduce("", (sum, m) -> sum += m + "\n\n", (sum1, sum2) -> sum1 + sum2).trim();
    }

    /**
     * @return Marine, who has max melee weapon.
     * @throws CollectionIsEmptyException If collection is empty.
     */
    public String maxByMeleeWeapon() throws CollectionIsEmptyException {
        Outputer.println("maxByMeleeWeapon(CollectionManager)");
        if (marinesCollection.isEmpty()) throw new CollectionIsEmptyException();

        MeleeWeapon maxMeleeWeapon = marinesCollection.stream().map(marine -> marine.getMeleeWeapon())
                .max(Enum::compareTo).get();
        return marinesCollection.stream()
                .filter(marine -> marine.getMeleeWeapon().equals(maxMeleeWeapon)).findFirst().get().toString();
    }

    /**
     * @param weaponToFilter Weapon to filter by.
     * @return Information about valid marines or empty string, if there's no such marines.
     */
    public String weaponFilteredInfo(Weapon weaponToFilter) {
        Outputer.println("weaponFilteredInfo(CollectionManager)");
        return marinesCollection.stream().filter(marine -> marine.getWeaponType().equals(weaponToFilter))
                .reduce("", (sum, m) -> sum += m + "\n\n", (sum1, sum2) -> sum1 + sum2).trim();
    }

    /**
     * Remove marines greater than the selected one.
     *
     * @param marineToCompare A marine to compare with.
     * @return Greater marines list.
     */
    public NavigableSet<SpaceMarine> getGreater(SpaceMarine marineToCompare) {
        Outputer.println("getGreater(CollectionManager)");
        return marinesCollection.stream().filter(marine -> marine.compareTo(marineToCompare) > 0).collect(
                TreeSet::new,
                TreeSet::add,
                TreeSet::addAll
        );
    }

    /**
     * Adds a new marine to collection.
     *
     * @param marine A marine to add.
     */
    public void addToCollection(SpaceMarine marine) {
        Outputer.println("addToCollection(CollectionManager)");
        marinesCollection.add(marine);
    }

    /**
     * Removes a new marine to collection.
     *
     * @param marine A marine to remove.
     */
    public void removeFromCollection(SpaceMarine marine) {
        Outputer.println("removeFromCollection(CollectionManager)");
        marinesCollection.remove(marine);
    }

    /**
     * Clears the collection.
     */
    public void clearCollection() {
        Outputer.println("clearCollection(CollectionManager)");
        marinesCollection.clear();
    }

    /**
     * Loads the collection from file.
     */
    private void loadCollection() {
        try {
            Outputer.println("loadCollection(CollectionManager)");
            marinesCollection = databaseCollectionManager.getCollection();
            lastInitTime = LocalDateTime.now();
            Outputer.println("Коллекция загружена.");
            App.logger.info("Коллекция загружена.");
        } catch (DatabaseHandlingException exception) {
            marinesCollection = new TreeSet<>();
            Outputer.println("тест3(CollectionManager)");
            Outputer.printerror("Коллекция не может быть загружена!" + exception.getMessage());
            App.logger.error("Коллекция не может быть загружена!");
        }
    }
}
