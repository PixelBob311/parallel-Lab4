public interface BankOperations {
    //Thread safe operations
    void replenish(double dollars);

    void withdraw(double dollars);

    void transfer(Account srcAccount, Account destAccount, double dollars);

}
