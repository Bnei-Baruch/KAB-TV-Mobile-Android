package kab.tv.connection;




import org.apache.http.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;



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
    private StringBuilder mStringFromCharacters;

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
    	ScheduleData.Day day = null;
    	try{
    	day = ScheduleData.Day.valueOf(localName);
    	}
    	catch (IllegalArgumentException e)
    	{
    	 
    	}
    	if (localName.equals("hash")) {
    		this.in_hash = true;
    		//if (localName.equals("*day*")) {
    	}else if (day != null) {
    			this.in_day = true;
    			myParsedSchedule.mCurrentday = day;
    			 Log.v("Schedule Handler", "day: " + day);
    			//set the current day
    		}else if (localName.equals("items")) {
    			this.in_items = true;




    		}else if (localName.equals("item")) {
    			this.in_item = true;
    		}else if (localName.equals("descr")) {
    			this.in_descr = true;
    			myParsedSchedule.SetFlag(Tags.Description);
    			//set description flag
    		} else if (localName.equals("title")) {
    			this.in_title = true;
    			myParsedSchedule.SetFlag(Tags.Title);
    			//set title flag
    		}       else if (localName.equals("time")) {
    			this.in_time = true;
    			myParsedSchedule.SetFlag(Tags.Time);
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

    
    
    /** Gets be called on closing tags like: 
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException {
    	ScheduleData.Day day = null;
    	try{
    	day = ScheduleData.Day.valueOf(localName);
    	}
    	catch (IllegalArgumentException e)
    	{
    	 
    	}
    	
    	if (localName.equals("hash") ) {
    		this.in_hash = false;
    	}else if (day  != null) {
    		this.in_day = false;
    	}else if (localName.equals("items")) {
    		this.in_items = false;
    	}else if (localName.equals("item")) {
    		this.in_item = false;
    		myParsedSchedule.SetEvent();
    	}else if (localName.equals("descr")) {
    		this.in_descr = false;
    		if(mStringFromCharacters == null)
    			mStringFromCharacters.append("");
    		myParsedSchedule.setDayData(mStringFromCharacters.toString());
    		mStringFromCharacters = null;
    	}else if (localName.equals("title")) {
    		this.in_title = false;
    		myParsedSchedule.setDayData(mStringFromCharacters.toString());
    		// Log.v("Schedule Handler", "title: " + mStringFromCharacters);
    		mStringFromCharacters = null;
    	}else if (localName.equals("time")) {
    		this.in_time = false;
    		myParsedSchedule.setDayData(mStringFromCharacters.toString());
    		// Log.e("Schedule Handler", "time: " + mStringFromCharacters);
    		mStringFromCharacters = null;
    	} else if (localName.equals("hdr")) {
			this.in_hdr = false;
			
			//set time flag
		}else if (localName.equals("day")) {
			this.in_day_in_month = false;
			myParsedSchedule.setDayInMonth(mStringFromCharacters.toString());
			mStringFromCharacters = null;
			//set time flag
		}else if (localName.equals("month")) {
			this.in_month = false;
			myParsedSchedule.setMonth(mStringFromCharacters.toString());
			mStringFromCharacters = null;
			//set time flag
		}else if (localName.equals("year")) {
			this.in_year = false;
			myParsedSchedule.setYear(mStringFromCharacters.toString());
			mStringFromCharacters = null;
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
            if(this.in_descr || this.in_title || this.in_time){
            	 if (mStringFromCharacters!=null) { 
            		 for (int i=start; i<start+length; i++) { 
            			 mStringFromCharacters.append(ch[i]);    
            			 }    
            		 }
            	 else
            	 {
            		 mStringFromCharacters = new StringBuilder();
            		 for (int i=start; i<start+length; i++) { 
            			 mStringFromCharacters.append(ch[i]);    
            			 }     
            	 }
            	
    } else if(this.in_hdr){
    	
    	
        	 if (mStringFromCharacters!=null) { 
        		 for (int i=start; i<start+length; i++) { 
        			 mStringFromCharacters.append(ch[i]);    
        			 }    
        		 }
        	 else
        	 {
        		 mStringFromCharacters = new StringBuilder();
        		 for (int i=start; i<start+length; i++) { 
        			 mStringFromCharacters.append(ch[i]);    
        			 }     
        	 
        	 
        	 
    
    	
        	 }
    }
}
}
   
    
