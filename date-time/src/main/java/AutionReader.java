import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AutionReader {

	private static final String FILENAME = "AuctionResults.xml";

	private static final String STEP_NAME = "Step2";

	private static final String DELIVERY_DATE = "17-06-2023";

	private static final String AUCTION_PERIOD_DELIVERY_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static void main(String[] args) {

		Date deliveryDate = getDate(DELIVERY_DATE, null);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(FILENAME));
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("AuctionDeliveryPeriod");

			// Logic
			int step2Count = 0;
			int step1Count = 0;
			for (int temp = 0; temp < list.getLength(); temp++) {
				Node node = list.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					String periodTo = element.getElementsByTagName("PeriodTo").item(0).getTextContent();

					String state = element.getElementsByTagName("State").item(0).getTextContent();

//					Date periodFromDate = getDate(DELIVERY_DATE, AUCTION_PERIOD_DELIVERY_FORMAT);
					Date periodFromDate = getDate(DELIVERY_DATE, null);
					Date periodToDate = getDate(periodTo, AUCTION_PERIOD_DELIVERY_FORMAT);

					if (periodToDate.compareTo(periodFromDate) == 1 && "Ended".equals(state) && "Step1".equals(STEP_NAME)) {
						step1Count++;
						break;
					} else if ("Step2".equals(STEP_NAME)) {
						periodFromDate = getUpdatedDate(periodFromDate, 4 * (step2Count + 1), Calendar.HOUR);
						if (periodToDate.compareTo(periodFromDate) == 0 && "Ended".equals(state)) {
							step2Count++;
						}
					}
				}
			}
			if (step1Count == 1) {
				System.out.println("Step 1 Condition is met !");
			} else if (list.getLength() - 1 == step2Count) {
				System.out.println("Step 2 Condition is met !");
			} else {
				System.out.println("None of the conditions are met !");
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	private static Date getDate(String input, String inputFormat) {
		if (inputFormat == null) {
			inputFormat = "dd-MM-yyyy";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
		Date date = null;
		try {
			date = formatter.parse(input);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	private static Date getUpdatedDate(Date inputDate, int delta, int type) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		calendar.add(type, delta);
		return calendar.getTime();
	}

}
