package tech.ravon.service.inver.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.CollectionDao;
import tech.ravon.mapper.CollectionItemDao;
import tech.ravon.model.inver.CollectionItem;
import tech.ravon.model.inver.Collections;
import tech.ravon.service.inver.FavoriteService;
import tech.ravon.service.inver.UserService;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private CollectionDao collectionDao;

    @Autowired
    private CollectionItemDao collectionItemDao;

    @Lazy
    @Autowired
    private UserService userService;

    @Override
    public String toggleFavorite(Long userId, Long itemId, String itemType) {

        List<Collections> collections = collectionDao.getCollectionsByUser(userId, "Default");
        Collections collection = new Collections();
        if (collections == null || collections.isEmpty()) {
            collection.setCollectionName("Default");
            collection.setCollectionDescription("Default Collection");
            collection.setUserId(userId);
            collectionDao.updateCollection(collection);
            collections = collectionDao.getCollectionsByUser(userId, "Default");
            if (collections == null || collections.isEmpty()) return "Error in generate default collection";
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
}
