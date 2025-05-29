package tech.ravon.service.iviep;

import java.util.List;

public interface PageService {
    <T> List<T> pageList(List<T> list, int page, int max);
}
