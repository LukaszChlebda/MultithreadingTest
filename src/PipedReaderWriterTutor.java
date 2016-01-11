import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;

public class PipedReaderWriterTutor {
    private static final String TEST_LINE = "test line";
    PipedWriter pipedWriter = new PipedWriter();
    PipedReader pipedReader;
    String pipeReaderResult = null;
    Object waiter = new Object();

    class PipeWriterThread implements Runnable {
        public void run() {
            BufferedWriter out = new BufferedWriter(pipedWriter);
            try {
                out.write(TEST_LINE);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class PipeReaderThread implements Runnable {
        public void run() {
            BufferedReader in = new BufferedReader(pipedReader);
            try {
                pipeReaderResult = in.readLine();
                synchronized (waiter) {
                    waiter.notify();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testPipe() throws InterruptedException {
        try {
            pipedReader = new PipedReader(pipedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread t1 = new Thread(new PipeWriterThread());
        Thread t2 = new Thread(new PipeReaderThread());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertEquals(TEST_LINE, pipeReaderResult);
    }

}
