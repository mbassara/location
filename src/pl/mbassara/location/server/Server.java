package pl.mbassara.location.server;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import pl.mbassara.location.Position;
import pl.mbassara.location.server.HTTPHelper.Method;

public class Server {

	private static final String url = "http://pastebin.maciekb.linuxpl.info/";

	public static boolean sendPosition(Position position) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", position.getName());
		parameters.put("latitude", position.getLatitude() + "");
		parameters.put("longitude", position.getLongitude() + "");

		try {
			PositionsXmlHandler handler = new PositionsXmlHandler();
			String response = HTTPHelper.sendRequest(url + "send_position", Method.GET, parameters,
					2000);
			System.out.println(response);
			parse(response, handler);
			return handler.isSuccess();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static List<Position> getPositions() {
		HashMap<String, String> parameters = new HashMap<String, String>();

		try {
			PositionsXmlHandler handler = new PositionsXmlHandler();
			String response = HTTPHelper.sendRequest(url + "get_positions", Method.GET, parameters,
					2000);
			parse(response, handler);
			if (handler.isSuccess())
				return handler.getPositions();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return new ArrayList<Position>();
	}

	private static boolean parse(String xml, PositionsXmlHandler handler) {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")), handler);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
