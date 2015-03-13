/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmltocsv;

/**
 *
 * @author fabiankaupmann
 */
public class csvArticle {
    
    private String articleId;
    private String articleTitle;
    private int pageId;
    private String wikiTitle;
    private float score;
    private float normalizedScore;
    private int frequency;

    
    public csvArticle(String articleId, int pageId, float score, float normalizedScore, int frequency){
        this.articleId = articleId;
        this.pageId = pageId;
        this.score = score;
        this.normalizedScore = normalizedScore;
        this.frequency = frequency;

    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getNormalizedScore() {
        return normalizedScore;
    }

    public void setNormalizedScore(float normalizedScore) {
        this.normalizedScore = normalizedScore;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }


    @Override
    public String toString() {
        return "csvArticle{" + "articleId=" + articleId + ", pageId=" + pageId + ", score=" + score + ", normalizedScore=" + normalizedScore + ", frequency=" + frequency + '}';
    }
    
    
    
    
    
}
