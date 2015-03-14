package com.example.plpa.project;

public class ResultData {
	
	private String ax,ay,az;
	private String ox,oy,oz;
	private String mx,my,mz;
	private String latitude;
	private String longitude;
	private String accuracy;
	private String bearing;
	private String altitude;
	private String speed;
	private String px ;
	private String temp ;
	private String li ;
	private String pres ;
	private String rssi;
	private String ssid;
	private String screen;
	private String extmedia;
	private String lan;
	private String country;
	// private String msgFrom;
	private String pkgname;
	private String apkact;
	private String incomingcount;
	private String outcomingcount;
	private String callinalltime;
	private String calloutalltime;
	private String title;
	private String url;
	// private String msgBody;
	private String powact;
	// private String local;		
	private String item;
	private String attribute;
	private String value;
	private String datetime;
	private String exid;
	private String deviceId;
	private String times;
	private String gsmrssi;
	private String gsmspn;
	private String callnumber;
	private String receivenumber;
	private String calltime;
	private String receivetime;
	private String powerstatus;
	
	public ResultData(){	
	}
	
	public void setAX(String ax){
		this.ax = ax;
	}
	public String getAX(){
		return ax;
	}
	public void setAY(String ay){
		this.ay = ay;
	}
	public String getAY(){
		return ay;
	}
	public void setAZ(String az){
		this.az = az;
	}
	public String getAZ(){
		return az;
	}
	public void setOX(String ox){
		this.ox = ox;
	}
	public String getOX(){
		return ox;
	}
	public void setOY(String oy){
		this.oy = oy;
	}
	public String getOY(){
		return oy;
	}
	public void setOZ(String oz){
		this.oz = oz;
	}
	public String getOZ(){
		return oz;
	}
	public void setMX(String mx){
		this.mx = mx;
	}
	public String getMX(){
		return mx;
	}
	public void setMY(String my){
		this.my = my;
	}
	public String getMY(){
		return my;
	}
	public void setMZ(String mz){
		this.mz = mz;
	}
	public String getMZ(){
		return mz;
	}
	public void setPX(String px){
		this.px = px;
	}
	public String getPX(){
		return px;
	}
	public void setLatitude(String latitude){
		this.latitude = latitude;
	}
	public String getLatitude(){
		return latitude;
	}
	public void setLongitude(String longitude){
		this.longitude = longitude;
	}
	public String getLongitude(){
		return longitude;
	}
	
	public void setAccuracy(String accuracy){
		this.accuracy = accuracy;
	}
	public String getAccuracy(){
		return accuracy;
	}
	
    public void setBearing(String bearing){
			this.bearing = bearing;
		}
		public String getBearing(){
			return bearing;
		}
		
		 public void setSpeed(String speed){
			    this.speed = speed;
				}
	    public String getSpeed(){
					return speed;
	    }		
		
   public void setAltitude(String altitude){
		    this.altitude = altitude;
			}
    public String getAltitude(){
				return altitude;
    }
    
	public void setTEMP(String temp){
			this.temp = temp;
		}	
	public String getTEMP(){
			return temp;
	}	
	
	public void setLI(String li){
				this.li = li;
	}	
	public String getLI(){
	     return li;
	}
	
	public void setPRES(String pres){
		this.pres = pres;
				}	
	public String getPRES(){
	return pres;
	}
	
	public void setRSSI(String rssi){
		this.rssi = rssi;
				}	
	public String getRSSI(){
	return rssi;
	}

    public void setSSID(String ssid){
    this.ssid = ssid;
				}
	public String getSSID(){
	return ssid;
	}
	
	public void setSCREEN(String screen){
		this.screen = screen;
				}
	public String getSCREEN(){
	return screen;
	}

    public void setEXTMEDIA(String extmedia){
   this.extmedia = extmedia;
				}
	public String getEXTMEDIA(){
	return extmedia;
	}

   public void setLANGUAGE(String lan){
   this.lan = lan;
				}
	public String getLANGUAGE(){
	return lan;
	}

        public void setCOUNTRY(String country){
		this.country = country;
				}

        public String getCOUNTRY(){
	return country;
	}

/*	public void setMSGFROM(String msgFrom){
		this.msgFrom = msgFrom;
				}

         public String getMSGFROM(){
	return msgFrom;
	}
    */
      public void setMSGTIMES(String times){
     		this.times = times;
     				}

       public String getMSGTIMES(){
     	return times;
     	}

        public void setPKGNAME(String pkgname){
		this.pkgname = pkgname;
				}
	public String getPKGNAME(){
	return pkgname;
	}
	
    public void setAPKACT(String apkact){
	this.apkact = apkact;
			}
    public String getAPKACT(){
    return apkact;
     }

    public void setCALLINCOUNT(String incomingcount){
	this.incomingcount = incomingcount;
			}
   public String getCALLINCOUNT(){
   return incomingcount;
            }
   
   public void setCALLOUTCOUNT(String outcomingcount){
		this.outcomingcount = outcomingcount;
				}
	    public String getCALLOUTCOUNT(){
	    return outcomingcount;
	    }
	    
    public void setCALLINALL(String callinalltime){
	    this.callinalltime = callinalltime;
	    			}
	public String getCALLINALL(){
	        return callinalltime;
	        }
	    
    public void setCALLOUTALL(String calloutalltime){
		 this.calloutalltime = calloutalltime;
		    			}
	 public String getCALLOUTALL(){
		        return calloutalltime;
		        }
   
    public void setBROWSERTITLE(String title){
	this.title = title;
			}
    public String getBROWSERTITLE(){
     return title;
       }

    public void setBROWSERURL(String url){
	this.url = url;
			}

    public String getBROWSERURL(){
      return url;
          }    

   

   /*
    public void setMSGBODY(String msgBody){
	this.msgBody = msgBody;
			}
    public String getMSGBODY(){
    return msgBody;
    }    */

    public void setPOWERSTATUS(String powerstatus){
    	this.powerstatus = powerstatus;
    			}

        public String getPOWERSTATUS(){
         return powerstatus;
        }

    public void setPOWER(String powact){
	this.powact = powact;
			}

    public String getPOWER(){
     return powact;
    }
    
    public void setItem(String item){
		this.item = item;
	}
	
	public String getItem(){
		return item;
	}
	
	public void setExId(String exid){
		this.exid = exid;
	}
	
	public String getExId(){
		return exid;
	}
	
	public void setAttribute(String attribute){
		this.attribute = attribute;
	}
	
	public String getAttribute(){
		return attribute;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setGSMRSSI(String gsmrssi){
		this.gsmrssi = gsmrssi;
	}
	
	public String getGSMRSSI(){
		return gsmrssi;
	}
	
	public void setGSMSPN(String gsmspn){
		this.gsmspn = gsmspn;
	}
	
	public String getGSMSPN(){
		return gsmspn;
	}
	
	public void setDeviceId(String deviceId){
		this.deviceId = deviceId;
	}
	
	public String getDeviceId(){
		return deviceId;
	}
	
	public void setDatetime(String datetime){
		this.datetime = datetime;
	}
	
	public String getDatetime(){
		return datetime;
	}
    
	 public void setCALLNUMBER(String callnumber){
			this.callnumber = callnumber;
					}
		   public String getCALLNUMBER(){
		   return callnumber;
		            }
		   
		   public void setCALLTIME(String calltime){
				this.calltime = calltime;
						}
			    public String getCALLTIME(){
			    return calltime;
			    }
			    
		    public void setRECEIVENUMBER(String receivenumber){
			    this.receivenumber = receivenumber;
			    			}
			public String getRECEIVENUMBER(){
			        return receivenumber;
			        }
			    
		    public void setRECEIVETIME(String receivetime){
				 this.receivetime = receivetime;
				    			}
			 public String getRECEIVETIME(){
				        return receivetime;
				        }
        
}
