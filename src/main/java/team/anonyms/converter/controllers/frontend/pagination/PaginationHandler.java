package team.anonyms.converter.controllers.frontend.pagination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> the type of elements to paginate.
 */
@Component
public class PaginationHandler<T> {
    private static final Logger logger = LoggerFactory.getLogger(PaginationHandler.class);

    /**
     * @param objectsToMakeSlice any objects to make slice.
     * @param offset number of page.
     * @param limit number of elements per page.
     *
     * @return a slice of {@code objectsToMakeSlice}. Note, that this method returns the empty slice
     * if {@code offset} is negative or is more than the offset of the last available slice of provided elements.
     */
    public List<T> makeSliceFromList(List<T> objectsToMakeSlice, int offset, int limit) {
        List<T> slice = new ArrayList<>();

        if (offset < 1) {
            logger.info("Offset must be positive; offset={}", offset);
        }

        if (limit * (offset - 1) > objectsToMakeSlice.size()) {
            logger.info("Number of page is out of range of provided elements; offset={}; objectsSize={}",
                    offset, objectsToMakeSlice.size());
        }

        if (limit * (offset) <= objectsToMakeSlice.size()) {
            slice = objectsToMakeSlice.subList(limit * (offset - 1), limit * offset);
        } else {
            if (limit * (offset - 1) < objectsToMakeSlice.size()) {
                slice = objectsToMakeSlice.subList(limit * (offset - 1), objectsToMakeSlice.size());
            } else if (limit * (offset - 1) == objectsToMakeSlice.size()) {
                slice.add(objectsToMakeSlice.getLast());
            }
        }

        return slice;
    }
}