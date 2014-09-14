package org.tfc.classes;

public class Event{
    private String Obj_id;
    private String ACS_idLlista;
    private String eventName;
    private String eventDate;
    private Boolean eventActive;
    // External value from list
    private String eventLocation;


    public Event(String strObj_id, String StrACS_idLlista, String StrEventName, String StrEventDate, Boolean BooEventActive,String StrEventLocation){
        this.Obj_id = strObj_id;
        this.ACS_idLlista = StrACS_idLlista;
        this.eventName = StrEventName;
        this.eventDate = StrEventDate;
        this.eventActive = BooEventActive;
        this.eventLocation = StrEventLocation;
    }

    public String getObj_id() {
        return Obj_id;
    }
    public String getACS_idLlista() {
        return ACS_idLlista;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public Boolean getEventActive() {
        return eventActive;
    }

    public String getEventLocation() {
        return eventLocation;
    }
}
