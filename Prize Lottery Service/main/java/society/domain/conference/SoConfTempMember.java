package thinkonweb.ml.society.domain.conference;


import org.apache.commons.lang.builder.ToStringBuilder;
import thinkonweb.ml.society.domain.registrant.TempMember;

import java.io.Serializable;

public class SoConfTempMember extends TempMember implements Serializable {
    private static final long serialVersionUID = 6658926809672908588L;
    private SoConfConference soConfConference;
    private int confId;

    public int getConfId() {
        return confId;
    }

    public void setConfId(int confId) {
        this.confId = confId;
    }

    public SoConfConference getSoConfConference() {
        return soConfConference;
    }

    public void setSoConfConference(SoConfConference soConfConference) {
        this.soConfConference = soConfConference;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
