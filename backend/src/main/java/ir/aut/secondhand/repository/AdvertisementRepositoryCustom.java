package ir.aut.secondhand.repository;

import ir.aut.secondhand.dto.SearchAdvertisementRequest;
import ir.aut.secondhand.model.Advertisement;
import java.util.List;

public interface AdvertisementRepositoryCustom {
    List<Advertisement> search(SearchAdvertisementRequest request);
}