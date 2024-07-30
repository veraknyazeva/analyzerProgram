import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final int LENGTH = 100_000;
    private static final String LETTERS = "abc";
    private static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);


    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                String text = generateText(LETTERS, LENGTH);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException ex) {
                    throw new RuntimeException();
                }
            }
        }).start();

        Thread a = createNewThread(queueA, 'a');
        Thread b = createNewThread(queueB, 'b');
        Thread c = createNewThread(queueC, 'c');

        a.start();
        b.start();
        c.start();

        a.join();
        b.join();
        c.join();
    }

    private static Thread createNewThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = findMaxCharCount(queue, letter);
            System.out.println("Максимальное количество символов " + letter + ": " + max);
        });
    }

    private static int findMaxCharCount(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            text = queue.take();
            for (char c : text.toCharArray()) {
                if (c == letter) {
                    count++;
                }
            }
            if (count > max) {
                max = count;
            }
        } catch (InterruptedException ex) {
            System.out.println(Thread.currentThread().getName());
            return -1;
        }
        return max;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
