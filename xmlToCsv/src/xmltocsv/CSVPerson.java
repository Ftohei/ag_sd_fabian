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
public class CSVPerson {
    private String name;
    private ArrayList<ArticleWithResults> articleList;
    
//    private ArrayList<String> urls = new ArrayList();
 
    public CSVPerson(String name, ArrayList<String> articleIds, ArrayList<String> confidences){
        this.articleList = new ArrayList();
        this.name = name;
        this.addArticles(articleIds, confidences);
    }

    private void addArticles(ArrayList<String> articleIds, ArrayList<String> confidences){
        for(int i = 0; i<articleIds.size(); i++){
            
            this.articleList.add(new ArticleWithResults(articleIds.get(i),confidences.get(i)));
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ArticleWithResults> getArticleList() {
        return articleList;
    }

    public void setArticleList(ArrayList<ArticleWithResults> articleList) {
        this.articleList = articleList;
    }

    @Override
    public String toString() {
        return "CSVPerson{" + "name=" + name + ", articleList=" + articleList + '}';
    }

    
}
