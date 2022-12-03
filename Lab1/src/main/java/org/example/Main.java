package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;

public class Main {

  public static void main(String[] args) {
    //Кількість потоків
    int amountOfThreads = 10;
    //Пул потоків на фіксовану кількість(10)
    ExecutorService threadPool = Executors.newFixedThreadPool(amountOfThreads);
    //Список завдання, які приходять з пулу потоків
    ExecutorCompletionService<Long> tasks = new ExecutorCompletionService<Long>(threadPool);

    //Доступ до файлів, які потрібно обробити
    String path = "./files";
    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();

    assert listOfFiles != null;
    //Проходимо по всіх файлах
    for (File file : listOfFiles) {
      if (file.isFile()) {
        //та добавляємо в список завдань наш власний клас
        tasks.submit(new FileCallable(file));
      }
    }

    FileWriter out = null;

    //Якщо робота пройшла успішно, то створюємо файл,
    // куди будемо записувати кількість співпадінь
    try {
      out = new FileWriter("success.txt");
      for (File file : listOfFiles) {
        Future<Long> task = tasks.take();

        //Вихідна строка - результат
        String result = task.get() + " matches";
        out.write(result + System.getProperty("line.separator"));
        System.out.println(result);
      }
      //В іншому випадку оброблюємо помилку
    } catch (InterruptedException | ExecutionException | IOException e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    threadPool.shutdown();
  }
}
