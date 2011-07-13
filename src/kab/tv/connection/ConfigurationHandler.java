package kab.tv.connection;




import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

 
 
public class ConfigurationHandler extends DefaultHandler{
 
        // ===========================================================
        // Fields
        // ===========================================================
        
        private boolean in_channels = false;
        private boolean in_channel = false;
        private boolean in_streams = false;
        private boolean in_stream = false;
        private boolean in_schedule = false;
       
        
        private Channels myParsedChannels;
 
        // ===========================================================
        // Getter & Setter
        // ===========================================================
 
        public Channels getParsedData() {
                return this.myParsedChannels;
        }
 
        // ===========================================================
        // Methods
        // ===========================================================
        @Override
        public void startDocument() throws SAXException {
                try {
					this.myParsedChannels = Channels.instance();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
        	try {
                if (localName.equals("channles")) {
                        this.in_channels = true;
                }else if (localName.equals("channel")) {
                        this.in_channel = true;
                        
							Channels.instance().LoadChannel();
						
						 String name = atts.getValue("name");
						 String icon = atts.getValue("icon");
						 
							Channels.instance().setChannelName(name);
							Channels.instance().setIconName(icon);
						
						
                }else if (localName.equals("streams")) {
                    this.in_streams = true;
                }else if (localName.equals("stream")) {
                	try {
						Channels.instance().LoadStream();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                        this.in_stream = true;
               
                        // Extract an Attribute
                        String format = atts.getValue("format");
                        String type = atts.getValue("type");
                        String quality = atts.getValue("quality");
                        String os = atts.getValue("os");
                        
                        Channels.instance().setStreamData(format,type,quality,os);
                        
                        
                }else if (localName.equals("schedule")) {
                    this.in_schedule = true;
                    
                }
        	}
                         catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        }
        
        /** Gets be called on closing tags like: 
         * </tag> */
        @Override
        public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException{
        	if (localName.equals("channles")) {
        		this.in_channels = false;
        	}else if (localName.equals("channel")) {
        		this.in_channel = false;
        		
        		 try {
        			 Channels.instance().SetChannelLoaded(true);
					Channels.instance().resetStreams();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}else if (localName.equals("streams")) {
        		this.in_streams = false;
        		
        	}else if (localName.equals("stream")) {
        		this.in_stream = false;
        	}else if (localName.equals("schedule")) {
        		this.in_schedule = false;
        		try{
        			Channels.instance().LoadSchedule();
        		}
        		catch (ParserConfigurationException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }

        /** Gets be called on the following structure: 
         * <tag>characters</tag> */
        @Override
    public void characters(char ch[], int start, int length) {
                if(this.in_stream){
                	myParsedChannels.setStreamUrl(new String(ch, start, length));
                }
                else if(this.in_schedule)
					try {
						myParsedChannels.setScheduleUrl(new String(ch, start, length));
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                		
       
    }
}

