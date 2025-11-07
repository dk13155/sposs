import java.util.concurrent.Semaphore;

class SharedBuffer {
    static final int BUFFER_SIZE = 5;
    int count = 0; // number of items in the buffer
    int in = 0, out = 0;
    int[] buffer = new int[BUFFER_SIZE];

    // Semaphores
    Semaphore mutex = new Semaphore(1);
    Semaphore empty = new Semaphore(BUFFER_SIZE);
    Semaphore full = new Semaphore(0);

    // Producer Method
    void produce(int item) {
        try {
            empty.acquire();     // wait if buffer is full
            mutex.acquire();     // enter critical section

            buffer[in] = item;
            in = (in + 1) % BUFFER_SIZE;
            count++;
            System.out.println("Producer produced: " + item);

            mutex.release();     // leave critical section
            full.release();      // signal that buffer has new item
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Consumer Method
    void consume() {
        try {
            full.acquire();      // wait if buffer is empty
            mutex.acquire();     // enter critical section

            int item = buffer[out];
            out = (out + 1) % BUFFER_SIZE;
            count--;
            System.out.println("Consumer consumed: " + item);

            mutex.release();     // leave critical section
            empty.release();     // signal that buffer has space
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Producer extends Thread {
    SharedBuffer buffer;

    Producer(SharedBuffer b) {
        this.buffer = b;
    }

    public void run() {
        for (int i = 1; i <= 10; i++) {
            buffer.produce(i);
            try {
                Thread.sleep(500); // simulate time delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumer extends Thread {
    SharedBuffer buffer;

    Consumer(SharedBuffer b) {
        this.buffer = b;
    }

    public void run() {
        for (int i = 1; i <= 10; i++) {
            buffer.consume();
            try {
                Thread.sleep(800); // simulate time delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class PSemaphore {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer();
        Producer p = new Producer(buffer);
        Consumer c = new Consumer(buffer);

        p.start();
        c.start();
    }
}
