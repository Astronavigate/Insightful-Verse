package tech.ravon.service.inver.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.AnnotationDao;
import tech.ravon.model.inver.Annotation;
import tech.ravon.service.inver.AnnotationService;

import java.util.List;

@Service
public class AnnotationServiceImpl implements AnnotationService {

    @Autowired
    private AnnotationDao annotationDao;

    /**
     * 获取用户在某一本书下的所有标注（EPUB / PDF 通用）
     */
    @Override
    public List<Annotation> getAnnotations(Long userId, Long bookId) {
        return annotationDao.getAnnotationsByUserAndBook(userId, bookId);
    }

    /**
     * 按类型获取标注
     */
    @Override
    public List<Annotation> getAnnotationsByType(Long userId, Long bookId, String type) {
        return annotationDao.getAnnotationsByType(userId, bookId, type);
    }

    /**
     * 新增 or 更新标注
     * id == null → 新增
     * id != null → 更新
     */
    @Override
    public boolean saveAnnotation(Annotation annotation) {
        return annotationDao.insertAnnotation(annotation) > 0;
    }

    /**
     * 删除单条标注
     */
    @Override
    public boolean deleteAnnotation(Long id, Long userId) {
        return annotationDao.deleteAnnotation(id, userId) > 0;
    }

    /**
     * 删除某本书下某用户的所有标注
     * （如重新导入、用户主动清空）
     */
    @Override
    public void deleteAnnotationsByUserAndBook(Long userId, Long bookId) {
        annotationDao.deleteAnnotationsByUserAndBook(userId, bookId);
    }

    @Override
    public void deleteAnnotationsByBook(Long bookId) {
        annotationDao.deleteAnnotationsByBook(bookId);
    }

    @Override
    public void deleteAnnotationsByUser(Long userId) {
        annotationDao.deleteAnnotationsByUser(userId);
    }
}
