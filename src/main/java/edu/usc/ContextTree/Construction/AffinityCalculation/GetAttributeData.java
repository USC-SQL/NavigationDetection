package edu.usc.ContextTree.Construction.AffinityCalculation;

import edu.usc.ContextTree.FunctionalArea;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;

import java.util.*;

public class GetAttributeData {

    public ArrayList AttributeData (WebDriver refDriver, WebElement web_ele){
        ArrayList ele_data = new ArrayList();

        String ele_text_data = GetTextData(web_ele);

        String id = web_ele.getAttribute("id");
        String name = web_ele.getAttribute("name");
        String text = web_ele.getText();
        /*
        String ele_class = web_ele.getAttribute("class");
        String role = web_ele.getAttribute("role");
        String value = web_ele.getAttribute("value");
        String all_theme = TransformString(id) + " " + TransformString(name) + " " + TransformString(text) + " ";
        all_theme += TransformString(ele_class) + " " + TransformString(role) + " " + TransformString(value);
        */
        ele_data.add(TransformString(id));
        ele_data.add(TransformString(name));
        ele_data.add(TransformString(text));

        //Finds MBR of element
        Point point = web_ele.getLocation();
        int TOP_LEFT_X = point.getX();
        int TOP_LEFT_Y = point.getY();
        int BOTTOM_RIGHT_X = point.getX() + web_ele.getSize().getWidth();
        int BOTTOM_RIGHT_Y = point.getY() + web_ele.getSize().getHeight();

        if((TOP_LEFT_X == 0 || TOP_LEFT_X < 0) && (TOP_LEFT_Y == 0 || TOP_LEFT_Y < 0)){
            if((BOTTOM_RIGHT_X == 0 || BOTTOM_RIGHT_X < 0) && (BOTTOM_RIGHT_Y == 0 || BOTTOM_RIGHT_Y < 0)){
                TOP_LEFT_X = web_ele.getLocation().getX();
                TOP_LEFT_Y = web_ele.getLocation().getY();
                BOTTOM_RIGHT_X = TOP_LEFT_X + web_ele.getRect().getWidth();
                BOTTOM_RIGHT_Y = TOP_LEFT_Y + web_ele.getRect().getHeight();
            }
        }

        List<Integer> MBR = new ArrayList<Integer>();
        MBR.add(TOP_LEFT_X);
        MBR.add(TOP_LEFT_Y);
        MBR.add(BOTTOM_RIGHT_X);
        MBR.add(BOTTOM_RIGHT_Y);


        //Finds centroid of element
        double midX = (TOP_LEFT_X + BOTTOM_RIGHT_X)/2;
        double midY = (TOP_LEFT_Y + BOTTOM_RIGHT_Y)/2;
        List<Double> centroid = new ArrayList<Double>();
        centroid.add(midX);
        centroid.add(midY);

        //Finds background color of element (via ancestry search)
        WebElement body = refDriver.findElement(By.tagName("body"));
        String background_color = GetBackgroundColor(body, web_ele);

        ele_data.add(MBR);
        ele_data.add(centroid);
        ele_data.add(ele_text_data);
        ele_data.add(background_color);
        return ele_data;
    }

    public String GetBackgroundColor(WebElement body, WebElement web_ele){
        while(!web_ele.equals(body)){
            WebElement parent = web_ele.findElement(By.xpath(".."));
            String color = parent.getCssValue("background-color");
            if(color.equals("rgba(0, 0, 0, 0)")){
                web_ele = parent;
            } else {
                return color;
            }
        }
        return "rgba(0, 0, 0, 0)";
    }

    public String GetTextData(WebElement web_ele){
        String text_data = "";
        Map<String, String> text_attributes = new HashMap<>();
        text_attributes.put("id", web_ele.getAttribute("id"));
        text_attributes.put("name", web_ele.getAttribute("name"));
        text_attributes.put("text", web_ele.getText());
        text_attributes.put("ele_class", web_ele.getAttribute("class"));
        text_attributes.put("role", web_ele.getAttribute("role"));
        text_attributes.put("value", web_ele.getAttribute("value"));
        for(String Tattribute: text_attributes.values()){
            if(!StringUtils.isBlank(Tattribute)){
                text_data += TransformString(Tattribute);
                text_data += " ";
            }
        }
        return text_data;
    }

    public HashSet<String> getAppliedCSS(WebDriver refDriver, WebElement web_ele){
        JavascriptExecutor executor = (JavascriptExecutor) refDriver;
        String script = "var s = '';" +
                "var o = getComputedStyle(arguments[0]);" +
                "for(var i = 0; i < o.length; i++){" +
                "s+=o[i] + ':' + o.getPropertyValue(o[i])+';';}" +
                "return s;";
        String css_string = (String) executor.executeScript(script, web_ele);
        List<String> css_list = new ArrayList<String>(Arrays.asList(css_string.split(";")));
        HashSet<String> css_set = new HashSet<String>(css_list);
        return css_set;
    }

    public void FilterCSS(Set work_set){
        //Finds all CSS attributes common to all keyboard elements
        HashSet<String> final_css_set = new HashSet<>();
        Iterator<FunctionalArea> setIterator = work_set.iterator();
        while(setIterator.hasNext()){
            FunctionalArea FA = setIterator.next();
            HashSet<String> FA_css = FA.getApplied_css();
            if(final_css_set.isEmpty()) {
                List<String> temp_list = new ArrayList<>(FA_css);
                final_css_set = new HashSet<>(temp_list);
            } else {
                final_css_set.retainAll(FA_css);
            }
        }
        //Removes all CSS attributes that are applied to all elements
        Iterator<FunctionalArea> setIterator2 = work_set.iterator();
        while(setIterator2.hasNext()){
            FunctionalArea FA = setIterator2.next();
            HashSet<String> FA_css = FA.getApplied_css();
            FA_css.removeAll(final_css_set);
        }
    }

    public String TransformString(String AString){
        try {
            if (AString.equals("")) {
                return AString;
            }
        } catch (NullPointerException e){
            return "";
        }
        if(CheckCamelCase(AString)){
            String fixed_string = SplitCamelCase(AString);
            AString = fixed_string;
        }

        String split = "";
        String[] parts = AString.split("-");
        if(parts.length > 1){
            for(String part : parts){
                split += part + " ";
            }
        } else {
            split = AString;
        }
        return split;


        //return AString;
    }

    public boolean CheckCamelCase(String AString){
        String camelCasePattern = "(?:[A-Z])(?:\\S?)+(?:[A-Z])(?:[a-z])+";
        String camelCasePattern2 = "([a-z]+[A-Z]+\\w+)+"; // 3rd edit, getting better
        return AString.matches(camelCasePattern) || AString.matches(camelCasePattern2);
    }

    public String SplitCamelCase(String AString){
        String new_string = "";
        for (String w : AString.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            new_string += w;
            new_string += " ";
        }
        return new_string;
    }

}
