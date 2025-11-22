package org.egov.edcr.feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static org.egov.edcr.constants.DxfFileConstants.INDEX_COLOR_TWO;

@Service
public class EntranceLobbyExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(EntranceLobbyExtract.class);

    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }

    @Override
    public PlanDetail extract(PlanDetail pl) {
        Map<String, Integer> lobbyOccupancyFeature = pl.getSubFeatureColorCodesMaster().get("EntranceLobby");
        Set<String> eLobbyOccupancyTypes = new HashSet<>();
        if(lobbyOccupancyFeature != null)
            eLobbyOccupancyTypes.addAll(lobbyOccupancyFeature.keySet());

        for (Block block : pl.getBlocks()) {
            if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty())
                for (Floor floor : block.getBuilding().getFloors()) {

                    Map<Integer, List<BigDecimal>> entrancelobbyHeightMap = new HashMap<>();
                    String entranceLobbyLayerName = String.format(layerNames.getLayerName("LAYER_NAME_ENTRANCE_LOBBY"), block.getNumber(), floor.getNumber(), "+\\d");
                    List<String> entranceLobbyLayers = Util.getLayerNamesLike(pl.getDoc(), entranceLobbyLayerName);

                    String lobbyLayerName = String.format(layerNames.getLayerName("LAYER_NAME_LOBBY"), block.getNumber(), floor.getNumber(), "+\\d");
                    List<String> lobbyLayers = Util.getLayerNamesLike(pl.getDoc(), lobbyLayerName);

                    // Extracting Entrance Lobbies
                    if (!entranceLobbyLayers.isEmpty()) {
                        for (String entranceLobbyLayer : entranceLobbyLayers) {
                            for (String type : eLobbyOccupancyTypes) {
                                Integer colorCode = lobbyOccupancyFeature.get(type);
                                List<BigDecimal> lobbyheights = Util.getListOfDimensionByColourCode(pl, entranceLobbyLayer, colorCode);
                                if (!lobbyheights.isEmpty())
                                    entrancelobbyHeightMap.put(colorCode, lobbyheights);
                            }

                            List<DXFLWPolyline> lobbyPolyLines = Util.getPolyLinesByLayer(pl.getDoc(), entranceLobbyLayer);

                            if (!entrancelobbyHeightMap.isEmpty() || !lobbyPolyLines.isEmpty()) {

                                boolean isClosed = lobbyPolyLines.stream().allMatch(dxflwPolyline -> dxflwPolyline.isClosed());

                                EntranceLobby lobby = new EntranceLobby();
                                String[] lobbyNo = entranceLobbyLayer.split("_");
                                if (lobbyNo != null && lobbyNo.length == 5) {
                                    lobby.setNumber(lobbyNo[4]);
                                }
                                lobby.setClosed(isClosed);

                                List<RoomHeight> lobbyHeights = new ArrayList<>();
                                if (!lobbyPolyLines.isEmpty()) {
                                    List<Measurement> lobbies = new ArrayList<Measurement>();
                                    lobbyPolyLines.stream().forEach(lp -> {
                                        Measurement m = new MeasurementDetail(lp, true);
                                        if (!entrancelobbyHeightMap.isEmpty() && entrancelobbyHeightMap.containsKey(m.getColorCode())) {
                                            for (BigDecimal value : entrancelobbyHeightMap.get(m.getColorCode())) {
                                                RoomHeight roomHeight = new RoomHeight();
                                                roomHeight.setColorCode(m.getColorCode());
                                                roomHeight.setHeight(value);
                                                lobbyHeights.add(roomHeight);
                                            }
                                            lobby.setHeights(lobbyHeights);
                                        }
                                        lobbies.add(m);
                                    });
                                    lobby.setLobbies(lobbies);
                                }
                                floor.addEntranceLobby(lobby);
                            }
                        }
                    }

                    // Extracting Lobbies
                    if (!lobbyLayers.isEmpty()) {
                        for (String lobbyLayer : lobbyLayers) {
                            Lobby lobby = new Lobby();
                            List<BigDecimal> lobbyWidth = Util.getListOfDimensionByColourCode(pl, lobbyLayer, INDEX_COLOR_TWO);
                            lobby.setLobbyWidths(lobbyWidth);

                            String[] lobbyNo = lobbyLayer.split("_");
                            if (lobbyNo != null && lobbyNo.length == 5) {
                                lobby.setNumber(lobbyNo[5]);
                            }

                            floor.addLobby(lobby);
                        }
                    }

                }
        }
        return pl;
    }
}
