package seng202.team5.services;

import seng202.team5.data.SqlBasedFilterOptionsRepo;
import seng202.team5.data.SqlBasedTrailRepo;
import seng202.team5.models.Trail;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for searching, sorting, filtering trails
 */
public class SearchService {

    /**
     * Default values for different filter types.
     */
    private static final Map<String, String> DEFAULT_VALUES = Map.of(
            "completionType", "All types",
            "timeUnit", "All durations",
            "difficulty", "All difficulties",
            "regions", "All regions");

    private final SqlBasedFilterOptionsRepo filterOptionsRepo;

    private final List<Trail> trails;
    private List<Trail> filteredTrails;
    private final Map<String, String> filters;

    private int maxResults = 50;
    private String currentSortBy = "name";
    private boolean isAscending = true;

    /**
     * Legacy constructor for testing (fallback without filter options repo).
     */
    public SearchService(SqlBasedTrailRepo sqlBasedTrailRepo, SqlBasedFilterOptionsRepo filterOptionsRepo) {
        this.filterOptionsRepo = filterOptionsRepo;
        this.trails = sqlBasedTrailRepo.getAllTrails();
        this.filteredTrails = trails;
        this.filters = new HashMap<>();
    }

    /**
     * Calculates the total number of pages required to display the currently
     * filtered list of trails.
     */
    public int getNumberOfPages() {
        if (maxResults <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) filteredTrails.size() / maxResults);
    }

    /**
     * Gets the total number of trails in the filtered results.
     */
    public int getNumberOfTrails() {
        return filteredTrails.size();
    }

    /**
     * Gets a specific page of trails from the filtered results.
     *
     * @param page the page number (0-indexed)
     * @return list of trails for the specified page
     */
    public List<Trail> getPage(int page) {
        updateTrails();
        int startIndex = page * maxResults;
        return filteredTrails.stream()
                .skip(startIndex)
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    /**
     * Updates trails based on current filters and sort settings.
     */
    public void updateTrails() {
        filteredTrails = trails.stream()
                .filter(this::matchesAllFilters)
                .filter(this::shouldIncludeInSort)
                .sorted(getSortComparator())
                .collect(Collectors.toList());
    }

    /**
     * Checks if a trail matches all current filters, strictly
     */
    private boolean matchesAllFilters(Trail trail) {
        String query = filters.get("query");
        if (query != null && !query.isEmpty() &&
                !trail.getName().toLowerCase().contains(query.toLowerCase())) {
            return false;
        }

        if (matchesFilter("completionType", filters.get("completionType"), trail.getCompletionType())) {
            return false;
        }

        if (matchesFilter("timeUnit", filters.get("timeUnit"), trail.getTimeUnit())) {
            return false;
        }

        if (matchesFilter("difficulty", filters.get("difficulty"), trail.getDifficulty())) {
            return false;
        }

        return matchesRegionFilter(filters.get("regions"), trail.getRegion());
    }

    /**
     * Checks if a trail attribute matches the selected filter values.
     */
    private boolean matchesFilter(String filterType, String filterValue, String trailValue) {
        if (filterValue == null) {
            return false; // No filter applied
        }

        if (filterValue.isEmpty()) {
            return true; // Empty filter means exclude all
        }

        List<String> selectedValues = List.of(filterValue.split(","));

        // If "Select All" is in the selected values, allow all trails through
        if (selectedValues.stream().anyMatch(selected -> selected.trim().equals("Select All"))) {
            return false;
        }

        String defaultValue = DEFAULT_VALUES.get(filterType);
        if (defaultValue != null && selectedValues.stream()
                .anyMatch(selected -> selected.trim().equalsIgnoreCase(defaultValue))) {
            return false; // include all the trails
        }

        return selectedValues.stream()
                .noneMatch(selected -> selected.trim().equalsIgnoreCase(trailValue));
    }

    /**
     * Checks if a trail's region matches the selected region filter values.
     */
    private boolean matchesRegionFilter(String regionFilter, String trailRegion) {
        if (regionFilter == null) {
            return true; // No filter applied
        }

        if (regionFilter.isEmpty()) {
            return false; // Empty filter means exclude all
        }

        List<String> selectedRegions = List.of(regionFilter.split(","));

        // If "Select All" is in the selected regions, allow all trails through
        if (selectedRegions.stream().anyMatch(selected -> selected.trim().equals("Select All"))) {
            return true;
        }

        if (trailRegion != null && !trailRegion.isEmpty()) {
            return selectedRegions.stream()
                    .anyMatch(selected -> selected.trim().equalsIgnoreCase(trailRegion));
        } else {
            return selectedRegions.stream()
                    .anyMatch(region -> region.trim().equalsIgnoreCase("Other"));
        }
    }

    /**
     * Filter out unwanted trails from sort
     */
    private boolean shouldIncludeInSort(Trail trail) {
        return switch (currentSortBy.toLowerCase()) {
            case "time" -> trail.getAvgCompletionTimeMinutes() > 0;
            case "difficulty" -> !trail.getDifficulty().equalsIgnoreCase("unknown");
            default -> true;
        };
    }

    /**
     * Updates a specific filter and refreshes the trail list.
     */
    public void updateFilter(String filter, String filterString) {
        filters.put(filter, filterString);
    }

    /**
     * Sets the current search query.
     */
    public void setCurrentQuery(String query) {
        filters.put("query", query);
    }

    /**
     * Updates the search query and refreshes results.
     */
    public void updateSearch(String query) {
        setCurrentQuery(query);
    }

    /**
     * Gets the comparator for sorting trails based on current settings.
     */
    private Comparator<Trail> getSortComparator() {
        Comparator<Trail> comparator = switch (currentSortBy.toLowerCase()) {
            case "time" -> Comparator.comparingInt(Trail::getAvgCompletionTimeMinutes);
            case "difficulty" -> getDifficultyComparator();
            case "match" -> Comparator.comparingDouble(Trail::getUserWeight).reversed();
            default -> Comparator.comparing(Trail::getName, String.CASE_INSENSITIVE_ORDER);
        };

        return isAscending ? comparator : comparator.reversed();
    }

    /**
     * Gets a comparator for sorting by difficulty level.
     */
    private Comparator<Trail> getDifficultyComparator() {
        List<String> difficultyOrder = getDifficultyOrder();
        return Comparator.comparing(trail -> {
            String difficulty = trail.getDifficulty().toLowerCase();
            int index = difficultyOrder.indexOf(difficulty);
            return index == -1 ? 999 : index;
        });
    }

    /**
     * Gets available filter options for a specific filter type.
     */
    public List<String> getFilterOptions(String filterType) {
        if (filterOptionsRepo != null) {
            if (!filterOptionsRepo.hasFilterOptions(filterType)) {
                filterOptionsRepo.refreshAllFilterOptions();
            }

            List<String> options = new ArrayList<>();

            String defaultValue = DEFAULT_VALUES.get(filterType);
            if (defaultValue != null) {
                options.add(defaultValue);
            }

            List<String> dbOptions = filterOptionsRepo.getFilterOptions(filterType);
            for (String option : dbOptions) {
                if (!option.isEmpty()) {
                    String capitalized = option.substring(0, 1).toUpperCase() + option.substring(1);
                    options.add(capitalized);
                }
            }

            return options;
        }

        return null;
    }

    /**
     * Gets the default filter value for a specific filter type.
     */
    public String getDefaultFilterValue(String filterType) {
        return DEFAULT_VALUES.getOrDefault(filterType, "All");
    }

    /**
     * Sets the maximum number of results per page.
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * Gets the maximum number of results per page.
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Sets the field to sort by.
     */
    public void setSortBy(String sortBy) {
        this.currentSortBy = sortBy;
    }

    /**
     * Gets the current sort field.
     */
    public String getSortBy() {
        return currentSortBy;
    }

    /**
     * Gets available sort options.
     */
    public List<String> getSortOptions() {
        return List.of("Name", "Time", "Difficulty", "Match");
    }

    /**
     * Sets whether sorting should be ascending.
     */
    public void setSortAscending(boolean ascending) {
        this.isAscending = ascending;
    }

    /**
     * Gets the current sort order.
     */
    public boolean isSortAscending() {
        return isAscending;
    }

    /**
     * Gets the difficulty order for UI components from the database.
     */
    public List<String> getDifficultyOrder() {
        return filterOptionsRepo.getFilterOptions("difficulty");
    }
}