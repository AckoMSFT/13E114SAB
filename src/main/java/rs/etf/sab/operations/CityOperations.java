package rs.etf.sab.operations;

import java.util.List;

public interface CityOperations {
    int createCity(String name);

    List<Integer> getCities();

    int connectCities(int cityId1, int cityId2, int distance);

    List<Integer> getConnectedCities(int cityId);

    List<Integer> getShops(int cityId);
}
