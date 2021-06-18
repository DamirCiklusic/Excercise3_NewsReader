package at.ac.fhcampuswien;

public class NewsApiException extends RuntimeException{
    public NewsApiException(String errorMessage, Throwable error){
        super(errorMessage, error);
    }
    public NewsApiException(String errorMessage){
        super(errorMessage);
    }
}