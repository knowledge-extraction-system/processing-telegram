package kz.kaznu.telegram.processing.services;


import kz.kaznu.telegram.processing.dao.entities.NlpUserToken;
import kz.kaznu.telegram.processing.dao.repositories.NlpUserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NlpUserTokenService {

    @Autowired
    private NlpUserTokenRepository nlpUserTokenRepository;

    public Long incrementCount(NlpUserToken nlpUserToken) {
        final NlpUserToken nlpUserTokenFromDB = nlpUserTokenRepository.findByUserAndToken(nlpUserToken.getTelegramUser().getId(),
                nlpUserToken.getNlpToken().getId());

        if (nlpUserTokenFromDB == null) {
            nlpUserToken.setCount(1L);
            nlpUserTokenRepository.insert(nlpUserToken);
            return nlpUserToken.getCount();
        } else {
            nlpUserTokenFromDB.setCount(nlpUserTokenFromDB.getCount() + 1);
            nlpUserTokenRepository.update(nlpUserTokenFromDB);
            return nlpUserTokenFromDB.getCount();
        }
    }

}
