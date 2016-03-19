package com.crawlask120.shuffle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class FileUtil {
	private static String filePath = "temp";

	public static File[] getAllFiles(String filePath) {// UTF-8
		File root = new File(filePath);
		File[] files = root.listFiles();
		return files;
	}

	public static String openFile(File fileName, String encode) {
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encode));
			String szContent = "";
			String szTemp;
			while ((szTemp = bis.readLine()) != null) {
				szContent += szTemp + "\n";
			}
			bis.close();
			return szContent;
		} catch (Exception e) {
			return "";
		}
	}

	public static void writeLog(List<String> itemList) {
		try {
			String path = "data\\data_ask120.txt";
			File file = new File(path);
			if (!file.exists())
				file.createNewFile();

			FileOutputStream out = new FileOutputStream(file, true); // 如果追加方式用true

			StringBuffer sb = new StringBuffer();
			Iterator<String> it = itemList.iterator();
			while (it.hasNext()) {
				sb.append(it.next() + "\n");
			}

			out.write(sb.toString().getBytes("utf-8"));// 注意需要转换对应的字符集
			out.close();
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());
		} finally {

		}
	}

	public static String getContent(File file) throws ParserException {
		String eL1 = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";// 正则表达式匹配时间
		NodeFilter titleFilter = new HasAttributeFilter("id", "d_askH1");
		NodeFilter infoFilter = new HasAttributeFilter("class", "b_askab1");
		NodeFilter describeFilter = new HasAttributeFilter("id", "d_msCon");// 病人自己的描述与想获得的帮助
		NodeFilter answerFilter = new HasAttributeFilter("class", "b_answerli");

		Parser parser1 = new Parser();
		Parser parser2 = new Parser();
		Parser parser3 = new Parser();
		Parser parser4 = new Parser();

		Pattern p1 = Pattern.compile(eL1);

		HtmlParser.totalFileNum++;
		// file=new File("temp\\www.120ask.com_question_61934295.htm.html");
		// if (i == 1) // 只显示一条
		// break;
		parser1.setInputHTML(FileUtil.openFile(file, "UTF-8"));
		parser2.setInputHTML(FileUtil.openFile(file, "UTF-8"));
		parser3.setInputHTML(FileUtil.openFile(file, "UTF-8"));
		parser4.setInputHTML(FileUtil.openFile(file, "UTF-8"));

		NodeList nodes = new NodeList();
		nodes.add(parser1.extractAllNodesThatMatch(titleFilter));
		nodes.add(parser2.extractAllNodesThatMatch(infoFilter));
		nodes.add(parser3.extractAllNodesThatMatch(describeFilter));
		nodes.add(parser4.extractAllNodesThatMatch(answerFilter));

		// System.out.println("显示文件:" + file.getPath()+totalFileNum);
		// System.out.println("nodes.size():" + nodes.size());
		StringBuffer textLine = new StringBuffer();
		StringBuffer splitLine = new StringBuffer();
		if (nodes != null) {

			String date = "";
			for (int j = 0; j < nodes.size(); j++) {
				Node textNode = (Node) nodes.elementAt(j);
				if (j == 0) {
					textLine.append(HtmlParser.totalFileNum + "|" + textNode.toPlainTextString() + "|null|");
				} else if (j == 1) {// 获取一部分：主要是病人信息a
					NodeList infoList = new NodeList();
					infoList = textNode.getChildren();
					int realNode = 0;
					for (int m = 0; m < infoList.size() - 3; m++) {// listnode很多空格
						boolean first = true;
						Node tmp = (Node) infoList.elementAt(m);
						String textTmp = tmp.toPlainTextString();
						Matcher matcher = p1.matcher(textTmp);

						if (textTmp.trim().length() != 0) {
							if (first) {
								textTmp = textTmp.replace("岁", "");
								first = false;
							}
							if (realNode == 1) {
								boolean dateFlag = matcher.matches();
								if (dateFlag) {// 是不是时间，正则一下
									// System.out.print(null+"|");

									textLine.append("null|");

									realNode++;
								}
							}
							if (matcher.matches()) {// 是时间
								// textLine.append(textTmp + "|");
								date = textTmp + "|";
							} else {
								textLine.append(textTmp.replace(" ", "") + "|");
							}
							realNode++;
						}
					}
				} else if (j == 2) {// 病情描述，与想获得的帮助
					NodeList descList = new NodeList();
					descList = textNode.getChildren();// 有两个或者一个孩子
					// System.out.println("descList.size()" + descList.size());
					int realNode = 0;
					for (int m = 0; m < descList.size(); m++) {// listnode很多空格
						if ((m == descList.size() - 1) && realNode == 1) {
							textLine.append("null|");
						}
						Node tmp = (Node) descList.elementAt(m);
						String textTmp = tmp.toPlainTextString();
						if (textTmp.trim().length() != 0) {
							textLine.append(textTmp.trim().replaceAll(" ", "").replaceAll("\n", "") + "|");
							realNode++;
						}
					}
					textLine.append(date);
				} else if (j >= 3) {
					NodeList ansList = new NodeList();
					ansList = textNode.getChildren();
					Parser temParser1 = new Parser();
					Parser temParser2 = new Parser();

					temParser1.setInputHTML(ansList.toHtml());
					temParser2.setInputHTML(ansList.toHtml());

					NodeFilter doctorFilter = new TagNameFilter("A");
					NodeFilter ansFilter = new HasAttributeFilter("class", "crazy_new");
					// System.out.println("ansList.size():" + ansList.size());

					splitLine.append(textLine.toString()
							+ ((Node) temParser1.extractAllNodesThatMatch(doctorFilter).elementAt(1))
									.toPlainTextString()
							+ "医生|"
							+ ((Node) temParser2.extractAllNodesThatMatch(ansFilter).elementAt(0)).toPlainTextString()
									.replaceAll("&nbsp;", "").replaceAll(" ", "").replaceAll("\n", "")
									.replaceFirst("指导意见", "|指导意见")
							+ "|null|\n");
				}
			}
			// System.out.println(textLine.toString());
			// FileUtil.writeLog(textLine.toString().replaceAll("\n","").replaceAll("&nbsp;",""));
		}
		System.out.println(HtmlParser.totalFileNum);
		return splitLine.toString();

	}
	public static void writeContent() throws ParserException {
		File[] files = FileUtil.getAllFiles(filePath);

		try {
			String path = "data\\data_ask120.txt";
			File dataFile = new File(path);
			if (!dataFile.exists())
				dataFile.createNewFile();

			FileOutputStream out = new FileOutputStream(dataFile, true); // 如果追加方式用true
			for (File file : files) {
				String content = FileUtil.getContent(file);
				if (content == null)
					break;
				StringBuffer sb = new StringBuffer();
				sb.append(content);
				
				out.write(sb.toString().getBytes("utf-8"));// 注意需要转换对应的字符集*/
			}

			out.close();
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());
		} finally {

		}
	}
	public static void main(String[] args) throws Exception {
	}

}
