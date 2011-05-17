package kab.tv.connection;




import org.apache.http.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class ScheduleHandler extends DefaultHandler{

    // ===========================================================
    // Fields
    // ===========================================================
	 private boolean in_hash = false;
    private boolean in_day = false;
    private boolean in_items = false;
    private boolean in_item = false;
    private boolean in_descr = false;
    private boolean in_title = false;
    private boolean in_time = false;
    private boolean in_date = false;
    private boolean in_hdr = false;
    private boolean in_day_in_month = false;
    private boolean in_month = false;
    private boolean in_year = false;
   
    
    private ScheduleData myParsedSchedule;

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public ScheduleData getParsedData() {
            return this.myParsedSchedule;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
            this.myParsedSchedule = new ScheduleData();
    }

    @Override
    public void endDocument() throws SAXException {
            // Nothing to do
    }

    /** Gets be called on opening tags like: 
     * <tag> 
     * Can provide attribute(s), when xml was like:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName,
                    String qName, Attributes atts) throws SAXException {
    	Day day;
    	if (localName.equals("hash")) {
    		this.in_hash = true;
    		//if (localName.equals("*day*")) {
    		if ((day = Day.valueOf(localName)) != null) {
    			this.in_day = true;
    			myParsedSchedule.mCurrentday = day;
    			//set the current day
    		}else if (localName.equals("items")) {
    			this.in_items = true;




    		}else if (localName.equals("item")) {
    			this.in_item = true;
    		}else if (localName.equals("descr")) {
    			this.in_descr = true;
    			myParsedSchedule.mFlag = Tags.Description;
    			//set description flag
    		} else if (localName.equals("title")) {
    			this.in_title = true;
    			myParsedSchedule.mFlag = Tags.Title;
    			//set title flag
    		}       else if (localName.equals("time")) {
    			this.in_time = true;
    			myParsedSchedule.mFlag = Tags.Time;
    			//set time flag
    		} else if (localName.equals("hdr")) {
    			this.in_hdr = true;
    			
    			//set time flag
    		}else if (localName.equals("day")) {
    			this.in_day_in_month = true;
    			
    			//set time flag
    		}else if (localName.equals("month")) {
    			this.in_month = true;
    			
    			//set time flag
    		}else if (localName.equals("year")) {
    			this.in_year = true;
    			
    			//set time flag
    		}
    	}

    }
    
    /** Gets be called on closing tags like: 
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException {
    	if (localName.equals("hash")) {
    		this.in_hash = false;
    	}else if (localName.equals("*day(")) {
    		this.in_day = false;
    	}else if (localName.equals("items")) {
    		this.in_items = false;
    	}else if (localName.equals("item")) {
    		this.in_item = false;
    		myParsedSchedule.SetEvent();
    	}else if (localName.equals("descr")) {
    		this.in_descr = false;

    	}else if (localName.equals("title")) {
    		this.in_title = false;
    	}else if (localName.equals("time")) {
    		this.in_time = false;
    	} else if (localName.equals("hdr")) {
			this.in_hdr = false;
			
			//set time flag
		}else if (localName.equals("day")) {
			this.in_day_in_month = false;
			
			//set time flag
		}else if (localName.equals("month")) {
			this.in_month = false;
			
			//set time flag
		}else if (localName.equals("year")) {
			this.in_year = false;
			try {
				myParsedSchedule.buildDate();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//set time flag
		}
    }
    
    /** Gets be called on the following structure: 
     * <tag>characters</tag> */
    @Override
public void characters(char ch[], int start, int length) {
            if(this.in_item){
            	myParsedSchedule.setDayData(new String(ch, start, length));
    } else if(this.in_hdr){
    	if(this.in_day_in_month)
            	myParsedSchedule.setDayInMonth(new String(ch, start, length));
    	if(this.in_month)
        	myParsedSchedule.setMonth(new String(ch, start, length));
    	if(this.in_day_in_month)
        	myParsedSchedule.setYear(new String(ch, start, length));
    	
    	
    }
    }
}
   
    
