package tech.ravon.service.inver.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.CollectionDao;
import tech.ravon.mapper.CollectionItemDao;
import tech.ravon.model.inver.CollectionItem;
import tech.ravon.model.inver.Collections;
import tech.ravon.service.inver.CollectionService;
import tech.ravon.service.inver.UserService;

import java.util.List;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionDao collectionDao;

    @Autowired
    private CollectionItemDao collectionItemDao;

    @Lazy
    @Autowired
    private UserService userService;

    @Override
    public String createCollection(Long userId, String collectionName, String collectionDescription) {
        Collections collection = new Collections();
        collection.setUserId(userId);
        collection.setCollectionName(collectionName);
        collection.setCollectionDescription(collectionDescription);
        collectionDao.updateCollection(collection);
        collection = collectionDao.getCollectionsByColl(collection);
        if (collection == null || collection.getCollectionId() == null) return "Error in create collection";
        return null;
    }

    @Override
    public String updateCollection(Long collectionId, String collectionName, String collectionDescription) {
        return "";
    }

    @Override
    public String deleteCollection(Long collectionId) {
        return "";
    }

    @Override
    public String deleteCollectionByUser(Long userId) {
        return "";
    }

    @Override
    public List<Collections> getCollectionsByUser(Long userId) {
        List<Collections> collections = collectionDao.getCollectionsByUser(userId, "Default");
        if (collections == null || collections.isEmpty()) return null;
        return collections;
    }

    @Override
    public Collections getCollectionById(Long collectionId) {
        return null;
    }

    @Override
    public String updateCollItem(Long userId, Long itemId, String itemType) {
        List<Collections> collections = getCollectionsByUser(userId);
        Collections collection = new Collections();
        if (collections == null || collections.isEmpty()) {
            String result = createCollection(userId, "Default", "Default Collection");
            if (result != null) return "Error in generate default collection";
        }
        collection = collections.get(0);

        CollectionItem collectionItem = new CollectionItem();
        collectionItem.setCollectionId(collection.getCollectionId());
        collectionItem.setItemType(itemType);
        collectionItem.setItemId(itemId);

        CollectionItem existCollectionItem = collectionItemDao.getCollectionItemByItem(collectionItem);

        if (existCollectionItem == null || existCollectionItem.getCollectionItemId() == null) {
            collectionItemDao.updateCollectionItem(collectionItem);
            collectionItem = collectionItemDao.getCollectionItemByItem(collectionItem);
            if (collectionItem == null || collectionItem.getCollectionItemId() == null) return "Error in insert to default collection";
        } else {
            collectionItemDao.deleteCollectionItemByItem(collectionItem);
        }
        return null;
    }

    @Override
    public String deleteCollItem(Long collectionItemId) {
        return "";
    }

    @Override
    public String deleteCollItemByUser(Long userId) {
        return "";
    }

    @Override
    public String deleteCollItemByColl(Long collectionId) {
        return "";
    }

    @Override
    public List<CollectionItem> getCollItemByColl(Long collectionId) {
        return null;
    }

    @Override
    public CollectionItem getCollectionItemByItem(Long collectionId, String itemType, Long itemId) {
        CollectionItem collectionItem = new CollectionItem();
        collectionItem.setCollectionId(collectionId);
        collectionItem.setItemType(itemType);
        collectionItem.setItemId(itemId);
        collectionItem = collectionItemDao.getCollectionItemByItem(collectionItem);
        if (collectionItem.getCollectionItemId() == null) return null;
        return collectionItem;
    }
}
