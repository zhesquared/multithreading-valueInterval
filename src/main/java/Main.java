import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        final ExecutorService threadPool = Executors.newFixedThreadPool(25);

        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis();// start time

        List<Future<String>> threads = new ArrayList<>();

        for (String text : texts) {
            Callable<String> myCallable = new MyCallable().textThread(text);
            Future<String> task = threadPool.submit(myCallable);
            threads.add(task);

        }
        threadPool.shutdown();

        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;

        for (Future<String> thread : threads) {
            int value = Integer.parseInt(thread.get());
            if(value > maxValue) {
                maxValue = value;
            }
            if (value < minValue) {
                minValue = value;
            }
        }

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
        System.out.println(threadPool.isShutdown());
        System.out.println("Maximum range of values is: " + (maxValue - minValue));
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

class MyCallable implements Callable<String> {

    @Override
    public String call() throws Exception {
        Thread.sleep(100);
        return Thread.currentThread().getName();
    }

    public Callable<String> textThread(String text) {
        Callable<String> task = () -> {
            int maxSize = 0;
            for (int i = 0; i < text.length(); i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
            }
            System.out.println(text.substring(0, 100) + " -> " + maxSize);
            return String.valueOf(maxSize);
        };
        return task;
    }
}