package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBuffer<T>{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    private final List<T> list;
    private final Integer size;

    BoundedBuffer(List<T> list, Integer size){
        this.list = list;
        this.size = size;
    }

    void put(T item){
        lock.lock();
        try {
            while (size == list.size()) {
                notFull.await();
            }
            list.add(item);
            System.out.println("Добавлено элемент" + item);
            notEmpty.signal();
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println("ошибка!");
        } finally {
            lock.unlock();
        }
    }

    T take(){
        lock.lock();
        try {
            while (list.isEmpty()) {
                notEmpty.await();
            }
            var element = list.getFirst();
            list.removeFirst();
            System.out.println("взял элемент" + element);
            notFull.signal();
            return element;
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println("ошибка!");
        }finally {
            lock.unlock();
        }
        return null;
    }
}

public class Main {

    static void main() {
        BoundedBuffer boundedBuffer = new BoundedBuffer<>(new ArrayList<Integer>(), 3);
        Random rand = new Random();

        /*for(int i = 0; i<4; i++){
            Thread thread = new Thread(() -> {

                boundedBuffer.put(rand.nextInt());
            });
            thread.start();
        }

        try {
            Thread.sleep(3000);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        for(int i = 0; i<4; i++){
            Thread thread = new Thread(() -> {
                boundedBuffer.take();
            });
            thread.start();
        }*/


        for(int i = 0; i<4; i++){
            Thread thread = new Thread(() -> {

                boundedBuffer.take();
            });
            thread.start();
        }

        try {
            Thread.sleep(3000);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        for(int i = 0; i<4; i++){
            Thread thread = new Thread(() -> {
                boundedBuffer.put(rand.nextInt());
            });
            thread.start();
        }
    }
}