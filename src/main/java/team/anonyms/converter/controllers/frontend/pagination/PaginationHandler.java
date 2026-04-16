package team.anonyms.converter.controllers.frontend.pagination;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *     This class is dedicated to handle all methods associated with pagination.
 * </p>
 * @param <Objects> any objects to handle.
 */
@Component
public final class PaginationHandler<Objects> {
    /**
     * <p>
     *     This method makes slices from lists of any objects.
     * </p>
     *
     * @param objectsToMakeSlice any objects to make slice.
     * @param offset number of page.
     * @param limit number of elements per page.
     *
     * @return slice.
     *
     * @throws IllegalArgumentException Either if {@code offset} is negative or if {@code offset} is more than
     * number of the last available slice of provided elements.
     */
    public List<Objects> makeSliceFromList(List<Objects> objectsToMakeSlice, int offset, int limit) {
        if (offset < 1) {
            throw new IllegalArgumentException("Offset must be positive; offset=" + offset);
        }

        if (limit * (offset - 1) > objectsToMakeSlice.size()) {
            throw new IllegalArgumentException("Number of page is out of range of provided elements;" +
                    " offset=" +  offset + ";objectsSize=" + objectsToMakeSlice.size());
        }

        List<Objects> slice = new ArrayList<>();

        if (limit * (offset) <= objectsToMakeSlice.size()) {
            slice = objectsToMakeSlice.subList(limit * (offset - 1), limit * offset);
        } else {
            if (limit * (offset - 1) < objectsToMakeSlice.size()) {
                slice = objectsToMakeSlice.subList(limit * (offset - 1), objectsToMakeSlice.size());
            } else {
                slice.add(objectsToMakeSlice.getLast());
            }
        }

        return slice;
    }
}