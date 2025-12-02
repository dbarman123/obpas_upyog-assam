package org.egov.common.entity.edcr;

import java.io.Serializable;
import java.util.*;

public class Garbage implements Serializable {

    private List<Measurement> dryGarbagePits = new ArrayList<>();
    private List<Measurement> wetGarbagePits = new ArrayList<>();

    public List<Measurement> getDryGarbagePits() {
        return dryGarbagePits;
    }

    public void setDryGarbagePits(List<Measurement> dryGarbagePits) {
        this.dryGarbagePits = dryGarbagePits;
    }

    public List<Measurement> getWetGarbagePits() {
        return wetGarbagePits;
    }

    public void setWetGarbagePits(List<Measurement> wetGarbagePits) {
        this.wetGarbagePits = wetGarbagePits;
    }
}
