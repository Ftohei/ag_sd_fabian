
package de.citec.util;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;

import com.kennycason.kumo.bg.RectangleBackground;

import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;


import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author swalter
 */
public class TagCloud {
    
    public static void createCloud(Map<String,Integer> words, String path_to_save){
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        List<WordFrequency> wordFrequencies = new ArrayList<>();
        for(String word:words.keySet()){
            WordFrequency w = new WordFrequency(word, words.get(word));
            wordFrequencies.add(w);
        }
        
        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        
        wordCloud.setPadding(0);
        wordCloud.setBackground(new RectangleBackground(dimension));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile(path_to_save);
    }
    
}
