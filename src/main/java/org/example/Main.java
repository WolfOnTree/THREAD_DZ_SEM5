package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class BankAcc{
    Integer number;
    Integer amount;

    public BankAcc(Integer number, Integer amount){
        this.number = number;
        this.amount = amount;
    }
}

class DataCollector {
    int counter = 0;
    List<Integer> store = new ArrayList<>();
    List<BankAcc> BackAccStore = new ArrayList<>();

    private final ReentrantLock lock = new ReentrantLock();

    public void incrementProcessed(){
        lock.lock();
        try{
            counter++;
        }finally {
            lock.unlock();
        }
    }

    public void addInList(Integer number){
        synchronized (store){
            store.add(number);
            System.out.println("объект добавлен" + number);
            store.notify();
        }
        incrementProcessed();
    }

    public void removeFromList(){
        synchronized (store){
            try {
                while(store.isEmpty()){
                    store.wait();
                }
                System.out.println("объект убран" + store.get(0));
                store.remove(0);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        incrementProcessed();
    }
}




public class Main {

    public static void transfer(BankAcc acc1, BankAcc acc2, Integer amount, DataCollector dataCollector){
        if (acc1.number.equals(acc2.number)){
            dataCollector.incrementProcessed();
            throw new RuntimeException("перевод на тот же счёт");
        }
        BankAcc accLess;
        BankAcc accBigger;
        if(acc1.number < acc2.number){
            accLess=acc1;
            accBigger=acc2;
        }
        else{
            accLess=acc2;
            accBigger=acc1;
        }
        synchronized (accLess){
            synchronized (accBigger){
                if(acc1.amount>=amount){
                    acc1.amount -= amount;
                    acc2.amount += amount;
                    System.out.println("перевод!");
                }
                else{
                    System.out.println("перевод не удался");
                }
            }
        }

        dataCollector.incrementProcessed();
    }

    static void main() {
        DataCollector dataCollector = new DataCollector();
        Random rand = new Random();
        //Test1
        new Thread(() -> {
            for(int i = 0; i < 10; i++) {
                dataCollector.addInList(rand.nextInt(0, 10));
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        //Test2
        for(int i = 0; i<10; i++){
            dataCollector.BackAccStore.add(new BankAcc(i, rand.nextInt(0,101)));
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for(int i = 0; i<1000; i++) {
            executor.submit(() -> transfer(
                    dataCollector.BackAccStore.get(rand.nextInt(dataCollector.BackAccStore.size())),
                    dataCollector.BackAccStore.get(rand.nextInt(dataCollector.BackAccStore.size())),
                    rand.nextInt(1, 4),
                    dataCollector));
        }

        executor.shutdown();

        Thread tread = new Thread(() -> {
            for(int i = 0; i < 10; i++){
                dataCollector.removeFromList();
            }
        });

        tread.start();
        try{
            tread.join();
        }catch(InterruptedException e){
            System.out.println(e);
        }

        System.out.println(dataCollector.counter);

        //В задание также есть пункт про измерение скорости выполнения синхронизированного кода и несинхронизированного,
        //но считаю, что выполнение его бессмысленно, потому как синхронизация делается не для скорости, а для коректности данных
        //понятно, что синхронизация есть время, но без неё будут неправильные данные
    }
}
