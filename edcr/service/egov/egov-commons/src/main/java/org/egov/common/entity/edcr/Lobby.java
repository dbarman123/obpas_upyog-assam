package org.egov.common.entity.edcr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Lobby extends Measurement{
    private static final long serialVersionUID = 90L;

    private String number ;
    private List<BigDecimal> lobbyWidths = new ArrayList<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<BigDecimal> getLobbyWidths() {
        return lobbyWidths;
    }

    public void setLobbyWidths(List<BigDecimal> widths) {
        this.lobbyWidths = widths;
    }
}
