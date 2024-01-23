package edu.usc.Clustering;

import java.util.*;

import org.openqa.selenium.WebElement;

public class GetVectors {

    public static Map Vectors(List<WebElement> elements){
        Map<WebElement, Vector> all_vectors = new HashMap<>();
        for(WebElement each_ele: elements){
            Vector ele_vector = new Vector<>();
            String ele_ID = each_ele.getAttribute("id");
            String ele_name = each_ele.getAttribute("name");
            String ele_class = each_ele.getAttribute("class");
            String ele_type = each_ele.getAttribute("type");
            String text = each_ele.getText();
            ele_vector.add(each_ele.getAttribute("id"));
            ele_vector.add(each_ele.getAttribute("name"));
            ele_vector.add(each_ele.getAttribute("class"));
            ele_vector.add(each_ele.getAttribute("type"));
            ele_vector.add(each_ele.getText());
            all_vectors.put(each_ele, ele_vector);
        }
        return all_vectors;
    }

}
