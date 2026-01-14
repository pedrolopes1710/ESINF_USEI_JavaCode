package data;

import model.KDTree;
import model.StationRecord;

import java.util.List;

public class KDTreeBuilder {

    /**
     * Cria um KDTree contendo todas as estações presentes no repositório.
     */
    public static KDTree buildFromRepo(StationRepository repository) throws Exception {
        if (repository == null) {
            throw new IllegalArgumentException("Repository não pode ser null");
        }

        List<StationRecord> allStations = repository.getStations();

        // delega a construção real para a classe KDTree
        return KDTree.buildFromStations(allStations);
    }
}
