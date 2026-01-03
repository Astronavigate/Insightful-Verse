package tech.ravon.service.inver;

import org.springframework.stereotype.Service;
import tech.ravon.model.inver.Annotation;

import java.util.List;

@Service
public interface AnnotationService {

    java.util.List<Annotation> getAnnotations(Long userId, Long bookId);

    /**
     * 按类型获取标注
     */
    List<Annotation> getAnnotationsByType(Long userId, Long bookId, String type);

    /**
     * 新增 or 更新标注
     * id == null → 新增
     * id != null → 更新
     */
    boolean saveAnnotation(Annotation annotation);

    /**
     * 删除单条标注
     */
    boolean deleteAnnotation(Long id, Long userId);

    /**
     * 删除某本书下某用户的所有标注
     * （如重新导入、用户主动清空）
     */
    void deleteAnnotationsByUserAndBook(Long userId, Long bookId);

    void deleteAnnotationsByBook(Long bookId);

    void deleteAnnotationsByUser(Long userId);
}
