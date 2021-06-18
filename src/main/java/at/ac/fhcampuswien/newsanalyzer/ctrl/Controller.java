package at.ac.fhcampuswien.newsanalyzer.ctrl;

import at.ac.fhcampuswien.NewsApiException;
import at.ac.fhcampuswien.downloads.ArticleDownloader;
import at.ac.fhcampuswien.newsapi.NewsApi;
import at.ac.fhcampuswien.newsapi.NewsApiBuilder;
import at.ac.fhcampuswien.newsapi.beans.Article;
import at.ac.fhcampuswien.newsapi.beans.NewsResponse;
import at.ac.fhcampuswien.newsapi.enums.Country;
import at.ac.fhcampuswien.newsapi.enums.Endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

	public static final String APIKEY = "94647264d6f6447db56483291f42e511";  //TODO add your api key

	public void process(String choice, String fromDate) {
		System.out.println("Start process");
		//TODO implement Error handling
		//TODO load the news based on the parameters
		//TODO implement methods for analysis
		try {
			NewsApi newsApi = getData(choice, fromDate);
			NewsResponse newsResponse = newsApi.getNews();

			if (newsResponse != null) {
				List<Article> articles = newsResponse.getArticles();
				articles.stream().forEach(article -> System.out.println(article.toString()));
				System.out.println("--------------Analytics--------------");
				System.out.println("Number of articles: " + articles.size());
				System.out.println("Provides most articles: " + analiseProvider(articles));
				System.out.println("Author with shortest name: " + shortestAuthorName(articles));
				System.out.println("Articles sorted alphabetically: ");
				List<Article> sortedArticleList = sortArticles(articles);
				sortedArticleList.stream().forEach(article -> System.out.println(article.toString()));
				try {
					String saveLocation = "C:\\Users\\damir\\IdeaProjects\\Excercise3_NewsReader\\src\\main\\java\\at\\ac\\fhcampuswien\\downloads\\DownloadedArticles.txt";
					ArticleDownloader.downloadUsingStream(newsApi.getUrlBase(), saveLocation);
					System.out.println("All articles have been downloaded successfully!\nLocation: " + saveLocation);
				}
				catch (IOException e){
					throw new NewsApiException("Problem with downloading the articles" + "\nERROR: " + e, e);
				}
			}
		}
		catch(NewsApiException e){
			System.out.println(e);
		}
		System.out.println("End process");
	}

	public NewsApi getData(String choice, String fromDate) {
		NewsApi newsApi = new NewsApiBuilder()
				.setApiKey(APIKEY)
				.setQ(choice)
				.setSourceCountry(Country.at)
				.setFrom(fromDate)
				.setEndPoint(Endpoint.TOP_HEADLINES)
				.createNewsApi();
		return newsApi;
	}

	public String analiseProvider(List<Article> articleList) {
		if(articleList.isEmpty()){
			throw new NewsApiException("No news for you.");
		}

		Map<String, Integer> providerNames = new HashMap<>();
		for (Article article : articleList) {
			Integer value = providerNames.get(article.getSource().getName());
			if (value == null) {
				value = 0;
			}
			value++;
			providerNames.put(article.getSource().getName(), value);
		}

		int value = 0;
		String mostCommonName = null;
		for (String name : providerNames.keySet()) {
			int test = providerNames.get(name);
			if (test > value){
				value = test;
				mostCommonName = name;
			}
		}
		return mostCommonName;
	}

	public String shortestAuthorName(List<Article> articleList){
		int maxLength = 100;
		String shortestName = null;
		for (Article article  :articleList) {
			if(article.getAuthor() != null) {
				int length = article.getAuthor().length();
				if (length < maxLength) {
					maxLength = length;
					shortestName = article.getAuthor();
				}
			}
		}
		return shortestName;
	}

	public List<Article> sortArticles(List<Article> articleList){
		List<Article> sortedList = new ArrayList<>();
		int listSize = articleList.size();
		Article toMove = null;

		for (int i = 0; i < listSize; i++) {
			int maxLength = 0;
			for (Article article : articleList) {
				int length = article.getTitle().length();
				if (length > maxLength) {
					toMove = article;
					maxLength = length;
				}
			}
			sortedList.add(toMove);
			articleList.remove(toMove);
		}

		List<Article> sortedListFinal = new ArrayList<>();
		toMove = sortedList.get(0);

		for (int j = 0; j < listSize; j++) {
			for (int i = 0; i < sortedList.size(); i++) {
				if (sortedList.get(0).getTitle().length() <= sortedList.get(i).getTitle().length()) {
					if (sortedList.get(0).getTitle().compareTo(sortedList.get(i).getTitle()) < 0) {
						toMove = sortedList.get(0);
					} else if (sortedList.get(0).getTitle().compareTo(sortedList.get(i).getTitle()) > 0) {
						toMove = sortedList.get(i);
					} else{
						toMove = sortedList.get(0);
					}
				}
			}
			sortedListFinal.add(toMove);
			sortedList.remove(toMove);
		}
		return sortedListFinal;
	}
}