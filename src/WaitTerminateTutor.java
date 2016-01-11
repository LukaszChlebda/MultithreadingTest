import org.junit.Test;

public class WaitTerminateTutor {
    static StringBuffer buf = new StringBuffer();
    static void log(String s) {
        buf.append(s+"\n");
    }

    Thread t1, t2;
    Object monitor = new Object();
    int runningThreadNumber = 1;

    class TestThread implements Runnable {
        String threadName;
        public boolean shouldTerminate;

        public TestThread(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
            for (int i=0;;i++) {
                log(threadName+":"+i);
                synchronized(monitor) {
                    try {
                        while (!threadName.equals("t"+runningThreadNumber)) {
                            log("wait for thread "+"t"+runningThreadNumber);
                            monitor.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runningThreadNumber++;
                    if (runningThreadNumber>2) runningThreadNumber=1;
                    monitor.notifyAll();
                    if (shouldTerminate) {
                        log("TERMINATED "+threadName+"!");
                        return;
                    }
                }
            }
        }
    }

    @Test
    public void testThread() {
        final TestThread testThread1 = new TestThread("t1");
        t1 = new Thread(testThread1);
        final TestThread testThread2 = new TestThread("t2");
        t2 = new Thread(testThread2);
        log("Starting threads...");
        t1.start();
        t2.start();

        Thread terminator = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                testThread1.shouldTerminate=true;
                // comment this line out and execute
                testThread2.shouldTerminate=true;
            }
        });
        terminator.start();

        log("Waiting threads to join...");
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(buf);
    }

}
