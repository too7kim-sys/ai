package egovframework.groupware.cmm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Paging {
    private int page = 1;
    private int size = 20;
    private long total;

    public int getOffset() {
        return Math.max(0, (page - 1) * size);
    }

    public int getTotalPages() {
        return size == 0 ? 0 : (int) ((total + size - 1) / size);
    }
}
