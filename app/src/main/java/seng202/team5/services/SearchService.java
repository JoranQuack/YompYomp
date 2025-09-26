package seng202.team5.services;

import seng202.team5.data.DatabaseService;
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
            "multiDay", "Any time range");

    /**
     * Predefined difficulty order for sorting.
     */
    private static final List<String> DIFFICULTY_ORDER = List.of(
            "easiest", "easy", "intermediate", "advanced", "expert");

    private final SqlBasedFilterOptionsRepo filterOptionsRepo;

    private List<Trail> trails;
    private List<Trail> filteredTrails;
    private Map<String, String> filters;

    private int maxResults = 50;
    private String currentSortBy = "name";
    private boolean isAscending = true;

    /**
     * Creates SearchService with database-backed filter options.
     */
    public SearchService(DatabaseService databaseService) {
        this.filterOptionsRepo = new SqlBasedFilterOptionsRepo(databaseService);
        this.trails = new SqlBasedTrailRepo(databaseService).getAllTrails();
        this.filteredTrails = trails;
        this.filters = new HashMap<>();
    }

    /**
     * Legacy constructor for testing (fallback without filter options repo).
     */
    public SearchService(SqlBasedTrailRepo sqlBasedTrailRepo) {
        this.filterOptionsRepo = null;
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
     * Checks if a trail matches all current filters.
     */
    private boolean matchesAllFilters(Trail trail) {
        String query = filters.get("query");
        if (query != null && !query.isEmpty() &&
                !trail.getName().toLowerCase().contains(query.toLowerCase())) {
            return false;
        }

        String completionType = filters.get("completionType");
        if (completionType != null && !completionType.equals("All types") &&
                !trail.getCompletionType().equalsIgnoreCase(completionType)) {
            return false;
        }

        String timeUnit = filters.get("timeUnit");
        if (timeUnit != null && !timeUnit.equals("All durations") &&
                !trail.getTimeUnit().equalsIgnoreCase(timeUnit)) {
            return false;
        }

        String difficulty = filters.get("difficulty");
        if (difficulty != null && !difficulty.equals("All difficulties") &&
                !trail.getDifficulty().equalsIgnoreCase(difficulty)) {
            return false;
        }

        String multiDay = filters.get("multiDay");
        if (multiDay != null && !multiDay.equals("Any time range")) {
            boolean isMultiDay = trail.isMultiDay();
            if (multiDay.equals("Multi-day") && !isMultiDay)
                return false;
            if (multiDay.equals("Day walk") && isMultiDay)
                return false;
        }

        return true;
    }

    /**
     * Filter out unwanted trails from sort
     */
    private boolean shouldIncludeInSort(Trail trail) {
        switch (currentSortBy.toLowerCase()) {
            case "time":
                return trail.getAvgCompletionTimeMinutes() > 0;
            case "difficulty":
                return !trail.getDifficulty().equalsIgnoreCase("unknown");
            default:
                return true;
        }
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
        Comparator<Trail> comparator;

        switch (currentSortBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(Trail::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "time":
                comparator = Comparator.comparingInt(Trail::getAvgCompletionTimeMinutes);
                break;
            case "difficulty":
                comparator = getDifficultyComparator();
                break;
            case "match":
                comparator = Comparator.comparingDouble(Trail::getUserWeight).reversed();
                break;
            default:
                comparator = Comparator.comparing(Trail::getName, String.CASE_INSENSITIVE_ORDER);
                break;
        }

        return isAscending ? comparator : comparator.reversed();
    }

    /**
     * Gets a comparator for sorting by difficulty level.
     */
    private Comparator<Trail> getDifficultyComparator() {
        return Comparator.comparing(trail -> {
            String difficulty = trail.getDifficulty().toLowerCase();
            int index = DIFFICULTY_ORDER.indexOf(difficulty);
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
     * Gets the difficulty order for UI components.
     */
    public static List<String> getDifficultyOrder() {
        return DIFFICULTY_ORDER;
    }
}