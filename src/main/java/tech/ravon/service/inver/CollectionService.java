package tech.ravon.service.inver;

import org.springframework.stereotype.Service;
import tech.ravon.model.inver.CollectionItem;
import tech.ravon.model.inver.Collections;

import java.util.List;

@Service
public interface CollectionService {

    String createCollection(Long userId, String collectionName, String collectionDescription);

    String updateCollection(Long collectionId, String collectionName, String collectionDescription);

    String deleteCollection(Long collectionId);

    String deleteCollectionByUser(Long userId);

    List<Collections> getCollectionsByUser(Long userId);

    Collections getCollectionById(Long collectionId);

    String updateCollItem(Long userId, Long itemId, String itemType);

    String deleteCollItem(Long collectionItemId);

    String deleteCollItemByUser(Long userId);

    String deleteCollItemByColl(Long collectionId);

    List<CollectionItem> getCollItemByColl(Long collectionId);

    CollectionItem getCollectionItemByItem(Long collectionId, String itemType, Long itemId);

}
