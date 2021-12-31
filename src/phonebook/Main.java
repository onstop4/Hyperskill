package phonebook;

import java.io.*;
import java.util.*;

class Contact {
    final String number;
    final String name;

    Contact(String number, String name) {
        this.number = number;
        this.name = name;
    }
}

interface SearchAlgo {
    boolean isContact(List<Contact> contacts, String people);

    default int getCountOfElement(List<Contact> contacts, String[] people) {
        int foundCount = 0;
        for (String person : people) {
            if (isContact(contacts, person)) {
                foundCount++;
            }
        }
        return foundCount;
    }
}

class LinearSearch implements SearchAlgo {
    public boolean isContact(List<Contact> contacts, String person) {
        for (Contact contact : contacts) {
            if (person.equals(contact.name)) {
                return true;
            }
        }
        return false;
    }
}

class JumpSearch implements SearchAlgo {
    public boolean isContact(List<Contact> contacts, String personName) {
        int contactsSize = contacts.size();
        if (contactsSize == 0) {
            return false;
        }
        if (personName.equals(contacts.get(0).name)) {
            return true;
        }

        int blockSize = (int) Math.sqrt(contactsSize);
        int blockStartIndex = 0;
        int blockEndIndex = 0;
        while (true) {
            blockEndIndex = Math.min(contactsSize - 1, blockEndIndex + blockSize);
            int compareStatus = personName.compareTo(contacts.get(blockEndIndex).name);
            if (compareStatus < 0) {
                break;
            }
            if (blockEndIndex == contactsSize - 1) {
                return false;
            }
            blockStartIndex += blockSize;
        }

        for (int i = blockEndIndex; i >= blockStartIndex; i--) {
            if (personName.equals(contacts.get(i).name)) {
                return true;
            }
        }
        return false;
    }
}

class BinarySearch implements SearchAlgo {
    public boolean isContact(List<Contact> contacts, String personName) {
        if (contacts.size() == 0) {
            return false;
        }
        if (personName.equals(contacts.get(0).name)) {
            return true;
        }

        Contact first = contacts.get(0);
        Contact last = contacts.get(contacts.size() - 1);

        if (personName.compareTo(first.name) >= 0 && personName.compareTo(last.name) <= 0) {
            int leftIndex = 0;
            int rightIndex = contacts.size() - 1;

            while (leftIndex <= rightIndex) {
                int middleIndex = leftIndex + (rightIndex - leftIndex) / 2;
                Contact middle = contacts.get(middleIndex);
                int compareStatus = personName.compareTo(middle.name);
                if (compareStatus == 0) {
                    return true;
                }
                if (compareStatus < 0) {
                    rightIndex = middleIndex - 1;
                } else {
                    leftIndex = middleIndex + 1;
                }

            }
        }

        return false;
    }
}

class Searcher {
    private SearchAlgo algo;

    public enum Algorithm {
        LINEAR,
        JUMP,
        BINARY
    }

    public Searcher(Algorithm algorithm) {
        if (algorithm == Algorithm.LINEAR) {
            algo = new LinearSearch();
        } else if (algorithm == Algorithm.JUMP) {
            algo = new JumpSearch();
        } else if (algorithm == Algorithm.BINARY) {
            algo = new BinarySearch();
        }
    }

    public int getValidContactCount(List<Contact> contacts, String[] people) {
        return algo.getCountOfElement(contacts, people);
    }
}

interface SortAlgo {
    default int compareContacts(Contact contact1, Contact contact2) {
        return contact1.name.compareTo(contact2.name);
    }

    void sort(List<Contact> contacts);
}

class BubbleSort implements SortAlgo {
    private final boolean descending;

    BubbleSort(boolean descending) {
        this.descending = descending;
    }

    @Override
    public void sort(List<Contact> contacts) {
        boolean needsSorting = true;
        int contactsSize = contacts.size();
        while (needsSorting) {
            needsSorting = false;
            for (int i = 0; i < contactsSize; i++) {
                if (i + 1 < contactsSize) {
                    Contact current = contacts.get(i);
                    Contact next = contacts.get(i + 1);
                    boolean canSortAscending = !descending && compareContacts(current, next) > 0;
                    boolean canSortDescending = descending && current.name.compareTo(next.name) < 0;
                    if (canSortAscending || canSortDescending) {
                        needsSorting = true;
                        contacts.set(i + 1, current);
                        contacts.set(i, next);
                    }
                }
            }
        }
    }
}

class QuickSort implements SortAlgo {
    private void swap(List<Contact> contacts, int index1, int index2) {
        Contact temp = contacts.get(index1);
        contacts.set(index1, contacts.get(index2));
        contacts.set(index2, temp);
    }

    private int getMedianPivotIndex(List<Contact> contacts, int left, int right) {
        int middle = left + (right - left) / 2;

        Contact leftValue = contacts.get(left);
        Contact rightValue = contacts.get(right);
        Contact middleValue = contacts.get(middle);

        if (compareContacts(leftValue, rightValue) < 0 && compareContacts(leftValue, middleValue) > 0) {
            return left;
        }
        if (compareContacts(middleValue, rightValue) < 0) {
            return middle;
        }
        return right;
    }

    private int partition(List<Contact> contacts, int left, int right) {
        int pivotIndex = getMedianPivotIndex(contacts, left, right);
        swap(contacts, pivotIndex, right);
        Contact pivot = contacts.get(right);
        int partitionIndex = left;

        for (int i = left; i < right; i++) {
            Contact current = contacts.get(i);
            if (compareContacts(current, pivot) < 0) {
                swap(contacts, i, partitionIndex++);
            }
        }

        swap(contacts, partitionIndex, right);

        return partitionIndex;
    }

    private void sort(List<Contact> contacts, int left, int right) {
        if (left < right) {
            int pivotIndex = partition(contacts, left, right);
            sort(contacts, left, pivotIndex - 1);
            sort(contacts, pivotIndex + 1, right);
        }
    }

    @Override
    public void sort(List<Contact> contacts) {
        if (contacts.size() > 1) {
            sort(contacts, 0, contacts.size() - 1);
        }
    }
}

class SortTimeOutException extends RuntimeException {
    public SortTimeOutException() {
        super();
    }
}

class Sorter {
    private SortAlgo algo = null;

    public enum Algorithm {
        BUBBLE,
        QUICK
    }

    Sorter(Algorithm algorithm, boolean descending) {
        if (algorithm == Algorithm.BUBBLE) {
            algo = new BubbleSort(descending);
        } else if (algorithm == Algorithm.QUICK) {
            algo = new QuickSort();
        }
    }

    public boolean sort(List<Contact> contacts) {
        if (algo != null) {
            try {
                algo.sort(contacts);
            } catch (SortTimeOutException e) {
                return false;
            }
        }
        return true;
    }
}

class HashEntry {
    public final String key;
    public final Contact contact;
    public HashEntry chainedEntry;

    public HashEntry(String key, Contact contact) {
        this(key, contact, null);
    }

    public HashEntry(String key, Contact contact, HashEntry chainedEntry) {
        this.key = key;
        this.contact = contact;
        this.chainedEntry = chainedEntry;
    }
}

class HashTable {
    private final HashEntry[] table;
    private final int size;

    public HashTable(int size) {
        table = new HashEntry[size];
        this.size = size;
    }

    public boolean put(String key, Contact contact) {
        int idx = calcKey(key);
        HashEntry currentEntry = table[idx];
        HashEntry newEntry = new HashEntry(key, contact);

        if (currentEntry == null) {
            table[idx] = newEntry;
        } else {
            while (currentEntry.chainedEntry != null) {
                currentEntry = currentEntry.chainedEntry;
            }
            currentEntry.chainedEntry = newEntry;
        }
        return true;
    }

    public Contact get(String key) {
        int idx = calcKey(key);
        HashEntry entry = table[idx];
        Contact potentialContact = null;

        while (true) {
            if (entry == null) {
                break;
            }
            potentialContact = entry.contact;
            if (potentialContact == null || key.equals(potentialContact.name)) {
                break;
            }
            entry = entry.chainedEntry;
        }

        return potentialContact;
    }

//    private int calcOriginalHash(String key) {
//        char[] charArray = key.toCharArray();
//        int sum = 0;
//        for (char c : charArray) {
//            if (c % 3 == 0) {
//                c *= c;
//            }
//            sum = (sum + c) % size;
//        }
//        return sum;
//    }

    private int calcKey(String key) {
        return Math.abs(key.hashCode()) % size;
    }

    public int getValidContactCount(String[] people) {
        int count = 0;

        for (String person : people) {
            Contact found = get(person);
            if (found != null && found.name.equals(person)) {
                count++;
            }
        }

        return count;
    }

    public void add(List<Contact> contacts) {
        for (Contact contact : contacts) {
            put(contact.name, contact);
        }
    }
}

public class Main {
    private static void handleFileError(IOException e, String filename) {
        if (e instanceof FileNotFoundException) {
            System.err.printf("File at \"%s\" could not be found.%n", filename);
        } else {
            System.err.printf("Error reading file at \"%s\"%n.", filename);
        }
    }

    private static List<Contact> readDirFile(String filename) {
        List<Contact> contacts = new ArrayList<>();

        try (Scanner fileReader = new Scanner(new File(filename))) {
            while (fileReader.hasNext()) {
                String number = fileReader.next();
                String name = fileReader.nextLine().strip();
                Contact contact = new Contact(number, name);
                contacts.add(contact);
            }
        } catch (IOException e) {
            handleFileError(e, filename);
        }

        return contacts;
    }

    private static List<String> readFindFile(String filename) {
        List<String> people = new ArrayList<>();

        try (Scanner fileReader = new Scanner(new File(filename))) {
            while (fileReader.hasNextLine()) {
                people.add(fileReader.nextLine());
            }
        } catch (IOException e) {
            handleFileError(e, filename);
        }

        return people;
    }

    private static String formatTime(long millis) {
        long millisReal = millis / 1000L;
        long seconds = millis / 1000L;
        long secondsReal = seconds % 60;
        long minutesReal = seconds / 60;
        return String.format("%d min. %d sec. %d ms.", minutesReal, secondsReal, millisReal);
    }

    private static String getFoundOutput(int found, int total, long millis) {
        return String.format("Found %d / %d entries. Time taken: %s", found, total, formatTime(millis));
    }

    private static long doLinearSearch(List<Contact> contacts, String[] people) {
        Searcher searcher = new Searcher(Searcher.Algorithm.LINEAR);
        System.out.println("Start searching (linear search)...");
        long startTime = System.currentTimeMillis();
        int found = searcher.getValidContactCount(contacts, people);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println(getFoundOutput(found, people.length, elapsedTime));
        return elapsedTime;
    }

    private static void doSortAndSearch(List<Contact> contacts, String[] people, long linearTime) {
        Sorter sorter = new Sorter(Sorter.Algorithm.QUICK, false);
        System.out.println("Start searching (bubble sort + jump search)...");
        long sortStartTime = System.currentTimeMillis();
        try
        {
            Thread.sleep(linearTime * 10);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        boolean sortSuccessStatus = sorter.sort(contacts);
        long sortEndTime = System.currentTimeMillis();
        long sortElapsedTime = sortEndTime - sortStartTime;

        Searcher searcher;
        if (sortSuccessStatus) {
            searcher = new Searcher(Searcher.Algorithm.JUMP);
        } else {
            searcher = new Searcher(Searcher.Algorithm.LINEAR);
        }

        long searchStartTime = System.currentTimeMillis();
        int foundCount = searcher.getValidContactCount(contacts, people);
        long searchEndTime = System.currentTimeMillis();
        long searchElapsedTime = searchEndTime - searchStartTime;

        System.out.println(getFoundOutput(foundCount, people.length, sortElapsedTime + searchElapsedTime));
        if (!sortSuccessStatus) {
            System.out.printf("Sorting time: %s - STOPPED, moved to linear search%n", formatTime(sortElapsedTime));
        } else {
            System.out.printf("Sorting time: %s%n", formatTime(sortElapsedTime));
        }
        System.out.printf("Searching time: %s%n", formatTime(searchElapsedTime));

    }

    private static void doSortAndSearch2(List<Contact> contacts, String[] people) {
        Sorter sorter = new Sorter(Sorter.Algorithm.QUICK, false);
        Searcher searcher = new Searcher(Searcher.Algorithm.BINARY);
        System.out.println("Start searching (quick sort + binary search)...");

        long sortStartTime = System.currentTimeMillis();
        sorter.sort(contacts);
        long sortEndTime = System.currentTimeMillis();
        long sortElapsedTime = sortEndTime - sortStartTime;

        long searchStartTime = System.currentTimeMillis();
        int found = searcher.getValidContactCount(contacts, people);
        long searchEndTime = System.currentTimeMillis();
        long searchElapsedTime = searchEndTime - searchStartTime;

        System.out.println(getFoundOutput(found, people.length, sortElapsedTime + searchElapsedTime));
        System.out.printf("Sorting time: %s%n", formatTime(sortElapsedTime));
        System.out.printf("Searching time: %s%n", formatTime(searchElapsedTime));
    }

    private static void doHashSearch(List<Contact> contacts, String[] people) {
        System.out.println("Start searching (hash table)...");
        long creationStartTime = System.currentTimeMillis();
        HashTable table = new HashTable(contacts.size());
        table.add(contacts);
        long creationEndTime = System.currentTimeMillis();
        long creationElapsedTime = creationEndTime - creationStartTime;

        long searchStartTime = System.currentTimeMillis();
        int found = table.getValidContactCount(people);
        long searchEndTime = System.currentTimeMillis();
        long searchElapsedTime = searchEndTime - searchStartTime;

        System.out.println(getFoundOutput(found, people.length, creationElapsedTime + searchElapsedTime));
        System.out.printf("Creating time: %s%n", formatTime(creationElapsedTime));
        System.out.printf("Searching time: %s%n", formatTime(searchElapsedTime));
    }

    public static void main(String[] args) {
        List<Contact> contacts = readDirFile("C:\\Users\\Public\\directory.txt");
        List<Contact> originalContacts = contacts;
        List<String> people = readFindFile("C:\\Users\\Public\\find.txt");

        String[] peopleArray = people.toArray(new String[0]);

        long linearTime = doLinearSearch(contacts, peopleArray);
        System.out.println();
        contacts = new ArrayList<>(originalContacts);
        doSortAndSearch(contacts, peopleArray, linearTime);
        System.out.println();
        contacts = new ArrayList<>(originalContacts);
        doSortAndSearch2(contacts, peopleArray);
        System.out.println();
        contacts = new ArrayList<>(originalContacts);
        doHashSearch(contacts, peopleArray);
    }
}
