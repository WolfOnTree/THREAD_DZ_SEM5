package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class Main {
    public static void time(Runnable operation){
        long startTime = System.nanoTime();

        operation.run();

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        System.out.println(durationMs);
    }

    public static void FilterStream(List<Integer> list) {
        time(() ->
                list.stream()
                        .filter(n -> n % 2 == 0)
                        .toList()
        );
    }

    public static void AggregationStream(List<Integer> list) {
        time(() ->
                list.stream()
                        .map(n -> n + 10)
                        .toList()
        );
    }

    public static void MultiplicationStream(List<Integer> list) {
        time(() ->
                list.stream()
                        .mapToInt(Integer::intValue)
                        .sum()
        );
    }

    public static void FilterPStream(List<Integer> list) {
        time(() ->
                list.parallelStream()
                        .filter(n -> n % 2 == 0)
                        .toList()
        );
    }

    public static void AggregationPStream(List<Integer> list) {
        time(() ->
                list.parallelStream()
                        .map(n -> n + 10)
                        .toList()
        );
    }

    public static void MultiplicationPStream(List<Integer> list) {
        time(() ->
                list.parallelStream()
                        .mapToInt(Integer::intValue)
                        .sum()
        );
    }

    static void main() {
        int size = 1000000;
        Random rand = new Random();
        List<Integer> list = new ArrayList<>(size);

        for(int i = 0; i<size; i++){
            list.add(rand.nextInt(101));
        }
        FilterStream(list); //16
        FilterPStream(list); //35

        AggregationStream(list); //16
        AggregationPStream(list); //58

        MultiplicationStream(list); //7
        MultiplicationPStream(list); //15
        //все результаты +- запускались по несколько раз

        //во всех ситуациях последовательное выполнение привело к лучшему результату,
        //потому как операция не слишком требовательна
        //вывод параллелизм выгоден при сложных операциях или на крайне больших объёмах данных,
        //потому как на разделение уходит больше ресурсов чем на работу
    }
}
