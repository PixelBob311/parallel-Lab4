public class Main {
    static public class ConsoleColors {
        // Reset
        public static final String RESET = "\033[0m";  // Text Reset

        // Regular Colors
        public static final String BLACK = "\033[0;30m";   // BLACK
        public static final String RED = "\033[0;31m";     // RED
        public static final String GREEN = "\033[0;32m";   // GREEN
        public static final String YELLOW = "\033[0;33m";  // YELLOW
        public static final String BLUE = "\033[0;34m";    // BLUE
        public static final String PURPLE = "\033[0;35m";  // PURPLE
        public static final String CYAN = "\033[0;36m";    // CYAN
        public static final String WHITE = "\033[0;37m";   // WHITE

        // Bold
        public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
        public static final String RED_BOLD = "\033[1;31m";    // RED
        public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
        public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
        public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
        public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
        public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
        public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

        // Underline
        public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
        public static final String RED_UNDERLINED = "\033[4;31m";    // RED
        public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
        public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
        public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
        public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
        public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
        public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

        // Background
        public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
        public static final String RED_BACKGROUND = "\033[41m";    // RED
        public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
        public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
        public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
        public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
        public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
        public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

        // High Intensity
        public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
        public static final String RED_BRIGHT = "\033[0;91m";    // RED
        public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
        public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
        public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
        public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
        public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
        public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

        // Bold High Intensity
        public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
        public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
        public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
        public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
        public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
        public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
        public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
        public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

        // High Intensity backgrounds
        public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
        public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
        public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
        public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
        public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
        public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
        public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
        public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE
    }

    static class BankAccount {
        int balance;
        int weight;//нужен для исключения баги с дедлоком при трансфере

        public BankAccount(int startBalance, int weight) {
            this.balance = startBalance;
            this.weight = weight;
        }

        public int getBalance() {
            return this.balance;
        }

        public int getWeight() {
            return this.weight;
        }

        public void replenish(int money) {
            synchronized (this) {
                if (money > 0) {
                    this.balance += money;
                }
            }
        }

        public boolean withdraw(int money) {
            synchronized (this) {
                if (this.balance < money) {
                    return false;
                }
                this.balance -= money;
                return true;
            }
        }

        public void transfer2(BankAccount target, int money) {
            if (this.getWeight() < target.getWeight()) {
                synchronized (this) {
                    synchronized (target) {
                        this.balance -= money;
                        target.balance += money;

                    }
                }
            } else {
                synchronized (target) {
                    synchronized (this) {
                        this.balance -= money;
                        target.balance += money;
                    }
                }
            }
        }

        public void transfer(BankAccount target, int money) {
            if (this.getWeight() < target.getWeight()) {
                synchronized (this) {
                    synchronized (target) {
                        if (this.withdraw(money)) {
                            target.replenish(money);
                        }
                    }
                }
            } else {
                synchronized (target) {
                    synchronized (this) {
                        if (this.withdraw(money)) {
                            target.replenish(money);
                        }
                    }
                }
            }
        }

    }


    public static void main(String[] args) throws InterruptedException {
        BankAccount account1 = new BankAccount(1000, 1);
        BankAccount account2 = new BankAccount(1000, 2);

        testTransfer(account1, account2, 10);
    }

    static void testTransfer(BankAccount account1, BankAccount account2, int money) throws InterruptedException {
        System.out.println("До операции транфера балансы:");
        System.out.println("Аккаунт1: " + account1.getBalance());
        System.out.println("Аккаунт2: " + account2.getBalance());
        final int ITERS = 100;
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < ITERS; i++) {
//                    account1.withdraw(money);
                    account1.transfer(account2, money);
                    System.out.println(ConsoleColors.RED + "THREAD1; iter=" + i + "; Account1: " + account1.getBalance());
                    System.out.println(ConsoleColors.RED + "THREAD1; iter=" + i + "; Account2: " + account2.getBalance());
                }
            }
        };

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < ITERS; i++) {
//                    account1.withdraw(money);
                    account2.transfer(account1, money);
                    System.out.println(ConsoleColors.GREEN + "THREAD2; iter= " + i + "; Account1: " + account1.getBalance());
                    System.out.println(ConsoleColors.GREEN + "THREAD2; iter= " + i + "; Account2: " + account2.getBalance());
                }
            }
        };

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("После операции транфера балансы:");
        System.out.println("Аккаунт1: " + account1.getBalance());
        System.out.println("Аккаунт2: " + account2.getBalance());

    }


    static void transfer(BankAccount a1, BankAccount a2, int money) {
        if (a1.getWeight() < a2.getWeight()) {
            synchronized (a1) {
                synchronized (a2) {
                    a1.balance -= money;
                    a2.balance += money;
                }
            }
        } else {
            synchronized (a2) {
                synchronized (a1) {
                    a1.balance -= money;
                    a2.balance += money;
                }
            }
        }
    }
}