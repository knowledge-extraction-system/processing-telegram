package kz.kaznu.telegram.processing.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenFrequency {

    private String token;
    private Double frequency;

}
