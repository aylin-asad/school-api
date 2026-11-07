
package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.entity.*;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final EntityManagerFactory entityManagerFactory;

    public CacheController(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private Statistics statistics() {
        return entityManagerFactory
                .unwrap(SessionFactory.class)
                .getStatistics();
    }

    public record RegionStats(
            long hits,
            long misses,
            long puts,
            Long entries
    ) {}

    public record CacheStats(
            long secondLevelCacheHitCount,
            long secondLevelCacheMissCount,
            long secondLevelCachePutCount,
            long queryCacheHitCount,
            long queryCacheMissCount,
            long queryCachePutCount,
            Map<String, RegionStats> regions
    ) {}

    @GetMapping("/stats")
    public CacheStats getStats() {

        Statistics stats = statistics();

        Map<String, RegionStats> regions = new LinkedHashMap<>();

        String[] names = {
                "departments",
                "courses",
                "authors",
                "books",
                "author-books",
                "author-authorBooks",
                "book-authorBooks",
                "default-query-results-region"
        };

        for (String name : names) {
            CacheRegionStatistics region =
                    stats.getCacheRegionStatistics(name);

            if (region != null) {
                long count = region.getElementCountInMemory();

                regions.put(name, new RegionStats(
                        region.getHitCount(),
                        region.getMissCount(),
                        region.getPutCount(),
                        count >= 0 ? count : null
                ));
            }
        }

        return new CacheStats(
                stats.getSecondLevelCacheHitCount(),
                stats.getSecondLevelCacheMissCount(),
                stats.getSecondLevelCachePutCount(),
                stats.getQueryCacheHitCount(),
                stats.getQueryCacheMissCount(),
                stats.getQueryCachePutCount(),
                regions
        );
    }

    @DeleteMapping("/stats")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearStats() {
        statistics().clear();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCache() {
        entityManagerFactory
                .unwrap(SessionFactory.class)
                .getCache()
                .evictAllRegions();
    }

    @DeleteMapping("/{region}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearRegion(@PathVariable String region) {

        Class<?> entityClass = switch (region) {
            case "courses" -> Course.class;
            case "departments" -> Department.class;
            case "authors" -> Author.class;
            case "books" -> Book.class;
            case "author-books" -> AuthorBook.class;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown cache region: " + region
            );
        };

        entityManagerFactory.getCache().evict(entityClass);
    }
}
