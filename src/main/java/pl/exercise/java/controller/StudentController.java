package pl.exercise.java.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.exercise.java.model.Student;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {
    private List<Student> databases;

    @PostConstruct
    public void initTestData(){
        databases=new ArrayList<>();
        databases.add(new Student("s.wojcik@gmail.com","Sylwester Wójcik",300, LocalDate.now()));
        databases.add(new Student("f.wojcik@gmail.com","Franciszek Wójcik",300, LocalDate.now()));
        databases.add(new Student("m.wojcik@gmail.com","Michał Wójcik",300, LocalDate.now()));
    }

    @GetMapping
    public ResponseEntity getAllStudents(){
        return new ResponseEntity(databases, HttpStatus.OK);
    }
    @GetMapping("/{email}")
    public ResponseEntity getSingleStudent(@PathVariable String email){
        return databases.stream()
                .filter(s->s.getEmail().equals(email))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(()-> new ResponseEntity("Brak studenta o email: "+ email,HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity addNewStudent(@RequestBody Student newStudent){
        //1. jak to zrobic thread-safe
        if(databases.stream().anyMatch(s->s.getEmail().equals(newStudent.getEmail())))
        {
            return new ResponseEntity("Taki email " +newStudent.getEmail()+" już istnieje",HttpStatus.BAD_REQUEST);
        }
        databases.add(newStudent);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @PutMapping("/{email}")
    public ResponseEntity editStudent(@PathVariable String email,@RequestBody Student dataToedit){
        if(databases.stream().anyMatch(s->s.getEmail().equals(email))){
            Student toEdit=databases.stream().filter(s->s.getEmail().equals(email)).findFirst().get();
            toEdit.setRate(dataToedit.getRate());
            toEdit.setName(dataToedit.getName());
            toEdit.setRegistrationDate(dataToedit.getRegistrationDate());
            return new ResponseEntity(toEdit,HttpStatus.OK);
        }
        return new ResponseEntity("Taki email " +email+" już istnieje",HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{email}")
    public ResponseEntity editStudentPartially(@PathVariable String email,@RequestBody Student dataToEdit){
        Student toEdit = databases.stream().filter((s->s.getEmail().equals(email))).findFirst().get();
        Optional.ofNullable(dataToEdit.getName()).ifPresent(toEdit::setName);
        Optional.ofNullable(dataToEdit.getRate()).ifPresent(toEdit::setRate);
        Optional.ofNullable(dataToEdit.getRegistrationDate()).ifPresent(toEdit::setRegistrationDate);
        return new ResponseEntity(toEdit,HttpStatus.OK);

    }
    @DeleteMapping("/{email}")
    public ResponseEntity deleteStudent(@PathVariable String email){
        if(databases.removeIf(s-> s.getEmail().equals(email))){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity("Brak studenta o email: "+ email,HttpStatus.NOT_FOUND);
    }
}
