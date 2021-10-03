package kz.kaznu.telegram.processing.dao.extended;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NlpChatTokenExtended {

    private String token;
    private Long chatCount;

}
