package dev.mateorossello.merp.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomUser {
    private Long id;
    private String username;
}
