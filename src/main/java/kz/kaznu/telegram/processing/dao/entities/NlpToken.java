package kz.kaznu.telegram.processing.dao.entities;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NlpToken {

    private Long id;
    private String token;

    public NlpToken(String token) {
        this.token = token;
    }

    public NlpToken(Long id) {
        this.id = id;
    }
}
