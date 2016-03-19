package com.crawlask120.crawl;

import java.util.Set;

public class MyCrawler {
	

	/**
	 * 使用种子初始化 URL 队列
	 * 
	 * @return
	 * @param seeds
	 *            种子URL
	 */
	private void initCrawlerWithSeeds(String seeds) {
		// for (int i = 0; i < seeds.length; i++)

		LinkQueue.addUnvisitedUrl(seeds);
	}

	/**
	 * 抓取过程
	 * 
	 * @return
	 * @param seeds
	 */
	public void crawling(String seeds) { // 定义过滤器，提取以xxx开头的链接
		LinkFilter filter = new LinkFilter() {
			public boolean accept(String url) {
				if (url.startsWith("http://www.120ask.com/question"))
					return true;
				else
					return false;
			}
		};
		// 初始化 URL 队列
		initCrawlerWithSeeds(seeds);

		Set<String> links = HtmlParserTool.extracLinks(seeds, filter);
		// 新的未访问的 URL 入队
		for (String link : links) {
			LinkQueue.addUnvisitedUrl(link);
		}
		while (!LinkQueue.unVisitedUrlsEmpty()) {
			// 队头URL出队列
			String visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();
			if (visitUrl == null)
				continue;
			DownLoadFile downLoader = new DownLoadFile();
			// 下载网页
			downLoader.downloadFile(visitUrl);
			// 该 url 放入到已访问的 URL 中
	//		LinkQueue.addVisitedUrl(visitUrl);
		}
	}

	private void initCrawl() {
		
		LinkQueue.removeAllUnvisited();
		LinkQueue.removeAllVisited();
	}

	// main 方法入口
	public static void main(String[] args) {
		MyCrawler crawler = new MyCrawler();
		for (int j = 1; j < 200; j++) {
//			
			crawler.initCrawl();
			crawler.crawling("http://www.120ask.com/list/gaoxueya/"+j);
		
			
		}
	}
}
