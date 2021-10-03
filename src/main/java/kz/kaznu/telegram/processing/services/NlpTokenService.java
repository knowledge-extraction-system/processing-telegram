package kz.kaznu.telegram.processing.services;

import kz.kaznu.telegram.processing.dao.entities.NlpToken;
import kz.kaznu.telegram.processing.dao.repositories.NlpTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NlpTokenService {

    @Autowired
    private NlpTokenRepository nlpTokenRepository;

    public Long saveAndReturnId(NlpToken nlpToken) {
        NlpToken nlpTokenFromDB = nlpTokenRepository.findByToken(nlpToken.getToken());
        if (nlpTokenFromDB == null) {
            return nlpTokenRepository.insert(nlpToken);
        } else {
            return nlpTokenFromDB.getId();
        }
    }

}
