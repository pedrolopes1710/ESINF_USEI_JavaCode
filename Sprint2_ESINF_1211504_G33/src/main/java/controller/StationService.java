package controller;

import data.StationRepository;
import model.StationRecord;

import java.util.List;

/**
 * Serviço de alto nível para operações relacionadas com estações.
 * Atua como camada intermédia entre o repositório e o resto da aplicação.
 */
public class StationService {

    private final StationRepository repository;

    /**
     * Inicializa o serviço com o repositório fornecido.
     *
     * @param repository instância do repositório de estações
     */
    public StationService(StationRepository repository) {
        this.repository = repository;
    }

    /**
     * Pede ao repositório para carregar as estações a partir de um CSV.
     *
     * @param csvPath       caminho do ficheiro CSV
     * @param showRejected  se true, imprime linhas inválidas
     * @return número de registos carregados com sucesso
     * @throws Exception quaisquer problemas de leitura ou parsing
     */
    public int loadStations(String csvPath, boolean showRejected) throws Exception {
        return repository.loadStationsFromCsv(csvPath, showRejected);
    }

    /**
     * Obtém estações que coincidem com a latitude especificada.
     *
     * @param latitude latitude exata a procurar
     * @return lista de estações correspondentes
     */
    public List<StationRecord> getStationsByLatitude(double latitude) {
        return repository.searchByLatitude(latitude);
    }

    /**
     * Obtém estações que coincidem com a longitude especificada.
     *
     * @param longitude longitude exata a procurar
     * @return lista de estações encontradas
     */
    public List<StationRecord> getStationsByLongitude(double longitude) {
        return repository.searchByLongitude(longitude);
    }

    /**
     * Devolve todas as estações pertencentes a um determinado grupo de fusos horários.
     *
     * @param tzGroup grupo de fuso horário (ex: "CET", "WET/GMT")
     * @return lista de estações desse grupo
     */
    public List<StationRecord> getStationsByTimeZoneGroup(String tzGroup) {
        return repository.searchByTimeZoneGroup(tzGroup);
    }

    /**
     * Pesquisa estações cuja zona horária esteja dentro de um intervalo.
     *
     * @param tzLower limite inferior (inclusivo)
     * @param tzUpper limite superior (inclusivo)
     * @return lista de estações dentro da janela especificada
     */
    public List<StationRecord> getStationsByTimeZoneWindow(String tzLower, String tzUpper) {
        return repository.searchByTimeZoneWindow(tzLower, tzUpper);
    }
}
