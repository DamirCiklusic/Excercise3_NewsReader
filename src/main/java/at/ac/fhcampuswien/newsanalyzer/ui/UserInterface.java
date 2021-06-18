package at.ac.fhcampuswien.newsanalyzer.ui;

import at.ac.fhcampuswien.newsanalyzer.ctrl.Controller;
import at.ac.fhcampuswien.newsapi.NewsApi;
import at.ac.fhcampuswien.newsapi.NewsApiBuilder;
import at.ac.fhcampuswien.newsapi.beans.NewsResponse;
import at.ac.fhcampuswien.newsapi.enums.Country;
import at.ac.fhcampuswien.newsapi.enums.Endpoint;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface
{
	private Controller ctrl = new Controller();

	public void getDataFromCtrl1(){
		System.out.println("ABC");

		ctrl.process("ABC", "2021-07-17");
	}

	public void getDataFromCtrl2(){
		// TODO implement me
		ctrl.process("ball", "2021-07-17");
	}

	public void getDataFromCtrl3(){
		// TODO implement me
		ctrl.process("corona", "2021-07-17");
	}

	public void getDataForCustomInput() {
		// TODO implement me
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter keyword: ");
		String choice = scanner.next();
		System.out.print("Enter From-Date(\"2021-07-17\"): ");
		String fromDate = scanner.next();
		System.out.print("Enter Country code (at): ");
		ctrl.process(choice, fromDate);
	}

	public void start() {
		Menu<Runnable> menu = new Menu<>("User Interface");
		menu.setTitle("WÃ¤hlen Sie aus:");
		menu.insert("a", "Choice ABC", this::getDataFromCtrl1);
		menu.insert("b", "Choice BALL", this::getDataFromCtrl2);
		menu.insert("c", "Choice CORONA", this::getDataFromCtrl3);
		menu.insert("d", "Choice User Input:",this::getDataForCustomInput);
		menu.insert("q", "Quit", null);
		Runnable choice;
		while ((choice = menu.exec()) != null) { choice.run(); }
		System.out.println("Program finished");
	}

	protected String readLine() {
		String value = "\0";
		BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			value = inReader.readLine();
		} catch (IOException ignored) {
		}
		return value.trim();
	}

	protected Double readDouble(int lowerlimit, int upperlimit)     {
		Double number = null;
		while (number == null) {
			String str = this.readLine();
			try {
				number = Double.parseDouble(str);
			} catch (NumberFormatException e) {
				number = null;
				System.out.println("Please enter a valid number:");
				continue;
			}
			if (number < lowerlimit) {
				System.out.println("Please enter a higher number:");
				number = null;
			} else if (number > upperlimit) {
				System.out.println("Please enter a lower number:");
				number = null;
			}
		}
		return number;
	}
}