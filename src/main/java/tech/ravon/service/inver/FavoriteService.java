package tech.ravon.service.inver;

import org.springframework.stereotype.Service;

@Service
public interface FavoriteService {
    String toggleFavorite(Long userId, Long itemId, String itemType);
}
