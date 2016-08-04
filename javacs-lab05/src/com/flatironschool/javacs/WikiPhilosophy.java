package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	final static String destURL = "https://en.wikipedia.org/wiki/Philosophy";
	
	public void runWebCrawler(String url) throws IOException, Exception {
		boolean keepCrawling = true;
		String currURL = url;
		ArrayList<String> urlsVisited = new ArrayList<String>();
		
		while(keepCrawling) {
			urlsVisited.add(currURL);
			if(findValidURL(currURL).equals(destURL)) {
				keepCrawling = false;
			} else {
				currURL = findValidURL(currURL);
			}
		}
	}
	
	private String findValidURL(String url) throws IOException, Exception {
		Elements doc = wf.fetchWikipedia(url);
		Element  firstpara = doc.get(0);
		Iterable <Node> iter = new WikiNodeIterable(firstpara);
		int numRParent = 0;
		int numLParent = 0;
		
		for (Node node: iter) {
			if (node instanceof TextNode){
				TextNode accesibleNode = (TextNode)node;
				char[] chars = accesibleNode.text().toCharArray();
					for (char c: chars){
						if (c == '('){
							numRParent++;
						}
						else if (c == ')') {
							numLParent++;
						}
					}
					
				}
			else if ((numLParent == numRParent	) && node instanceof Element){
				Element accesibleNode = (Element)node;
				String tag = accesibleNode.tagName();
					if(tag.equals("a") && isValidLink(accesibleNode)){
					return accesibleNode.attr("abs:href");
					}
			}
		}
		throw new Exception("No links found");
	}
	
	public static boolean isValidLink(Element link){
		Element parent = link.parent();
		String parentTag = parent.tagName();
		return !(parentTag.equals("i") || parentTag.equals("em"));
	}
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		WikiPhilosophy wp = new WikiPhilosophy();
		wp.runWebCrawler(url);
	}
}
