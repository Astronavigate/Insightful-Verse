package tech.ravon.service.iviep.impl;

import org.springframework.stereotype.Service;
import tech.ravon.service.iviep.PageService;

import java.util.List;

@Service
public class PageServiceImpl implements PageService {
    @Override
    public <T> List<T> pageList(List<T> list, int page, int max) {
        int startIndex = (page - 1) * max;
        if (startIndex >= list.size()) {
            startIndex = list.size() - 1;
        } else if (startIndex < 0) {
            startIndex = 0;
        }
        int endIndex = Math.min(startIndex + max, list.size());
        if (endIndex > list.size()) {
            endIndex = list.size() - 1;
        } else if (endIndex < 0) {
            endIndex = 0;
        } else if (endIndex < startIndex) {
            endIndex = startIndex;
        }
        return list.subList(startIndex, endIndex);
    }
}
