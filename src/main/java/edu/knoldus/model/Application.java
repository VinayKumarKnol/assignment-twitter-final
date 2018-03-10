package edu.knoldus.model;

import twitter4j.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Application {

  private Application() {

  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    TwitterAnalysis twitterAnalysis = new TwitterAnalysis();
    LocalDate today = LocalDate.now();
    CompletableFuture<List<Status>> result =
        twitterAnalysis.getLikesOn("LFC");
    Thread.sleep(5000);
//    result.thenAccept(statuses -> statuses.forEach(status -> System.out.println(status.getRetweetCount())));
//    result.get().forEach(status -> System.out.println(status.getCreatedAt()));
//    result.get().forEach(status -> System.out.println(status.getFavoriteCount()));
//    result.get().forEach(status -> System.out.println(status.getCreatedAt()));
    result.get().stream().map(Status::getFavoriteCount)
        .reduce((a, b) -> a + b);
  }
}
