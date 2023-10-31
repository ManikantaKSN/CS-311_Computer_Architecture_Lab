package processor.pipeline;
import configuration.Configuration;
import generic.*;
import processor.Clock;
import processor.Processor;
import processor.memorysystem.Cache;

public class InstructionFetch implements Element {
    Processor containingProcessor;
    IF_EnableLatchType IF_EnableLatch;
    IF_OF_LatchType IF_OF_Latch;
    EX_IF_LatchType EX_IF_Latch;
    Cache L1iCache;

    public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch,
            IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch, Cache L1iCache) {
        this.containingProcessor = containingProcessor;
        this.IF_EnableLatch = iF_EnableLatch;
        this.IF_OF_Latch = iF_OF_Latch;
        this.EX_IF_Latch = eX_IF_Latch;
        this.L1iCache = L1iCache;
    }

    public void performIF() {
        if (EX_IF_Latch.isIF_enable()) {// checking if EX_IF latch is enabled or not
            containingProcessor.getRegisterFile().setProgramCounter(EX_IF_Latch.getBranchPC());// setting the program
                                                                                               // counter by taking the
                                                                                               // input from EX-IF latch
            int newInstruction = containingProcessor.getMainMemory().getWord(EX_IF_Latch.getBranchPC());// fetching the
                                                                                                        // inst from
                                                                                                        // main memory

            Simulator.ins_count++;// updating the instruction count
            if (L1iCache.isInCache(EX_IF_Latch.getBranchPC())) {// checking if the inst is present in the latch or not
                                                                // using the current pc
                Simulator.getEventQueue().addEvent(
                        new MemoryReadEvent(
                                Clock.getCurrentTime() + Configuration.L1i_latency,
                                this,
                                (Element) containingProcessor.getMainMemory(),
                                EX_IF_Latch.getBranchPC()));
            } else {
                // add to L1iCache
                L1iCache.addToCache(EX_IF_Latch.getBranchPC());
                Simulator.getEventQueue().addEvent(
                        new MemoryReadEvent(
                                Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                                this,
                                (Element) containingProcessor.getMainMemory(),
                                EX_IF_Latch.getBranchPC()));
            }
            if (newInstruction == 0) {
                IF_OF_Latch.setNop(true);
                return;
            }
            if (IF_OF_Latch.getInstruction() == -402653184) { // for end
                containingProcessor.getRegisterFile().setProgramCounter(EX_IF_Latch.getBranchPC());
            } else {
                containingProcessor.getRegisterFile().setProgramCounter(EX_IF_Latch.getBranchPC() + 1);
                IF_OF_Latch.setInstruction(newInstruction);
            }

            IF_OF_Latch.setInstruction(newInstruction);

            EX_IF_Latch.setIF_enable(false);

            IF_OF_Latch.setOF_enable(true);
        } else if (IF_EnableLatch.isIF_enable()) {
            int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
            int newInstruction = containingProcessor.getMainMemory().getWord(currentPC);
            Simulator.ins_count++;
            if (L1iCache.isInCache(currentPC)) {
                Simulator.getEventQueue().addEvent(
                        new MemoryReadEvent(
                                Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                                this,
                                (Element) containingProcessor.getMainMemory(),
                                currentPC));
            } else {
                L1iCache.addToCache(currentPC);
                Simulator.getEventQueue().addEvent(
                        new MemoryReadEvent(
                                Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                                this,
                                (Element) containingProcessor.getMainMemory(),
                                currentPC));

            }
            Simulator.getEventQueue().addEvent(
                    new MemoryReadEvent(
                            Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                            this,
                            (Element) containingProcessor.getMainMemory(),
                            currentPC));

            if (newInstruction == 0) {
                IF_OF_Latch.setNop(true);

                return;
            }
            if (IF_OF_Latch.getInstruction() == -402653184) { // for end we pass end again, and don't get any new inst
                                                              // or increment pc
                containingProcessor.getRegisterFile().setProgramCounter(currentPC);
            } else {
                containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);
                IF_OF_Latch.setInstruction(newInstruction);
            }

            EX_IF_Latch.setIF_enable(false);

            IF_OF_Latch.setOF_enable(true);
        }
        IF_OF_Latch.setOF_enable(true);
    }

    @Override
    public void handleEvent(Event event) {

    }
}
