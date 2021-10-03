package kz.kaznu.telegram.processing.services;

import kz.kaznu.telegram.processing.dao.entities.NlpChatToken;
import kz.kaznu.telegram.processing.dao.repositories.NlpChatTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NlpChatTokenService {

    @Autowired
    private NlpChatTokenRepository nlpChatTokenRepository;

    public Long incrementCount(NlpChatToken nlpChatToken) {
        final NlpChatToken nlpChatTokenFromDB = nlpChatTokenRepository.findByChatAndToken(nlpChatToken.getTelegramChat().getId(),
                nlpChatToken.getNlpToken().getId());

        if (nlpChatTokenFromDB == null) {
            nlpChatToken.setCount(1L);
            nlpChatTokenRepository.insert(nlpChatToken);
            return nlpChatToken.getCount();
        } else {
            nlpChatTokenFromDB.setCount(nlpChatTokenFromDB.getCount() + 1);
            nlpChatTokenRepository.update(nlpChatTokenFromDB);
            return nlpChatTokenFromDB.getCount();
        }
    }

}
