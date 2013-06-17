package pl.mbassara.location.server;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.mbassara.location.Position;

public class PositionsXmlHandler extends DefaultHandler {

	private enum State {
		SUCCESS, NAME, LAT, LONG
	}

	private boolean isSuccess = false;
	private ArrayList<Position> positions = new ArrayList<Position>();

	private State currentState;
	private String tmpName;
	private double tmpLatitude;
	private double tmpLongitude;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (qName.equals("success"))
			currentState = State.SUCCESS;
		else if (qName.equals("name"))
			currentState = State.NAME;
		else if (qName.equals("latitude"))
			currentState = State.LAT;
		else if (qName.equals("longitude"))
			currentState = State.LONG;

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);

		String text = new String(ch, start, length);

		switch (currentState) {
		case SUCCESS:
			isSuccess = Boolean.parseBoolean(text);
			break;
		case NAME:
			tmpName = text;
			break;
		case LAT:
			tmpLatitude = Double.parseDouble(text);
			break;
		case LONG:
			tmpLongitude = Double.parseDouble(text);
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);

		if (qName.equals("position"))
			positions.add(new Position(tmpName, tmpLatitude, tmpLongitude));

	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public ArrayList<Position> getPositions() {
		return positions;
	}
}
