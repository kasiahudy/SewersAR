package com.example.sewersar;

import com.example.sewersar.database.SewersNode;
import com.example.sewersar.database.SewersPipe;

import java.util.List;

public class SewersNodesController {
    private static List<SewersNode> sewersNodes;
    private static List<SewersNode> selectedSewersNodes;
    private static List<SewersPipe> sewersPipes;
    private static List<SewersPipe> selectedSewersPipes;

    private static List<String> allSewersTypesStrings;
    private static List<String> selectedSewersTypesStrings;

    private static boolean selectedSewersChanged = false;

    public static List<SewersNode> getSewersNodes() {
        return sewersNodes;
    }

    public static void setSewersNodes(List<SewersNode> sewersNodes) {
        SewersNodesController.sewersNodes = sewersNodes;
    }

    public static List<SewersNode> getSelectedSewersNodes() {
        return selectedSewersNodes;
    }

    public static void setSelectedSewersNodes(List<SewersNode> selectedSewersNodes) {
        SewersNodesController.selectedSewersNodes = selectedSewersNodes;
    }

    public static List<SewersPipe> getSewersPipes() {
        return sewersPipes;
    }

    public static void setSewersPipes(List<SewersPipe> sewersPipes) {
        SewersNodesController.sewersPipes = sewersPipes;
    }

    public static List<SewersPipe> getSelectedSewersPipes() {
        return selectedSewersPipes;
    }

    public static void setSelectedSewersPipes(List<SewersPipe> selectedSewersPipes) {
        SewersNodesController.selectedSewersPipes = selectedSewersPipes;
    }

    public static List<String> getAllSewersTypesStrings() {
        return allSewersTypesStrings;
    }

    public static void setAllSewersTypesStrings(List<String> allSewersTypesStrings) {
        SewersNodesController.allSewersTypesStrings = allSewersTypesStrings;
    }

    public static List<String> getSelectedSewersTypesStrings() {
        return selectedSewersTypesStrings;
    }

    public static void setSelectedSewersTypesStrings(List<String> selectedSewersTypesStrings) {
        SewersNodesController.selectedSewersTypesStrings = selectedSewersTypesStrings;
    }

    public static boolean isSelectedSewersChanged() {
        return selectedSewersChanged;
    }

    public static void setSelectedSewersChanged(boolean selectedSewersChanged) {
        SewersNodesController.selectedSewersChanged = selectedSewersChanged;
    }
}
