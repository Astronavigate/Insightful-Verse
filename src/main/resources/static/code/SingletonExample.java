public class Main {
    public static void main(String[] args) {
        try {
            PrintSpooler spooler = PrintSpooler.getInstance();

            spooler.print("Document 1", "Hello World!");

            spooler.cancelPrint("Document 2");
            spooler.cancelPrint("Document 1");

            PrintSpooler spooler2 = PrintSpooler.getInstance();
        } catch (RuntimeException e) {
            System.out.println("Exception caught: " + e.getMessage());
        }
    }
}

class PrintSpooler {
    private static volatile PrintSpooler instance;
    private String document;
    private String title;

    private PrintSpooler() {
    }

    public static synchronized PrintSpooler getInstance() {
        if (instance == null) {
            synchronized (PrintSpooler.class) {
                if (instance == null) {
                    instance = new PrintSpooler();
                }
            }
        }
        return instance;
    }

    public void print(String title, String document) {
        this.document = document;
        this.title = title;
        System.out.println("Printing " + title + ": " + document);
    }

    public void cancelPrint(String titleToCancel) {
        if (titleToCancel.equals(title)) {
            System.out.println("Canceled printing " + title + ": " + document);
            document = null;
            title = null;
        } else {
            System.out.println("Cannot cancel printing " + titleToCancel + " or no printing in progress.");
        }
    }
}
