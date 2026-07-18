package ir.aut.secondhand.repository;

import ir.aut.secondhand.dto.SearchAdvertisementRequest;
import ir.aut.secondhand.model.Advertisement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvertisementRepositoryCustomImpl implements AdvertisementRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<Advertisement> search(SearchAdvertisementRequest request) {
        StringBuilder sb = new StringBuilder("SELECT a.* FROM advertisements a WHERE a.status = 'APPROVED'");
        Map<String, Object> parameters = new HashMap<>();
        
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            sb.append(" AND (LOWER(a.title) LIKE :keyword OR LOWER(a.description) LIKE :keyword)");
            parameters.put("keyword", "%" + request.getKeyword().toLowerCase() + "%");
        }
        
        if (request.getCategoryId() != null) {
            sb.append(" AND a.category_id = :categoryId");
            parameters.put("categoryId", request.getCategoryId());
        }
        if (request.getLocationId() != null) {
            sb.append(" AND a.location_id = :locationId");
            parameters.put("locationId", request.getLocationId());
        }
        if (request.getMinPrice() != null) {
            sb.append(" AND a.price_amount >= :minPrice");
            parameters.put("minPrice", request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            sb.append(" AND a.price_amount <= :maxPrice");
            parameters.put("maxPrice", request.getMaxPrice());
        }

        if (request.getDynamicFilters() != null && !request.getDynamicFilters().isEmpty()) {
            int filterIndex = 0;
            for (Map.Entry<String, Object> entry : request.getDynamicFilters().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    if (key.matches("^[a-zA-Z0-9_]+$")) {
                        String paramName = "dynParam" + filterIndex;

                        sb.append(" AND a.dynamic_attributes ->> '").append(key).append("' = :").append(paramName);
                        parameters.put(paramName, value.toString());

                        filterIndex++;
                    }
                }
            }
        }

        if (request.getSortBy() != null) {
            switch (request.getSortBy()) {
                case CHEAPEST:
                    sb.append(" ORDER BY a.price_amount ASC");
                    break;
                case EXPENSIVE:
                    sb.append(" ORDER BY a.price_amount DESC");
                    break;
                case NEWEST:
                default:
                    sb.append(" ORDER BY a.updated_at DESC");
                    break;
            }
        }

        Query query = entityManager.createNativeQuery(sb.toString(), Advertisement.class);
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }

        query.setFirstResult(request.getPage() * request.getSize());
        query.setMaxResults(request.getSize());

        return query.getResultList();
    }
}