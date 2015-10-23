package com.vnp.core.common;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CommonXMLfunctions {

	public static String getXML(String url) {
		String line = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			line = EntityUtils.toString(httpEntity);
		} catch (Exception e) {
			line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			line = null;
		}
		return line;
	}

	public final static Document XMLfromString(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
		} catch (Exception e) {
			return null;
		}

		return doc;
	}

	public final static String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid
						.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	public static int numResults(Document doc) {
		Node results = doc.getDocumentElement();
		int res = -1;
		try {
			res = Integer.valueOf(results.getAttributes().getNamedItem("count")
					.getNodeValue());
		} catch (Exception e) {
			res = -1;
		}
		return res;
	}

	public static String getValue(Element item, String str) {
		try {
			NodeList n = item.getElementsByTagName(str);
			return CommonXMLfunctions.getElementValue(n.item(0));
		} catch (Exception e) {
			return "";
		}
	}

	public static String[] login(String userName, String password) {
		String _return[] = new String[3];
		String strXml = CommonXMLfunctions.getXML("".replace("{0}", userName)
				.replace("{1}", password));
		if (strXml != null) {
			Document document = CommonXMLfunctions.XMLfromString(strXml);
			NodeList nodes = document.getElementsByTagName("response");
			Element e = (Element) nodes.item(0);
			_return[0] = CommonXMLfunctions.getValue(e, "success");
			if ("true".equals(_return[0])) {
				_return[1] = CommonXMLfunctions.getValue(e, "token");
				_return[2] = CommonXMLfunctions.getValue(e, "Role_ID");
			}
		}
		return _return;
	}

	public static final String TOTAL_ITEM = "total_item";
	public static final String COUNT = "count";

	// edit contact

}
