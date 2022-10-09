public class BankAccount extends Account implements BankOperations {

    private double balance;

    public BankAccount(double startBalance) {
        this.balance = startBalance;
    }

    @Override
    public synchronized void replenish(double dollars) {
        if(dollars > 0){
            this.balance += dollars;
        }
    }

    @Override
    public synchronized void withdraw(double dollars) {

    }

    @Override
    public synchronized void transfer(Account srcAccount, Account destAccount, double dollars) {

    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
