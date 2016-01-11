import java.util.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class SynchronizedTutor {
    static StringBuffer buf = new StringBuffer();
    Integer counter = 0;

    Object monitor = new Object();

    public void increment() {
        synchronized (this) {
            counter++;
        }
    }

    public synchronized void increment2() {
        counter++;
    }

    static void log(String s) {
        buf.append(s+"\n");
    }

    class TestThread implements Runnable {
        String threadName;

        public TestThread(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
            for (int i=0;i<1000;i++) {
                //increment();
                synchronized (monitor) {
                    counter++;
                    log(threadName+":"+i+":"+counter);
                }

            }
        }
    }

    @Test
    public void testThread() {
        List<Thread> threads = new ArrayList<Thread>();
        for (int i=0;i<1000;i++) {
            threads.add(new Thread(new TestThread("t"+i)));
        }
        System.out.println("Starting threads");
        for (int i=0;i<1000;i++) {
            threads.get(i).start();
        }
        System.out.println("Waiting for threads");
        try {
            for (int i=0;i<1000;i++) {
                threads.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Uncomment this to look how counter is changing
        //System.out.println(buf);
        System.out.println("counter="+counter);
        assertTrue(counter==1000000);
    }

}
