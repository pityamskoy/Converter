package team.anonyms.converter.controllers.frontend.pagination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     This class is dedicated to handle all methods associated with pagination.
 * </p>
 *
 * @param <Objects> any objects to handle.
 */
@Component
public class PaginationHandler<Objects> {
    private static final Logger logger = LoggerFactory.getLogger(PaginationHandler.class);

    /**
     * <p>
     *     This method makes a slice from the provided list of any objects.
     * </p>
     *
     * @param objectsToMakeSlice any objects to make slice.
     * @param offset number of page.
     * @param limit number of elements per page.
     *
     * @return a slice of {@code objectsToMakeSlice}. Note, that this method returns the empty slice
     * if {@code offset} is negative or is more than the offset of the last available slice of provided elements.
     */
    public List<Objects> makeSliceFromList(List<Objects> objectsToMakeSlice, int offset, int limit) {
        List<Objects> slice = new ArrayList<>();

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