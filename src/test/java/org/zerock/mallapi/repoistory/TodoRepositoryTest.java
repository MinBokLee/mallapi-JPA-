package org.zerock.mallapi.repoistory;

import static org.mockito.ArgumentMatchers.refEq;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.TodoDTO;
import org.zerock.mallapi.repository.TodoRepository;
import org.zerock.mallapi.service.TodoService;

import lombok.extern.log4j.Log4j2;


@SpringBootTest
@Log4j2
public class TodoRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoService todoService;

    @Test
    public void test1(){

        log.info("------------------------");
        log.info(todoRepository);
    }

    @Test
    public void testInsert() {
        for(int i=1; i <= 100; i++){
            Todo todo = Todo.builder()
                            .title("Title" + i)
                            .dueDate(LocalDate.of(2025,12,31))
                            .writer("user00")
                            .build();
                        todoRepository.save(todo);
        }
    }

    
    @Test
    public void testready(){
        Long tno = 34L;
        
        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();
            
        log.info(todo.getTitle());
        
    }

    @Test 
    public void testModify(){
        Long tno = 10L;

        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();
        todo.changeTitle("Modified 22...");
        todo.changeComplete(true);
        todo.changeDueDate(LocalDate.of(2025,12,24));

        todoRepository.save(todo);

    }

    @Test
    public void testDelete(){
        Long tno = 22L;

        todoRepository.deleteById(tno);
    }

    @Test
    public void testpaging(){
        Pageable pageable   = PageRequest.of(0,10,Sort.by("tno").descending());
        Page<Todo> result = todoRepository.findAll(pageable);
        log.info(result.getTotalElements());

        result.getContent().stream().forEach((todo -> log.info(todo.toString())));
    }

    @Test
    public void testRegister(){
        TodoDTO todoDto = TodoDTO.builder()
                            .title("서비스 테스트")
                            .writer("tester")
                            .dueDate(LocalDate.of(2025,12,02))
                            .build();

        Long tno = todoService.register(todoDto);
        log.info("TNO" + tno);

    }

    @Test
    public void testGet(){
        Long tno = 101L;

            TodoDTO todoDto = todoService.get(tno);
            log.info(todoDto);
    }

    @Test
    public void modify(){
        Long tno = 102L;

        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();
        todo.changeTitle("modified 101...");
        todo.changeComplete(true);
        todo.changeDueDate(LocalDate.of(2025, 12, 02));

        todoRepository.save(todo);

    }
@Test
public void remove(){

Long tno = 101L;

    todoRepository.deleteById(tno);
}

@Test
public void testList(){
    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                                                  .page(2)
                                                  .size(10)
                                                  .build();
    PageResponseDTO<TodoDTO> response = todoService.list(pageRequestDTO);   

    log.info(response);
}

}
