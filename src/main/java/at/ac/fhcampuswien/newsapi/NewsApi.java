package at.ac.fhcampuswien.newsapi;

import at.ac.fhcampuswien.NewsApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import at.ac.fhcampuswien.newsapi.beans.NewsResponse;
import at.ac.fhcampuswien.newsapi.enums.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;

public class NewsApi {
    public static final String DELIMITER = "&";

    /**
     * For detailed documentation of the API see: https://newsapi.org/docs
     *
     * %s is a filler for endpoint like top-headlines, everything (see /newsapi/enums/Endpoint)
     * q=%s is a filler for specified query
     *
     * Example URL: https://newsapi.org/v2/top-headlines?country=us&apiKey=myKey
     */

    public static final String NEWS_API_URL = "http://newsapi.org/v2/%s?q=%s&apiKey=%s";

    private Endpoint endpoint;
    private String q;
    private String qInTitle;
    private Country sourceCountry;
    private Category sourceCategory;
    private String domains;
    private String excludeDomains;
    private String from;
    private String to;
    private Language language;
    private SortBy sortBy;
    private String pageSize;
    private String page;
    private String apiKey;
    private String urlbase;

    public Endpoint getEndpoint() { return endpoint; }
    public String getQ() { return q; }
    public String getqInTitle() { return qInTitle; }
    public Country getSourceCountry() { return sourceCountry; }
    public Category getSourceCategory() { return sourceCategory; }
    public String getDomains() { return domains; }
    public String getExcludeDomains() { return excludeDomains; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public Language getLanguage() { return language; }
    public SortBy getSortBy() { return sortBy; }
    public String getPageSize() { return pageSize; }
    public String getPage() { return page; }
    public String getApiKey() { return apiKey; }

    public NewsApi(String q, String qInTitle, Country sourceCountry, Category sourceCategory, String domains, String excludeDomains, String from, String to, Language language, SortBy sortBy, String pageSize, String page, String apiKey, Endpoint endpoint) {
        this.q = q;
        this.qInTitle = qInTitle;
        this.sourceCountry = sourceCountry;
        this.sourceCategory = sourceCategory;
        this.domains = domains;
        this.excludeDomains = excludeDomains;
        this.from = from;
        this.to = to;
        this.language = language;
        this.sortBy = sortBy;
        this.pageSize = pageSize;
        this.page = page;
        this.apiKey = apiKey;
        this.endpoint = endpoint;
    }

    protected String requestData() {
        String url = buildURL();
        System.out.println("URL: " + url);
        URL obj = null;

        try { obj = new URL(url); }

        catch (MalformedURLException e) {
            // TODO improve ErrorHandling
            e.printStackTrace();
            throw new NewsApiException("The URL has not been built correctly and is malformed." + "\nERROR: " + e, e);
        }

        HttpURLConnection con;
        StringBuilder response = new StringBuilder();
        try {
            con = (HttpURLConnection) obj.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        catch (IOException e) {
            // TODO improve ErrorHandling
            if(e.getMessage().contains("426")){
                throw new NewsApiException("The date you've entered is too far in the past." + "\nERROR: " + e, e);
            }
            else if(e.getMessage().contains("400")){
                throw new NewsApiException("The country param is not currently supported on the /everything endpoint." + "\nERROR: " + e, e);

            }
            else if(e.getMessage().contains("429")){
                throw new NewsApiException("Too many requests taken, please wait 24h." + "\nERROR: " + e, e);
            }
            else {
                throw new NewsApiException("Error: " + e, e);
            }
        }
        return response.toString();
    }

    protected String buildURL() {
        // TODO ErrorHandling
        try {
            String urlbase = String.format(NEWS_API_URL, getEndpoint().getValue(), getQ(), getApiKey());
            StringBuilder sb = new StringBuilder(urlbase);

            System.out.println(urlbase);
            this.urlbase = urlbase;

            if (getFrom() != null) {
                sb.append(DELIMITER).append("from=").append(getFrom());
            }
            if (getTo() != null) {
                sb.append(DELIMITER).append("to=").append(getTo());
            }
            if (getPage() != null) {
                sb.append(DELIMITER).append("page=").append(getPage());
            }
            if (getPageSize() != null) {
                sb.append(DELIMITER).append("pageSize=").append(getPageSize());
            }
            if (getLanguage() != null) {
                sb.append(DELIMITER).append("language=").append(getLanguage());
            }
            if (getSourceCountry() != null) {
                sb.append(DELIMITER).append("country=").append(getSourceCountry());
            }
            if (getSourceCategory() != null) {
                sb.append(DELIMITER).append("category=").append(getSourceCategory());
            }
            if (getDomains() != null) {
                sb.append(DELIMITER).append("domains=").append(getDomains());
            }
            if (getExcludeDomains() != null) {
                sb.append(DELIMITER).append("excludeDomains=").append(getExcludeDomains());
            }
            if (getqInTitle() != null) {
                sb.append(DELIMITER).append("qInTitle=").append(getqInTitle());
            }
            if (getSortBy() != null) {
                sb.append(DELIMITER).append("sortBy=").append(getSortBy());
            }
            return sb.toString();
        }
        catch(Exception e){ throw new NewsApiException("The URL could not be built correctly." + "\nERROR: " + e, e); }
    }

    public NewsResponse getNews() {
        NewsResponse newsReponse = null;
        String jsonResponse = requestData();
        try {
            if (jsonResponse != null && !jsonResponse.isEmpty()) {

                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    newsReponse = objectMapper.readValue(jsonResponse, NewsResponse.class);
                    if (!"ok".equals(newsReponse.getStatus())) {
                        System.out.println("Error: " + newsReponse.getStatus());
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        //TODO improve Errorhandling
        catch(Exception e){ throw new NewsApiException("ERROR: " + e,e); }
        return newsReponse;
    }
    public String getUrlBase(){ return urlbase; }
}