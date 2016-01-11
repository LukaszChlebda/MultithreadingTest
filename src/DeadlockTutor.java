import org.junit.Test;

public class DeadlockTutor {
    Thread t1, t2;
    Account account1 = new Account(100);
    Account account2 = new Account(50);

    static StringBuffer buf = new StringBuffer();
    static void log(String s) {
        buf.append(s+"\n");
    }

    class Account {
        double balance;
        int id;

        public Account(double balance) {
            this.balance = balance;
        }

        void withdraw(double amount){
            balance -= amount;
        }

        void deposit(double amount){
            balance += amount;
        }

        synchronized void transfer(Account from, double amount) {
            // block the current account
            deposit(amount);
            // block the account, from which transfer is done
            from.withdraw(amount);
        }
    }

    @Test
    public void testDeadlock() {
        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<100000;i++) {
                    account1.transfer(account2, 30);
                }
            }
        });
        t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<100000;i++) {
                    account2.transfer(account1, 30);
                }
            }
        });
        System.out.println("Starting threads");
        t1.start();
        t2.start();

        System.out.println("Waiting for threads");
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(buf);
    }

}
