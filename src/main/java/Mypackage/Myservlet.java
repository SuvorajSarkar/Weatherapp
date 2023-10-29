package Mypackage;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class Myservlet
 */
public class Myservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Myservlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	//request and response is a object
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//get the city from the user input
		String city=request.getParameter("city");
		
		//api setup
		String Apikey="c2cb01984265d484fb9f240252c15ebf";
		
		String encodedCity = URLEncoder.encode(city, "UTF-8");
		String Apiurl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + Apikey;
		//api integraion
		
		try {
		
		//step-1 create url 
		URL url=new URL(Apiurl);
		//step-2 connection to the url
		HttpURLConnection connection=(HttpURLConnection)url.openConnection();
		
		connection.setRequestMethod("GET");
		
		//get the data from the server
		//InputStream: It's used to read binary data (like bytes) from a source, 
		//often used for network or file input.
		
		InputStream inputstream=connection.getInputStream();
		
		//InputStreamReader: It converts bytes from an InputStream into characters, 
		//making it easier to read text data.
		
		InputStreamReader reader=new InputStreamReader(inputstream);
		//want to store in a string
		
		StringBuilder responsecontent=new StringBuilder();
		//create a scanner to read the reader
		
		Scanner scanner=new Scanner(reader);
		 
		//read line by line append into the response content
		while(scanner.hasNext()) {
			responsecontent.append(scanner.nextLine());
		}
		
		scanner.close();
		 
		//parse the JSON response to extract to temperature,date and humidity
		Gson gson=new Gson();
		JsonObject jsonObject=gson.fromJson(responsecontent.toString(),JsonObject.class);
		//System.out.println(jsonObject);
		
		
		
		//date time
		long datetimestamp=jsonObject.get("dt").getAsLong()*1000;
		String date=new java.util.Date(datetimestamp).toString(); //object type casting
		
		
		//temperature
		double temperaturekelvin=jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
		int temperatureCelcius=(int)(temperaturekelvin-273.15); 
		
		//humidity
		int humidity=jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
		
		//wind speed
		double windspeed=jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
		
		//weather condition
		String weathercondition=jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
		
		//set the data as request attributes(for sending to the jsp page)
		request.setAttribute("date", date);
		request.setAttribute("city", city);
		request.setAttribute("temperature", temperatureCelcius);
		request.setAttribute("weather condition", weathercondition);
		request.setAttribute("wind speed", windspeed);
		request.setAttribute("humidity", humidity);
		request.setAttribute("weather data", responsecontent.toString());
		
		connection.disconnect();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		//forward the request to the weather.jsp page for rendering 
		request.getRequestDispatcher("Index.jsp").forward(request, response);
		
	}

}
