package processor.memorysystem;

public class CacheLine{
    int[] tag = new int[2]; 
    int[] data = new int[2];
    int LRU;

    public CacheLine() {
        this.tag[0] = -1;
        this.tag[1] = -1;
        this.LRU = 0;
    }


    public void setValue(int tag, int val) {

        if(tag == this.tag[0]) {
            this.data[0] = val;
            this.LRU = 1;
        }
        else if(tag == this.tag[1]) {
            this.data[1] = val;
            this.LRU = 0;
        }
        else {
            // replacement of LRU block is occurring and LRU is changing
            this.tag[this.LRU] = tag;
            this.data[this.LRU] = val;
            this.LRU = 1- this.LRU;
        }
	}

    public int setLru(int curLru) {
        this.LRU = curLru;
        return this.LRU;
    }

    public int getTag(int idx) {
        return this.tag[idx];
    }

    public int getData(int idx) {
        return this.data[idx];
    }

    public int getLru() {
        return this.LRU;
    }

}