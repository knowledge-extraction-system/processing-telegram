package kz.kaznu.telegram.processing.services;

import kz.kaznu.telegram.processing.dao.entities.TelegramUser;
import kz.kaznu.telegram.processing.dao.repositories.TelegramUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramUserService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private TelegramUserRepository telegramUserRepository;

    public TelegramUser getTelegramUserById(Long telegramUserId) {
        return telegramUserRepository.findById(telegramUserId);
    }

}
