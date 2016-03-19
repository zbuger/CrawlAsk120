package com.crawlask120.crawl;

import java.util.Set;

public class MyCrawler {
	

	/**
	 * ʹ�����ӳ�ʼ�� URL ����
	 * 
	 * @return
	 * @param seeds
	 *            ����URL
	 */
	private void initCrawlerWithSeeds(String seeds) {
		// for (int i = 0; i < seeds.length; i++)

		LinkQueue.addUnvisitedUrl(seeds);
	}

	/**
	 * ץȡ����
	 * 
	 * @return
	 * @param seeds
	 */
	public void crawling(String seeds) { // �������������ȡ��xxx��ͷ������
		LinkFilter filter = new LinkFilter() {
			public boolean accept(String url) {
				if (url.startsWith("http://www.120ask.com/question"))
					return true;
				else
					return false;
			}
		};
		// ��ʼ�� URL ����
		initCrawlerWithSeeds(seeds);

		Set<String> links = HtmlParserTool.extracLinks(seeds, filter);
		// �µ�δ���ʵ� URL ���
		for (String link : links) {
			LinkQueue.addUnvisitedUrl(link);
		}
		while (!LinkQueue.unVisitedUrlsEmpty()) {
			// ��ͷURL������
			String visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();
			if (visitUrl == null)
				continue;
			DownLoadFile downLoader = new DownLoadFile();
			// ������ҳ
			downLoader.downloadFile(visitUrl);
			// �� url ���뵽�ѷ��ʵ� URL ��
	//		LinkQueue.addVisitedUrl(visitUrl);
		}
	}

	private void initCrawl() {
		
		LinkQueue.removeAllUnvisited();
		LinkQueue.removeAllVisited();
	}

	// main �������
	public static void main(String[] args) {
		MyCrawler crawler = new MyCrawler();
		for (int j = 1; j < 200; j++) {
//			
			crawler.initCrawl();
			crawler.crawling("http://www.120ask.com/list/gaoxueya/"+j);
		
			
		}
	}
}
