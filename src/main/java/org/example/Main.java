package org.example;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class safeStructure{
    private  AtomicReference<String> atomicReference = new AtomicReference<>(null);

    public String get(){
        String value = atomicReference.get();

        if (value != null) {
            return value;
        }

        String newValue = UUID.randomUUID().toString();

        if (atomicReference.compareAndSet(null, newValue)) {
            return newValue;
        }

        return atomicReference.get();
    }
}



public class Main {

    volatile static boolean canWePass = true;

    static class safeCountrer{
        private static AtomicInteger counter = new AtomicInteger(0);

        public static void increment(){
            counter.getAndIncrement();
        }

        public static Integer get(){
            return counter.get();
        }
    }


    static void main() {
        //Task 1

        new Thread(() -> {
            while(canWePass){
                try {
                    System.out.println("Работаю");
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("закончил");
        }).start();

         new Thread(() -> {
             try {
                 Thread.sleep(5000);
                 System.out.println("переключаю");
                 canWePass = false;
             }catch (InterruptedException e){
                 Thread.currentThread().interrupt();
             }
         }).start();

        //Task 2

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for(int i = 0; i < 10000; i++){
                executorService.submit(() -> {
                    safeCountrer.increment();
                });
        }

        try {
            Thread.sleep(1000);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }

        executorService.shutdown();

        System.out.println(safeCountrer.get());
        //Task 3

        safeStructure safeStructure = new safeStructure();

        for(int i = 0; i < 4; i++){
            new Thread(() ->{
                System.out.println(safeStructure.get());
            }).start();
        }

    }

}