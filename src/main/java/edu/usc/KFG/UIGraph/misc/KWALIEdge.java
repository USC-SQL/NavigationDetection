package edu.usc.KFG.UIGraph.misc;

public class KWALIEdge implements Cloneable {

    String edgeType = "";				// KFE or EFE
    KWALIElementWrapper v1;
    KWALIElementWrapper v2;
    String keystroke = "";			// phi of KFE
    String eventDispathPhase = "";	// theta of EFE
    String eventType = "";	// sigma of MFE


    String buildByWhatMethod = "";

//	NFGNode nn1;
//	NFGNode nn2;

//	public KWALIEdge() {
//
//	}
//
//	public KWALIEdge(KWALIElementWrapper v1, KWALIElementWrapper v2) {
//		this.v1 = v1;
//		this.v2 = v2;
//	}

    public KWALIEdge(KWALIElementWrapper v1, KWALIElementWrapper v2, String edgeType, String edgeArg, String buildByWhatMethod) {
        this.v1 = v1;
        this.v2 = v2;
        this.edgeType = edgeType;
        if(edgeType.equals("KFE")) {
            this.keystroke = edgeArg;
        } else if(edgeType.equals("EFE")) {
            this.eventDispathPhase = edgeArg;
        } else if(edgeType.equals("MFE")) {
            this.eventType = edgeArg;
        }
        this.buildByWhatMethod = buildByWhatMethod;

    }

    public KWALIElementWrapper getV1() {
        return v1;
    }
    public void setV1(KWALIElementWrapper v1) {
        this.v1 = v1;
    }

    public KWALIElementWrapper getV2() {
        return v2;
    }
    public void setV2(KWALIElementWrapper v2) {
        this.v2 = v2;
    }


    public String getEdgeType() {
        return edgeType;
    }
    public void setEdgeType(String edgeType) {
        this.edgeType = edgeType;
    }

    // get phi from KFE
    public String getKeystroke() {
        return keystroke;
    }
    public void setKeystroke(String keystroke) {
        this.keystroke = keystroke;
    }
    // get theta from EFE
    public String getEventDispathPhase() {
        return eventDispathPhase;
    }
    public void setEventDispathPhase(String eventDispathPhase) {
        this.eventDispathPhase = eventDispathPhase;
    }
    // get sigma from MFE
    public String getEventType() {
        return eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }



    public String getBuildByWhatMethod() {
        return buildByWhatMethod;
    }
    public void setBuildByWhatMethod(String buildByWhatMethod) {
        this.buildByWhatMethod = buildByWhatMethod;
    }
	/*
	// nfg node
	public KWALIEdge(NFGNode nn1, NFGNode nn2, String keystroke) {
		this.nn1 = nn1;
		this.nn2 = nn2;
		this.keystroke = keystroke;
	}

	public NFGNode getNn1() {
		return nn1;
	}

	public void setNn1(NFGNode nn1) {
		this.nn1 = nn1;
	}

	public NFGNode getNn2() {
		return nn2;
	}

	public void setNn2(NFGNode nn2) {
		this.nn2 = nn2;
	}
*/


//	@Override
//	public String toString() {
//		if(v1 != null && v2 != null) {
//			return v1.thisElementIndex + " --" +  keystroke + "--> " + v2.thisElementIndex;
//		} else {
//			String nn1IndexStr = (nn1 == null) ? ("null") : (nn1.index + "");
//			String nn2IndexStr = (nn2 == null) ? ("null") : (nn2.index + "");
//			return nn1IndexStr + " --" +  keystroke + "--> " + nn2IndexStr;
//		}
//	}




    public void printEdgeInfo_index() {
        String edgeInfo = null;
        if(edgeType.equals("KFE")) {  	edgeInfo = keystroke;	}
        else if(edgeType.equals("EFE")) {  	edgeInfo = eventDispathPhase;	}
        else if(edgeType.equals("MFE")) {  	edgeInfo = eventType;	}
        System.out.println(v1.getThisElementIndex() + " --" +  edgeInfo + "--> " + v2.getThisElementIndex());
    }

    public void printEdgeInfo_xpath() {
        String edgeInfo = null;
        if(edgeType.equals("KFE")) {  	edgeInfo = keystroke;	}
        else if(edgeType.equals("EFE")) {  	edgeInfo = eventDispathPhase;	}
        else if(edgeType.equals("MFE")) {  	edgeInfo = eventType;	}
        System.out.println(v1.getXpath() + " --" +  edgeInfo + "--> " + v2.getXpath());
    }

//	@Override
//	public String toString() {
//		String edgeInfo = (edgeType.equals("KFE")) ? keystroke : eventDispathPhase;
//		return v1.thisElementIndex + " --" +  edgeInfo + "--> " + v2.thisElementIndex;
//	}


    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    //Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + v1.getXpath().hashCode() + v2.getXpath().hashCode();
        return result;
    }

//	@Override
//	public int compareTo(KWALIEdge ke) {
//        if(edgeType.equals("KFE")) {
//        	return (v1.getXpath().equals(ke.getV1().getXpath())) && (v2.getXpath().equals(ke.getV2().getXpath())) && (keystroke.equals(ke.getKeystroke()));
//        } else {
//        	return (v1.getXpath().equals(ke.getV1().getXpath())) && (v2.getXpath().equals(ke.getV2().getXpath())) && (eventDispathPhase.equals(ke.getEventDispathPhase()));
//        }
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KWALIEdge ke = (KWALIEdge) o;
        return (edgeType.equals(ke.getEdgeType()) &&
                v1.getXpath().equals(ke.getV1().getXpath()) &&
                v2.getXpath().equals(ke.getV2().getXpath()) &&
                keystroke.equals(ke.getKeystroke()) &&
                eventDispathPhase.equals(ke.getEventDispathPhase()) &&
                eventType.equals(ke.getEventType()));
//        if(edgeType.equals("KFE")) {
//        	return (v1.getXpath().equals(ke.getV1().getXpath())) && (v2.getXpath().equals(ke.getV2().getXpath())) && (keystroke.equals(ke.getKeystroke()));
//        } else {
//        	return (v1.getXpath().equals(ke.getV1().getXpath())) && (v2.getXpath().equals(ke.getV2().getXpath())) && (eventDispathPhase.equals(ke.getEventDispathPhase()));
//        }
    }

    @Override
    public String toString() {
        return "KWALIEdge [edgeType=" + edgeType + ", v1=" + v1 + ", v2=" + v2 + ", keystroke=" + keystroke
                + ", eventDispathPhase=" + eventDispathPhase + ", buildByWhatMethod=" + buildByWhatMethod + "]";
    }



}