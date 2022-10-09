public class Main {
    public static void main(String[] args) throws Exception {
//        System.out.println("Hello world!");
        BankAccount account1 = new BankAccount(0);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 1000; i++){
                    account1.replenish(100);
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 1000; i++){
                    account1.replenish(100);
                }
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(account1.getBalance());
    }
}