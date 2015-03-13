/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmltocsv;

import java.util.ArrayList;

/**
 *
 * @author fabiankaupmann
 */
public class ArticleWithResults {
    
    private String articleId;
    private String title;
    private String confidence;
    private ArrayList<String> pageIds = new ArrayList();
    private ArrayList<String> wikiTitles = new ArrayList();
    private ArrayList<Float> scores = new ArrayList();
    
    public ArticleWithResults(String articleId, String confidence){
        this.title = "";
        this.articleId = articleId;
        this.confidence = confidence;
        this.pageIds = new ArrayList();
        this.wikiTitles = new ArrayList();
        this.scores = new ArrayList();
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getPageIds() {
        return pageIds;
    }

    public void setPageIds(ArrayList<String> pageIds) {
        this.pageIds = pageIds;
    }

    public ArrayList<String> getWikiTitles() {
        return wikiTitles;
    }

    public void setWikiTitles(ArrayList<String> wikiTitles) {
        this.wikiTitles = wikiTitles;
    }

    public ArrayList<Float> getScores() {
        return scores;
    }

    public void setScores(ArrayList<Float> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "ArticleWithResults{" + "articleId=" + articleId + ", title=" + title + ", confidence=" + confidence + ", pageIds=" + pageIds + ", wikiTitles=" + wikiTitles + ", scores=" + scores + '}';
    }


    
    
    
}
