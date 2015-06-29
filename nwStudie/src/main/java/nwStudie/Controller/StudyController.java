package nwStudie.Controller;

import com.google.common.collect.Lists;
import nwStudie.Domain.Proband;
import nwStudie.Domain.TmpData;
import nwStudie.Domain.TmpData2;
import nwStudie.Domain.Welcome2Tmp;
import nwStudie.Persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by fabiankaupmann on 19.04.15.
 */

@Controller
public class StudyController {

    private static final int NUMBER_OF_ARTICLES_PER_CATEGORY_PART_ONE = 6;      // artikel für part 1 (5 Kategorien mit jeweils n Artikeln)
    private static final int NUMBER_OF_ARTICLES_PART_TWO = 5;                   // artikel für part 2 der studie

    //TODO:

    // - pdfs lokal laden

    @Autowired
    private ProbandRepository probandRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ProbandArticleListRepository probandArticleListRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private CompRepository compRepository;

    /*
    Registration
     */
    @RequestMapping(value="/nw", method= RequestMethod.GET)
    public String explanationFormNW(Model model){

        String origin = "nw";

        Proband proband = new Proband();
        proband.setOrigin(origin);

        model.addAttribute("proband", proband);
        return "welcome";
    }


    @RequestMapping(value="/f", method= RequestMethod.GET)
    public String explanationFormFacebook(Model model){

        String origin = "facebook";

        Proband proband = new Proband();
        proband.setOrigin(origin);

        model.addAttribute("proband", proband);
        return "welcome";
    }

    @RequestMapping(value="/tw", method= RequestMethod.GET)
    public String explanationFormTwitter(Model model){

        String origin = "twitter";

        Proband proband = new Proband();
        proband.setOrigin(origin);

        model.addAttribute("proband", proband);
        return "welcome";
    }

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String explanationForm(Model model){

        String origin = "standard";

        Proband proband = new Proband();
        proband.setOrigin(origin);

        model.addAttribute("proband", proband);

        return "welcome";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String explanationSubmit(@ModelAttribute Proband proband, RedirectAttributes redirectAttributes){

        String origin = proband.getOrigin();

        //origin kurzfristig in der Session speichern
        redirectAttributes.addAttribute("origin", origin);

        return "redirect:/registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registrationForm(@ModelAttribute("origin") String origin, Model model){

//        System.out.println("Registration GET");

        Proband proband = new Proband();

        proband.setOrigin(origin);

        model.addAttribute("proband", proband);

        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registrationSubmit(@ModelAttribute Proband proband, RedirectAttributes redirectAttributes){

//        System.out.println("Registration POST");

        String origin = proband.getOrigin();

        //Daten des Probanden in DB speichern und zufällig Artikel zuweisen
        ProbandEntity probandEntity = new ProbandEntity(proband.getSex(),
                Integer.parseInt(String.valueOf(proband.getAge())), proband.getPostalCode(), proband.getRubrik(),
                proband.getInterests(), origin, proband.getGraduation(),
                proband.getInterestInPolitics(), proband.getInterestInCulture(), proband.getInterestInLocalArticles(),
                proband.getInterestInSports(), proband.getInterestInLocalSports());
        probandRepository.save(probandEntity);

        //zugewiesene probandId aus Datenbank abfragen
        int probandId = probandRepository.findNewestId();

        //bel Anzahl an Artikeln für Part 1 zuweisen
        for(byte[] articleId : this.randomArticlesWithCategory(NUMBER_OF_ARTICLES_PER_CATEGORY_PART_ONE)){
            probandArticleListRepository.save(new ProbandArticleListEntity(articleId,probandId));
        }

        //bel Anzahl an Artikeln für Part 2 zuweisen
        for(byte[] articleId : this.randomArticles(NUMBER_OF_ARTICLES_PART_TWO)){
            probandArticleListRepository.save(new ProbandArticleListEntity(articleId,probandId));
        }

        //probandId kurzfristig in der Session speichern
        redirectAttributes.addAttribute("probandId", probandId);

        return "redirect:/articles";
    }

    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    public String firstArticle(@ModelAttribute("probandId") int probandId, Model model){

        TmpData tmpData = new TmpData(probandArticleListRepository.findMinIndexOfArticleListForProband(probandId),
                probandArticleListRepository.findMaxIndexOfArticleListForProband(probandId));

        byte[] articleId = probandArticleListRepository.findArticleIdByPraeferenzId(tmpData.getCurrentIndex());

        ArticleEntity article = articleRepository.findById(articleId);

        double currentArticle = (tmpData.currentIndex - tmpData.getMinIndex());

        double maxArticle = (tmpData.getMaxIndex() - NUMBER_OF_ARTICLES_PART_TWO - tmpData.getMinIndex());

        double progress = Math.ceil((currentArticle / maxArticle) * 100);

//        System.out.println(tmpData.getMinIndex());
//        System.out.println(tmpData.getCurrentIndex());
//        System.out.println(tmpData.getMaxIndex());
//
//        System.out.println("Aktueller Artikel: " + currentArticle);
//        System.out.println("MaxAnzahl artikel: " + maxArticle);
//        System.out.println("Progress: " + progress);

        model.addAttribute("article", article);
        model.addAttribute("tmpData", tmpData);
        model.addAttribute("progress", progress);

        return "articles";
    }


    @Transactional
    @RequestMapping(value="/articles", method = RequestMethod.POST)
    public String articles(@ModelAttribute TmpData tmpData, Model model, RedirectAttributes redirectAttributes){

        if(tmpData.getCurrentIndex() < (tmpData.getMaxIndex() - NUMBER_OF_ARTICLES_PART_TWO)){

            //Praeferenz speichern
            int preference = Integer.parseInt(tmpData.getPreference());
            this.savePreference(preference, tmpData.getCurrentIndex());

            //currentIndex inkrementieren
            int i = tmpData.getCurrentIndex();
            i++;
            tmpData.setCurrentIndex(i);

            byte[] articleId = probandArticleListRepository.findArticleIdByPraeferenzId(tmpData.getCurrentIndex());

            ArticleEntity article = articleRepository.findById(articleId);

            double currentArticle = (tmpData.currentIndex - tmpData.getMinIndex());

            double maxArticle = (tmpData.getMaxIndex() - NUMBER_OF_ARTICLES_PART_TWO - tmpData.getMinIndex());

            double progress = Math.ceil((currentArticle / maxArticle) * 100);

//            System.out.println(tmpData.getMinIndex());
//            System.out.println(tmpData.getCurrentIndex());
//            System.out.println(tmpData.getMaxIndex());
//
//            System.out.println("Aktueller Artikel: " + currentArticle);
//            System.out.println("MaxAnzahl artikel: " + maxArticle);
//            System.out.println("Progress: " + progress);

            model.addAttribute("article", article);
            model.addAttribute("tmpData", tmpData);
            model.addAttribute("progress", progress);

            return "articles";
        }
        else if( tmpData.getCurrentIndex() == (tmpData.getMaxIndex() - NUMBER_OF_ARTICLES_PART_TWO)){
            //Praeferenz speichern
            int preference = Integer.parseInt(tmpData.getPreference());
            this.savePreference(preference, tmpData.getCurrentIndex());

            //currentIndex inkrementieren
            int i = tmpData.getCurrentIndex();
            i++;
            tmpData.setCurrentIndex(i);

            redirectAttributes.addAttribute("ci", tmpData.getCurrentIndex());
            redirectAttributes.addAttribute("mi", tmpData.getMaxIndex());

            return "redirect:/part2";
        } else {

            redirectAttributes.addAttribute("ci", tmpData.getCurrentIndex());
            redirectAttributes.addAttribute("mi", tmpData.getMaxIndex());
            return "redirect:/part2";
        }

    }

    @RequestMapping(value="/part2", method = RequestMethod.GET)
    public String welcome2(@ModelAttribute("ci") Integer currentIndex, @ModelAttribute("mi") Integer maxIndex, Model model){

        Welcome2Tmp welcome2Tmp = new Welcome2Tmp(currentIndex, maxIndex);

        model.addAttribute("welcome2Tmp", welcome2Tmp);

        return "welcomePart2";
    }

    @RequestMapping(value="/part2", method = RequestMethod.POST)
    public String welcome2(@ModelAttribute Welcome2Tmp welcome2Tmp, RedirectAttributes redirectAttributes){

        redirectAttributes.addAttribute("ci", welcome2Tmp.getCurrentIndex());
        redirectAttributes.addAttribute("mi", welcome2Tmp.getMaxIndex());

        return "redirect:/articles2";
    }

    @Transactional
    @RequestMapping(value="/articles2", method = RequestMethod.GET)
    public String articles2(@ModelAttribute("ci") Integer currentIndex, @ModelAttribute("mi") Integer maxIndex, Model model){

        TmpData2 tmpData2 = new TmpData2(currentIndex, maxIndex);

        byte[] articleId = probandArticleListRepository.findArticleIdByPraeferenzId(tmpData2.getCurrentIndex());

        ArticleEntity article = articleRepository.findById(articleId);

        double currentArticle = (tmpData2.currentIndex - tmpData2.getMinIndex());

        double maxArticle = (tmpData2.getMaxIndex() - tmpData2.getMinIndex());

        double progress = Math.ceil((currentArticle / maxArticle) * 100);

//        System.out.println(tmpData2.getMinIndex());
//        System.out.println(tmpData2.getCurrentIndex());
//        System.out.println(tmpData2.getMaxIndex());
//
//        System.out.println("Aktueller Artikel: " + currentArticle);
//        System.out.println("MaxAnzahl artikel: " + maxArticle);
//        System.out.println("Progress: " + progress);

        model.addAttribute("article", article);
        model.addAttribute("tmpData", tmpData2);
        model.addAttribute("progress", progress);

        return "articles2";

    }

    @Transactional
    @RequestMapping(value="/articles2", method = RequestMethod.POST)
    public String articles2(@ModelAttribute TmpData2 tmpData2, Model model){

        if(tmpData2.getCurrentIndex() < tmpData2.getMaxIndex()){

            //Praeferenz speichern
            int comprehensibility = Integer.parseInt(tmpData2.getComprehensibility());
            int complexity = Integer.parseInt(tmpData2.getComplexity());
            int interest = Integer.parseInt(tmpData2.getInterest());

            this.savePreference(comprehensibility, complexity, interest, tmpData2.getCurrentIndex());

            //currentIndex inkrementieren
            int i = tmpData2.getCurrentIndex();
            i++;
            tmpData2.setCurrentIndex(i);

            byte[] articleId = probandArticleListRepository.findArticleIdByPraeferenzId(tmpData2.getCurrentIndex());

            ArticleEntity article = articleRepository.findById(articleId);

            double currentArticle = (tmpData2.currentIndex - tmpData2.getMinIndex());

            double maxArticle = (tmpData2.getMaxIndex() - tmpData2.getMinIndex());

            double progress = Math.ceil((currentArticle / maxArticle) * 100);

//            System.out.println(tmpData2.getMinIndex());
//            System.out.println(tmpData2.getCurrentIndex());
//            System.out.println(tmpData2.getMaxIndex());
//
//            System.out.println("Aktueller Artikel: " + currentArticle);
//            System.out.println("MaxAnzahl artikel: " + maxArticle);
//            System.out.println("Progress: " + progress);

            model.addAttribute("article", article);
            model.addAttribute("tmpData", tmpData2);
            model.addAttribute("progress", progress);

            return "articles2";
        }
        else if( tmpData2.getCurrentIndex() == tmpData2.getMaxIndex()){
            //Praeferenz speichern
            int comprehensibility = Integer.parseInt(tmpData2.getComprehensibility());
            int complexity = Integer.parseInt(tmpData2.getComplexity());
            int interest = Integer.parseInt(tmpData2.getInterest());

            this.savePreference(comprehensibility, complexity, interest, tmpData2.getCurrentIndex());

            return "redirect:/end";
        } else {
            return "redirect:/end";
        }

    }

    @RequestMapping(value="/end")
    public String thankYouSubmit(){
        return "end";
    }

    private void savePreference(int preference, int praeferenzId){
        preferenceRepository.save(new PreferenceEntity(praeferenzId,preference));
    }

    private void savePreference(int comprehensibilty, int complexity, int interest, int praeferenzId){
        compRepository.save(new CompEntity(praeferenzId, comprehensibilty, complexity, interest));
    }

    private ArrayList<byte[]> randomArticlesWithCategory(int numberOfArticlesPerCategory){

        ArrayList<byte[]> articles = new ArrayList<>();

        ArrayList<Integer> randomNumbers = new ArrayList<>();

        ArrayList<byte[]> articlesBielefeld = Lists.newArrayList(articleRepository.findAllArticlesBielefeld());

        for(int i = 0; i < articlesBielefeld.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0;i< Math.min(randomNumbers.size(), numberOfArticlesPerCategory);i++){
            articles.add(articlesBielefeld.get(randomNumbers.get(i)));
        }

        ArrayList<byte[]> articlesKultur = Lists.newArrayList(articleRepository.findAllArticlesKultur());

        randomNumbers.clear();

        for(int i = 0; i < articlesKultur.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0; i< Math.min(randomNumbers.size(), numberOfArticlesPerCategory); i++){
            articles.add(articlesKultur.get(randomNumbers.get(i)));
        }

        ArrayList<byte[]> articlesPolitik = Lists.newArrayList(articleRepository.findAllArticlesPolitik());

        randomNumbers.clear();

        for(int i = 0; i < articlesPolitik.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0; i< Math.min(randomNumbers.size(), numberOfArticlesPerCategory); i++){
            articles.add(articlesPolitik.get(randomNumbers.get(i)));
        }

        ArrayList<byte[]> articlesSportBund = Lists.newArrayList(articleRepository.findAllArticlesSportBund());

        randomNumbers.clear();

        for(int i = 0; i < articlesSportBund.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0; i< Math.min(randomNumbers.size(), numberOfArticlesPerCategory); i++){
            articles.add(articlesSportBund.get(randomNumbers.get(i)));
        }

        ArrayList<byte[]> articlesSportBielefeld = Lists.newArrayList(articleRepository.findAllArticlesSportBielefeld());

        randomNumbers.clear();

        for(int i = 0; i < articlesSportBielefeld.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0; i< Math.min(randomNumbers.size(), numberOfArticlesPerCategory); i++){
            articles.add(articlesSportBielefeld.get(randomNumbers.get(i)));
        }

        //shuffle final list
        Collections.shuffle(articles);

        return articles;

    }

    private ArrayList<byte[]> randomArticles (int numberOfArticles){

        ArrayList<byte[]> articles = new ArrayList<>();

        ArrayList<Integer> randomNumbers = new ArrayList<>();

        ArrayList<byte[]> allArticles = Lists.newArrayList(articleRepository.findAllArticles());

        for(int i = 0; i < allArticles.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0;i< Math.min(randomNumbers.size(), numberOfArticles);i++){
            articles.add(allArticles.get(randomNumbers.get(i)));
        }

        return articles;

    }


}
