package com.crawlask120.crawl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyCrawlerMultiThread {
	public static List<Thread> childThread = new ArrayList<Thread>();
	private final static int FROM=1;
	private final static int TO=201;
	/**
	 * 使用种子初始化 URL 队列
	 * 
	 * @return
	 * @param seeds
	 *            种子URL
	 */
	private static void initCrawlerWithSeeds(String seeds) {
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

		while (!LinkQueue.unVisitedUrlsEmpty()) {// 这里用多线程
			String visitUrl;
			// 队头URL出队列

			visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();// 很快不会影响吧

			if (visitUrl == null)
				continue;
			DownLoadFile downLoader = new DownLoadFile();
			// 下载网页
			downLoader.downloadFile(visitUrl);
		}
	}

	private void initCrawl() {
		LinkQueue.removeAllUnvisited();
		LinkQueue.removeAllVisited();
	}

	// main 方法入口
	public static void main(String[] args) {
		MyCrawlerMultiThread crawler = new MyCrawlerMultiThread();
		BThread bt = null;
        AThread at = null;
   /*     */
		for (int j =FROM; j < TO; j++) {
			crawler.initCrawl();
			// crawler.crawling("http://www.999ask.com/list/gaoxueya/all/" + j +
			// ".html");
			LinkFilter filter = new LinkFilter() {
				public boolean accept(String url) {
					if (url.startsWith("http://www.120ask.com/question"))
						return true;
					else
						return false;
				}
			};
			String seeds = null;
			seeds ="http://www.120ask.com/list/gaoxueya/"+j;
			// 初始化 URL 队列
			initCrawlerWithSeeds(seeds);

			Set<String> links = HtmlParserTool.extracLinks(seeds, filter);
			// 新的未访问的 URL 入队
			for (String link : links) {
				LinkQueue.addUnvisitedUrl(link);
			} // 进队列
	//		new AThread(new BThread()).start();
			bt=new BThread();
			at=new AThread(bt);
			try {
	            bt.start();
	            at.start();
	            bt.join();
	        } catch (Exception e) {
	            System.out.println("Exception from main");
	        }
		}
	}

}

class CThread extends Thread {
	private String visitUrl;

	public CThread(String url) {
		super("[CThread] Thread");
		this.visitUrl = url;
	};

	public void run() {
		String threadName = Thread.currentThread().getName();
		try {

			DownLoadFile downLoader = new DownLoadFile();
			// 下载网页
			downLoader.downloadFile(visitUrl);

		} catch (Exception e) {
			System.out.println("Exception from " + threadName + ".run");
		}
	}
}

class BThread extends Thread {
	public BThread() {
		super("[BThread] Thread");
	};

	public void run() {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " start.");
		try {
			while (!LinkQueue.unVisitedUrlsEmpty()) {// 这里用多线程
				String visitUrl;
				// 队头URL出队列

				visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();// 很快不会影响吧

				if (visitUrl == null)
					continue;
				new CThread(visitUrl).start();
			}

		} catch (Exception e) {
			System.out.println("Exception from " + threadName + ".run");
		}
	}
}

class AThread extends Thread {
	BThread bt;

	public AThread(BThread bt) {
		super("[AThread] Thread");
		this.bt = bt;
	}

	public void run() {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " start.");
		try {
			bt.join();
			System.out.println(threadName + " end.");
		} catch (Exception e) {
			System.out.println("Exception from " + threadName + ".run");
		}
	}
}