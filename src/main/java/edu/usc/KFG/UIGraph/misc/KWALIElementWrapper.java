package edu.usc.KFG.UIGraph.misc;


import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class KWALIElementWrapper implements Comparable<KWALIElementWrapper>, Cloneable {

    //Dictionary dict;
    //WebDriver refDriver;
    WebElement webElement;
    int thisElementIndex = -1;

    //NFGNode nfgNode = null;

    String elementType;
    String elementCategory;
    String xpath;
    String generalizedWebElement;


    boolean isDefaultSelected = false;
    String defaultTextValue = "";

    String obtainedByWhatMethod;

    String parentFrameXpath = null;
    WebElement parentFrameElement = null;

    int x_location = -1;
    int y_location = -1;
    int width = -1;
    int height = -1;

    // javascript event flow info
    //List<JSEventListener> listeners = new ArrayList<JSEventListener>();



    //// start variables for KNF paper
    BufferedImage notFocusedImgState = null;
    BufferedImage focusedImgState = null;
    ArrayList<Integer> corrdsOfInnerContentBox = null;

    BufferedImage minimumAreaFocusedImgStateCondition1 = null;
    BufferedImage minimumAreaFocusedImgStateCondition2 = null;
    BufferedImage minimumAreaFocusedImgStateCondition1b = null;
    BufferedImage minimumAreaFocusedImgStateCondition2b = null;







    public WebElement getParentFrameElement() {
        return parentFrameElement;
    }

    public void setParentFrameElement(WebElement parentFrameElement) {
        this.parentFrameElement = parentFrameElement;
    }

    public String getParentFrameXpath() {
        return parentFrameXpath;
    }

    public void setParentFrameXpath(String parentFrame) {
        this.parentFrameXpath = parentFrame;
    }

//WebDriver refDriverthis.refDriver = refDriver;

    public KWALIElementWrapper(WebElement webElement, String xpath, String elementCategory, String elementType, String obtainedByWhatMethod) {

        //this.dict = new Dictionary(refDriver);
        this.webElement = webElement;
        //this.domNode = domNode;
        this.xpath = xpath;
        this.elementCategory = elementCategory;
        this.elementType = elementType;
        this.obtainedByWhatMethod = obtainedByWhatMethod;
    }



    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public String getElementCategory() {
        return elementCategory;
    }

    public void setElementCategory(String elementCategory) {
        this.elementCategory = elementCategory;
    }

    public void setObtainedByWhatMethod(String obtainedByWhatMethod) {
        this.obtainedByWhatMethod = obtainedByWhatMethod;
    }

    public int getThisElementIndex() {
        return thisElementIndex;
    }

    public void setThisElementIndex(int thisElementIndex) {
        this.thisElementIndex = thisElementIndex;
    }




    public boolean isDefaultSelected() {
        return isDefaultSelected;
    }

    public void setDefaultSelected(boolean isDefaultSelected) {
        this.isDefaultSelected = isDefaultSelected;
    }

    public String getDefaultTextValue() {
        return defaultTextValue;
    }

    public void setDefaultTextValue(String defaultTextValue) {
        this.defaultTextValue = defaultTextValue;
    }

    public String getObtainedByWhatMethod() {
        return obtainedByWhatMethod;
    }


    public String getGeneralizedWebElement() {
        return generalizedWebElement;
    }

    public void setGeneralizedWebElement(String generalizedWebElement) {
        this.generalizedWebElement = generalizedWebElement;
    }


    // mbr properties
    public int getX_location() {
        return x_location;
    }
    public void setX_location(int x_location) {
        this.x_location = x_location;
    }
    public int getY_location() {
        return y_location;
    }
    public void setY_location(int y_location) {
        this.y_location = y_location;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }





//	public NFGNode getNfgNode() {
//		return nfgNode;
//	}
//
//	public void setNfgNode(NFGNode nfgNode) {
//		this.nfgNode = nfgNode;
//	}


    public BufferedImage getNotFocusedImgState() {
        return notFocusedImgState;
    }
    public void setNotFocusedImgState(BufferedImage notFocusedImgState) {
        this.notFocusedImgState = notFocusedImgState;
    }

    public BufferedImage getFocusedImgState() {
        return focusedImgState;
    }
    public void setFocusedImgState(BufferedImage focusedImgState) {
        this.focusedImgState = focusedImgState;
    }

    public ArrayList<Integer> getCorrdsOfInnerContentBox() {
        return corrdsOfInnerContentBox;
    }
    public void setCorrdsOfInnerContentBox(ArrayList<Integer> corrdsOfInnerContentBox) {
        this.corrdsOfInnerContentBox = corrdsOfInnerContentBox;
    }
    // minimum area oracles
    public BufferedImage getMinimumAreaFocusedImgStateCondition1() {
        return minimumAreaFocusedImgStateCondition1;
    }
    public void setMinimumAreaFocusedImgStateCondition1(BufferedImage minimumAreaFocusedImgStateCondition1) {
        this.minimumAreaFocusedImgStateCondition1 = minimumAreaFocusedImgStateCondition1;
    }

    public BufferedImage getMinimumAreaFocusedImgStateCondition2() {
        return minimumAreaFocusedImgStateCondition2;
    }
    public void setMinimumAreaFocusedImgStateCondition2(BufferedImage minimumAreaFocusedImgStateCondition2) {
        this.minimumAreaFocusedImgStateCondition2 = minimumAreaFocusedImgStateCondition2;
    }

    public BufferedImage getMinimumAreaFocusedImgStateCondition1b() {
        return minimumAreaFocusedImgStateCondition1b;
    }
    public void setMinimumAreaFocusedImgStateCondition1b(BufferedImage minimumAreaFocusedImgStateCondition1b) {
        this.minimumAreaFocusedImgStateCondition1b = minimumAreaFocusedImgStateCondition1b;
    }

    public BufferedImage getMinimumAreaFocusedImgStateCondition2b() {
        return minimumAreaFocusedImgStateCondition2b;
    }
    public void setMinimumAreaFocusedImgStateCondition2b(BufferedImage minimumAreaFocusedImgStateCondition2b) {
        this.minimumAreaFocusedImgStateCondition2b = minimumAreaFocusedImgStateCondition2b;
    }









    //public List<JSEventListener> getListeners() {
        //return listeners;
    //}


    //public void addListener(JSEventListener listener) {
        //this.listeners.add(listener);
    //}

    public void printMBR() {
        System.out.println("[x_location=" + x_location + ", y_location=" + y_location + ", width=" + width + ", height=" + height + "]");
    }


    @Override
    public String toString() {
        return "[index: " + thisElementIndex + ", " + xpath + ", obtainedByWhatMethod=" + obtainedByWhatMethod + "]";
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    //Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + xpath.hashCode();
        return result;
    }

    @Override
    public int compareTo(KWALIElementWrapper kew2) {
        return xpath.compareTo(kew2.getXpath());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KWALIElementWrapper kew = (KWALIElementWrapper) o;
        return xpath.equals(kew.getXpath());// || generalizedWebElement.equals(kew.getGeneralizedWebElement());
    }

}
