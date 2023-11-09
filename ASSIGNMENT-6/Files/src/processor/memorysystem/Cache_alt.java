package processor.memorysystem;

import configuration.Configuration;
import generic.*;
import processor.*;

public class Cache implements Element {
	public int cache_latency ;
	Processor containingProcessor;
    Element missElement;
	int cachesize;
    int miss_address;
    int read;
    int write;
    int temporary;
    boolean isThere = true;
    CacheLine[] cacheSet;
    int[] idx;
	
	public Cache(Processor containingProcessor, int latency, int cSize) {
		this.containingProcessor = containingProcessor;
        this.cache_latency = latency;
        this.cachesize = cSize;
        this.temporary = (int)(Math.log(this.cachesize/8)/Math.log(2));
        this.cacheSet = new CacheLine[cachesize/8];
		for(int i = 0; i < cachesize/8; i++) {
			this.cacheSet[i] = new CacheLine();
        }
    }

    public void setProcessor(Processor proc) {
        this.containingProcessor = proc;
    }

     public Processor getProcessor() {
        return this.containingProcessor;
    }

    public CacheLine[] getCaches() {
        return this.cacheSet;
    }

    public boolean checkThere() {
        return this.isThere;
    }

    public int[] getIndexes() {
        return this.idx;
    }
    
    public void handleCacheMiss(int address) {
		Simulator.getEventQueue().addEvent(
            new MemoryReadEvent(
                Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                this, containingProcessor.getMainMemory(), address));                   
	}
    
    public int cacheRead(int addr){
        String binary_address = Integer.toBinaryString(addr);
        int binary_address_length = binary_address.length();
        String id = "";
        int index;  
        for(int i = 0; i < 32-binary_address_length; i++){
            binary_address = "0" + binary_address;
        }
        for(int j = 0; j < temporary; j++) {
            id = id + "1";
        }
        if(temporary == 0){
            index = 0;
        }
        else{
            index = addr & Integer.parseInt(id, 2);
        }

        int tag_added = Integer.parseInt(binary_address.substring(0, binary_address.length()-temporary),2);

        if(tag_added == cacheSet[index].tag[0]){
            cacheSet[index].LRU = 1;
            isThere = true;
            return cacheSet[index].data[0];
        }
        else if(tag_added == cacheSet[index].tag[1]){
            isThere = true;
            cacheSet[index].LRU = 0;
            return cacheSet[index].data[1];
        }
        else {
            isThere = false;
            return -1;
        }
    }

    void handleResponse(int addr, int val){
        cacheWrite(addr, val);
        Simulator.getEventQueue().addEvent(
                new MemoryResponseEvent(
                Clock.getCurrentTime(), this, missElement, val));
    }

    void cacheWrite(int addr, int val){
        String binary_addr = Integer.toBinaryString(addr);
        int binary_addr_length = binary_addr.length();
        String id = "";
        int index;

        for(int i = 0; i < 32-binary_addr_length; i++)
            binary_addr = "0" + binary_addr;
        
        for(int j = 0; j < temporary; j++ )
            id = id + "1";
        
        if(temporary == 0){
            index = 0;
        }
        else{
            index = addr & Integer.parseInt(id, 2);
        }
        int tag_add = Integer.parseInt(binary_addr.substring(0, binary_addr.length()-temporary),2);
        cacheSet[index].setValue(tag_add, val);
    }

    @Override
	public void handleEvent(Event e) {
        if(e.getEventType() == Event.EventType.MemoryRead){
            MemoryReadEvent eve = (MemoryReadEvent) e;
            int data_read = cacheRead(eve.getAddressToReadFrom());
            if(isThere == true){
                Simulator.getEventQueue().addEvent(
                    new MemoryResponseEvent(
                        Clock.getCurrentTime() + this.cache_latency, 
                        this, eve.getRequestingElement(), data_read));
            }
            else{
                missElement = eve.getRequestingElement();
                this.miss_address = eve.getAddressToReadFrom();
                eve.setEventTime(Clock.getCurrentTime() + Configuration.mainMemoryLatency+1);
                Simulator.getEventQueue().addEvent(eve);
                handleCacheMiss(eve.getAddressToReadFrom());
            }
        }

       else if(e.getEventType() == Event.EventType.MemoryResponse){
            MemoryResponseEvent eve = (MemoryResponseEvent) e;
            handleResponse(this.miss_address, eve.getValue());
        }
        
        else if(e.getEventType() == Event.EventType.MemoryWrite){
            MemoryWriteEvent eve = (MemoryWriteEvent) e;
            cacheWrite(eve.getAddressToWriteTo(), eve.getValue());
            Simulator.getEventQueue().addEvent(
				new MemoryWriteEvent(
					Clock.getCurrentTime()+Configuration.mainMemoryLatency, containingProcessor.getMainMemory(), 
                    eve.getRequestingElement(),eve.getAddressToWriteTo(),eve.getValue()));
		}
	}
}