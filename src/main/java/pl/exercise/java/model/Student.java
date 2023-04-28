package pl.exercise.java.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private String email;
    private String name;
    private Integer rate;
    private LocalDate registrationDate;
}
