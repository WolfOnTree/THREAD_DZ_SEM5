package org.example;

public class Main {
    static void main() {
        class Counter{
            int value = 0;
        }
        Counter counter = new Counter();

        //создаение потоков NEW
        Thread thread1 = new Thread(() -> {
            synchronized (counter){
                try {
                    IO.println("Поток 1 вывод counter " + counter.value);
                    counter.value++;
                    IO.println("Поток 1 засыпаю");
                    Thread.sleep(5000); //блокировка монитора из за чего второй поток будет BLOCKED
                    IO.println("Поток 1 проснулся");
                    IO.println("Поток 1 освобождаю монитор");
                    counter.wait(); //WAITING
                    IO.println("Поток 1 ожидание 2, итоговый вывод: " + counter.value);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        Thread thread2 = new Thread(() -> {
            try{
                Thread.sleep(1000);
                synchronized (counter) {
                    IO.println("Поток 2 вывод counter: " + counter.value);
                    counter.value++;
                    counter.notify();
                }
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }

        });
        //Потоки загружаются в очередь RUNNABLE
        thread1.start();
        thread2.start();

        try{
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IO.println("потоки завершены"); //основной поток дождался завершения и теперь thread1 и thread2 TERMINATED
    }
}
