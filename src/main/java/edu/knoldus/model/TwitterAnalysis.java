package edu.knoldus.model;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Twitter api is used and simple analysis are made.
 */
public class TwitterAnalysis {


  final private String userHandle = "LFC";
  /**
   * Used for converting epoch to days.
   */
  private final long ConvertToDay = 1000 * 60 * 60 * 24;
  /**
   *
   */
  private final int LIMIT = 10;
  /**
   * twitter connector used as pipeline to get data.
   */
  private Twitter twitter;
  /**
   *
   */
  private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /**
   * Constructor : sub you CONSUMER key.
   * secrets and other stuff when you use the app.
   */
  TwitterAnalysis() {
    ConfigurationBuilder configBuilder = new ConfigurationBuilder();
    configBuilder.setDebugEnabled(true)
        .setOAuthConsumerKey(TwitterConfiguration.CONSUMER_KEY)
        .setOAuthConsumerSecret(TwitterConfiguration.CONSUMER_SECRET)
        .setOAuthAccessToken(TwitterConfiguration.ACCESS_TOKEN)
        .setOAuthAccessTokenSecret(TwitterConfiguration.ACCESS_TOKEN_SECRET);
    TwitterFactory tweetFactory = new TwitterFactory(configBuilder.build());
    this.twitter = tweetFactory.getInstance();
  }

  public CompletableFuture<List<Status>> getLatestTweets() {
    return supplyAsync(
        () -> {
          ArrayList<Status> tweets = new ArrayList<Status>();
          try {
            Query setLimit = new Query("@LFC");
            setLimit.setCount(LIMIT);
            QueryResult result = this.twitter.search(setLimit);
            tweets.addAll(result.getTweets());
          } catch (TwitterException ex) {
            System.out.println("Error Occured:" + ex.getMessage());
          }
          return tweets;
        }
    );
  }

  public CompletableFuture<List<Status>> oldToNewerTweets() {
    return supplyAsync(
        () -> {
          ArrayList<Status> tweets = new ArrayList<>();
          try {
            Query olderTweets = new Query("#LFC");
            olderTweets.setCount(LIMIT * LIMIT);
            olderTweets.resultType(Query.ResultType.recent);
            QueryResult queryResult = this.twitter.search(olderTweets);
            tweets.addAll(queryResult.getTweets());
            tweets.sort(Comparator.comparingLong(status -> status.getCreatedAt().getTime()));
          } catch (TwitterException ex) {
            System.out.println("Error Occured:" + ex.getMessage());
          }
          return tweets;
        }
    );
  }

  public CompletableFuture<List<Status>> numberOfRetweets() {
    return supplyAsync(
        () -> {
          ArrayList<Status> tweets = new ArrayList<>();
          try {
            Query olderTweets = new Query("#LFC");
            olderTweets.setCount(LIMIT * LIMIT);
            olderTweets.resultType(Query.ResultType.popular);
            QueryResult queryResult = this.twitter.search(olderTweets);
            queryResult.getTweets().sort((statusOne, statusTwo) ->
                statusTwo.getRetweetCount() - statusOne.getRetweetCount());
            tweets.addAll(queryResult.getTweets());

          } catch (TwitterException ex) {
            System.out.println("Error Occured:" + ex.getMessage());
          }
          return tweets;
        }
    );
  }

  public CompletableFuture<List<Status>> numberOfLikes() {
    return supplyAsync(
        () -> {
          ArrayList<Status> tweets = new ArrayList<>();
          try {
            Query getLikes = new Query("#LFC");
            getLikes.setCount(LIMIT * LIMIT);
            getLikes.resultType(Query.ResultType.popular);
            QueryResult queryResult = this.twitter.search(getLikes);
            queryResult.getTweets().sort((statusOne, statusTwo) ->
                statusTwo.getFavoriteCount() - statusOne.getFavoriteCount());
            tweets.addAll(queryResult.getTweets());
          } catch (TwitterException ex) {
            System.out.println("Error Occured:" + ex.getMessage());
          }
          return tweets;
        }
    );
  }

  public CompletableFuture<List<Status>> getListOfTweetsOn(LocalDate givenDate) {
    return supplyAsync(
        () -> {
          ArrayList<Status> tweets = new ArrayList<>();
          try {
            Query getTweetsOnDate = new Query("#LFC");
            getTweetsOnDate.setCount(LIMIT * LIMIT);
            getTweetsOnDate.setSince(givenDate.toString());
            getTweetsOnDate.resultType(Query.ResultType.recent);
            QueryResult queryResult = this.twitter.search(getTweetsOnDate);
            tweets.addAll(queryResult.getTweets());
          } catch (TwitterException ex) {
            System.out.println("Error Occured:" + ex.getMessage());
          }
          return tweets;
        }
    );
  }

  public CompletableFuture<List<Status>> getLikesOn(String givenKeyWord) {
    return supplyAsync(
        () -> {
          ArrayList<Status> tweets = new ArrayList<>();
          try {
            LocalDateTime start = LocalDateTime.parse("2018-03-05 00:07:07", timeFormatter);
            return this.twitter.getUserTimeline("LFC", new Paging(2, 100))
                .stream()
                .filter(status -> status.getCreatedAt().toInstant().isAfter(start.toInstant(ZoneOffset.MAX)))
                .collect(Collectors.toList());

          } catch (TwitterException ex) {
            System.out.println("Error Occured:" + ex.getMessage());
          }
          return tweets;
        }
    );
  }

}

